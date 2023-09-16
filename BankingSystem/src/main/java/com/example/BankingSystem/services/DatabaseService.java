package com.example.BankingSystem.services;

import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.SneakyThrows;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.example.web.model.Admin;

@Service
public class DatabaseService {

    @SneakyThrows
    public String createDatabase(String db_name, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", admin.getUsername());
        headers.set("password", admin.getPassword());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String url = "http://worker1:9000/api/createDB/" + db_name;
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        return responseEntity.getBody();
    }


    @SneakyThrows
    public String deleteDatabase(String db_name, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", admin.getUsername());
        headers.set("password", admin.getPassword());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String url = "http://worker1:9000/api/deleteDB/" + db_name;
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
        return responseEntity.getBody();
    }

    @SneakyThrows
    public List<String> fetchExistingDatabases(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("login");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", admin.getUsername());
        headers.set("password", admin.getPassword());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String url = "http://worker1:9000/api/fetchExistingDatabases";
        ResponseEntity<List<String>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
        });
        return responseEntity.getBody();
    }

    public List<String> getAllDatabases(HttpSession session) {
        List<String> databaseNames = this.fetchExistingDatabases(session);
        if (databaseNames.isEmpty()) {
            return Collections.emptyList();
        }
        return cleanAndSortDatabases(databaseNames);
    }

    private List<String> cleanAndSortDatabases(List<String> databaseNames) {
        List<String> cleansedDatabaseNames = new ArrayList<>();
        for (String databaseName : databaseNames) {
            String[] subNames = databaseName.split(", ");
            for (String subName : subNames) {
                if (!subName.isEmpty()) {
                    cleansedDatabaseNames.add(subName);
                }
            }
        }
        cleansedDatabaseNames.sort((name1, name2) -> {
            int num1 = Integer.parseInt(name1.replaceAll("[^0-9]", ""));
            int num2 = Integer.parseInt(name2.replaceAll("[^0-9]", ""));
            return Integer.compare(num1, num2);
        });
        return cleansedDatabaseNames;
    }
}