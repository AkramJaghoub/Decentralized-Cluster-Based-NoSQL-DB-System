package com.example.BankingSystem.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthenticationService {

    public ResponseEntity<String> checkAdmin(String username, String password) {
        return checkCredentials("1", "username", username, password, "/api/check/admin");
    }

    public ResponseEntity<String> checkCustomer(String accountNumber, String password) {
        return checkCredentials(getWorker(accountNumber), "accountNumber", accountNumber, password, "/api/check/customer");
    }

    public String getWorker(String identity) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://host.docker.internal:8081/bootstrapper/getWorker/" + identity;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        return (response.getStatusCode() == HttpStatus.OK) ? response.getBody() : "Not Found";
    }

    private ResponseEntity<String> checkCredentials(String workerId, String identityType, String identity, String password, String url) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set(identityType, identity);
        headers.set("password", password);
        HttpEntity<String> request = new HttpEntity<>(headers);
        String customURL = "http://worker" + workerId + ":9000" + url;
        try {
            return restTemplate.exchange(customURL, HttpMethod.GET, request, String.class);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>("Internal Server Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}