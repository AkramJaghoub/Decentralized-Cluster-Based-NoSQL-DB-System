package com.example.BankingSystem.services;

import com.example.BankingSystem.Model.BankAccount;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.json.simple.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.example.BankingSystem.Model.Admin;

import java.util.Collections;
import java.util.List;

@Service
public class DocumentService {
    public ResponseEntity<String> createAccount(String dbName, String collectionName, BankAccount bankAccount, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", admin.getUsername());
        headers.set("password", admin.getPassword());
        headers.set("X-Broadcast", "false");
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject bankAccountJson = bankAccount.bankAccountToJSON();
        HttpEntity<String> requestEntity = new HttpEntity<>(bankAccountJson.toJSONString(), headers);
        String url = "http://worker1:9000/api/" + dbName + "/" + collectionName + "/createDoc";
        try {
            return restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @SneakyThrows
    public List<BankAccount> readAccounts(String dbName, String collectionName, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", admin.getUsername());
        headers.set("password", admin.getPassword());
        headers.setContentType(MediaType.APPLICATION_JSON);
        String url = "http://worker1:9000/api/" + dbName + "/" + collectionName + "/readDocs";
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String responseBody = responseEntity.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(responseBody, new TypeReference<>() {
            });
        } else {
            return Collections.emptyList();
        }
    }

    public ResponseEntity<String> updateDocumentProperty(String dbName, String collectionName, String documentId, String propertyName, Object newPropertyValue, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", admin.getUsername());
        headers.set("password", admin.getPassword());
        headers.set("newPropertyValue", newPropertyValue.toString());
        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(headers);
        String url = "http://worker1:9000/api/" + dbName + "/" + collectionName + "/updateDoc/" + propertyName + "?doc_id=" + documentId;
        try {
            return restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @SneakyThrows
    public ResponseEntity<String> deleteAccount(String dbName, String collectionName, String documentId, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", admin.getUsername());
        headers.set("password", admin.getPassword());
        headers.set("X-Broadcast", "false");
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String url = "http://worker1:9000/api/" + dbName + "/" + collectionName + "/deleteDoc?doc_id=" + documentId;
        try {
            return restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }
}