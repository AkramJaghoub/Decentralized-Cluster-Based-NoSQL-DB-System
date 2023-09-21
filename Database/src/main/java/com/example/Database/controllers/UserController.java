package com.example.Database.controllers;

import com.example.Database.model.ApiResponse;
import com.example.Database.services.AuthenticationService;
import com.example.Database.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    UserService userService;

    @PostMapping("/add/customer")
    public String addCustomer(@RequestHeader("adminUsername") String adminUsername,
                              @RequestHeader("adminPassword") String adminPassword,
                              @RequestHeader("accountNumber") String accountNumber,
                              @RequestHeader("password") String password) {
        if(!authenticationService.isAdmin(adminUsername, adminPassword)){
            return "User is not authorized";
        }
        return userService.addCustomer(accountNumber, password);
    }

    @PostMapping("/add/admin")
    public String addAdmin(@RequestHeader("username") String username,
                           @RequestHeader("password") String password) {
        return userService.addAdmin(username, password);
    }

    @GetMapping("/check/admin")
    public ResponseEntity<String> checkAdminCredentials(@RequestHeader("username") String username,
                                                             @RequestHeader("password") String password) {
        ApiResponse response = authenticationService.verifyAdminCredentials(username, password);
        return new ResponseEntity<>(response.getMessage(), response.getStatus());
    }

    @GetMapping("/check/customer")
    public ResponseEntity<String> checkCustomerCredentials(@RequestHeader("accountNumber") String accountNumber,
                                                                @RequestHeader("password") String password) {
        ApiResponse response = authenticationService.verifyCustomerCredentials(accountNumber, password);
        return new ResponseEntity<>(response.getMessage(), response.getStatus());
    }
}
