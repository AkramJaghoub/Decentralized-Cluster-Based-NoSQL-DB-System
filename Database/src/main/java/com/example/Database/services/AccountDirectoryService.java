package com.example.Database.services;

import com.example.Database.file.FileService;
import com.example.Database.model.AccountReference;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountDirectoryService {
    private final Map<String, AccountReference> accountDirectory = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        this.accountDirectory.putAll(FileService.loadAccountDirectory());
        System.out.println("loadinggggggg directoryyyy");
    }

    public void registerAccount(String accountNumber, String dbName, String collectionName, String documentId) {
        accountDirectory.put(accountNumber, new AccountReference(dbName, collectionName, documentId));
        FileService.saveAccountDirectory(this.accountDirectory);
    }

    public AccountReference getAccountLocation(String accountNumber) {
        return accountDirectory.get(accountNumber);
    }
}
