package com.example.Database.query.Command.CollectionCommand;

import com.example.Database.model.ApiResponse;
import com.example.Database.model.Database;
import com.example.Database.query.Command.CommandUtils;
import com.example.Database.query.Command.QueryCommand;
import com.example.Database.query.QueryType;
import com.example.Database.services.CollectionService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReadCollectionsCommand implements QueryCommand {

    @Autowired
    private CollectionService collectionService;

    @Override
    public QueryType getQueryType() {
        return QueryType.READ_COLLECTIONS;
    }

    @Override
    public ApiResponse execute(JSONObject query) {
        try {
            Database database = CommandUtils.getDatabase(query);
            List<String> collections = collectionService.readCollections(database);
            return new ApiResponse(String.join(", ", collections));
        } catch (Exception e) {
            return new ApiResponse("Error reading collections: " + e.getMessage());
        }
    }
}
