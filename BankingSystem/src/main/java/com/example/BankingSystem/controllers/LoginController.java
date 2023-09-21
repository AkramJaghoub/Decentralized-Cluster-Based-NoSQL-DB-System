package com.example.BankingSystem.controllers;

import com.example.BankingSystem.Model.Admin;
import com.example.BankingSystem.Model.Customer;
import com.example.BankingSystem.enums.Role;
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
    public String login(@RequestParam(value = "username", required = false) String username,
                        @RequestParam(value = "accountNumber", required = false) String accountNumber,
                        @RequestParam("password") String password,
                        @RequestParam("role") String roleString,
                        Model model,
                        HttpSession httpSession) {

        Role role = Role.fromString(roleString);
        ResponseEntity<String> responseEntity;
        if (role == Role.ADMIN) {
            responseEntity = authenticateService.checkAdmin(username, password);
        } else if (role == Role.CUSTOMER) {
            responseEntity = authenticateService.checkCustomer(accountNumber, password);
        } else {
            model.addAttribute("result", "Invalid role selected");
            return "login-page";
        }
        HttpStatus responseStatus = (HttpStatus) responseEntity.getStatusCode();
        String responseBody = responseEntity.getBody();
        System.out.println(responseStatus);
        System.out.println(responseBody);
        if (responseStatus == HttpStatus.OK) {
            if (role == Role.ADMIN) {
                Admin admin = new Admin(username, password);
                httpSession.setAttribute("login", admin);
                return "admin-dashboard";
            }else {
                Customer customer = new Customer(Long.parseLong(accountNumber), password);
                httpSession.setAttribute("login", customer);
                return "redirect:/customer-dashboard/banking-system/";
            }
        }
        model.addAttribute("result", responseBody);
        return "login-page";
    }
}