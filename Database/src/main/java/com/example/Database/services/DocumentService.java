package com.example.Database.services;

import com.example.Database.affinity.AffinityManager;
import com.example.Database.file.DatabaseFileOperations;
import com.example.Database.file.FileService;
import com.example.Database.index.IndexManager;
import com.example.Database.model.ApiResponse;
import com.example.Database.model.Collection;
import com.example.Database.model.Document;
import com.example.Database.schema.SchemaValidator;
import com.example.Database.schema.datatype.DataTypeUtil;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.util.*;
import java.io.File;

import org.json.simple.JSONObject;

@Service
public class DocumentService {

    private final SchemaValidator schemaValidator;
    private final IndexManager indexManager;
    private final AffinityManager affinityManager;


    @Autowired
    public DocumentService(SchemaValidator schemaValidator, IndexManager indexManager, AffinityManager affinityManager) {
        this.schemaValidator = schemaValidator;
        this.indexManager = indexManager;
        this.affinityManager = affinityManager;
    }

    @SuppressWarnings("unchecked")
    public ApiResponse createDocument(Collection collection, Document document) {
        System.out.println("Thread " + Thread.currentThread().getName() + ": Entering createDocument method with document id: " + document.getId());
        String collectionName = collection.getCollectionName().toLowerCase();
        collection.getDocumentLock().lock();
        try {
            if (!indexManager.indexExists(collectionName)) {
                indexManager.createIndex(collectionName);
            }
            JSONObject jsonData = document.getData();
            String accountNumber = (jsonData.get("accountNumber") instanceof Long)
                    ? Long.toString((Long) jsonData.get("accountNumber"))
                    : document.getData().get("accountNumber").toString();
            if (accountNumber != null) {
                if (!indexManager.propertyIndexExists(collectionName, "accountNumber")) {
                    indexManager.createPropertyIndex(collectionName, "accountNumber");
                }
                String existingDocumentId = indexManager.searchInPropertyIndex(collectionName, "accountNumber", accountNumber);
                System.out.println(existingDocumentId + " existing documentttttttt");
                if (existingDocumentId != null) {
                    return new ApiResponse("An account with the same account number already exists.", HttpStatus.CONFLICT);
                }
            }

            System.out.println("TRYING TO HASH PASSWORDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
            if (jsonData.containsKey("password") && !document.isReplicated()) { // added the check for the flag
                String potentialPassword = jsonData.get("password").toString();
                System.out.println(PasswordHashing.isAlreadyHashed(potentialPassword) + " hashhhhhhhhhhhhhhhhhhhhhhhhhh");
                if (!PasswordHashing.isAlreadyHashed(potentialPassword)) {
                    System.out.println("PASSWORD HAVEN'T BEEN PROCESSED YET WAIT UNTIL WE HASH IT.............");
                    String hashedPassword = PasswordHashing.hashPassword(potentialPassword);
                    jsonData.put("password", hashedPassword);
                }
            }
            if (document.isValidDocument(schemaValidator, collectionName)) {
                String documentId = document.getId();
                int workerPort = affinityManager.getWorkerPort(documentId);   //adding affinity based on the document's id
                document.setHasAffinity(true);
                document.setNodeWithAffinity(workerPort);
                System.out.println(document.getNodeWithAffinity());
                System.out.println(document.hasAffinity());
                List<String> indexedProperties = Arrays.asList("accountType", "hasInsurance", "balance", "accountNumber");
                for (Object key : jsonData.keySet()) {
                    Object value = jsonData.get(key);
                    if (key != null && value != null && indexedProperties.contains(key.toString())) {
                        String propertyName = key.toString();
                        if (!indexManager.propertyIndexExists(collectionName, propertyName)) {
                            indexManager.createPropertyIndex(collectionName, propertyName);
                        }
                        indexManager.insertIntoPropertyIndex(collectionName, propertyName, value.toString(), document.getId());
                    }
                }
                ApiResponse response = DatabaseFileOperations.appendDocumentToFile(collectionName, document);
                if (response.getMessage().contains("successfully")) {
                    int lastIndex = getJSONArrayLength(FileService.getCollectionFile(collectionName)) - 1;
                    indexManager.insertIntoIndex(collectionName, document.getId(), lastIndex);
                }
                return response;
            } else {
                return new ApiResponse("Document does not match the schema for " + collectionName, HttpStatus.CONFLICT);
            }
        } finally {
            collection.getDocumentLock().unlock();
        }
    }


    private int getJSONArrayLength(File collectionFile) {
        JSONArray jsonArray = FileService.readJsonArrayFile(collectionFile);
        return jsonArray != null ? jsonArray.size() : 0;
    }


    public List<JSONObject> readDocuments(Collection collection) {
        String collectionName = collection.getCollectionName().toLowerCase();
        collection.getDocumentLock().lock();
        try {
            Map<String, Integer> documentIdIndexes = DatabaseFileOperations.readDocumentIndexesFromFile(collectionName);
            List<JSONObject> documents = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : documentIdIndexes.entrySet()) {
                String documentId = entry.getKey();
                String indexResult = indexManager.searchInIndex(collectionName, documentId);
                if (indexResult != null) {
                    int index = Integer.parseInt(indexResult);
                    JSONObject document = DatabaseFileOperations.readDocumentFromFile(collectionName, index);
                    if (document != null) {
                        documents.add(document);
                    }
                }
            }
            return documents;
        } finally {
            collection.getDocumentLock().unlock();
        }
    }

