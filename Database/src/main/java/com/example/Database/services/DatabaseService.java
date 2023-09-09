package com.example.Database.services;
import com.example.Database.file.DatabaseFileOperations;
import com.example.Database.model.ApiResponse;
import com.example.Database.model.InMemoryDatabase;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

   public ApiResponse createDB(String dbName) {
       InMemoryDatabase.getInstance().createDatabase(dbName);
       return DatabaseFileOperations.createDatabase(dbName);
   }

    public ApiResponse deleteDB(String dbName) {
        InMemoryDatabase.getInstance().deleteDatabase(dbName);
        return DatabaseFileOperations.deleteDatabase(dbName);
    }

//    public List<String> listDatabases(){
//        File dbDirectory = FileService.getFilePath();
//        if(!dbDirectory.exists())
//            return Collections.emptyList();
//        return Arrays.asList(Objects.requireNonNull(dbDirectory.list()));
//    }
}