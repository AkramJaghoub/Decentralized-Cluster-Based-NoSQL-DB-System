package com.example.BankingSystem.services;

import com.example.BankingSystem.Model.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class CustomerService {

    AuthenticationService authenticationService;
    ObjectMapper objectMapper;

    @Autowired
    public CustomerService(AuthenticationService authenticationService, ObjectMapper objectMapper){
        this.authenticationService = authenticationService;
        this.objectMapper = objectMapper;
    }

    public Double getAccountBalance(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("accountNumber", String.valueOf(customer.getAccountNumber()));
        String workerPort = authenticationService.getWorker(String.valueOf(customer.getAccountNumber()));
        String url = "http://worker" + workerPort + ":9000/api/search/balance";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                return Double.parseDouble(Objects.requireNonNull(response.getBody()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public String getClientName(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("accountNumber", String.valueOf(customer.getAccountNumber()));
        String workerPort = authenticationService.getWorker(String.valueOf(customer.getAccountNumber()));
        String url = "http://worker" + workerPort + ":9000/api/search/clientName";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return "couldn't get client's name";
    }

    public ResponseEntity<String> depositAmount(Double amount, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("accountNumber", String.valueOf(customer.getAccountNumber()));
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("amount", amount);
        String workerPort = authenticationService.getWorker(String.valueOf(customer.getAccountNumber()));
        String url = "http://worker" + workerPort + ":9000/api/deposit";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(rootNode.toString(), headers), String.class);
        if (response.getStatusCode() == HttpStatus.ACCEPTED) {
            return ResponseEntity.accepted().body(response.getBody());
        }
        return ResponseEntity.badRequest().body("Operation failed");
    }

    public ResponseEntity<String> withdrawAmount(Double amount, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("accountNumber", String.valueOf(customer.getAccountNumber()));
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("amount", amount);
        String workerPort = authenticationService.getWorker(String.valueOf(customer.getAccountNumber()));
        String url = "http://worker" + workerPort + ":9000/api/withdraw";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(rootNode.toString(), headers), String.class);
        if (response.getStatusCode() == HttpStatus.ACCEPTED) {
            return ResponseEntity.accepted().body(response.getBody());
        }
        return ResponseEntity.badRequest().body("Operation failed");
    }
}