    public synchronized ApiResponse deleteDocument(Collection collection, Document document) {
        System.out.println("Thread " + Thread.currentThread().getName() + ": Entering deleteDocument method with document id: " + document.getId());
        String collectionName = collection.getCollectionName().toLowerCase();
        collection.getDocumentLock().lock();
        try {
            if (!indexManager.indexExists(collectionName)) {
                indexManager.createIndex(collectionName);
            }
            JSONObject deletedDocument = DatabaseFileOperations.deleteDocument(collectionName, document.getId(), indexManager);
            if (deletedDocument != null) {
                indexManager.deleteFromIndex(collectionName, document.getId());
                List<String> indexedProperties = Arrays.asList("accountNumber", "balance", "accountType", "hasInsurance");
                for (String propertyName : indexedProperties) {
                    if (deletedDocument.containsKey(propertyName) && indexManager.propertyIndexExists(collectionName, propertyName)) {
                       String propertyValue = deletedDocument.get(propertyName).toString();
                        indexManager.deleteFromPropertyIndex(collectionName, propertyName, propertyValue);
                    }
                }
                return new ApiResponse("Document deleted successfully from " + collectionName, HttpStatus.ACCEPTED);
            } else {
                return new ApiResponse("Failed to delete document from " + collectionName, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } finally {
            collection.getDocumentLock().unlock();
        }
    }

    public ApiResponse updateDocumentProperty(Collection collection, Document document) {
        String collectionName = collection.getCollectionName().toLowerCase();
        String documentId = document.getId();
        System.out.println("Attempting to update documentId: " + documentId + " on " + Thread.currentThread().getName());
        String propertyName = document.getPropertyName();
        String newPropertyValue = (String) document.getPropertyValue();
        Object castedValue = DataTypeUtil.castToDataType(newPropertyValue, collectionName, propertyName);
        String castedValueString = castedValue.toString();
        collection.getDocumentLock().lock();
        try {
            if (!indexManager.propertyIndexExists(collectionName, propertyName)) {
                indexManager.createPropertyIndex(collectionName, propertyName);
            }
            ApiResponse response = DatabaseFileOperations.updateDocumentProperty(collectionName, documentId, propertyName, castedValueString, indexManager);
            if (response.getMessage().contains("successfully")) {
                indexManager.insertIntoPropertyIndex(collectionName, propertyName, castedValueString, documentId);
            }
            return response;
        } finally {
            collection.getDocumentLock().unlock();
        }
    }
}