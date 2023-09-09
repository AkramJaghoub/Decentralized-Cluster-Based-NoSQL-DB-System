package com.example.Database.file;

import com.example.Database.index.IndexManager;
import com.example.Database.model.ApiResponse;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public final class DatabaseFileOperations {

    private DatabaseFileOperations() {}

    public static ApiResponse createDatabase(String databaseName) {
        File dbDirectory = FileService.getDatabasePath();
        if (FileService.isExists(dbDirectory)) {
            if (dbDirectory.mkdir()) {
                File schemasDirectory = new File(dbDirectory + "/schemas/");
                schemasDirectory.mkdirs();
                return new ApiResponse("Database " + databaseName + " created.");
            } else {
                return new ApiResponse("Failed to create database.");
            }
        } else {
            return new ApiResponse("Database already exists.");
        }
    }

    public static ApiResponse deleteDatabase(String databaseName) {
        File dbDirectory = FileService.getDatabasePath();
        if (FileService.isExists(dbDirectory)) {
            return new ApiResponse("Database does not exist.");
        }
        try {
            FileUtils.deleteDirectory(dbDirectory);
            return new ApiResponse("Database " + databaseName + " deleted.");
        } catch (IOException e) {
            return new ApiResponse("Failed to delete database: " + e.getMessage());
        }
    }

    public static ApiResponse createCollection(String databaseName, String collectionName, JSONObject jsonSchema) {
        File dbDirectory = FileService.getDatabasePath();
        if (FileService.isExists(dbDirectory)) {
            return new ApiResponse("Database not found.");
        }
        File collectionFile = FileService.getCollectionFile(collectionName);
        File schemaFile = FileService.getSchemaPath(collectionName);
        if (collectionFile.exists()) {
            return new ApiResponse("Collection already exists.");
        }
        try {
            Files.write(Paths.get(collectionFile.getAbsolutePath()), "[]".getBytes());
            Files.write(Paths.get(schemaFile.getAbsolutePath()), jsonSchema.toString().getBytes());
            return new ApiResponse("Collection " + collectionName + " created in database " + databaseName);
        } catch (IOException e) {
            return new ApiResponse("Failed to create collection: " + e.getMessage());
        }
    }

    public static ApiResponse deleteCollection(String databaseName, String collectionName) {
        File databaseFile = FileService.getDatabasePath();
        if (FileService.isExists(databaseFile)) {
            return new ApiResponse("Database not found.");
        }
        File collectionFile = FileService.getCollectionFile(collectionName);
        File schemaFile = FileService.getSchemaPath(collectionName);
        if (!collectionFile.exists()) {
            return new ApiResponse("Collection not found.");
        }
        boolean isCollectionDeleted = collectionFile.delete();
        boolean isSchemaDeleted = schemaFile.delete();
        if (isCollectionDeleted && isSchemaDeleted) {
            return new ApiResponse("Collection " + collectionName + " deleted from database " + databaseName);
        } else {
            return new ApiResponse("Failed to delete collection or associated schema.");
        }
    }

    @SuppressWarnings("unchecked")
    public static ApiResponse appendDocumentToFile(String collectionName, JSONObject document) {
        File collectionFile = FileService.getCollectionFile(collectionName);
        JSONArray jsonArray = FileService.readJsonArrayFile(collectionFile);
        if (jsonArray == null) {
            return new ApiResponse("Failed to read the existing documents");
        }
        jsonArray.add(document);
        boolean writeStatus = FileService.writeJsonArrayFile(collectionFile, jsonArray);
        if (!writeStatus) {
            return new ApiResponse("Failed to append document.");
        }
        return new ApiResponse("Document added successfully.");
    }

    @SuppressWarnings("unchecked")
    private static int getDocumentIndex(String collectionName, String documentId, IndexManager indexManager, JSONArray jsonArray) throws Exception {
        File collectionFile = FileService.getCollectionFile(collectionName);
        JSONArray tempArray = FileService.readJsonArrayFile(collectionFile);
        if (tempArray == null) {
            throw new Exception("Failed to read the existing documents for " + collectionName);
        }
        String searchResult = indexManager.searchInIndex(collectionName, documentId);
        if (searchResult == null) {
            throw new Exception("Document not found in " + collectionName);
        }
        jsonArray.addAll(tempArray);
        return Integer.parseInt(searchResult);
    }

    public static ApiResponse deleteDocument(String collectionName, String documentId, IndexManager indexManager) {
        try {
            JSONArray jsonArray = new JSONArray();
            int index = getDocumentIndex(collectionName, documentId, indexManager, jsonArray);
            if(index < 0) {
                return new ApiResponse("Document with id " + documentId + " not found in " + collectionName);
            }
            jsonArray.remove(index);
            File collectionFile = FileService.getCollectionFile(collectionName);
            boolean writeStatus = FileService.writeJsonArrayFile(collectionFile, jsonArray);
            return writeStatus ?
                    new ApiResponse("Document deleted successfully from " + collectionName) :
                    new ApiResponse("Failed to delete document from " + collectionName);
        } catch (Exception e) {
            return new ApiResponse(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static ApiResponse updateDocumentProperty(String collectionName, String documentId, String propertyName, Object newValue, IndexManager indexManager) {
        try {
            JSONArray jsonArray = new JSONArray();
            int index = getDocumentIndex(collectionName, documentId, indexManager, jsonArray);
            System.out.println(index);
            if(index < 0) {
                return new ApiResponse("Document with id " + documentId + " not found in " + collectionName);
            }
            JSONObject documentData = (JSONObject) jsonArray.get(index);
            documentData.put(propertyName, newValue);
            File collectionFile = FileService.getCollectionFile(collectionName);
            boolean writeStatus = FileService.writeJsonArrayFile(collectionFile, jsonArray);
            return writeStatus ?
                    new ApiResponse("Document with id " + documentId + " updated successfully in " + collectionName) :
                    new ApiResponse("Failed to update document with id " + documentId + " in " + collectionName);
        } catch (Exception e) {
            return new ApiResponse("Error updating document property: " + e.getMessage());
        }
    }
}