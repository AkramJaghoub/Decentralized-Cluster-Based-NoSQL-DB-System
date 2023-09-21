package com.example.Database.services;

import com.example.Database.file.FileService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class UserService {

    @SuppressWarnings("unchecked")
    public String addCustomer(String accountNumber, String password) {
        String filePath = FileService.getUserJsonPath("customers");
        if (!FileService.isFileExists(filePath)) {
            FileService.writeJsonArrayFile(new File(filePath), new JSONArray());
        }
        JSONArray customersArray = FileService.readJsonArrayFile(new File(filePath));
        if (customersArray == null) {
            return "Error reading the file. in database " + filePath;
        }
        for (Object userObj : customersArray) {
            JSONObject customer = (JSONObject) userObj;
            if (customer.get("accountNumber").equals(accountNumber)) {
                return "Customer already exists";
            }
        }
        JSONObject newCustomer = new JSONObject();
        newCustomer.put("accountNumber", accountNumber);
        newCustomer.put("password", password);
        customersArray.add(newCustomer);
        FileService.writeJsonArrayFile(new File(filePath), customersArray);
        return "Customer added successfully";
    }

    @SuppressWarnings("unchecked")
    public String addAdmin(String username, String password) {
        String filePath = FileService.getUserJsonPath("admin");
        JSONObject adminObject;
        if (!FileService.isFileExists(filePath)) {
            adminObject = new JSONObject();
            FileService.writeJsonObjectFile(new File(filePath), adminObject);
        } else {
            adminObject = FileService.readJsonObjectFile(new File(filePath));
            if (adminObject == null) {
                return "Error reading the file. in database " + filePath;
            }
        }
        if (adminObject.containsKey("username")) {
            if (adminObject.get("username").equals(username)) {
                return "admin already exists";
            }
        }
        adminObject.put("username", username);
        adminObject.put("password", password);
        FileService.writeJsonObjectFile(new File(filePath), adminObject);
        return "admin added successfully";
    }
}
