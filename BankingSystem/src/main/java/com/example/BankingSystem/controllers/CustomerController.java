package com.example.BankingSystem.controllers;

import com.example.BankingSystem.Model.Customer;
import com.example.BankingSystem.services.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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


    private final HttpSession session;
    private final CustomerService customerService;

    @Autowired
    public CustomerController (HttpSession session, CustomerService customerService){
        this.session = session;
        this.customerService = customerService;
    }

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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not logged in.");
        }
        return customerService.depositAmount(amount, session);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestParam Double amount) {
        Customer customer = (Customer) session.getAttribute("login");
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not logged in.");
        }
        Double currentBalance = customerService.getAccountBalance(session);
        if (amount > currentBalance) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please withdraw an amount less than your balance.");
        }
        return customerService.withdrawAmount(amount, session);
    }
}