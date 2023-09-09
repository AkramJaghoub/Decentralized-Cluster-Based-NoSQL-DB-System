package com.example.Database.services;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import java.io.FileReader;

@Service
public class WorkerService {

    public String getWorkerIdentity(String username) {
        JSONParser parser = new JSONParser();
        try {
            String PATH = "./src/main/resources/static/admin.json";
            Object obj = parser.parse(new FileReader(PATH));
            JSONObject jsonObject = (JSONObject) obj;
            if (jsonObject.get("username").equals(username)) {
                return (String) jsonObject.get("worker");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "not found";
    }
}