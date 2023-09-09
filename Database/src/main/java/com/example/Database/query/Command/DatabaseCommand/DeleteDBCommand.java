package com.example.Database.query.Command.DatabaseCommand;

import com.example.Database.model.ApiResponse;
import com.example.Database.model.Database;
import com.example.Database.query.Command.CommandUtils;
import com.example.Database.query.Command.QueryCommand;
import com.example.Database.query.QueryType;
import com.example.Database.services.DatabaseService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteDBCommand implements QueryCommand {

    @Autowired
    private DatabaseService databaseService;

    @Override
    public QueryType getQueryType() {
        return QueryType.DELETE_DATABASE;
    }

    @Override
    public ApiResponse execute(JSONObject query) {
        try {
            Database database = CommandUtils.getDatabase(query);
            return databaseService.deleteDB(database.getDatabaseName());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}