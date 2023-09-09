package com.example.Database.query.Command;

import com.example.Database.query.QueryType;
import org.json.simple.JSONObject;

public interface QueryCommand {
    QueryType getQueryType();
    JSONObject execute(JSONObject query);
}