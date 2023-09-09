package com.example.Bootstrapper.Controller;

import com.example.Bootstrapper.model.User;
import com.example.Bootstrapper.services.AuthenticationService;
import com.example.Bootstrapper.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bootstrapper")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    AuthenticationService authenticationService;

    @GetMapping("/add/user")
    public String addUser(@RequestHeader("accountNumber") String accountNumber,
                          @RequestHeader("password") String password,
                          @RequestHeader("adminUsername") String adminUsername,
                          @RequestHeader("adminPassword") String adminPassword) {
        if(!authenticationService.isAdmin(adminUsername, adminPassword)){
            return "User is not authorized";
        }
        System.out.println("Received request to register a new user with account number: " + accountNumber);
        User user = new User();
        user.setAccountNumber(accountNumber);
        user.setPassword(password);
        if (authenticationService.isUserExists(user)) {
            return "User already exists.";
        }
        userService.addUser(user);
        return "User added successfully in.";
    }

    @GetMapping("/check/admin")
    public String checkAdminCredentials(@RequestHeader("username") String username,
                                        @RequestHeader("password") String password) {
        return authenticationService.verifyAdminCredentials(username, password);
    }
}
