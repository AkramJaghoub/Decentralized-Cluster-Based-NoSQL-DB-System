package com.example.Database.file;

import com.example.Database.index.Index;
import com.example.Database.index.PropertyIndex;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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


    public static Map<String, String> readIndexFile(String path) {
        System.out.println(path);
        File file = new File(path);
        Map<String, String> indexData = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int separatorPosition = line.indexOf(",");
                if (separatorPosition == -1) continue;
                String key = line.substring(0, separatorPosition).trim();
                String value = line.substring(separatorPosition + 1).trim();
                indexData.put(key, value);
            }
        } catch (IOException e) {
            System.err.println("Error reading the file.");
            e.printStackTrace();
        }
        return indexData;
    }


    public static void createDirectoryIfNotExist(File file){
        if (!file.exists()) {
            boolean dirCreated = file.mkdirs();
            if (!dirCreated) {
                System.err.println("Failed to create " + file.getName());
            }
        }
    }

    public static File getRootFile(){
        return new File(FILE_PATH);
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
        String dirPath = DB_DIRECTORY + "/indexes/" + collectionName + "_indexes";
        createDirectoryIfNotExist(new File(dirPath));
        return dirPath + "/" + collectionName + "_index.txt";
    }

    public static String getPropertyIndexFilePath(String collectionName, String propertyName) {
        String dirPath = DB_DIRECTORY + "/indexes/" + collectionName + "_indexes";
        createDirectoryIfNotExist(new File(dirPath));
        return dirPath + "/" + collectionName + "_" + propertyName + "_property_index.txt";
    }


    public static void setDatabaseDirectory(String dbName) {
        DB_DIRECTORY = FILE_PATH + "/" + dbName;
    }

    public static boolean isIndexFile(String fileName) {
        return fileName.endsWith("_index.txt");
    }

    public static boolean isPropertyIndexFile(String fileName) {
        return fileName.endsWith("_property_index.txt");
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
            if (file.length() == 0) {
                return new JSONArray();
            }
            Object obj = parser.parse(reader);
            return (JSONArray) obj;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            System.err.println("Error while reading JSON file: " + e.getMessage());
            return null;
        }
    }

    public static void appendToIndexFile(String path, Object key, String value) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, true))) {
            writer.write(key + "," + value);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void rewriteIndexFile(String collectionName, Index index) {
        File file = new File(getIndexFilePath(collectionName));
        List<Map.Entry<String, String>> allEntries = index.getBPlusTree().getAllEntries();
        if (allEntries.isEmpty()) {
            if (FileService.isFileExists(file.getPath())) {
                file.delete();
            }
            return;
        }
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Map.Entry<String, String> entry : allEntries) {
                    writer.write(entry.getKey() + "," + entry.getValue());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void rewritePropertyIndexFile(String path, PropertyIndex propertyIndex) {
        File file = new File(path);
        List<Map.Entry<String, String>> allEntries = propertyIndex.getBPlusTree().getAllEntries();
        if (allEntries.isEmpty()) {
            if (FileService.isFileExists(file.getPath())) {
                file.delete();
            }
            return;
        }
        try {
            file.createNewFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Map.Entry<String, String> entry : allEntries) {
                    writer.write(entry.getKey() + "," + entry.getValue());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean writeJsonArrayFile(File file, JSONArray jsonArray) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error while creating the file: " + e.getMessage());
                return false;
            }
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(jsonArray.toJSONString());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error while writing JSON file: " + e.getMessage());
            return false;
        }
    }

    public static String getUserJsonPath(String fileName){
        String usersDirectoryPath="/app/data/static";
        return usersDirectoryPath + "/" + fileName + ".json";
    }

    public static boolean isDirectoryExists(File file) {
        return !file.exists() || !file.isDirectory();
    }
}
