package com.example.BankingSystem.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthenticationService {

    private static final String localhost = "http://localhost:9000";

    public String checkAdmin(String username, String token) {
        return checkCredentials(username, token, localhost + "/api/checkAdmin/");
    }

    public String checkUser(String username, String token) {
        return checkCredentials(username, token, localhost + "/api/checkUser/");
    }

    public String getWorker(String username) {
        RestTemplate restTemplate = new RestTemplate();
        String url = localhost + "/api/getWorker/" + username;
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

    private String checkCredentials(String username, String token, String url) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("token", token);
        String customURL = url + username;
        HttpEntity<String> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(customURL, HttpMethod.GET, request, String.class);
            return response.getBody();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return "Server Error";
        }
    }
}