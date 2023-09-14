package com.example.Database.controllers;

import com.example.Database.file.FileService;
import com.example.Database.index.IndexManager;
import com.example.Database.model.ApiResponse;
import com.example.Database.query.QueryManager;
import com.example.Database.services.AuthenticationService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DocumentController {

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private QueryManager queryManager;
    @Autowired
    IndexManager indexManager;

    @PostMapping("/{db_name}/{collection_name}/createDoc")
    public String createDocument(@PathVariable("db_name") String dbName,
                                 @PathVariable("collection_name") String collectionName,
                                 @RequestBody JSONObject document,
                                 @RequestHeader("username") String username,
                                 @RequestHeader("password") String password) {
        if(authenticationService.isAdmin(username, password)){
            return "User is not authorized";
        }
        FileService.setDatabaseDirectory(dbName);
        indexManager.loadAllIndexes();
        ApiResponse response = queryManager.createDocument(dbName, collectionName, document);
        return response.getMessage();
    }

    @DeleteMapping("/{db_name}/{collection_name}/deleteDoc")
    public String deleteDocument(@PathVariable("db_name") String dbName,
                                 @PathVariable("collection_name") String collectionName,
                                 @RequestParam("doc_id") String documentId,
                                 @RequestHeader("username") String username,
                                 @RequestHeader("password") String password) {
        if(authenticationService.isAdmin(username, password)){
            return "User is not authorized";
        }
        FileService.setDatabaseDirectory(dbName);
        indexManager.loadAllIndexes();
        ApiResponse response = queryManager.deleteDocument(dbName, collectionName, documentId);
        return response.getMessage();
    }

    @PutMapping("/{db_name}/{collection_name}/updateDoc/{property_name}")
    public String updateDocument(@PathVariable("db_name") String dbName,
                                   @PathVariable("collection_name") String collectionName,
                                   @RequestParam("doc_id") String documentId,
                                   @PathVariable("property_name") String propertyName,
                                   @RequestHeader("newPropertyValue") Object newPropertyValue,
                                   @RequestHeader("username") String username,
                                   @RequestHeader("password") String password) {
        if(authenticationService.isAdmin(username, password)){
            return "User is not authorized";
        }
        FileService.setDatabaseDirectory(dbName);
        indexManager.loadAllIndexes();
        ApiResponse response = queryManager.updateProperty(dbName, collectionName, documentId, propertyName, newPropertyValue);
        return response.getMessage();
    }

    @GetMapping("/{db_name}/{collection_name}/readDocs")
    @SuppressWarnings("unchecked")
    public JSONArray readDocuments(@PathVariable("db_name") String dbName,
                                   @PathVariable("collection_name") String collectionName,
                                   @RequestHeader("username") String username,
                                   @RequestHeader("password") String password) {
        if (authenticationService.isAdmin(username, password)) {
            JSONObject errorMessage = new JSONObject();
            errorMessage.put("error", "User is not authorized");
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(errorMessage);
            return jsonArray;
        }
        FileService.setDatabaseDirectory(dbName);
        indexManager.loadAllIndexes();
        List<JSONObject> documents = queryManager.readDocuments(dbName, collectionName);
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(documents);
        return jsonArray;
    }
}
