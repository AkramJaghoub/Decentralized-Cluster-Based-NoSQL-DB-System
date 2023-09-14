package com.example.Database.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Database {
    private final Map<String, Collection> collections;
    private final ReentrantLock collectionLock;
    private final String databaseName;

    public Database(String databaseName) {
        this.databaseName = databaseName;
        collections = new HashMap<>();
        collectionLock = new ReentrantLock();
    }

    public String getDatabaseName() {
        return databaseName;
    }

   public ReentrantLock getCollectionLock(){
        return collectionLock;
   }

    public void createCollection(String collectionName) {
        if (collectionName == null || collectionName.trim().isEmpty()) {
            throw new IllegalArgumentException("Collection name cannot be null or empty.");
        }
        Collection collection = new Collection(collectionName);
        collections.put(collectionName, collection);
    }

    public List<String> readCollections() {
        return new ArrayList<>(collections.keySet());
    }

    public void deleteCollection(String collectionName) {
        if (collectionName == null || collectionName.trim().isEmpty()) {
            throw new IllegalArgumentException("Database name cannot be null or empty.");
        }
        collections.remove(collectionName);
    }
}