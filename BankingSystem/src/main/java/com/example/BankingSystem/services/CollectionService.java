package com.example.BankingSystem.services;

import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import lombok.SneakyThrows;
import com.example.web.model.Admin;
import org.springframework.core.ParameterizedTypeReference;
import java.util.ArrayList;
import java.util.List;

@Service
public class CollectionService {
    @SneakyThrows
    public String createCollection(String dbName, String collectionName, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", admin.getUsername());
        headers.set("password", admin.getPassword());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String url = "http://worker1:9000/api/" + dbName + "/createCol/" + collectionName;
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        return responseEntity.getBody();
    }

    @SneakyThrows
    public String deleteCollection(String dbName, String collectionName, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", admin.getUsername());
        headers.set("password", admin.getPassword());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String url = "http://worker1:9000/api/" + dbName + "/deleteCol/" + collectionName;
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
        return responseEntity.getBody();
    }

    @SneakyThrows
    public List<String> fetchExistingCollections(String dbName, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", admin.getUsername());
        headers.set("password", admin.getPassword());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String url = "http://worker1:9000/api/fetchExistingCollections/" + dbName;
        ResponseEntity<List<String>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
        });
        return responseEntity.getBody();
    }


    public List<String> getAllCollections(String dbName, HttpSession session) {
        List<String> collectionNames = this.fetchExistingCollections(dbName, session);
        return cleanAndSortCollections(collectionNames);
    }

    private List<String> cleanAndSortCollections(List<String> collectionNames) {
        List<String> cleansedCollectionNames = new ArrayList<>();
        for (String colName : collectionNames) {
            String[] splitNames = colName.split(", ");
            for (String splitName : splitNames) {
                String cleansedName = splitName.trim();
                if (!cleansedName.isEmpty()) {
                    cleansedCollectionNames.add(cleansedName);
                }
            }
        }
        cleansedCollectionNames.sort((name1, name2) -> {
            int num1 = Integer.parseInt(name1.replaceAll("[^0-9]", ""));
            int num2 = Integer.parseInt(name2.replaceAll("[^0-9]", ""));
            return Integer.compare(num1, num2);
        });
        return cleansedCollectionNames;
    }
}