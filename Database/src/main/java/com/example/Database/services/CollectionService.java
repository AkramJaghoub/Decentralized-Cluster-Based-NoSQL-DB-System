package com.example.Database.services;

import com.example.Database.file.DatabaseFileOperations;
import com.example.Database.model.ApiResponse;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import com.example.Database.model.Database;

@Service
public class CollectionService {

    public ApiResponse createCollection(Database database, String collectionName, JSONObject jsonSchema) {
        String dbName = database.getDatabaseName();
        dbName = dbName.trim().toLowerCase();
        database.getCollectionLock().lock();
        try {
            database.createCollection(collectionName);
            return DatabaseFileOperations.createCollection(dbName, collectionName, jsonSchema);
        } finally {
            database.getCollectionLock().unlock();
        }
    }

    public ApiResponse deleteCollection(Database database, String collectionName) {
        String dbName = database.getDatabaseName();
        dbName = dbName.trim().toLowerCase();
        database.getCollectionLock().lock();
        try {
            database.deleteCollection(collectionName);
            return DatabaseFileOperations.deleteCollection(dbName, collectionName);
        } finally {
            database.getCollectionLock().unlock();
        }
    }

//    public ApiResponse listCollections(String dbName) {
//        dbName = dbName.trim().toLowerCase();
//        File dbDirectory = FileService.getDatabasePath();
//        if (!dbDirectory.exists()) return new ApiResponse("Database directory does not exist.");
//
//        File[] files = dbDirectory.listFiles();
//        if (files == null) return new ApiResponse("Failed to list collections.");
//
//        List<String> collections = new ArrayList<>();
//        for (File file : files) {
//            if (isCollection(file)) {
//                collections.add(file.getName());
//            }
//        }
//
//        if (collections.isEmpty()) {
//            return new ApiResponse("No collections found.");
//        }
//
//        // Convert the list of collections to a descriptive string or JSON.
//        // Here, I'm assuming ApiResponse has a constructor that accepts an object.
//        // Modify as needed for your actual ApiResponse implementation.
//        return new ApiResponse(collections);
//    }

//    private boolean isCollection(File file) {
//        return file.isFile();
//    }
}