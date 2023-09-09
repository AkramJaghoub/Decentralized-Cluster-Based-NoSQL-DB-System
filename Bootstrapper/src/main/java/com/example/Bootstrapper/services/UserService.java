package com.example.Bootstrapper.services;

import com.example.Bootstrapper.File.FileServices;
import com.example.Bootstrapper.model.Node;
import com.example.Bootstrapper.model.User;
import com.example.Bootstrapper.loadbalancer.LoadBalancer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private LoadBalancer loadBalancer;


    private final List<User> users = new ArrayList<>();

//    public List<User> getUsers() {
//        String url = null;
//        HttpHeaders headers = new HttpHeaders();
//        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//        RestTemplate restTemplate = new RestTemplate();
//        JSONArray usersArray = restTemplate.exchange(url, HttpMethod.GET, requestEntity, JSONArray.class).getBody();
//        for (int i = 0; i < Objects.requireNonNull(usersArray).size(); i++) {
//            users.add(User.readJson((JSONObject) usersArray.get(i)));
//        }
//        return users;
//    }

    public void addUser(User user) {
        users.add(user);
        Node node = loadBalancer.assignUserToNextNode(user.getAccountNumber());
        FileServices.saveUserToJson("users", user.toJson()); //first adding the user to json file
        String url = "http://" + node.getNodeIp() + ":9000/api/add/user";
        HttpHeaders headers = new HttpHeaders();
        headers.set("accountNumber", user.getAccountNumber());
        headers.set("password", user.getPassword());
        headers.set("adminUsername", "admin");
        headers.set("adminPassword", "admin@12345");
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        System.out.println("sending user with account number " + user.getAccountNumber());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        System.out.println("Response from database: " + response.getBody());
    }
}