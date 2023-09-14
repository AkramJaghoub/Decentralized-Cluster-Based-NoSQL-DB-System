package com.example.Database.query;

import com.example.Database.model.ApiResponse;
import com.example.Database.model.Document;
import com.example.Database.query.Command.QueryCommand;
import com.example.Database.query.Command.Factory.QueryObjectFactory;
import lombok.SneakyThrows;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings("unchecked")
public class QueryManager {

    private final Map<QueryType, QueryCommand> queryMap;

    @Autowired
    public QueryManager(QueryObjectFactory queryObjectFactory) {
        this.queryMap = queryObjectFactory.queryMap();
    }

    public ApiResponse createDatabase(String dbName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryType", QueryType.CREATE_DATABASE.toString());
        jsonObject.put("databaseName", dbName);
        return execute(jsonObject);
    }

    public ApiResponse deleteDatabase(String dbName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryType", QueryType.DELETE_DATABASE.toString());
        jsonObject.put("databaseName", dbName);
        return execute(jsonObject);
    }

    public List<String> readDatabases() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryType", QueryType.READ_DATABASES.toString());
        ApiResponse response = execute(jsonObject);
        List<String> databaseList = new ArrayList<>();
        if (response != null) {
            databaseList.add(response.getMessage());
        }
        return databaseList;
    }

    public ApiResponse createCollection(String dbName, String collectionName, JSONObject schema) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryType", QueryType.CREATE_COLLECTION.toString());
        jsonObject.put("databaseName", dbName);
        jsonObject.put("collectionName", collectionName);
        jsonObject.put("schema", schema);
        return execute(jsonObject);
    }

    public ApiResponse deleteCollection(String dbName, String collectionName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryType", QueryType.DELETE_COLLECTION.toString());
        jsonObject.put("databaseName", dbName);
        jsonObject.put("collectionName", collectionName);
        return execute(jsonObject);
    }

    public List<String> readCollections(String databaseName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryType", QueryType.READ_COLLECTIONS.toString());
        jsonObject.put("databaseName", databaseName);
        ApiResponse response = execute(jsonObject);
        List<String> collectionList = new ArrayList<>();
        if (response != null) {
            collectionList.add(response.getMessage());
        }
        return collectionList;
    }

    public ApiResponse createDocument(String databaseName, String collectionName, JSONObject document) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryType", QueryType.CREATE_DOCUMENT.toString());
        jsonObject.put("databaseName", databaseName);
        jsonObject.put("collectionName", collectionName);
        jsonObject.put("document", document);
        return execute(jsonObject);
    }

    public ApiResponse deleteDocument(String databaseName, String collectionName, String documentId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryType", QueryType.DELETE_DOCUMENT.toString());
        jsonObject.put("databaseName", databaseName);
        jsonObject.put("collectionName", collectionName);
        jsonObject.put("documentId",documentId);
        return execute(jsonObject);
    }

    public List<JSONObject> readDocuments(String databaseName, String collectionName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryType", QueryType.READ_DOCUMENTS.toString());
        jsonObject.put("databaseName", databaseName);
        jsonObject.put("collectionName", collectionName);
        ApiResponse response = execute(jsonObject);
        String jsonInput = response.getMessage();
        System.out.println("JSON Input: " + jsonInput);
        if (response != null) {
            JSONParser jsonParser = new JSONParser();
            try {
                JSONArray documentsArray = (JSONArray) jsonParser.parse(response.getMessage());
                List<JSONObject> documentList = new ArrayList<>();
                for (Object documentObj : documentsArray) {
                    // Create JSONObject objects from JSON and add them to the list
                    JSONObject document = (JSONObject) documentObj;
                    documentList.add(document);
                }
                return documentList;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return Collections.emptyList();
    }


    public ApiResponse updateProperty(String databaseName,String collectionName, String documentId, String propertyName, Object newPropertyValue) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryType", QueryType.UPDATE_INDEX.toString());
        jsonObject.put("databaseName", databaseName);
        jsonObject.put("collectionName", collectionName);
        jsonObject.put("documentId",documentId);
        jsonObject.put("propertyName", propertyName);
        jsonObject.put("newPropertyValue", newPropertyValue);
        return execute(jsonObject);
    }

    private ApiResponse execute(JSONObject query) {
        QueryType queryType = QueryType.valueOf((String) query.get("queryType"));
        return queryMap.get(queryType).execute(query);
    }
}