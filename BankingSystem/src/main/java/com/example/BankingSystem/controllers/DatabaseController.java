package com.example.BankingSystem.controllers;

import com.example.BankingSystem.services.DatabaseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.web.model.Admin;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/admin-dashboard/banking-system")
public class DatabaseController {

    @Autowired
    DatabaseService databaseService;

    @PostMapping("/createDB")
    public ResponseEntity<?> createDb(@RequestParam("db_name") String dbName,
                                      HttpSession session) {
        Admin login = (Admin) session.getAttribute("login");
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/login-page").body("login-page");
        }
        String response = databaseService.createDatabase(dbName, session);
        if (response.contains("successful")) {
            List<String> allDatabases = databaseService.getAllDatabases(session);
            return ResponseEntity.ok(allDatabases);
        }
        else if (response.contains("Database already exists.")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/deleteDB")
    public ResponseEntity<?> deleteDatabase(@RequestParam("db_name") String dbName,
                                            HttpSession session) {
        Admin login = (Admin) session.getAttribute("login");
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/login-page").body("login-page");
        }
        String response = databaseService.deleteDatabase(dbName, session);
        if (response.contains("successful")) {
            List<String> allDatabases = databaseService.getAllDatabases(session);
            return ResponseEntity.ok(allDatabases);
        }
        else if (response.contains("Database does not exist.")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/fetchExistingDatabases")
    @ResponseBody
    public ResponseEntity<List<String>> fetchExistingDatabases(HttpSession session) {
        Admin login = (Admin) session.getAttribute("login");
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/login-page").body(Collections.emptyList());
        }
        List<String> databaseNames = databaseService.getAllDatabases(session);
        return ResponseEntity.ok(databaseNames);
    }
}