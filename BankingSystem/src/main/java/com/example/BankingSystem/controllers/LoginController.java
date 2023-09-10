package com.example.BankingSystem.controllers;

import com.example.web.model.Admin;
import com.example.BankingSystem.services.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
                        @RequestParam("password") String password,
                        Model model,
                        HttpSession httpSession) {
        String response = authenticateService.checkAdmin(username, password);
        System.out.println(response);
        if ("Authenticated".equals(response)) {
            Admin login = new Admin(username, password);
            httpSession.setAttribute("login", login);
            return "admin-dashboard";
        } else {
            model.addAttribute("result", response);
            return "login-page";
        }
    }
}