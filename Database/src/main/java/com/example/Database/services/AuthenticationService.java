package com.example.Database.services;

import com.example.Database.file.FileService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import java.io.FileReader;

@Service
public class AuthenticationService {

    public boolean isAdmin(String username, String token) {
        if (username == null || token == null) {
            throw new RuntimeException("username or token is null");
        }
        String path = FileService.adminJsonFilePath();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(path));
            JSONObject jsonObject = (JSONObject) obj;
            String fileUsername = (String) jsonObject.get("username");
            String filePassword = (String) jsonObject.get("password");
            if (fileUsername.equals(username) && filePassword.equals(token)) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

//    public String verifyCredentials(String username, String token) {
//        JSONParser parser = new JSONParser();
//        String path = FileService.adminJsonFilePath();
//        try {
//            Object obj = parser.parse(new FileReader(path));
//            JSONObject jsonObject = (JSONObject) obj;
//            String fileUsername = (String) jsonObject.get("username");
//            String fileToken = (String) jsonObject.get("password");
//            if (!fileUsername.equals(username)) {
//                return "Wrong Username";
//            }
//            if (!fileToken.equals(token)) {
//                return "Wrong Password";
//            }
//            return "Authenticated";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "server error";
//        }
//    }
}