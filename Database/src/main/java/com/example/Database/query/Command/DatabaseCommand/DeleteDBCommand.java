package com.example.Database.query.Command.DatabaseCommand;

import com.example.Database.query.Command.CommandUtils;
import com.example.Database.query.Command.QueryCommand;
import com.example.Database.query.QueryType;
import com.example.Database.services.DatabaseService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unchecked")
public class DeleteDBCommand implements QueryCommand {

    @Autowired
    private DatabaseService databaseService;

    @Override
    public QueryType getQueryType() {
        return QueryType.DELETE_DATABASE;
    }

    @Override
    public JSONObject execute(JSONObject query) {
        try {
            String databaseName = CommandUtils.getDatabaseName(query);
            String result = databaseService.deleteDB(databaseName);
            JSONObject response = new JSONObject();
            response.put("status", result);
            return response;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}