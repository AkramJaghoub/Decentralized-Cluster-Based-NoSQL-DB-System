package com.example.Database.query.Command.DocumentCommand;

import com.example.Database.query.Command.CommandUtils;
import com.example.Database.query.Command.QueryCommand;
import com.example.Database.query.QueryType;
import com.example.Database.services.DocumentService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unchecked")
public class CreateDocumentCommand implements QueryCommand {

    @Autowired
    DocumentService documentServices;

    @Override
    public QueryType getQueryType() {
        return QueryType.CREATE_DOCUMENT;
    }

    @Override
    public JSONObject execute(JSONObject commandJson) {
        try {
            String databaseName = CommandUtils.getDatabaseName(commandJson);
            String collectionName = CommandUtils.getCollectionName(commandJson);
            JSONObject document = CommandUtils.getDocumentJson(commandJson);
            String result = documentServices.createDocument(databaseName, collectionName, document);
            JSONObject response = new JSONObject();
            response.put("status", result);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}