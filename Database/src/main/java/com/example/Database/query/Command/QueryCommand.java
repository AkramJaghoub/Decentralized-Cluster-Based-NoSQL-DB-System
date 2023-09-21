package com.example.Database.query.Command;

import com.example.Database.model.ApiResponse;
import com.example.Database.query.QueryType;
import org.json.simple.JSONObject;

public interface QueryCommand {
    QueryType getQueryType();
    ApiResponse execute(JSONObject query);
    default void broadcastOperation(JSONObject details) {
        //Default implementation: do nothing
    }
}