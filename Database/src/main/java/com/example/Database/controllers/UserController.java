package com.example.Database.controllers;

import com.example.Database.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    AuthenticationService authenticationService;

    @GetMapping("/check/admin")
    public String checkAdminCredentials(@RequestHeader("username") String username,
                                        @RequestHeader("password") String password) {
        return authenticationService.verifyCredentials(username, password);
    }
}
