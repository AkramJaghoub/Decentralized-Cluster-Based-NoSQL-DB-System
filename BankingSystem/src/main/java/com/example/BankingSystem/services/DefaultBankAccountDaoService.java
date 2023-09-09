//package com.example.BankingSystem.services;
//
//import com.example.BankingSystem.Dao.BankAccountDao;
//import com.example.BankingSystem.Model.BankAccount;
//import json.JsonBuilder;
//import org.json.simple.JSONObject;
//
//import java.util.List;
//
//public class DefaultBankAccountDaoService implements BankAccountDao {
//
//    private static volatile DefaultBankAccountDaoService instance;
//
//    private DefaultBankAccountDaoService(){
//        JSONObject indexProperty= JsonBuilder.getBuilder()
//                .add("accountNumber","")
//                .build();
////        queryManager.createIndex(databaseName,collectionName,indexProperty);
//    }
//
//    @Override
//    public void insertAccount(BankAccount account) {
//
//    }
//
//    @Override
//    public void removeAccountByNumber(long accountNumber) {
//
//    }
//
//    @Override
//    public void modifyAccountBalance(long accountNumber, double newBalance) {
//
//    }
//
//    @Override
//    public BankAccount findAccountByNumber(long accountNumber) {
//        return null;
//    }
//
//    @Override
//    public long getCustomerId(long accountNumber) {
//        return 0;
//    }
//
//    @Override
//    public List<BankAccount> fetchAllAccounts() {
//        return null;
//    }
//}
