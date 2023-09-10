package com.example.Database.query.Command.DatabaseCommand;

import com.example.Database.model.ApiResponse;
import com.example.Database.query.Command.QueryCommand;
import com.example.Database.query.QueryType;
import com.example.Database.services.DatabaseService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReadDBsCommand implements QueryCommand {
    @Autowired
    private DatabaseService databaseService;

    @Override
    public QueryType getQueryType() {
        return QueryType.READ_DATABASES;
    }

    @Override
    public ApiResponse execute(JSONObject query) {
        try {
            List<String> databases = databaseService.readDBs();
            if (databases.isEmpty()) {
                return new ApiResponse("No databases found");
            } else {
                return new ApiResponse(String.join(", ", databases));
            }
        } catch (Exception e) {
            return new ApiResponse("Error retrieving databases: " + e.getMessage());
        }
    }
}
