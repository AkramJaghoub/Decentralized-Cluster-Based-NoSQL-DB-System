package com.example.Database.services;

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
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;

@Service
public class DocumentService {

    private final IndexManager indexManager;
    private final SchemaValidator schemaValidator;

    @Autowired
    public DocumentService(IndexManager indexingManager, SchemaValidator schemaValidator) {
        this.indexManager = indexingManager;
        this.schemaValidator = schemaValidator;
    }

    @SuppressWarnings("unchecked")
    public ApiResponse createDocument(Collection collection, Document document) {
        String collectionName = collection.getCollectionName().toLowerCase();
        collection.getDocumentLock().lock();
        try {
            if (indexManager.indexExists(collectionName)) {
                indexManager.createIndex(collectionName);
            }
            if (document.isValidDocument(schemaValidator, collectionName)) {
                if (document.getId() == null) {
                    String id = UUID.randomUUID().toString();
                    document.setId(id);
                    document.getData().put("_id", id);       //here we are storing document's id
                }
                ApiResponse response = DatabaseFileOperations.appendDocumentToFile(collectionName, document.getData());
                if (response.getMessage().contains("successfully")) {
                    int lastIndex = getJSONArrayLength(FileService.getCollectionFile(collectionName)) - 1;
                    indexManager.insertIntoIndex(collectionName, document.getId(), lastIndex);
                }
                return response;
            } else {
                return new ApiResponse("Document does not match schema for " + collectionName);
            }
        } finally {
            collection.getDocumentLock().unlock();
        }
    }

    private int getJSONArrayLength(File collectionFile) {
        JSONArray jsonArray = FileService.readJsonArrayFile(collectionFile);
        return jsonArray != null ? jsonArray.size() : 0;
    }

    public ApiResponse deleteDocument(Collection collection, Document document) {
        String collectionName = collection.getCollectionName().toLowerCase();
        collection.getDocumentLock().lock();
        try {
            if (indexManager.indexExists(collectionName)) {
                indexManager.createIndex(collectionName);
            }
            ApiResponse response = DatabaseFileOperations.deleteDocument(collectionName, document.getId(), indexManager);
            if (response.getMessage().contains("successfully")) {
                indexManager.deleteFromIndex(collectionName, document.getId());
            }
            return response;
        } finally {
            collection.getDocumentLock().unlock();
        }
    }

    public ApiResponse updateDocumentProperty(Collection collection, Document document) {
        String collectionName = collection.getCollectionName().toLowerCase();
        String documentId = document.getId();
        String propertyName = document.getPropertyName();
        String newPropertyValue = (String) document.getPropertyValue();
        Object castedValue = DataTypeUtil.castToDataType(newPropertyValue, collectionName, propertyName);
        collection.getDocumentLock().lock();
        try {
            if (!indexManager.propertyIndexExists(collectionName, propertyName)) {
                indexManager.createPropertyIndex(collectionName, propertyName);
            }
            ApiResponse response = DatabaseFileOperations.updateDocumentProperty(collectionName, documentId, propertyName, castedValue, indexManager);
            if (response.getMessage().contains("successfully")) {
                int index = Integer.parseInt(indexManager.searchInIndex(collectionName, documentId));
                indexManager.insertIntoPropertyIndex(collectionName, propertyName, castedValue.hashCode(), index);
            }

            return response;
        } finally {
            collection.getDocumentLock().unlock();
        }
    }
}