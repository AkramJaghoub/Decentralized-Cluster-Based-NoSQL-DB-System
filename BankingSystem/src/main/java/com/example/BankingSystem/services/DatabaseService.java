package com.example.BankingSystem.services;

import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import com.example.web.model.User;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.logging.Logger;

@Service
public class DatabaseService {

    @Autowired
    AuthenticationService authenticationService;
    private final Logger logger = Logger.getLogger(DatabaseService.class.getName());

    @SneakyThrows
    public String createDatabase(String db_name , HttpSession session){
        User user = (User) session.getAttribute("login");
        String workerIdentity = authenticationService.getWorker(user.getUsername());
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", user.getUsername());
        headers.set("password", user.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String workerHost = "worker-" + workerIdentity; // Construct the worker hostname
        String url = "http://localhost:9000/api/createDB/" + db_name;
        System.out.println("URL being used: " + url);
        logger.info("URL being used: " + url);
        System.out.println("Creating database");
        logger.info("Creating database");
        HttpEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        return responseEntity.getBody();
    }
}
