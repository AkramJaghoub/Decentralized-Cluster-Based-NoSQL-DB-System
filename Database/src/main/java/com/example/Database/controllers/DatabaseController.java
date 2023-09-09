package com.example.Database.controllers;

import com.example.Database.index.IndexManager;
import com.example.Database.query.QueryManager;
import com.example.Database.services.AuthenticationService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DatabaseController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private QueryManager queryManager;
    @Autowired
    private  IndexManager indexManager;

    @PostMapping("/createDB/{db_name}")
    public String createDatabase(@PathVariable("db_name") String dbName,
                                 @RequestHeader("username") String username,
                                 @RequestHeader("password") String token) {

        if(!authenticationService.isAdmin(username, token)){
            return "User is not authorized";
        }
        JSONObject response = queryManager.createDatabase(dbName);
        return (String) response.get("status");
    }

    @DeleteMapping("/deleteDB/{db_name}")
    public String deleteDatabase(@PathVariable("db_name") String dbName,
                                 @RequestHeader("username") String username,
                                 @RequestHeader("password") String token) {
        if (!authenticationService.isAdmin(username, token)) {
            return "User is not authorized";
        }
        JSONObject response = queryManager.deleteDatabase(dbName);
        return (String) response.get("status");
    }

//    @GetMapping("/databasesList")
//    public String databaseList(@RequestHeader("username") String username,
//                                     @RequestHeader("password") String token) {
//        if (!authenticationService.isAdmin(username, token)) {
//            return "User is not authorized";
//        }
//        List<String> databases = databaseService.listDatabases();
//        if (databases.isEmpty()) {
//            return "No collections found";
//        }
//        return databases.toString();
//    }
}