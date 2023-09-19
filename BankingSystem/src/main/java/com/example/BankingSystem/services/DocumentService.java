package com.example.BankingSystem.services;
import com.example.BankingSystem.Model.BankAccount;
import com.example.web.model.Admin;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.json.simple.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class DocumentService {
    public String openAccount(String dbName, String collectionName, BankAccount bankAccount, HttpSession session) {
        System.out.println("sssssssssssssssserdqwewqeqewqeq");
        Admin admin = (Admin) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", admin.getUsername());
        headers.set("password", admin.getPassword());
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject bankAccountJson = bankAccount.bankAccountToJSON();
        HttpEntity<String> requestEntity = new HttpEntity<>(bankAccountJson.toJSONString(), headers);
        String url = "http://worker1:9000/api/" + dbName + "/" + collectionName + "/createDoc";
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        return responseEntity.getBody();
    }

    public List<BankAccount> readAccounts(String dbName, String collectionName, HttpSession session) {
        System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
        Admin admin = (Admin) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", admin.getUsername());
        headers.set("password", admin.getPassword());
        headers.setContentType(MediaType.APPLICATION_JSON);
        String url = "http://worker1:9000/api/" + dbName + "/" + collectionName + "/readDocs";
        ResponseEntity<List<BankAccount>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            return Collections.emptyList();
        }
    }

    public String updateDocumentProperty(String dbName, String collectionName, String documentId, String propertyName, Object newPropertyValue, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", admin.getUsername());
        headers.set("password", admin.getPassword());
        headers.set("newPropertyValue", newPropertyValue.toString());
        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(headers);
        String url = "http://worker1:9000/api/" + dbName + "/" + collectionName + "/updateDoc/" + propertyName + "?doc_id=" + documentId;
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
        return responseEntity.getBody();
    }


    @SneakyThrows
    public String deleteAccount(String dbName, String collectionName, String documentId, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", admin.getUsername());
        headers.set("password", admin.getPassword());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String url = "http://worker1:9000/api/" + dbName + "/" + collectionName + "/deleteDoc?doc_id=" + documentId;
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
        return responseEntity.getBody();
    }
}