package com.example.BankingSystem.Model;

import com.example.BankingSystem.database.collection.docuemnt.AccountTypes;
import json.JsonBuilder;
import org.json.simple.JSONObject;

public class BankAccount{

    private long accountNumber;

    private String clientName;
    private double balance;
    private AccountTypes accountType;
    private boolean hasInsurance;
    private String password;
    private String _id;


    public BankAccount(){
    }

    public long getAccountNumber(){
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber){
        this.accountNumber = accountNumber;
    }


    public String getClientName(){
        return clientName;
    }

    public void setClientName(String clientName){
        this.clientName = clientName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public AccountTypes getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountTypes accountType) {
        this.accountType = accountType;
    }


    public boolean getHasInsurance() {
        return hasInsurance;
    }

    public void setHasInsurance(boolean hasInsurance) {
        this.hasInsurance = hasInsurance;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public static BankAccount addBankAccount(JSONObject accountObject) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber((long) accountObject.get("accountNumber"));
        bankAccount.setClientName((String) accountObject.get("clientName"));
        bankAccount.setBalance((double) accountObject.get("balance"));
        bankAccount.setAccountType(AccountTypes.valueOf((String) accountObject.get("accountType")));
        bankAccount.setHasInsurance((boolean) accountObject.get("hasInsurance"));
        bankAccount.setPassword((String) accountObject.get("password"));
        return bankAccount;
    }


    public JSONObject bankAccountToJSON() {
        return JsonBuilder.getBuilder()
                .add("accountNumber",accountNumber)
                .add("clientName", clientName)
                .add("balance",balance)
                .add("accountType",accountType.toString())
                .add("hasInsurance",hasInsurance)
                .add("password", password)
                .build();
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "accountNumber=" + accountNumber +
                ", clientName='" + clientName + '\'' +
                ", balance=" + balance +
                ", accountType=" + accountType +
                ", hasInsurance=" + hasInsurance +
                ", password=" + password +
                '}';
    }
}