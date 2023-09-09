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
public class CreateDocumentCommand implements QueryCommand {

    @Autowired
    DocumentService documentService;

    @Override
    public QueryType getQueryType() {
        return QueryType.CREATE_DOCUMENT;
    }

    @Override
    public ApiResponse execute(JSONObject commandJson) {
        try {
            Collection collection = CommandUtils.getCollection(commandJson);
            JSONObject documentJson = CommandUtils.getDocumentJson(commandJson);
            Document document = new Document(documentJson);
            return documentService.createDocument(collection, document);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}