package com.example.Bootstrapper.services;

import com.example.Bootstrapper.File.FileServices;
import com.example.Bootstrapper.model.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Service
public class AuthenticationService {
    public boolean isAdmin(String username, String password) {
        if (username == null || password == null) {
            throw new RuntimeException("username or token is null");
        }
        String path = FileServices.adminJsonFilePath();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(path));
            JSONObject jsonObject = (JSONObject) obj;
            String fileUsername = (String) jsonObject.get("username");
            String filePassword = (String) jsonObject.get("password");
            if (fileUsername.equals(username) && filePassword.equals(password)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String verifyAdminCredentials(String username, String password) {
        JSONParser parser = new JSONParser();
        String path = FileServices.adminJsonFilePath();
        try {
            Object obj = parser.parse(new FileReader(path));
            JSONObject jsonObject = (JSONObject) obj;
            String fileUsername = (String) jsonObject.get("username");
            String fileToken = (String) jsonObject.get("password");
            if (!fileUsername.equals(username)) {
                return "Wrong Username";
            }
            if (!fileToken.equals(password)) {
                return "Wrong Password";
            }
            return "Authenticated";
        } catch (Exception e) {
            e.printStackTrace();
            return "server error";
        }
    }

    public boolean isUserExists(User user) {
        if (user == null || user.getAccountNumber() == null) {
            return false;
        }
        String path = FileServices.getUserJsonPath("users");
        File jsonFile = new File(path);
        if (jsonFile.exists()) {
            JSONArray jsonArray = FileServices.readJsonArrayFile(jsonFile);
            if (jsonArray != null) {
                for (Object obj : jsonArray) {
                    JSONObject userObject = (JSONObject) obj;
                    String accountNumber = (String) userObject.get("accountNumber");
                    if (accountNumber != null && accountNumber.equals(user.getAccountNumber())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
