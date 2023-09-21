package com.example.BankingSystem.services;

import com.example.BankingSystem.Model.Customer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpSession;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
public class CustomerService {

    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    ObjectMapper objectMapper;


    public Double getAccountBalance(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("accountNumber", String.valueOf(customer.getAccountNumber()));
        headers.set("propertyName", "balance");
        String workerPort = authenticationService.getWorker(String.valueOf(customer.getAccountNumber()));
        String url = "http://worker" + workerPort + ":9000/api/search";
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
        headers.set("propertyName", "clientName");
        String workerPort = authenticationService.getWorker(String.valueOf(customer.getAccountNumber()));
        String url = "http://worker" + workerPort + ":9000/api/search";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return "couldn't get client's name";
    }

    public String depositAmount(Double amount, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("accountNumber", String.valueOf(customer.getAccountNumber()));
        headers.set("password", customer.getPassword());
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("amount", amount);
        String workerPort = authenticationService.getWorker(String.valueOf(customer.getAccountNumber()));
        String url = "http://worker" + workerPort + ":9000/api/deposit/" + customer.getAccountNumber();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(rootNode.toString(), headers), String.class);
        if (response.getStatusCode() == HttpStatus.ACCEPTED) {
            return response.getBody();
        }
        return "Operation failed";
    }

    public String withdrawAmount(Double amount, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("accountNumber", String.valueOf(customer.getAccountNumber()));
        headers.set("password", customer.getPassword());
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("amount", amount);
        String workerPort = authenticationService.getWorker(String.valueOf(customer.getAccountNumber()));
        String url = "http://worker" + workerPort + ":9000/api/withdraw/" + customer.getAccountNumber();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(rootNode.toString(), headers), String.class);
        if (response.getStatusCode() == HttpStatus.ACCEPTED) {
            return response.getBody();
        }
        return "Operation failed";
    }
}
