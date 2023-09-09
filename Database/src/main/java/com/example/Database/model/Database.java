package com.example.Database.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public class Database {
    private final Map<String, Collection> collections;
    private final ReentrantLock collectionLock;

    public Database() {
        collections = new HashMap<>();
        collectionLock = new ReentrantLock();
    }

    public ReentrantLock getCollectionLock() {
        return collectionLock;
    }

    public Collection createCollection(String name) {
        Collection collection = new Collection();
        collections.put(name, collection);
        return collection;
    }

    public Optional<Collection> getCollection(String collectionName) {
        return Optional.ofNullable(collections.get(collectionName));
    }

    public void deleteCollection(String collectionName) throws ClassNotFoundException {
        if (!collections.containsKey(collectionName)) {
            throw new ClassNotFoundException();
        }
        collections.remove(collectionName);
    }
}
