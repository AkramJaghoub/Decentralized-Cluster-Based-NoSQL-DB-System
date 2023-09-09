package com.example.Database.query.Command.CollectionCommand;

import com.example.Database.query.Command.CommandUtils;
import com.example.Database.query.Command.QueryCommand;
import com.example.Database.query.QueryType;
import com.example.Database.services.CollectionService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unchecked")
public class DeleteCollectionCommand implements QueryCommand {

    @Autowired
    private CollectionService collectionService;

    @Override
    public QueryType getQueryType() {
        return QueryType.DELETE_COLLECTION;
    }

    @Override
    public JSONObject execute(JSONObject query) {
        try {
            String databaseName = CommandUtils.getDatabaseName(query);
            String collectionName = CommandUtils.getCollectionName(query);
            String result = collectionService.deleteCollection(databaseName, collectionName);
            JSONObject response = new JSONObject();
            response.put("status", result);
            return response;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
