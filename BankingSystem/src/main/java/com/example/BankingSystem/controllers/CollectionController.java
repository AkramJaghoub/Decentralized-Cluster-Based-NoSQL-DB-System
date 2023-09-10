package com.example.BankingSystem.controllers;

import com.example.BankingSystem.services.CollectionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.web.model.Admin;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/admin-dashboard/banking-system")
public class CollectionController {

    @Autowired
    CollectionService collectionService;

    @PostMapping("/createCol")
    public ResponseEntity<?> createCollection(@RequestParam("db_name") String dbName,
                                              @RequestParam("collection_name") String collectionName,
                                              HttpSession session) {
        Admin login = (Admin) session.getAttribute("login");
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/login-page").body("login-page");
        }
        String response = collectionService.createCollection(dbName, collectionName, session);
        if (response.contains("successful")) {
            List<String> allCollections = collectionService.getAllCollections(dbName, session);
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", String.format("Collection %s has been successfully created!", collectionName));
            responseBody.put("collections", allCollections);
            return ResponseEntity.ok(responseBody);
        }
        else if (response.contains("Collection already exists.")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/deleteCol")
    public ResponseEntity<?> deleteCollection(@RequestParam("db_name") String dbName,
                                              @RequestParam("collection_name") String collectionName,
                                              HttpSession session) {
        Admin login = (Admin) session.getAttribute("login");
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/login-page").body("login-page");
        }
        String response = collectionService.deleteCollection(dbName, collectionName, session);
        if (response.contains("successful")) {
            List<String> allCollections = collectionService.getAllCollections(dbName, session);
            return ResponseEntity.ok(allCollections);
        } else if (response.contains("Collection does not exist.")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/fetchExistingCollections")
    @ResponseBody
    public ResponseEntity<List<String>> fetchExistingCollections(@RequestParam("db_name") String dbName,
                                                                 HttpSession session) {
        Admin login = (Admin) session.getAttribute("login");
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/login-page").body(Collections.emptyList());
        }
        List<String> collectionNames = collectionService.getAllCollections(dbName, session);
        return ResponseEntity.ok(collectionNames);
    }
}
