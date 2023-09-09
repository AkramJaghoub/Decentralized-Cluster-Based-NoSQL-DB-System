package com.example.Database.model;

import java.util.HashMap;
import java.util.Map;

public class InMemoryDatabase {
    private final Map<String, Database> databases;
    private static InMemoryDatabase instance;

    private InMemoryDatabase() {
        this.databases = new HashMap<>();
    }

    public static InMemoryDatabase getInstance(){
        if(instance == null)
            instance = new InMemoryDatabase();
        return instance;
    }

    public void createDatabase(String databaseName) {
        if (databaseName == null || databaseName.trim().isEmpty()) {
            throw new IllegalArgumentException("Database name cannot be null or empty.");
        }
        if (databases.containsKey(databaseName)) {
            throw new IllegalArgumentException("Database already exists: " + databaseName);
        }
        Database database = new Database(databaseName);
        databases.put(databaseName, database);
    }

    public Database getDatabase(String databaseName) {
        if (databaseName == null || databaseName.trim().isEmpty()) {
            throw new IllegalArgumentException("Database name cannot be null or empty.");
        }

        return databases.get(databaseName);
    }

    public void deleteDatabase(String databaseName) {
        if (databaseName == null || databaseName.trim().isEmpty()) {
            throw new IllegalArgumentException("Database name cannot be null or empty.");
        }
        if (!databases.containsKey(databaseName)) {
            throw new IllegalArgumentException("Database does not exist: " + databaseName);
        }
        databases.remove(databaseName);
    }
}