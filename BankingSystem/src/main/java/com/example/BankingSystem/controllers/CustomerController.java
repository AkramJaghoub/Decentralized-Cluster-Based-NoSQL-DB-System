package com.example.BankingSystem.controllers;

import com.example.BankingSystem.Model.Customer;
import com.example.BankingSystem.services.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/customer-dashboard/banking-system")
public class CustomerController {
    @Autowired
    private HttpSession session;
    @Autowired
    CustomerService customerService;

    @GetMapping("/")
    public String getCustomerDashboard(Model model) {
        Customer customer = (Customer) session.getAttribute("login");
        if (customer == null) {
            return "login-page";
        }
        Double balance = customerService.getAccountBalance(session);
        String customerName = customerService.getClientName(session);
        model.addAttribute("balance", balance);
        model.addAttribute("clientName", customerName);
        return "customer-dashboard";
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestParam Double amount) {
        Customer customer = (Customer) session.getAttribute("login");
        if (customer == null) {
            return ResponseEntity.badRequest().body("User not logged in.");
        }
        String response = customerService.depositAmount(amount, session);
        if (response.startsWith("New balance: ")) {
            return ResponseEntity.accepted().body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestParam Double amount) {
        Customer customer = (Customer) session.getAttribute("login");
        if (customer == null) {
            return ResponseEntity.badRequest().body("User not logged in.");
        }
        Double currentBalance = customerService.getAccountBalance(session);
        if (amount > currentBalance) {
            return ResponseEntity.badRequest().body("Please withdraw an amount less than your balance.");
        }
        String response = customerService.withdrawAmount(amount, session);
        if (response.startsWith("New balance: ")) {
            return ResponseEntity.accepted().body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}