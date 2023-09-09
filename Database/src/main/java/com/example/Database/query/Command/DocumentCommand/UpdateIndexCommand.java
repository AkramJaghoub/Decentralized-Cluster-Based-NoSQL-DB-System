package com.example.Database.query.Command.DocumentCommand;

import com.example.Database.model.ApiResponse;
import com.example.Database.model.Collection;
import com.example.Database.model.Document;
import com.example.Database.query.Command.CommandUtils;
import com.example.Database.query.Command.QueryCommand;
import com.example.Database.query.QueryType;
import com.example.Database.services.DocumentService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateIndexCommand implements QueryCommand {

    @Autowired
    DocumentService documentServices;

    @Override
    public QueryType getQueryType() {
        return QueryType.UPDATE_INDEX;
    }

    @Override
    public ApiResponse execute(JSONObject commandJson) {
        try {
            Collection collection = CommandUtils.getCollection(commandJson);
            String documentId = CommandUtils.getDocumentId(commandJson);
            String propertyName = CommandUtils.getPropertyName(commandJson);
            String newPropertyValue = CommandUtils.getNewPropertyValue(commandJson);
            Document document = new Document(documentId);
            document.setPropertyName(propertyName);
            document.setPropertyValue(newPropertyValue);
            return documentServices.updateDocumentProperty(collection, document);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}