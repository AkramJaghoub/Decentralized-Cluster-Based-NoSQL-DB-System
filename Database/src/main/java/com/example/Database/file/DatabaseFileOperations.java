package com.example.Database.file;

import com.example.Database.index.IndexManager;
import com.example.Database.model.ApiResponse;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public final class DatabaseFileOperations {

    private DatabaseFileOperations() {}

    public static ApiResponse createDatabase() {
        File dbDirectory = FileService.getDatabasePath();
        if (FileService.isDirectoryExists(dbDirectory)) {
            FileService.createDirectoryIfNotExist(dbDirectory);
            File schemasDirectory = new File(dbDirectory + "/schemas/");
            FileService.createDirectoryIfNotExist(schemasDirectory);
            return new ApiResponse("database added successfully", HttpStatus.ACCEPTED);
        } else {
            return new ApiResponse("Database already exists.", HttpStatus.BAD_REQUEST);
        }
    }

    public static ApiResponse deleteDatabase() {
        File dbDirectory = FileService.getDatabasePath();
        if (FileService.isDirectoryExists(dbDirectory)) {
            return new ApiResponse("Database does not exist.", HttpStatus.NOT_FOUND);
        }
        try {
            FileUtils.deleteDirectory(dbDirectory);
            return new ApiResponse("Database has been successfully deleted.", HttpStatus.ACCEPTED);
        } catch (IOException e) {
            return new ApiResponse("Failed to delete database: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public static List<String> readDatabases() {
        File rootDirectory = FileService.getRootFile();
        if (FileService.isDirectoryExists(rootDirectory)) {
            return Collections.emptyList();
        }
        String[] directories = rootDirectory.list((current, name) -> new File(current, name).isDirectory());
        if (directories == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(directories);
    }

    public static ApiResponse createCollection(String collectionName, JSONObject jsonSchema) {
        File dbDirectory = FileService.getDatabasePath();
        if (FileService.isDirectoryExists(dbDirectory)) {
            return new ApiResponse("Database not found.", HttpStatus.NOT_FOUND);
        }
        File collectionFile = FileService.getCollectionFile(collectionName);
        File schemaFile = FileService.getSchemaPath(collectionName);
        if (FileService.isFileExists(collectionFile.getPath())) {
            return new ApiResponse("Collection already exists.", HttpStatus.BAD_REQUEST);
        }
        try {
            Files.write(Paths.get(collectionFile.getAbsolutePath()), "[]".getBytes());
            Files.write(Paths.get(schemaFile.getAbsolutePath()), jsonSchema.toString().getBytes());
            return new ApiResponse("Collection has been successfully created", HttpStatus.ACCEPTED);
        } catch (IOException e) {
            return new ApiResponse("Failed to create collection: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    public static ApiResponse deleteCollection(String collectionName) {
        File databaseFile = FileService.getDatabasePath();
        if (FileService.isDirectoryExists(databaseFile)) {
            return new ApiResponse("Database not found.", HttpStatus.NOT_FOUND);
        }
        File collectionFile = FileService.getCollectionFile(collectionName);
        File schemaFile = FileService.getSchemaPath(collectionName);
        File indexDirectory = new File(FileService.getDatabasePath() + "/indexes/" + collectionName + "_indexes");
        if (!FileService.isFileExists(collectionFile.getPath()) || !FileService.isFileExists(schemaFile.getPath())) {
            return new ApiResponse("Collection or schema do not exist.", HttpStatus.NOT_FOUND);
        }
        boolean isCollectionDeleted = collectionFile.delete();
        boolean isSchemaDeleted = schemaFile.delete();
        deleteDirectoryRecursively(indexDirectory);
        if (isCollectionDeleted && isSchemaDeleted) {
            return new ApiResponse("Collection, and it's schema and indexes have been successfully deleted", HttpStatus.ACCEPTED);
        } else {
            return new ApiResponse("Failed to delete collection, associated schema, or index.", HttpStatus.BAD_REQUEST);
        }
    }

    public static void deleteDirectoryRecursively(File directory) {
        if (directory.exists()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                System.out.println(file.getAbsolutePath());
                if (file.isDirectory()) {
                    deleteDirectoryRecursively(file);
                } else {
                    file.delete();
                }
            }
            directory.delete();
        }
    }

    public static List<String> readCollections() {
        File dbDirectory = FileService.getDatabasePath();
        if (FileService.isDirectoryExists(dbDirectory)) {
            return Collections.emptyList();
        }
        File[] collectionFiles = dbDirectory.listFiles((dir, name) -> name.endsWith(".json"));
        if (collectionFiles == null) {
            return Collections.emptyList();
        }
        List<String> collectionNames = new ArrayList<>();
        for (File collectionFile : collectionFiles) {
            String fileName = collectionFile.getName();
            int extensionIndex = fileName.lastIndexOf(".");
            if (extensionIndex >= 0) {
                String collectionName = fileName.substring(0, extensionIndex);
                collectionNames.add(collectionName);
            }
        }
        return collectionNames;
    }

    @SuppressWarnings("unchecked")
    public static ApiResponse appendDocumentToFile(String collectionName, JSONObject document) {
        File collectionFile = FileService.getCollectionFile(collectionName);
        JSONArray jsonArray = FileService.readJsonArrayFile(collectionFile);
        if (jsonArray == null) {
            return new ApiResponse("Failed to read the existing documents", HttpStatus.BAD_REQUEST);
        }
        jsonArray.add(document);
        boolean writeStatus = FileService.writeJsonArrayFile(collectionFile, jsonArray);
        if (!writeStatus) {
            return new ApiResponse("Failed to append document.", HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse("Document added successfully.", HttpStatus.ACCEPTED);
    }

    @SuppressWarnings("unchecked")
    private static int getDocumentIndex(String collectionName, String documentId, JSONArray jsonArray, IndexManager indexManager) throws Exception {
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

    public static Map<String, Integer> readDocumentIndexesFromFile(String collectionName) {
        String indexFilePath = FileService.getIndexFilePath(collectionName);
        Map<String, Integer> documentIdIndexMap = new HashMap<>();
        try {
            if (!FileService.isFileExists(indexFilePath)) {
                return documentIdIndexMap;
            }
            List<String> lines = Files.readAllLines(Paths.get(indexFilePath), StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String documentId = parts[0].trim();
                    int index = Integer.parseInt(parts[1].trim());
                    documentIdIndexMap.put(documentId, index);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return documentIdIndexMap;
    }

    public static JSONObject readDocumentFromFile(String collectionName, int index) {
        File collectionFile = FileService.getCollectionFile(collectionName);
        JSONArray jsonArray = FileService.readJsonArrayFile(collectionFile);
        if (jsonArray != null && index >= 0 && index < jsonArray.size()) {
            return (JSONObject) jsonArray.get(index);
        }
        return null;
    }


    public static JSONObject deleteDocument(String collectionName, String documentId, IndexManager indexManager) {
        try {
            JSONArray jsonArray = new JSONArray();
            int index = getDocumentIndex(collectionName, documentId, jsonArray, indexManager);
            if (index < 0) {
                return null;
            }
            JSONObject removedDocument = (JSONObject) jsonArray.get(index);
            jsonArray.remove(index);
            File collectionFile = FileService.getCollectionFile(collectionName);
            boolean writeStatus = FileService.writeJsonArrayFile(collectionFile, jsonArray);
            return writeStatus ? removedDocument : null;
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static ApiResponse updateDocumentProperty(String collectionName, String documentId, String propertyName, Object newValue, IndexManager indexManager) {
        try {
            JSONArray jsonArray = new JSONArray();
            int index = getDocumentIndex(collectionName, documentId, jsonArray, indexManager);
            if (index < 0) {
                return new ApiResponse("Document with id " + documentId + " not found in " + collectionName, HttpStatus.NOT_FOUND);
            }
            JSONObject documentData = (JSONObject) jsonArray.get(index);
            Object oldValue = documentData.get(propertyName);
            if (Objects.equals(oldValue, newValue)) {
                return new ApiResponse("No changes needed. Document with id " + documentId + " in " + collectionName + " already has the same value for property " + propertyName, HttpStatus.BAD_REQUEST);
            }
            documentData.put(propertyName, newValue);
            File collectionFile = FileService.getCollectionFile(collectionName);
            boolean writeStatus = FileService.writeJsonArrayFile(collectionFile, jsonArray);
            indexManager.insertIntoPropertyIndex(collectionName, propertyName, newValue.toString(), documentId);
            if (oldValue != null) {
                indexManager.deleteFromPropertyIndex(collectionName, propertyName, oldValue.toString());
            }
            return writeStatus ?
                    new ApiResponse("Document with id " + documentId + " updated successfully in " + collectionName, HttpStatus.ACCEPTED) :
                    new ApiResponse("Failed to update document with id " + documentId + " in " + collectionName, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ApiResponse("Error updating document property: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}