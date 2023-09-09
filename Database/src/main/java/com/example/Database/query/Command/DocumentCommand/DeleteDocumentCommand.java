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
public class DeleteDocumentCommand implements QueryCommand {

    @Autowired
    DocumentService documentServices;

    @Override
    public QueryType getQueryType() {
        return QueryType.DELETE_DOCUMENT;
    }

    @Override
    public JSONObject execute(JSONObject commandJson) {
        try {
            String databaseName = CommandUtils.getDatabaseName(commandJson);
            String collectionName = CommandUtils.getCollectionName(commandJson);
            String documentId = CommandUtils.getDocumentId(commandJson);
            String result = documentServices.deleteDocument(databaseName, collectionName, documentId);
            JSONObject response = new JSONObject();
            response.put("status", result);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
