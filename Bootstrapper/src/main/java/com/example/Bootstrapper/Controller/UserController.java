package com.example.Bootstrapper.Controller;

import com.example.Bootstrapper.model.Admin;
import com.example.Bootstrapper.model.Customer;
import com.example.Bootstrapper.services.AuthenticationService;
import com.example.Bootstrapper.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bootstrapper")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/add/customer")
    public String addUser(@RequestHeader("accountNumber") String accountNumber,
                          @RequestHeader("password") String password,
                          @RequestHeader("adminUsername") String adminUsername,
                          @RequestHeader("adminPassword") String adminPassword) {
        if(!authenticationService.isAdmin(adminUsername, adminPassword)){
            return "User is not authorized";
        }
        System.out.println("Received request to register a new customer with account number: " + accountNumber);
        Customer customer = new Customer(accountNumber, password);
        if (authenticationService.isUserExists(customer)) {
            return "Customer already exists";
        }
        userService.addCustomer(customer);
        return "Customer added successfully";
    }


    @PostMapping("/add/admin")
    public String addAdmin(@RequestHeader("username") String username,
                           @RequestHeader("password") String password) {
        System.out.println("Received request to register the admin with username: " + username);
        if(authenticationService.adminExists()){
            return "Admin already exists";
        }
        Admin admin = new Admin(username, password);
        userService.addAdmin(admin);
        return "admin added successfully";
    }
}
