package com.example.BankingSystem.controllers;

import com.example.BankingSystem.Model.Admin;
import com.example.BankingSystem.Model.BankAccount;
import com.example.BankingSystem.Model.User;
import com.example.BankingSystem.services.DocumentService;
import com.example.BankingSystem.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin-dashboard/banking-system")
public class DocumentController {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private UserService userService;

    @PostMapping("/createAccount")
    public ResponseEntity<?> createAccount(@RequestParam("db_name") String dbName,
                                         @RequestParam("collection_name") String collectionName,
                                         @ModelAttribute("bankAccount") BankAccount bankAccount,
                                         HttpSession session) {
        Admin login = (Admin) session.getAttribute("login");
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/login-page").body("login-page");
        }
        ResponseEntity<String> responseEntity = documentService.createAccount(dbName, collectionName, bankAccount, session);
        HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
        String message = responseEntity.getBody();
        if (status == HttpStatus.CREATED) {
            List<BankAccount> accounts = documentService.readAccounts(dbName, collectionName, session);
            userService.addUser(bankAccount.getAccountNumber(), bankAccount.getPassword());
            return new ResponseEntity<>(accounts, status);
        }else if (status == HttpStatus.CONFLICT) {
            return ResponseEntity.status(status).body(message);
        }else {
            return ResponseEntity.status(status).body(message);
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
        List<BankAccount> accounts = documentService.readAccounts(dbName, collectionName, session);
        if (!accounts.isEmpty()) {
            return ResponseEntity.ok(accounts);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No accounts found.");
        }
    }

    @PutMapping("/updateDocument")
    public ResponseEntity<?> updateDocumentProperty(
            @RequestParam("db_name") String dbName,
            @RequestParam("collection_name") String collectionName,
            @RequestParam("property_name") String propertyName,
            @RequestParam("doc_id") String documentId,
            @RequestHeader("newPropertyValue") Object newPropertyValue,
            HttpSession session) {
        Admin login = (Admin) session.getAttribute("login");
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Location", "/login-page").body("login-page");
        }
        ResponseEntity<String> responseEntity = documentService.
                updateDocumentProperty(dbName, collectionName, documentId, propertyName, newPropertyValue, session);
        HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
        String message = responseEntity.getBody();
        if (status == HttpStatus.ACCEPTED){
            List<BankAccount> accounts = documentService.readAccounts(dbName, collectionName, session);
            return new ResponseEntity<>(accounts, status);
        }else if(status == HttpStatus.NOT_FOUND) {
            return ResponseEntity.status(status).body(message);
        }else {
            return ResponseEntity.status(status).body(message);
        }
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
        ResponseEntity<String> responseEntity = documentService.deleteAccount(dbName, collectionName, documentId, session);
        HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
        String message = responseEntity.getBody();
        if (status == HttpStatus.ACCEPTED){
            List<BankAccount> accounts = documentService.readAccounts(dbName, collectionName, session);
            return new ResponseEntity<>(accounts, status);
        }else if(status == HttpStatus.NOT_FOUND) {
            return ResponseEntity.status(status).body(message);
        }else {
            return ResponseEntity.status(status).body(message);
        }
    }
}