package com.example.BankingSystem.controllers;

import com.example.BankingSystem.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import com.example.web.model.User;

@RestController
@RequestMapping("/admin-dashboard")
public class AdminController {

    @Autowired
    private HttpSession session;
    @Autowired
    private DatabaseService databaseService;

    @GetMapping("/")
    public String getAdminDashboard() {
       User login = (User) session.getAttribute("login");
        if (login == null)
            return "login-page";
        return "admin-dashboard";
    }

    @GetMapping("/createDB")
    public String createDbForm(){
        User login = (User) session.getAttribute("login");
        if (login == null)
            return "login-page";
        return "create-db";
    }

    @PostMapping("/createDB")
    public String createDb(@RequestParam("db_name") String dbName,
                           HttpSession session,
                           Model model){
        User login = (User) session.getAttribute("login");
        if (login == null)
            return "login-page";
        String response = databaseService.createDatabase(dbName,session);
        System.out.println(response);
        if(response.equalsIgnoreCase("Database " + dbName + " created."))
            model.addAttribute("result","add database done");
        else
            model.addAttribute("result",response);
        return "admin-dashboard";
    }


    @GetMapping("/open-account") //this is a new user (document)
    public String openAccountForm(){
        User login = (User) session.getAttribute("login");
        if (login == null)
            return "login-page";
        return "new-account-page";
    }

    @PostMapping("/open-account")
    public String openAccount(){
        User login = (User) session.getAttribute("login");
        if (login == null)
            return "login-page";
        return "new-account-page";
    }

}