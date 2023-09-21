package com.example.Bootstrapper.services;

import com.example.Bootstrapper.File.FileServices;
import com.example.Bootstrapper.model.Admin;
import com.example.Bootstrapper.model.Customer;
import com.example.Bootstrapper.model.Node;
import com.example.Bootstrapper.loadbalancer.LoadBalancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

    @Autowired
    private LoadBalancer loadBalancer;

    public void addCustomer(Customer customer) {
        Node node = loadBalancer.assignUserToNextNode(customer.getAccountNumber());
        FileServices.saveUserToJson("customers", customer.toJson()); //first adding the customer to json file
        String url = "http://" + node.getNodeIP() + ":9000/api/add/customer";
        HttpHeaders headers = new HttpHeaders();
        headers.set("accountNumber", customer.getAccountNumber());
        headers.set("password", customer.getPassword());
        headers.set("adminUsername", "admin");
        headers.set("adminPassword", "admin@12345");
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        System.out.println("sending customer with account number " + customer.getAccountNumber());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class); //adding the users into the database worker nodes
        System.out.println("Response from database: " + response.getBody());
    }

    public void addAdmin(Admin admin) {
        FileServices.saveAdminToJson(admin.toJson());
        for(int i = 1; i <= 4; i++) {
            Node node = loadBalancer.assignUserToNextNode(admin.getUsername());
            String url = "http://" + node.getNodeIP() + ":9000/api/add/admin";
            System.out.println(url);
            HttpHeaders headers = new HttpHeaders();
            System.out.println(admin.getUsername());
            System.out.println(admin.getPassword());
            headers.set("username", admin.getUsername());
            headers.set("password", admin.getPassword());
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            System.out.println("sending user with username " + admin.getUsername());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class); //adding the admin into the database worker nodes
            System.out.println("Response from database: " + response.getBody());
        }
    }
}