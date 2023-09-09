package com.example.BankingSystem.controllers;

import com.example.web.model.User;
import com.example.BankingSystem.services.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private AuthenticationService authenticateService;

    @GetMapping("/login")
    public String loginPage() {
        return "login-page";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("token") String token,
                        Model model,
                        HttpSession httpSession) {
        String response = authenticateService.checkAdmin(username, token);
        System.out.println(response);
        if ("Authenticated".equals(response)) {
            User login = new User(username, token);
            httpSession.setAttribute("login", login);
            return "admin-dashboard";
        } else {
            model.addAttribute("result", response);
            return "login-page";
        }
    }
}