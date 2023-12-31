package com.example.BankingSystem.Model;

import com.example.BankingSystem.enums.AccountTypes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import json.JsonBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankAccount{

    private long accountNumber;

    private String clientName;
    private double balance;
    private AccountTypes accountType;
    private boolean hasInsurance;
    private String password;
    private String _id;

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
}