package com.example.Database.file;

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
public final class FileService {
    private static final String FILE_PATH = "/app/data/databases";
    private static String DB_DIRECTORY;

    private FileService(){}

    public static JSONObject readSchema(String collectionName) {
        try {
            File schemaFile = getSchemaPath(collectionName);
            String schemaString = Files.readString(Paths.get(schemaFile.getAbsolutePath()));
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(schemaString);
        } catch (IOException | ParseException e) {
            System.out.println(e.getMessage());
        }
        return new JSONObject();
    }

    public static File getCollectionFile(String collectionName) {
        return new File(DB_DIRECTORY + "/" + collectionName + ".json");
    }

    public static File getSchemaPath(String collectionName) {
        return new File(DB_DIRECTORY + "/schemas/" + collectionName + ".json");
    }

    public static File getDatabasePath() {
        return new File( DB_DIRECTORY);
    }

    public static String getIndexFilePath(String collectionName) {
        return DB_DIRECTORY + "/indexes/" +  collectionName + "_index.txt";
    }

    public static void setDatabaseDirectory(String dbName) {
        DB_DIRECTORY = FILE_PATH + "/" + dbName;
    }

    public static String adminJsonFilePath(){
        return "/app/data/static/admin.json";
    }

    public static boolean isFileExists(String filePath){
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
            System.err.println("Error while reading JSON file: " + e.getMessage());
            return null;
        }
    }

    public static boolean writeJsonArrayFile(File file, JSONArray jsonArray) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error while creating the file: " + e.getMessage());
                return false; // return false since the file couldn't be created
            }
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(jsonArray.toJSONString());
            return true;  // return true if write was successful
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error while writing JSON file: " + e.getMessage());
            return false; // return false if there was an error writing
        }
    }

    public static String getUserJsonPath(String fileName){
        String usersDirectoryPath="/app/data/static";
        return usersDirectoryPath + "/" + fileName + ".json";
    }

    public static boolean isExists(File file) {
        return !file.exists() || !file.isDirectory();
    }
}