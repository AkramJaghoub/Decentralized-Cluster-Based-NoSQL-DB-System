package com.example.Database.Interceptors;

import com.example.Database.file.FileService;
import com.example.Database.index.IndexManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class IndexInitializationService {

    private final Set<String> initializedDatabases = new HashSet<>();

    @Autowired
    private IndexManager indexManager;

    public synchronized void initializeOnce(String dbName) {
        if (initializedDatabases.contains(dbName)) {
            return;
        }
        FileService.setDatabaseDirectory(dbName);
        indexManager.initializeIndexes();
        initializedDatabases.add(dbName);
    }
}
