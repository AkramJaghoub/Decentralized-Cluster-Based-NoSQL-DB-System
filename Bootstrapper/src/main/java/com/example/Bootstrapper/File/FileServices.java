package com.example.Bootstrapper.File;

import com.example.Bootstrapper.model.Admin;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public final class FileServices {
    private static final String usersDirectoryPath="src/main/resources/static";
    private FileServices(){}


    @SuppressWarnings("unchecked")
    public static void saveUserToJson(String fileName, JSONObject document) {
        String documentPath = usersDirectoryPath + "/" + fileName + ".json";
        JSONArray jsonArray = new JSONArray();
        System.out.println(isFileExists(documentPath));
        if (isFileExists(documentPath)) {
            try (FileReader reader = new FileReader(documentPath)) {
                JSONParser parser = new JSONParser();
                jsonArray = (JSONArray) parser.parse(reader);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                System.err.println("Error reading the file: " + e.getMessage());
                return;
            }
        }
        jsonArray.add(document);
        File parentDir = new File(documentPath).getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
       writeJsonArrayFile(new File(documentPath), jsonArray);
    }

    public static void saveAdminToJson(JSONObject document) {
        String documentPath = usersDirectoryPath + "/admin.json";
        File parentDir = new File(documentPath).getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        writeJsonObjectFile(new File(documentPath), document);
    }

    @SuppressWarnings("unchecked")
    public static void deleteUserFromJson(String fileName, String accountNumber) {
        String documentPath = getUserJsonPath(fileName);
        JSONArray jsonArray = readJsonArrayFile(new File(documentPath));
        if (jsonArray == null) {
            return;
        }
        JSONArray updatedArray = new JSONArray();
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            String userAccountNumber = (String) jsonObject.get("accountNumber");
            if (!userAccountNumber.equals(accountNumber)) {
                updatedArray.add(jsonObject);
            }
        }
        writeJsonArrayFile(new File(documentPath), updatedArray);
    }

    public static Optional<Admin> getAdminCredentials() {
        ObjectMapper mapper = new ObjectMapper();
        String path = FileServices.adminJsonFilePath();
        File file = new File(path);
        if (!file.exists()) {
            return Optional.empty();
        }
        try {
            Admin credentials = mapper.readValue(file, Admin.class);
            return Optional.of(credentials);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static void writeJsonObjectFile(File file, JSONObject jsonObject) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error writing to the file: " + e.getMessage());
        }
    }


    public static boolean isFileExists(String filePath){
        Path path = Paths.get(filePath);
        return Files.exists(path);
    }

    public static JSONArray readJsonArrayFile(File file) {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(file)) {
            if (file.length() == 0) {
                return new JSONArray();
            }
            Object obj = parser.parse(reader);
            return (JSONArray) obj;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            System.err.println("Error while reading JSON file in bootstrapping: " + e.getMessage());
            return null;
        }
    }

    public static void writeJsonArrayFile(File file, JSONArray jsonArray) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(jsonArray.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error while writing JSON file: " + e.getMessage());
        }
    }

    public static String getUserJsonPath(String fileName){
        return usersDirectoryPath + "/" + fileName + ".json";
    }

    public static String adminJsonFilePath(){
        return "src/main/resources/static/admin.json";
    }
}