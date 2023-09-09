package com.example.Database.services;

import com.example.Database.file.FileService;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CollectionService {
    public String createCollection(String dbName, String collectionName, JSONObject jsonSchema) {
        dbName = dbName.trim().toLowerCase();
        File dbDirectory = FileService.getDatabasePath();
        if (!FileService.isExists(dbDirectory)) {
            return "Database not found.";
        }
        File collectionFile = FileService.getCollectionPath(collectionName);
        File schemaFile = FileService.getSchemaPath(collectionName);
        if (collectionFile.exists()) {
            return "Collection already exists.";
        }
        try {
            Files.write(Paths.get(collectionFile.getAbsolutePath()), "[]".getBytes());
            Files.write(Paths.get(schemaFile.getAbsolutePath()), jsonSchema.toString().getBytes());
            return "Collection " + collectionName + " created in database " + dbName;
        } catch (IOException e) {
            return "Failed to create collection: " + e.getMessage();
        }
    }

    public String deleteCollection(String dbName, String collectionName) {
        dbName = dbName.trim().toLowerCase();
        File databaseFile = FileService.getDatabasePath();
        if(!FileService.isExists(databaseFile)){
            return "Database not found.";
        }
        File collectionFile = FileService.getCollectionPath(collectionName);
        File schemaFile = FileService.getSchemaPath(collectionName);
        if (!collectionFile.exists()) {
            return "Collection not found.";
        }
        boolean isCollectionDeleted = collectionFile.delete();
        boolean isSchemaDeleted = schemaFile.delete();
        if (isCollectionDeleted && isSchemaDeleted) {
            return "Collection " + collectionName + " deleted from database " + dbName;
        } else {
            return "Failed to delete collection or associated schema.";
        }
    }

    public List<String> listCollections(String dbName) {
        dbName = dbName.trim().toLowerCase();
        File dbDirectory = FileService.getDatabasePath();
        if (!dbDirectory.exists()) return Collections.emptyList();
        File[] files = dbDirectory.listFiles();
        if (files == null) return Collections.emptyList();
        List<String> collections = new ArrayList<>();
        for (File file : files) {
            if (isCollection(file)) {
                collections.add(file.getName());
            }
        }
        return collections;
    }

    private boolean isCollection(File file) {
        return file.isFile();
    }
}