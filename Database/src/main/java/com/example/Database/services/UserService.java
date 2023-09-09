package com.example.Database.services;

import com.example.Database.file.FileService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class UserService {

    @SuppressWarnings("unchecked")
    public String addUser(String accountNumber, String password) {
        String filePath = FileService.getUserJsonPath("users");
        if (!FileService.isFileExists(filePath)) {
            FileService.writeJsonArrayFile(new File(filePath), new JSONArray());
        }
        JSONArray usersArray = FileService.readJsonArrayFile(new File(filePath));
        if (usersArray == null) {
            return "Error reading the file. in database " + filePath;
        }
        for (Object userObj : usersArray) {
            JSONObject user = (JSONObject) userObj;
            if (user.get("accountNumber").equals(accountNumber)) {
                return "User already exists";
            }
        }
        JSONObject newUser = new JSONObject();
        newUser.put("accountNumber", accountNumber);
        newUser.put("password", password);
        usersArray.add(newUser);
        FileService.writeJsonArrayFile(new File(filePath), usersArray);
        return "User added successfully";
    }
}
