package com.example.Database.services;

import com.example.Database.file.FileService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import java.io.FileReader;

@Service
public class AuthenticationService {

    public boolean isAdmin(String username, String password) {
        if (username == null || password == null) {
            throw new RuntimeException("username or token is null");
        }
        String path = FileService.adminJsonFilePath();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(path));
            JSONObject jsonObject = (JSONObject) obj;
            String fileUsername = (String) jsonObject.get("username");
            String filePassword = (String) jsonObject.get("password");
            if (fileUsername.equals(username) && filePassword.equals(password)) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


        public String verifyCredentials(String username, String password) {
        JSONParser parser = new JSONParser();
        String path = FileService.adminJsonFilePath();
        try {
            Object obj = parser.parse(new FileReader(path));
            JSONObject jsonObject = (JSONObject) obj;
            String fileUsername = (String) jsonObject.get("username");
            String filePassword = (String) jsonObject.get("password");
            if (!fileUsername.equals(username)) {
                return "Wrong Username";
            }
            if (!filePassword.equals(password)) {
                return "Wrong Password";
            }
            return "Authenticated";
        } catch (Exception e) {
            e.printStackTrace();
            return "server error";
        }
    }
}