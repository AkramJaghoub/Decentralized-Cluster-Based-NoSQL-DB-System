package com.example.BankingSystem.controllers;

import com.example.BankingSystem.Model.BankAccount;
import com.example.BankingSystem.services.DocumentService;
import jakarta.servlet.http.HttpSession;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.web.model.Admin;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin-dashboard/banking-system")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/openAccount")
    public ResponseEntity<?> openAccount(@RequestParam("db_name") String dbName,
                                         @RequestParam("collection_name") String collectionName,
                                         @ModelAttribute("bankAccount") BankAccount bankAccount,
                                         HttpSession session) {
        System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
        Admin login = (Admin) session.getAttribute("login");
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/login-page").body("login-page");
        }
        String response = documentService.openAccount(dbName, collectionName, bankAccount, session);
        if (response.contains("successful")) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Account opened successfully.");
            List<BankAccount> accounts = documentService.readAccounts(dbName, collectionName, session);
            responseBody.put("accounts", accounts);
            return ResponseEntity.ok(responseBody);
        }else if (response.contains("already exists.")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/readAccounts")
    public ResponseEntity<?> readAccounts(@RequestParam("db_name") String dbName,
                                          @RequestParam("collection_name") String collectionName,
                                          HttpSession session) {
        Admin login = (Admin) session.getAttribute("login");
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/login-page").body("login-page");
        }
        System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
        List<BankAccount> accounts = documentService.readAccounts(dbName, collectionName, session);
        if (!accounts.isEmpty()) {
            return ResponseEntity.ok(accounts);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No accounts found.");
        }
    }

    @PutMapping("/updateDocument")
    public ResponseEntity<String> updateDocumentProperty(
            @RequestParam("db_name") String dbName,
            @RequestParam("collection_name") String collectionName,
            @RequestParam("property_name") String propertyName,
            @RequestParam("doc_id") String documentId,
            @RequestHeader("newPropertyValue") Object newPropertyValue,
            @RequestHeader("username") String username,
            @RequestHeader("password") String password,
            HttpSession session) {
        System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
        Admin login = (Admin) session.getAttribute("login");
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/login-page").body("login-page");
        }
        String response = documentService.updateDocumentProperty(dbName, collectionName, documentId, propertyName, newPropertyValue, session);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteAccount")
    public ResponseEntity<?> deleteAccount(@RequestParam("db_name") String dbName,
                                           @RequestParam("collection_name") String collectionName,
                                           @RequestParam("doc_id") String documentId,
                                           HttpSession session) {
        Admin login = (Admin) session.getAttribute("login");
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/login-page").body("login-page");
        }
        System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
        String response = documentService.deleteAccount(dbName, collectionName, documentId, session);
        Map<String, Object> responseBody = new HashMap<>();
        if (response.contains("successful")) {
            responseBody.put("message", "Account deleted successfully.");
            List<BankAccount> accounts = documentService.readAccounts(dbName, collectionName, session);
            responseBody.put("accounts", accounts);
            return ResponseEntity.ok(responseBody);
        } else if (response.contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}