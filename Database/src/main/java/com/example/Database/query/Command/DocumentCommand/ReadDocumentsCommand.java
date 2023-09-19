package com.example.Database.query.Command.DocumentCommand;

import com.example.Database.model.ApiResponse;
import com.example.Database.model.Collection;
import com.example.Database.query.Command.CommandUtils;
import com.example.Database.query.Command.QueryCommand;
import com.example.Database.query.QueryType;
import com.example.Database.services.DocumentService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReadDocumentsCommand implements QueryCommand {

    @Autowired
    DocumentService documentService;

    @Override
    public QueryType getQueryType() {
        return QueryType.READ_DOCUMENTS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ApiResponse execute(JSONObject query) {
        try {
            Collection collection = CommandUtils.getCollection(query);
            List<JSONObject> documents = documentService.readDocuments(collection);
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(documents);
            return new ApiResponse(jsonArray.toJSONString(), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ApiResponse("Error reading documents: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void broadcastOperation(JSONObject details) {

    }
}