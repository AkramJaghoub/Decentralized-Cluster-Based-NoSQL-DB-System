package com.example.Database.services;

import com.example.Database.file.FileService;
import com.example.Database.index.IndexManager;
import com.example.Database.schema.SchemaValidator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class DocumentService {

    private final IndexManager indexManager;

    @Autowired
    public DocumentService() {
        this.indexManager = new IndexManager();
    }


    @SuppressWarnings("unchecked")
    public String createDocument(String dbName, String collectionName, JSONObject document) {
        dbName = dbName.trim().toLowerCase();
        collectionName = collectionName.toLowerCase();
        File collectionFile = FileService.getCollectionPath(collectionName);
        if (!indexManager.indexExists(collectionName)) {
            indexManager.createIndex(collectionName);
        }
        if (isValidDocument(collectionName, document)) {
            UUID uuid = UUID.randomUUID();
            String id = uuid.toString();
            if (!document.containsKey("id")) {
                document.put("_id", id);
            }
            String status = appendDocumentToFile(collectionFile.getAbsolutePath(), document);
            if ("Document added successfully.".equals(status)) {
                int lastIndex = getJSONArrayLength(collectionFile) - 1;
                indexManager.insertIntoIndex(collectionName, id, lastIndex);
            }
            return status;
        } else {
            return "Document does not match schema";
        }
    }

    public String deleteDocument(String dbName, String collectionName, String documentId) {
        collectionName = collectionName.toLowerCase();
        File collectionFile = FileService.getCollectionPath(collectionName);
        if (!indexManager.indexExists(collectionName)) {
            indexManager.createIndex(collectionName);
        }
        JSONArray jsonArray = readJSONArrayFromFile(collectionFile);
        if (jsonArray == null) {
            return "Failed to read the existing documents";
        }
        String searchResult = indexManager.searchInIndex(collectionName, documentId);
        if (searchResult == null) {
            return "Document not found.";
        }
        int index = Integer.parseInt(searchResult);
        jsonArray.remove(index);
        try (FileWriter writer = new FileWriter(collectionFile)) {
            writer.write(jsonArray.toJSONString());
        } catch (IOException e) {
            return "Failed to delete document: " + e.getMessage();
        }
        indexManager.deleteFromIndex(collectionName, documentId);
        return "Document deleted successfully.";
    }


    @SuppressWarnings("unchecked")
    public String updateDocumentProperty(String dbName, String collectionName, String documentId, String propertyName, String newValue) {
        Object castedValue = castToDataType(newValue, collectionName, propertyName);
        String searchResult = indexManager.searchInIndex(collectionName, documentId);
        if (searchResult == null) {
            return "Document not found.";
        }
        int index = Integer.parseInt(searchResult);
        File collectionFile = FileService.getCollectionPath(collectionName.toLowerCase());
        JSONArray jsonArray = readJSONArrayFromFile(collectionFile);
        if (jsonArray == null) {
            return "Failed to read the existing documents";
        }
        JSONObject document = (JSONObject) jsonArray.get(index);
        if (document == null) {
            return "Document not found at index.";
        }
        document.put(propertyName, castedValue);
        if (!indexManager.propertyIndexExists(collectionName, propertyName)) {
            indexManager.createPropertyIndex(collectionName, propertyName);
        }
        indexManager.insertIntoPropertyIndex(collectionName, propertyName, castedValue.hashCode(), index);
        try (FileWriter writer = new FileWriter(collectionFile)) {
            writer.write(jsonArray.toJSONString());
        } catch (IOException e) {
            return "Failed to update document: " + e.getMessage();
        }
        return "Document updated successfully.";
    }

    private Object castToDataType(String newValue, String collectionName, String propertyName) {
        String dataType = getDataType(collectionName, propertyName);
        System.out.println(dataType);
        return switch (Objects.requireNonNull(dataType).toUpperCase()) {
            case "STRING" -> newValue;
            case "LONG" -> Long.parseLong(newValue);
            case "DOUBLE" -> Double.parseDouble(newValue);
            case "BOOLEAN" -> Boolean.parseBoolean(newValue);
            default -> null;
        };
    }

    private String getDataType(String collectionName, String property) {
        JSONObject schema = FileService.readSchema(collectionName);
        if (schema != null && schema.containsKey("properties") && ((JSONObject)schema.get("properties")).containsKey(property)) {
            return (String)((JSONObject)schema.get("properties")).get(property);
        }
        return null;
    }

    private JSONArray readJSONArrayFromFile(File collectionFile) {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(collectionFile)) {
            Object obj = parser.parse(reader);
            return (JSONArray) obj;
        } catch (IOException | ParseException e) {
            return null;
        }
    }

    public boolean isValidDocument(String collectionName, JSONObject document) {
        SchemaValidator validator = new SchemaValidator();
        String jsonString = document.toJSONString();
        return validator.schemaValidator(collectionName, jsonString);
    }

    @SuppressWarnings("unchecked")
    public String appendDocumentToFile(String filePath, JSONObject document) {
        JSONArray jsonArray = readJSONArrayFromFile(new File(filePath));
        if (jsonArray == null) {
            return "Failed to read the existing documents";
        }
        jsonArray.add(document);
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(jsonArray.toJSONString());
        } catch (IOException e) {
            return "Failed to append document: " + e.getMessage();
        }
        return "Document added successfully.";
    }

    private int getJSONArrayLength(File collectionFile) {
        JSONArray jsonArray = readJSONArrayFromFile(collectionFile);
        return jsonArray != null ? jsonArray.size() : 0;
    }
}