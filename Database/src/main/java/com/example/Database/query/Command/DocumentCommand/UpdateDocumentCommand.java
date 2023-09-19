package com.example.Database.query.Command.DocumentCommand;

import com.example.Database.affinity.AffinityManager;
import com.example.Database.file.FileService;
import com.example.Database.model.ApiResponse;
import com.example.Database.model.Collection;
import com.example.Database.model.Database;
import com.example.Database.model.Document;
import com.example.Database.query.Command.CommandUtils;
import com.example.Database.query.Command.QueryCommand;
import com.example.Database.query.QueryType;
import com.example.Database.services.DocumentService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class UpdateDocumentCommand implements QueryCommand {

    @Autowired
    DocumentService documentServices;

    @Autowired
    AffinityManager affinityManager;

    @Override
    public QueryType getQueryType() {
        return QueryType.UPDATE_INDEX;
    }

    @Override
    public ApiResponse execute(JSONObject commandJson) {
        try {
            Database database = CommandUtils.getDatabase(commandJson);
            Collection collection = CommandUtils.getCollection(commandJson);
            String documentId = CommandUtils.getDocumentId(commandJson);
            String propertyName = CommandUtils.getPropertyName(commandJson);
            Object newPropertyValue = CommandUtils.getNewPropertyValue(commandJson);
            Document document = new Document(documentId);
            document.setPropertyName(propertyName);
            document.setPropertyValue(newPropertyValue);
            int workerWithAffinity = affinityManager.getWorkerPort(document.getId());
            if (redirectToNodeWithAffinity(database, collection, document)) {
                return new ApiResponse("Redirected to worker: " + workerWithAffinity, HttpStatus.TEMPORARY_REDIRECT);
            }
            return documentServices.updateDocumentProperty(collection, document);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void broadcastOperation(JSONObject details) {

    }

    private boolean redirectToNodeWithAffinity(Database database, Collection collection, Document document) {
        int currentWorker = affinityManager.getCurrentWorkerPort();
        int  workerWithAffinity = affinityManager.getWorkerPort(document.getId());
        if (currentWorker == workerWithAffinity) {
            System.out.println("stop wait a minute");
            return false;
        }
        System.out.println("Current worker: " + currentWorker);
        System.out.println("Redirecting to: " + workerWithAffinity);
        String url = "http://" + workerWithAffinity + ":9000/api/" + database.getDatabaseName() + "/" +
                collection.getCollectionName() + "/updateDoc/" + document.getPropertyName() + "?doc_id=" + document.getId();
        JsonNode adminCredentials = FileService.readAdminCredentialsFromJson();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", adminCredentials.get("username").asText());
        headers.set("password", adminCredentials.get("password").asText());
        headers.set("newPropertyValue", (String) document.getPropertyValue());
        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
        return true;
    }
}