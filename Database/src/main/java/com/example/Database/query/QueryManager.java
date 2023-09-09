package com.example.Database.query;

import com.example.Database.query.Command.QueryCommand;
import com.example.Database.query.Command.Factory.QueryObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.json.simple.JSONObject;
import java.util.Map;

@Service
public class QueryManager {

    private final Map<QueryType, QueryCommand> queryMap;

    @Autowired
    public QueryManager(QueryObjectFactory queryObjectFactory) {
        this.queryMap = queryObjectFactory.queryMap();
    }

    public JSONObject createDatabase(String dbName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryType", QueryType.CREATE_DATABASE.toString());
        jsonObject.put("databaseName", dbName);
        return execute(jsonObject);
    }

    public JSONObject deleteDatabase(String dbName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryType", QueryType.DELETE_DATABASE.toString());
        jsonObject.put("databaseName", dbName);
        return execute(jsonObject);
    }

    public JSONObject createCollection(String dbName, String collectionName, JSONObject schema) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryType", QueryType.CREATE_COLLECTION.toString());
        jsonObject.put("databaseName", dbName);
        jsonObject.put("collectionName", collectionName);
        jsonObject.put("schema", schema);
        return execute(jsonObject);
    }

    public JSONObject deleteCollection(String dbName, String collectionName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryType", QueryType.DELETE_COLLECTION.toString());
        jsonObject.put("databaseName", dbName);
        jsonObject.put("collectionName", collectionName);
        return execute(jsonObject);
    }

    public JSONObject createDocument(String databaseName, String collectionName, JSONObject document) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryType", QueryType.CREATE_DOCUMENT.toString());
        jsonObject.put("databaseName", databaseName);
        jsonObject.put("collectionName", collectionName);
        jsonObject.put("document", document);
        return execute(jsonObject);
    }

    public JSONObject deleteDocument(String databaseName, String collectionName, String documentId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryType", QueryType.DELETE_DOCUMENT.toString());
        jsonObject.put("databaseName", databaseName);
        jsonObject.put("collectionName", collectionName);
        jsonObject.put("documentId",documentId);
        return execute(jsonObject);
    }

    public JSONObject updateProperty(String databaseName,String collectionName, String documentId, String propertyName, String newValue) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("queryType", QueryType.UPDATE_INDEX.toString());
        jsonObject.put("databaseName", databaseName);
        jsonObject.put("collectionName", collectionName);
        jsonObject.put("documentId",documentId);
        jsonObject.put("propertyName", propertyName);
        jsonObject.put("newValue", newValue);
        return execute(jsonObject);
    }

    private JSONObject execute(JSONObject query) {
        QueryType queryType = QueryType.valueOf((String) query.get("queryType"));
        return queryMap.get(queryType).execute(query);
    }
}