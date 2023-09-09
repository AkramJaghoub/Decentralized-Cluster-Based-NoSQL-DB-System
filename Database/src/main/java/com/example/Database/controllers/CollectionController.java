package com.example.Database.controllers;

import com.example.Database.file.FileService;
import com.example.Database.model.ApiResponse;
import com.example.Database.schema.SchemaBuilder;
import com.example.Database.query.QueryManager;
import com.example.Database.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CollectionController {
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    QueryManager queryManager;

    @PostMapping("/{db_name}/createCol/{collection_name}")
    public String createCollection(@PathVariable("db_name") String dbName,
                                   @PathVariable("collection_name") String collectionName,
                                   @RequestHeader("username") String username,
                                   @RequestHeader("password") String token) {
        if(authenticationService.isAdmin(username, token)){
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
                                   @RequestHeader("password") String token) {
        if(authenticationService.isAdmin(username, token)){
            return "User is not authorized";
        }
        FileService.setDatabaseDirectory(dbName);
        ApiResponse response = queryManager.deleteCollection(dbName, collectionName);
        return response.getMessage();
    }

//    @GetMapping("/{db_name}/collectionsList")
//    public String databaseList(@PathVariable("db_name") String dbName,
//                               @RequestHeader("username") String username,
//                               @RequestHeader("password") String token) {
//        if (!authenticationService.isAdmin(username, token)) {
//            return "User is not authorized";
//        }
//        List<String> collections = collectionService.listCollections(dbName);
//        if (collections.isEmpty()) {
//            return "No collections found";
//        }
//        return collections.toString();
//    }
}
