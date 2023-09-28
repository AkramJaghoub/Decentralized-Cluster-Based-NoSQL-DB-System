package com.example.Database.controllers;

import com.example.Database.file.FileService;
import com.example.Database.model.AccountReference;
import com.example.Database.model.ApiResponse;
import com.example.Database.query.QueryManager;
import com.example.Database.services.AccountDirectoryService;
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

    private final AuthenticationService authenticationService;
    private final AccountDirectoryService accountDirectoryService;
    private final QueryManager queryManager;

    @Autowired
    public DocumentController(AuthenticationService authenticationService, AccountDirectoryService accountDirectoryService,
                              QueryManager queryManager){
        this.authenticationService = authenticationService;
        this.accountDirectoryService = accountDirectoryService;
        this.queryManager = queryManager;
    }

    @PostMapping("/{db_name}/{collection_name}/createDoc")
    public ResponseEntity<String> createDocument(@PathVariable("db_name") String dbName,
                                                 @PathVariable("collection_name") String collectionName,
                                                 @RequestBody JSONObject document,
                                                 @RequestHeader("username") String username,
                                                 @RequestHeader("password") String password,
                                                 @RequestHeader(value = "X-Broadcast", required = false, defaultValue = "false") String isBroadcasted){
        if(!authenticationService.isAdmin(username, password)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authorized");
        }
        FileService.setDatabaseDirectory(dbName);
        ApiResponse response = queryManager.createDocument(dbName, collectionName, document, isBroadcasted);
        return ResponseEntity.status(response.getStatus()).body(response.getMessage());
    }

    @GetMapping("/{db_name}/{collection_name}/readDocs")
    public ResponseEntity<String> readDocuments(@PathVariable("db_name") String dbName,
                                                @PathVariable("collection_name") String collectionName,
                                                @RequestHeader("username") String username,
                                                @RequestHeader("password") String password) {
        if (!authenticationService.isAdmin(username, password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authorized");
        }
        FileService.setDatabaseDirectory(dbName);
        List<JSONObject> documents = queryManager.readDocuments(dbName, collectionName);
        if(documents.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No documents found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(documents.toString());
    }

    @PutMapping("/{db_name}/{collection_name}/updateDoc/{property_name}")
    public ResponseEntity<String> updateDocument(@PathVariable("db_name") String dbName,
                                                 @PathVariable("collection_name") String collectionName,
                                                 @RequestParam("doc_id") String documentId,
                                                 @PathVariable("property_name") String propertyName,
                                                 @RequestHeader("newPropertyValue") Object newPropertyValue,
                                                 @RequestHeader(value = "X-Broadcast", required = false, defaultValue = "false") String isBroadcasted,
                                                 @RequestHeader("username") String username,
                                                 @RequestHeader("password") String password) {
        if(!authenticationService.isAdmin(username, password)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authorized");
        }
        FileService.setDatabaseDirectory(dbName);
        ApiResponse response = queryManager.updateDocumentProperty(dbName, collectionName, documentId, propertyName, newPropertyValue, isBroadcasted);
        return ResponseEntity.status(response.getStatus()).body(response.getMessage());
    }

    @DeleteMapping("/{db_name}/{collection_name}/deleteDoc")
    public ResponseEntity<String> deleteDocument(@PathVariable("db_name") String dbName,
                                 @PathVariable("collection_name") String collectionName,
                                 @RequestParam("doc_id") String documentId,
                                 @RequestHeader(value = "X-Broadcast", required = false, defaultValue = "false") String isBroadcasted,
                                 @RequestHeader("username") String username,
                                 @RequestHeader("password") String password) {
        if(!authenticationService.isAdmin(username, password)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authorized");
        }
        FileService.setDatabaseDirectory(dbName);
        ApiResponse response = queryManager.deleteDocument(dbName, collectionName, documentId, isBroadcasted);
        return ResponseEntity.status(response.getStatus()).body(response.getMessage());
    }

    @GetMapping("/search/{propertyName}")
    public ResponseEntity<String> searchForProperty(@PathVariable String propertyName,
                                                    @RequestHeader("accountNumber") String accountNumber) {
        AccountReference accountReference = accountDirectoryService.getAccountLocation(accountNumber);
        System.out.println(accountReference + " sssssssssssssssssssssdqdwqdwqdqwdasdaxdavvvvvvvvvvxxxxxxxxxx");
        String dbName = accountReference.getDatabaseName();
        String collectionName = accountReference.getCollectionName();
        String documentId = accountReference.getDocumentId();
        FileService.setDatabaseDirectory(dbName);
        System.out.println("searching for " + propertyName);
        ApiResponse response = queryManager.searchForProperty(dbName, collectionName, documentId, propertyName);
        return ResponseEntity.status(response.getStatus()).body(response.getMessage());
    }
}