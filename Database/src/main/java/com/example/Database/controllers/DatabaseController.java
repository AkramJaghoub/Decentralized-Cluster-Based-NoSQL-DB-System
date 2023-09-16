package com.example.Database.controllers;

import com.example.Database.file.FileService;
import com.example.Database.model.ApiResponse;
import com.example.Database.query.QueryManager;
import com.example.Database.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DatabaseController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private QueryManager queryManager;

    @PostMapping("/createDB/{db_name}")
    public ResponseEntity<String> createDatabase(@PathVariable("db_name") String dbName,
                                                 @RequestHeader("username") String username,
                                                 @RequestHeader("password") String password) {

        if(authenticationService.isAdmin(username, password)){
            return new ResponseEntity<>("User is not authorized", HttpStatus.UNAUTHORIZED);
        }
        FileService.setDatabaseDirectory(dbName);
        ApiResponse response = queryManager.createDatabase(dbName);
        return new ResponseEntity<>(response.getMessage(), response.getHttpStatus());
    }

    @DeleteMapping("/deleteDB/{db_name}")
    public ResponseEntity<String> deleteDatabase(@PathVariable("db_name") String dbName,
                                                 @RequestHeader("username") String username,
                                                 @RequestHeader("password") String password) {

        if(authenticationService.isAdmin(username, password)){
            return new ResponseEntity<>("User is not authorized", HttpStatus.UNAUTHORIZED);
        }
        FileService.setDatabaseDirectory(dbName);
        ApiResponse response = queryManager.deleteDatabase(dbName);
        return new ResponseEntity<>(response.getMessage(), response.getHttpStatus());
    }

    @GetMapping("/fetchExistingDatabases")
    public ResponseEntity<List<String>> fetchExistingDatabases(
            @RequestHeader("username") String username,
            @RequestHeader("password") String password) {
        if (authenticationService.isAdmin(username, password)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<String> databases = queryManager.readDatabases();
        return new ResponseEntity<>(databases, HttpStatus.OK);
    }
}