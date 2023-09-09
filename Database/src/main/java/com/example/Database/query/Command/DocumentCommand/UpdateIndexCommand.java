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
public class UpdateIndexCommand implements QueryCommand {

    @Autowired
    DocumentService documentServices;

    @Override
    public QueryType getQueryType() {
        return QueryType.UPDATE_INDEX;
    }

    @Override
    public JSONObject execute(JSONObject commandJson) {
        try {
            String databaseName = CommandUtils.getDatabaseName(commandJson);
            String collectionName = CommandUtils.getCollectionName(commandJson);
            String documentId = CommandUtils.getDocumentId(commandJson);
            String propertyName = CommandUtils.getPropertyName(commandJson);
            String newValue = CommandUtils.getNewValue(commandJson);
            System.out.println(propertyName + " in execute");
            System.out.println(newValue + " in execute");
            String result = documentServices.updateDocumentProperty(databaseName, collectionName, documentId, propertyName, newValue);
            JSONObject response = new JSONObject();
            response.put("status", result);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}