package com.example.Database.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            System.out.println("Database already exists: " + databaseName);
        }
        Database database = new Database(databaseName);
        databases.put(databaseName, database);
    }


    public Database getOrCreateDatabase(String databaseName) {
        if (databaseName == null || databaseName.trim().isEmpty()) {
            throw new IllegalArgumentException("Database name cannot be null or empty.");
        }
        System.out.println(databaseName);
        return databases.computeIfAbsent(databaseName, Database::new);
    }

    public List<String> readDatabases() {
        return new ArrayList<>(databases.keySet());
    }

    public void deleteDatabase(String databaseName) {
        if (databaseName == null || databaseName.trim().isEmpty()) {
            throw new IllegalArgumentException("Database name cannot be null or empty.");
        }
        databases.remove(databaseName);
    }
}