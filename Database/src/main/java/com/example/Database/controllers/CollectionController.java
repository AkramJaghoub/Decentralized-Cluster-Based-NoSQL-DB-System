package com.example.Database.controllers;

import com.example.Database.file.FileService;
import com.example.Database.model.ApiResponse;
import com.example.Database.schema.SchemaBuilder;
import com.example.Database.query.QueryManager;
import com.example.Database.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CollectionController {

    private final AuthenticationService authenticationService;

    private final QueryManager queryManager;

    @Autowired
    public CollectionController(QueryManager queryManager, AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
        this.queryManager = queryManager;
    }

    @PostMapping("/{db_name}/createCol/{collection_name}")
    public String createCollection(@PathVariable("db_name") String dbName,
                                   @PathVariable("collection_name") String collectionName,
                                   @RequestHeader("username") String username,
                                   @RequestHeader("password") String password) {
        if(authenticationService.isAdmin(username, password)){
            return "User is not authorized";
        }
        FileService.setDatabaseDirectory(dbName);
        ApiResponse response = queryManager.createCollection(dbName, collectionName, SchemaBuilder.buildSchema().getSchemaAsJSON());
        return response.getMessage();
    }

    @DeleteMapping("/{db_name}/deleteCol/{collection_name}")
    public String deleteCollection(@PathVariable("db_name") String dbName,
                                   @PathVariable("collection_name") String collectionName,
                                   @RequestHeader("username") String username,
                                   @RequestHeader("password") String password) {
        if(authenticationService.isAdmin(username, password)){
            return "User is not authorized";
        }
        FileService.setDatabaseDirectory(dbName);
        ApiResponse response = queryManager.deleteCollection(dbName, collectionName);
        return response.getMessage();
    }

    @GetMapping("/fetchExistingCollections/{db_name}")
    public ResponseEntity<List<String>> fetchExistingCollections(
            @PathVariable("db_name") String dbName,
            @RequestHeader("username") String username,
            @RequestHeader("password") String password) {
        if (authenticationService.isAdmin(username, password)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        FileService.setDatabaseDirectory(dbName);
        List<String> collections = queryManager.readCollections(dbName);
        if (collections.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(collections, HttpStatus.OK);
    }
}
