package com.example.Database.services;

import com.example.Database.file.FileService;
import com.example.Database.model.ApiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;

@Service
public class AuthenticationService {

    public boolean isAdmin(String username, String password) {
        if (username == null || password == null) {
            throw new RuntimeException("username or token is null");
        }
        String path = FileService.getUserJsonPath("admin");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(new File(path));
            String fileUsername = rootNode.path("username").asText();
            String filePassword = rootNode.path("password").asText();
            return fileUsername.equals(username) && filePassword.equals(password);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ApiResponse verifyAdminCredentials(String username, String password) {
        return verifyCredentials("admin", username, password);
    }

    public ApiResponse verifyCustomerCredentials(String accountNumber, String password) {
        return verifyCredentials("customers", accountNumber, password);
    }

    private ApiResponse verifyCredentials(String userType, String identity, String password) {
        ObjectMapper objectMapper = new ObjectMapper();
        String path = FileService.getUserJsonPath(userType);
        String identityField = userType.equals("admin") ? "username" : "accountNumber";

        try {
            if (userType.equals("admin")) {
                JsonNode rootNode = objectMapper.readTree(new File(path));
                String storedIdentity = rootNode.path(identityField).asText();
                String storedPassword = rootNode.path("password").asText();
                if (identity.equals(storedIdentity) && password.equals(storedPassword)) {
                    return new ApiResponse("Authenticated", HttpStatus.OK);
                }
                return new ApiResponse("Wrong Username or Password", HttpStatus.BAD_REQUEST);

            } else {
                JsonNode[] rootNodes = objectMapper.readValue(new File(path), JsonNode[].class);
                for (JsonNode rootNode : rootNodes) {
                    String storedIdentity = rootNode.path(identityField).asText();
                    String storedPassword = rootNode.path("password").asText();
                    if (identity.equals(storedIdentity) && password.equals(storedPassword)) {
                        return new ApiResponse("Authenticated", HttpStatus.OK);
                    }
                }
                return new ApiResponse("Wrong Account Number or Password", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse("Server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}