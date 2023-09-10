package com.example.BankingSystem.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import com.example.web.model.Admin;

@Controller
@RequestMapping("/admin-dashboard")
public class AdminController {

    @Autowired
    private HttpSession session;

    @GetMapping("/")
    public String getAdminDashboard() {
        Admin login = (Admin) session.getAttribute("login");
        if (login == null) {
            return "login-page";
        }
        return "admin-dashboard";
    }
}