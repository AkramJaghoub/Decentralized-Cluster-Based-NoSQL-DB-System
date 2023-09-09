package com.example.Database.query.Command.CollectionCommand;

import com.example.Database.model.ApiResponse;
import com.example.Database.model.Collection;
import com.example.Database.model.Database;
import com.example.Database.query.Command.CommandUtils;
import com.example.Database.query.Command.QueryCommand;
import com.example.Database.query.QueryType;
import com.example.Database.services.CollectionService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateCollectionCommand implements QueryCommand {

    @Autowired
    private CollectionService collectionService;

    @Override
    public QueryType getQueryType() {
        return QueryType.CREATE_COLLECTION;
    }

    @Override
    public ApiResponse execute(JSONObject query) {
        try {
            Database database = CommandUtils.getDatabase(query);
            Collection collection = CommandUtils.getCollection(query);
            JSONObject jsonSchema = CommandUtils.getSchemaJson(query);
            System.out.println(collection.getCollectionName());
            return collectionService.createCollection(database, collection.getCollectionName(), jsonSchema);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}