package com.example.Database.index;

import com.example.Database.file.FileService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class IndexManager {

    private final Map<String, Index> indexMap = new ConcurrentHashMap<>();
    private final Map<String, PropertyIndex> propertyIndexMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        List<String> allDatabases = FileService.getAllKnownDatabases();
        for (String dbName : allDatabases) {
            FileService.setDatabaseDirectory(dbName);
            this.loadAllIndexes();
        }
    }

    public void loadAllIndexes() {
        System.out.println("Loading all indexes...");
        File databasePath = new File(FileService.getDatabasePath().toURI());
        File indexesDirectory = new File(databasePath, "indexes");
        if(!indexesDirectory.exists()){
            return;
        }
        String[] collectionDirectories = indexesDirectory.list();
        if (collectionDirectories != null) {
            for (String collectionDir : collectionDirectories) {
                File currentDir = new File(indexesDirectory, collectionDir);
                String[] filesInDir = currentDir.list();
                if (filesInDir != null) {
                    for (String indexFile : filesInDir) {
                        if (FileService.isPropertyIndexFile(indexFile)) {
                            loadPropertyIndexForCollection(indexFile);
                        } else if (FileService.isIndexFile(indexFile)) {
                            loadIndexForCollection(indexFile);
                        }
                    }
                }
            }
        } else {
            System.out.println("No collections found.");
        }
    }

    private void loadIndexForCollection(String collectionFileName) {
        String cleanCollectionName = collectionFileName.substring(0, collectionFileName.length() - 10);
        String indexPath = FileService.getIndexFilePath(cleanCollectionName);
        Index index = new Index();
        indexMap.put(cleanCollectionName, index);
        loadFromFile(indexPath, index);
    }

    private void loadPropertyIndexForCollection(String collectionFileName) {
        String cleanCollectionName = collectionFileName.substring(0, collectionFileName.length() - 17);
        String[] split = cleanCollectionName.split("_");
        String derivedCollectionName = split[0];
        String property = split[1];
        String propertyIndexPath = FileService.getPropertyIndexFilePath(derivedCollectionName, property);
        PropertyIndex propertyIndex = new PropertyIndex();
        propertyIndexMap.put(derivedCollectionName + "_" + property, propertyIndex);
        loadFromFile(propertyIndexPath, propertyIndex);
    }

    private synchronized void loadFromFile(String path, Object index) {
        if (!FileService.isFileExists(path)) {
            return;
        }
        Map<String, String> indexData = FileService.readIndexFile(path);
        for (Map.Entry<String, String> entry : indexData.entrySet()) {
            if(index instanceof Index) {
                ((Index) index).insert(entry.getKey(), entry.getValue());
            } else if(index instanceof PropertyIndex) {
                ((PropertyIndex) index).insert(entry.getKey(), entry.getValue());
            }
        }
    }

    private Index getIndex(String collectionName) {
        Index index = indexMap.get(collectionName);
        if (index == null) {
            throw new IllegalArgumentException("Index does not exist.");
        }
        return index;
    }


    public void createIndex(String collectionName) {
        if (!indexMap.containsKey(collectionName)) {
            Index index = new Index();
            indexMap.put(collectionName, index);
        }
    }

    public synchronized void insertIntoIndex(String collectionName, String documentId, int index) {
        String existingValue = getIndex(collectionName).search(documentId);
        if (existingValue == null) {
            getIndex(collectionName).insert(documentId, String.valueOf(index));
            FileService.appendToIndexFile(FileService.getIndexFilePath(collectionName), documentId, String.valueOf(index));
        }
    }

    public synchronized void deleteFromIndex(String collectionName, String documentId) {
        Index index = getIndex(collectionName);
        int deletedIndex = -1;
        List<Map.Entry<String, String>> allEntries = new ArrayList<>(index.getBPlusTree().getAllEntries());
        String indexValue = index.search(documentId);
        if (indexValue != null) {
            deletedIndex = Integer.parseInt(indexValue);
        } else {
            throw new IllegalArgumentException("Document not found in the index.");
        }
        index.delete(documentId);
        Map<String, String> updatedEntries = new HashMap<>();
        for (Map.Entry<String, String> entry : allEntries) {
            int currentIndex = Integer.parseInt(entry.getValue());
            if (currentIndex > deletedIndex) {
                updatedEntries.put(entry.getKey(), String.valueOf(currentIndex - 1));
            } else {
                updatedEntries.put(entry.getKey(), String.valueOf(currentIndex));
            }
        }
        updatedEntries.remove(documentId);
        index.getBPlusTree().clearTree();
        for (Map.Entry<String, String> entry : updatedEntries.entrySet()) {
            index.insert(entry.getKey(), entry.getValue());
        }
        FileService.rewriteIndexFile(collectionName, index);
    }

    public String searchInIndex(String collectionName, String documentId) {
        return getIndex(collectionName).search(documentId);
    }

    public synchronized void createPropertyIndex(String collectionName, String propertyName) {
        String propertyIndexKey = collectionName + "_" + propertyName;
        if (!propertyIndexMap.containsKey(propertyIndexKey)) {
            PropertyIndex index = new PropertyIndex();
            propertyIndexMap.put(propertyIndexKey, index);
        }
    }

    public synchronized void insertIntoPropertyIndex(String collectionName, String propertyName, String propertyValue, String documentId) {
        String propertyIndexKey = collectionName + "_" + propertyName;
        PropertyIndex propertyIndex = propertyIndexMap.get(propertyIndexKey);
        if (propertyIndex == null) {
            throw new IllegalArgumentException("Property Index does not exist.");
        }
        String combinedKey = documentId + "_" + propertyName;
        String existingPropertyValue = propertyIndex.search(combinedKey);
        if (!propertyValue.equals(existingPropertyValue)) {
            System.out.println("Inserted new entry in property index for collection: " + collectionName + ", property: " + propertyName);
            propertyIndex.insert(combinedKey, propertyValue);
            FileService.appendToIndexFile(FileService.getPropertyIndexFilePath(collectionName, propertyName), combinedKey, propertyValue);
        } else {
            System.out.println("Entry already exists in property index for collection: " + collectionName + ", property: " + propertyName);
        }
    }

    public synchronized String searchInPropertyIndex(String collectionName, String propertyName, String documentId) {
        String propertyIndexKey = collectionName + "_" + propertyName;
        PropertyIndex propertyIndex = propertyIndexMap.get(propertyIndexKey);
        if (propertyIndex == null) {
            throw new IllegalArgumentException("Property Index does not exist.");
        }
        String combinedKey = documentId + "_" + propertyName;
        System.out.println("searching for combinedKey " + combinedKey + " property name " + propertyName + " collection " + collectionName);
        return propertyIndex.search(combinedKey);
    }

    // Delete method also needs a slight modification.
    public synchronized void deleteFromPropertyIndex(String collectionName, String propertyName, String documentId) {
        String propertyIndexKey = collectionName + "_" + propertyName;
        PropertyIndex propertyIndex = propertyIndexMap.get(propertyIndexKey);
        if (propertyIndex == null) {
            throw new IllegalArgumentException("Property Index does not exist.");
        }
        String combinedKey = documentId + "_" + propertyName;
        propertyIndex.delete(combinedKey);
        System.out.println("Deleted property with combinedKey " + combinedKey + " property name " + propertyName + " collection " + collectionName);
        FileService.rewritePropertyIndexFile(FileService.getPropertyIndexFilePath(collectionName, propertyName), propertyIndex);
    }


    public boolean propertyIndexExists(String collectionName, String propertyName) {
        String propertyIndexKey = collectionName + "_" + propertyName;
        return propertyIndexMap.containsKey(propertyIndexKey);
    }

    public boolean indexExists(String collectionName) {
        return indexMap.containsKey(collectionName);
    }
}