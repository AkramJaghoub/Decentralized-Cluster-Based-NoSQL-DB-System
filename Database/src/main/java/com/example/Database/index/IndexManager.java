package com.example.Database.index;

import com.example.Database.file.FileService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import static com.example.Database.file.FileService.getIndexFilePath;


@Component
@Lazy
public class IndexManager {
    private final Map<String, Index> indexMap = new HashMap<>();
    private final Map<String, PropertyIndex> propertyIndexMap = new HashMap<>();

    public IndexManager() {}

    public void initializeIndexes() {
        loadAllIndexes();
    }

    private void loadAllIndexes() {
        File databasePath = new File(FileService.getDatabasePath().toURI());
        File indexesDirectory = new File(databasePath, "indexes");
        if (!indexesDirectory.exists()) {
            boolean dirCreated = indexesDirectory.mkdirs();
            if (!dirCreated) {
                System.out.println("Failed to create 'indexes' directory");
                return;
            }
        }
        String[] collectionNames = indexesDirectory.list();
        if (collectionNames != null) {
            for (String collectionName : collectionNames) {
                String cleanCollectionName = collectionName.endsWith("_index.txt") ? collectionName.substring(0, collectionName.length() - 10) : collectionName;
                String indexPath = getIndexFilePath(cleanCollectionName);
                Index index = new Index();
                indexMap.put(cleanCollectionName, index);
                if (new File(indexPath).exists()) {
                    System.out.println("File " + indexPath + " Loading...");
                    loadIndexFromFile(indexPath, index);
                } else {
                    System.out.println("File doesn't exist.");
                }
            }
        } else {
            System.out.println("No collections found.");
        }
    }


    private void loadIndexFromFile(String path, Index index) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                int key = Integer.parseInt(parts[0]);
                String value = parts[1];
                index.insert(key, value);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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

    public void insertIntoIndex(String collectionName, String documentId, int index) {
        int hashcode = documentId.hashCode();
        String existingValue = getIndex(collectionName).search(hashcode);
        if (existingValue == null) {
            getIndex(collectionName).insert(hashcode, String.valueOf(index));
            appendToFile(collectionName, hashcode, index);
        }
    }

    private void appendToFile(String collectionName, int hashcode, int index) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(getIndexFilePath(collectionName), true))) {
            writer.write(hashcode + "," + index);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFromIndex(String collectionName, String documentId) {
        int hashcode = documentId.hashCode();
        Index index = getIndex(collectionName);
        boolean documentFound = false;
        int deletedIndex = -1;
        List<Map.Entry<Integer, String>> allEntries = new ArrayList<>(index.getBPlusTree().getAllEntries());
        for (Map.Entry<Integer, String> entry : allEntries) {
            if (entry.getKey() == hashcode) {
                documentFound = true;
                deletedIndex = Integer.parseInt(entry.getValue());
                index.delete(hashcode);
                break;
            }
        }
        if (!documentFound) {
            throw new IllegalArgumentException("Document not found in the index.");
        }
        Map<Integer, String> updatedEntries = new HashMap<>();
        for (Map.Entry<Integer, String> entry : allEntries) {
            int currentIndex = Integer.parseInt(entry.getValue());
            if (currentIndex > deletedIndex) {
                updatedEntries.put(entry.getKey(), String.valueOf(currentIndex - 1));
            } else {
                updatedEntries.put(entry.getKey(), String.valueOf(currentIndex));
            }
        }
        updatedEntries.remove(hashcode);
        index.getBPlusTree().clearTree();
        for (Map.Entry<Integer, String> entry : updatedEntries.entrySet()) {
            index.insert(entry.getKey(), entry.getValue());
        }
        rewriteIndexFile(collectionName, index);
    }

    private void rewriteIndexFile(String collectionName, Index index) {
        File file = new File(getIndexFilePath(collectionName));
        if (file.exists()) {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Map.Entry<Integer, String> entry : index.getBPlusTree().getAllEntries()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String searchInIndex(String collectionName, String documentId) {
        int hashcode = documentId.hashCode();
        return getIndex(collectionName).search(hashcode);
    }


    public void createPropertyIndex(String collectionName, String propertyName) {
        String propertyIndexKey = collectionName + "_" + propertyName;
        if (!propertyIndexMap.containsKey(propertyIndexKey)) {
            PropertyIndex index = new PropertyIndex();
            propertyIndexMap.put(propertyIndexKey, index);
        }
    }

    public void insertIntoPropertyIndex(String collectionName, String propertyName, int propertyHashCode, int documentIndex) {
        String propertyIndexKey = collectionName + "_" + propertyName;
        PropertyIndex indexObj = propertyIndexMap.get(propertyIndexKey);
        if (indexObj == null) {
            throw new IllegalArgumentException("Property Index does not exist.");
        }
        indexObj.insert(Integer.toString(propertyHashCode), documentIndex);
    }

    public Integer searchInPropertyIndex(String collectionName, String propertyName, int propertyHashCode) {
        String propertyIndexKey = collectionName + "_" + propertyName;
        PropertyIndex index = propertyIndexMap.get(propertyIndexKey);
        if (index == null) {
            throw new IllegalArgumentException("Property Index does not exist.");
        }
        return index.search(Integer.toString(propertyHashCode));
    }

    public boolean propertyIndexExists(String collectionName, String propertyName) {
        String propertyIndexKey = collectionName + "_" + propertyName;
        return propertyIndexMap.containsKey(propertyIndexKey);
    }

    public boolean indexExists(String collectionName) {
        return indexMap.containsKey(collectionName);
    }
}