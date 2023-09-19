package com.example.BankingSystem.services;

import com.example.BankingSystem.Model.BankAccount;
import com.example.BankingSystem.Model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final List<User> users = new ArrayList<>();
    public void addUser(long accountNumber, String password) {
        User user = new User(accountNumber, password);
        users.add(user);
        String url = "http://host.docker.internal:8081/bootstrapper/add/user";
        HttpHeaders headers = new HttpHeaders();
        headers.set("accountNumber", String.valueOf(accountNumber));
        headers.set("password", user.getPassword());
        headers.set("adminUsername", "admin");
        headers.set("adminPassword", "admin@12345");
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        System.out.println("sending user with account number " + user.getAccountNumber() + " to be added to a node");
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        System.out.println("Response from bootstrapper: " + response.getBody());
    }
}