package com.example.Bootstrapper.model;

import org.json.simple.JSONObject;

public class User {
    private String accountNumber;
    private String password;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String username) {
        this.accountNumber = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @SuppressWarnings("unchecked")
    public JSONObject toJson() {
        JSONObject userJson = new JSONObject();
        userJson.put("accountNumber", accountNumber);
        userJson.put("password", password);
        return userJson;
    }

    public static User readJson(JSONObject jsonObject) {
        User user = new User();
        user.setAccountNumber((String) jsonObject.get("accountNumber"));
        user.setPassword((String) jsonObject.get("password"));
        return user;
    }
}