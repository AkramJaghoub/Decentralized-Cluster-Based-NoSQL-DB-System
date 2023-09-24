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


@RestController
@RequestMapping("/api")
public class CustomerController {

    private final AccountDirectoryService accountDirectoryService;
    private final QueryManager queryManager;

    @Autowired
    public CustomerController(AccountDirectoryService accountDirectoryService, QueryManager queryManager){
        this.accountDirectoryService = accountDirectoryService;
        this.queryManager = queryManager;
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestHeader("accountNumber") String accountNumber,
                                          @RequestBody JSONObject request) {
        double amountToDeposit = Double.parseDouble(request.get("amount").toString());
        System.out.println(amountToDeposit + " amount being deposited");
        AccountReference accountReference = accountDirectoryService.getAccountLocation(accountNumber);
        String dbName = accountReference.getDatabaseName();
        String collectionName = accountReference.getCollectionName();
        String documentId = accountReference.getDocumentId();
        String propertyName = "balance";

        ApiResponse response = queryManager.searchForProperty( //first search for the existing balance
                dbName,
                collectionName,
                documentId,
                propertyName
        );

        //response.getMessage has the original balance value
        double newBalance = Double.parseDouble(response.getMessage()) + amountToDeposit; //update the balance after withdrawing

        response = queryManager.updateDocumentProperty(
                dbName,
                collectionName,
                documentId,
                propertyName,
                newBalance,
                "false"
        );

        response.setMessage(String.valueOf(newBalance));
        if (response.getStatus() == HttpStatus.ACCEPTED) {
            return ResponseEntity.status(response.getStatus()).body(response.getMessage()); // Return the new balance
        } else {
            return ResponseEntity.status(response.getStatus()).body("Deposit failed");
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestHeader("accountNumber") String accountNumber,
                                           @RequestBody JSONObject request) {
        double amountToWithdraw = Double.parseDouble(request.get("amount").toString());
        AccountReference accountReference = accountDirectoryService.getAccountLocation(accountNumber);
        String dbName = accountReference.getDatabaseName();
        String collectionName = accountReference.getCollectionName();
        String documentId = accountReference.getDocumentId();
        String propertyName = "balance";

        ApiResponse response = queryManager.searchForProperty( //first search for the existing balance
                dbName,
                collectionName,
                documentId,
                propertyName
        );

        //response.getMessage has the original balance value
        double newBalance = Double.parseDouble(response.getMessage()) - amountToWithdraw;  //update the balance after withdrawing

        response = queryManager.updateDocumentProperty(
                dbName,
                collectionName,
                documentId,
                propertyName,
                newBalance,
                "false"
        );

        response.setMessage(String.valueOf(newBalance));
        if (response.getStatus() == HttpStatus.ACCEPTED) {
            return ResponseEntity.status(response.getStatus()).body(response.getMessage()); // Return the new balance
        } else {
            return ResponseEntity.status(response.getStatus()).body("Withdrawal failed");
        }
    }
}