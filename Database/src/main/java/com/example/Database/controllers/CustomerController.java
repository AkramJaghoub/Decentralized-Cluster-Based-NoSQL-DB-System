package com.example.Database.controllers;

import com.example.Database.model.AccountReference;
import com.example.Database.model.ApiResponse;
import com.example.Database.query.QueryManager;
import com.example.Database.services.AccountDirectoryService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CustomerController {
    @Autowired
    private AccountDirectoryService accountDirectoryService;
    @Autowired
    private QueryManager queryManager;

    @PostMapping("/deposit/{accountNumber}")
    public ResponseEntity<String> deposit(@PathVariable String accountNumber,
                                          @RequestBody JSONObject request) {
        double amountToDeposit = Double.parseDouble(request.get("amount").toString());
        System.out.println(amountToDeposit + " amount being deposited");
        AccountReference accountReference = accountDirectoryService.getAccountLocation(accountNumber);
        String dbName = accountReference.getDatabaseName();
        String collectionName = accountReference.getCollectionName();
        String documentId = accountReference.getDocumentId();
        String propertyName = "balance";

        ApiResponse response = queryManager.search( //first search for the existing balance
                dbName,
                collectionName,
                documentId,
                propertyName
        );

        double newBalance = Double.parseDouble(response.getMessage()) + amountToDeposit; //update the balance after withdrawing

        response = queryManager.updateProperty(
                dbName,
                collectionName,
                documentId,
                propertyName,
                newBalance,
                "false"
        );
        if (response.getStatus() == HttpStatus.ACCEPTED) {
            return new ResponseEntity<>(String.valueOf(newBalance), HttpStatus.ACCEPTED); // Return the new balance
        } else {
            return ResponseEntity.badRequest().body("Deposit failed");
        }
    }

    @PostMapping("/withdraw/{accountNumber}")
    public ResponseEntity<String> withdraw(@PathVariable String accountNumber,
                                           @RequestBody JSONObject request) {
        double amountToWithdraw = Double.parseDouble(request.get("amount").toString());
        AccountReference accountReference = accountDirectoryService.getAccountLocation(accountNumber);
        String dbName = accountReference.getDatabaseName();
        String collectionName = accountReference.getCollectionName();
        String documentId = accountReference.getDocumentId();
        String propertyName = "balance";

        ApiResponse response = queryManager.search( //first search for the existing balance
                dbName,
                collectionName,
                documentId,
                propertyName
        );

        double newBalance = Double.parseDouble(response.getMessage()) - amountToWithdraw;  //update the balance after withdrawing

        response = queryManager.updateProperty(
                dbName,
                collectionName,
                documentId,
                propertyName,
                newBalance,
                "false"
        );

        if (response.getStatus() == HttpStatus.ACCEPTED) {
            return new ResponseEntity<>(String.valueOf(newBalance), HttpStatus.ACCEPTED); // Return the new balance
        } else {
            return ResponseEntity.badRequest().body("Withdrawal failed");
        }
    }
}