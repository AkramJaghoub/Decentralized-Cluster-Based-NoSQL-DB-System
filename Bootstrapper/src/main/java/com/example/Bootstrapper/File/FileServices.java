package com.example.Bootstrapper.File;

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

@Service
public final class FileServices {
    private static final String usersDirectoryPath="/app/data/static";
    private FileServices(){}


    @SuppressWarnings("unchecked")
    public static void saveUserToJson(String fileName, JSONObject document) {
        String documentPath = usersDirectoryPath + "/" + fileName + ".json";
        JSONArray jsonArray = new JSONArray();
        if (isFileExists(documentPath)) {
            try (FileReader reader = new FileReader(documentPath)) {
                JSONParser parser = new JSONParser();
                jsonArray = (JSONArray) parser.parse(reader);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                System.err.println("Error reading the file: " + e.getMessage());
            }
        }
        jsonArray.add(document);
        try (FileWriter writer = new FileWriter(documentPath)) {
            writer.write(jsonArray.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error writing to the file: " + e.getMessage());
        }
    }


    private static boolean isFileExists(String filePath){
        Path path = Paths.get(filePath);
        return Files.exists(path);
    }

    public static JSONArray readJsonArrayFile(File file) {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(file)) {
            if (file.length() == 0) { // Check if the file is empty
                return new JSONArray(); // Return an empty JSONArray
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
        return "/app/data/static/admin.json";
    }
}
