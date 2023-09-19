package com.example.Database.controllers;

import com.example.Database.file.FileService;
import com.example.Database.model.ApiResponse;
import com.example.Database.query.QueryManager;
import com.example.Database.services.AuthenticationService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DocumentController {

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private QueryManager queryManager;

    @PostMapping("/{db_name}/{collection_name}/createDoc")
    public ResponseEntity<String> createDocument(@PathVariable("db_name") String dbName,
                                                 @PathVariable("collection_name") String collectionName,
                                                 @RequestBody JSONObject document,
                                                 @RequestHeader("username") String username,
                                                 @RequestHeader("password") String password,
                                                 @RequestHeader("X-Broadcast") String isBroadcasted){
        if(authenticationService.isAdmin(username, password)){
            return new ResponseEntity<>("User is not authorized", HttpStatus.UNAUTHORIZED);
        }
        FileService.setDatabaseDirectory(dbName);
        document.put("X-Broadcast", isBroadcasted);
        System.out.println(isBroadcasted + " is broadcasted in controller");
        ApiResponse response = queryManager.createDocument(dbName, collectionName, document, isBroadcasted);
        return new ResponseEntity<>(response.getMessage(), response.getStatus());
    }

    @DeleteMapping("/{db_name}/{collection_name}/deleteDoc")
    public ResponseEntity<String> deleteDocument(@PathVariable("db_name") String dbName,
                                 @PathVariable("collection_name") String collectionName,
                                 @RequestParam("doc_id") String documentId,
                                 @RequestHeader("X-Broadcast") String isBroadcasted,
                                 @RequestHeader("username") String username,
                                 @RequestHeader("password") String password) {
        if(authenticationService.isAdmin(username, password)){
            return new ResponseEntity<>("User is not authorized", HttpStatus.UNAUTHORIZED);
        }
        FileService.setDatabaseDirectory(dbName);
        ApiResponse response = queryManager.deleteDocument(dbName, collectionName, documentId, isBroadcasted);
        return new ResponseEntity<>(response.getMessage(), response.getStatus());
    }

    @PutMapping("/{db_name}/{collection_name}/updateDoc/{property_name}")
    public ResponseEntity<String> updateDocument(@PathVariable("db_name") String dbName,
                                   @PathVariable("collection_name") String collectionName,
                                   @RequestParam("doc_id") String documentId,
                                   @PathVariable("property_name") String propertyName,
                                   @RequestHeader("newPropertyValue") Object newPropertyValue,
                                   @RequestHeader("username") String username,
                                   @RequestHeader("password") String password) {
        if(authenticationService.isAdmin(username, password)){
            return new ResponseEntity<>("User is not authorized", HttpStatus.UNAUTHORIZED);
        }
        FileService.setDatabaseDirectory(dbName);
        ApiResponse response = queryManager.updateProperty(dbName, collectionName, documentId, propertyName, newPropertyValue);
        return new ResponseEntity<>(response.getMessage(), response.getStatus());
    }

    @GetMapping("/{db_name}/{collection_name}/readDocs")
    public ResponseEntity<String> readDocuments(@PathVariable("db_name") String dbName,
                                                @PathVariable("collection_name") String collectionName,
                                                @RequestHeader("username") String username,
                                                @RequestHeader("password") String password) {
        if (authenticationService.isAdmin(username, password)) {
            return new ResponseEntity<>("User is not authorized", HttpStatus.UNAUTHORIZED);
        }
        FileService.setDatabaseDirectory(dbName);
        List<JSONObject> documents = queryManager.readDocuments(dbName, collectionName);
        if(documents.isEmpty()) {
            return new ResponseEntity<>("No documents found.", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(documents.toString(), HttpStatus.OK);
    }
}