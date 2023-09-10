package com.example.Database.services;
import com.example.Database.file.DatabaseFileOperations;
import com.example.Database.model.ApiResponse;
import com.example.Database.model.InMemoryDatabase;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DatabaseService {

   public ApiResponse createDB(String dbName) {
       InMemoryDatabase.getInstance().createDatabase(dbName);
       return DatabaseFileOperations.createDatabase();
   }

    public ApiResponse deleteDB(String dbName) {
        InMemoryDatabase.getInstance().deleteDatabase(dbName);
        return DatabaseFileOperations.deleteDatabase();
    }

    public List<String> readDBs() {
        List<String> inMemoryDbs = InMemoryDatabase.getInstance().readDatabases();
        Set<String> uniqueDatabases = new HashSet<>(inMemoryDbs);
        List<String> inFileDBS = DatabaseFileOperations.readDatabases();
        uniqueDatabases.addAll(inFileDBS);
        return new ArrayList<>(uniqueDatabases);
    }
}