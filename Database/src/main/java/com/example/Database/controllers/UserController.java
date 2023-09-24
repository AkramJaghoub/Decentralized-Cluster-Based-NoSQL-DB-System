package com.example.Database.controllers;

import com.example.Database.services.AuthenticationService;
import com.example.Database.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Autowired
    public UserController(AuthenticationService authenticationService, UserService userService){
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

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

    @DeleteMapping ("/delete/customer")
    public String deleteCustomer(@RequestHeader("accountNumber") String accountNumber,
                                 @RequestHeader("adminUsername") String adminUsername,
                                 @RequestHeader("adminPassword") String adminPassword){
        if(!authenticationService.isAdmin(adminUsername, adminPassword)){
            return "User is not authorized";
        }
        return userService.deleteCustomer(accountNumber);
    }

    @PostMapping("/add/admin")
    public String addAdmin(@RequestHeader("username") String username,
                           @RequestHeader("password") String password) {
        return userService.addAdmin(username, password);
    }
}