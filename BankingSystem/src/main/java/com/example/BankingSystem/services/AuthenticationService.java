package com.example.BankingSystem.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthenticationService {


    public String checkAdmin(String username, String password) {
        return checkCredentials("1", username, password, "/api/check/admin");
    }

    public String checkUser(String username, String password) {
        return checkCredentials("5", username, password, "/api/check/user");
    }

    public String getWorker(String username) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "/api/getWorker/" + username;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody(); // Return the worker identity directly
        } else {
            return "Not Found";
        }
    }

    private String checkCredentials(String workerId, String username, String password, String url) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("username", username);
        headers.set("password", password);
        HttpEntity<String> request = new HttpEntity<>(headers);
        try {
            String customURL = "http://worker" + workerId + ":9000" + url;
            System.out.println(customURL);
            ResponseEntity<String> response = restTemplate.exchange(customURL, HttpMethod.GET, request, String.class);
            return response.getBody();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return "Server Error";
        }
    }
}