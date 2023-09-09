package com.example.Database.controllers;

import com.example.Database.query.QueryManager;
import com.example.Database.services.AuthenticationService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DocumentController {

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private QueryManager queryManager;

    @PostMapping("/{db_name}/{collection_name}/createDoc")
    public String createDocument(@PathVariable("db_name") String dbName,
                                 @PathVariable("collection_name") String collectionName,
                                 @RequestBody JSONObject document,
                                 @RequestHeader("username") String username,
                                 @RequestHeader("password") String token) {
        if (!authenticationService.isAdmin(username, token)) {
            return "User is not authorized";
        }
        JSONObject response = queryManager.createDocument(dbName, collectionName, document);
        return (String) response.get("status");
    }

    @DeleteMapping("/{db_name}/{collection_name}/deleteDoc")
    public String deleteDocument(@PathVariable("db_name") String dbName,
                                 @PathVariable("collection_name") String collectionName,
                                 @RequestParam("doc_id") String documentId,
                                 @RequestHeader("username") String username,
                                 @RequestHeader("password") String token) {
        if (!authenticationService.isAdmin(username, token)) {
            return "User is not authorized";
        }
        JSONObject response = queryManager.deleteDocument(dbName, collectionName, documentId);
        return (String) response.get("status");
    }

    @PutMapping("/{db_name}/{collection_name}/updateDoc/{property_name}")
    public String updateDocument(@PathVariable("db_name") String dbName,
                                   @PathVariable("collection_name") String collectionName,
                                   @RequestParam("doc_id") String documentId,
                                   @PathVariable("property_name") String propertyName,
                                   @RequestHeader("newValue") String newValue,
                                   @RequestHeader("username") String username,
                                   @RequestHeader("password") String token) {
        if(!authenticationService.isAdmin(username, token)){
            return "User is not authorized";
        }
        JSONObject response = queryManager.updateProperty(dbName, collectionName, documentId, propertyName, newValue);
        return (String) response.get("status");
    }
}
