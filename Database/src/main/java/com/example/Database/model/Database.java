package com.example.Database.model;

import java.util.HashMap;
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
        System.out.println(collectionName);
        System.out.println(collections.entrySet().toString());
        System.out.println(collections.values());
    }

    public Collection getCollection(String collectionName) {
        if (collectionName == null || collectionName.trim().isEmpty()) {
            throw new IllegalArgumentException("Collection name cannot be null or empty.");
        }
        return collections.get(collectionName);
    }

    public void deleteCollection(String collectionName){
        if (collectionName == null || collectionName.trim().isEmpty()) {
            throw new IllegalArgumentException("Collection name cannot be null or empty.");
        }
        try {
            Collection collection = collections.get(collectionName);
            if (collection == null) {
                throw new ClassNotFoundException("Collection '" + collectionName + "' not found.");
            }
            collections.remove(collectionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}