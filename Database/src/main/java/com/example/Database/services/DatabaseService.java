package com.example.Database.services;
import com.example.Database.file.FileService;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.apache.commons.io.FileUtils;

@Service
public class DatabaseService {
   public String createDB(String dbName) {
       File dbDirectory = FileService.getDatabasePath();
       if (!FileService.isExists(dbDirectory)) {
           if (dbDirectory.mkdir()) {
               File schemasDirectory = new File(dbDirectory + "/schemas/");
               schemasDirectory.mkdirs();
               return "databases " + dbName + " created.";
           } else {
               return "Failed to create database.";
           }
       } else {
           return "Database already exists.";
       }
   }


    public String deleteDB(String dbName) {
        File dbDirectory = FileService.getDatabasePath();
        if (!FileService.isExists(dbDirectory)) {
            return "Database does not exist.";
        }
        try {
            FileUtils.deleteDirectory(dbDirectory);
            return "Database " + dbName + " deleted.";
        } catch (IOException e) {
            return "Failed to delete database: " + e.getMessage();
        }
    }

    public List<String> listDatabases(){
        File dbDirectory = FileService.getFilePath();
        if(!dbDirectory.exists())
            return Collections.emptyList();
        return Arrays.asList(Objects.requireNonNull(dbDirectory.list()));
    }
}