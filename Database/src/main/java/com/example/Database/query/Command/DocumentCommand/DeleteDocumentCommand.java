package com.example.Database.query.Command.DocumentCommand;

import com.example.Database.affinity.AffinityManager;
import com.example.Database.cluster.BroadcastService;
import com.example.Database.cluster.RedirectionService;
import com.example.Database.file.FileService;
import com.example.Database.model.ApiResponse;
import com.example.Database.model.Collection;
import com.example.Database.model.Database;
import com.example.Database.model.Document;
import com.example.Database.query.Command.CommandUtils;
import com.example.Database.query.Command.QueryCommand;
import com.example.Database.query.QueryType;
import com.example.Database.services.DocumentService;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DeleteDocumentCommand implements QueryCommand {

    @Autowired
    DocumentService documentServices;
    @Autowired
    AffinityManager affinityManager;
    @Autowired
    RedirectionService redirectionService;
    @Autowired
    BroadcastService broadcastService;

    @Override
    public QueryType getQueryType() {
        return QueryType.DELETE_DOCUMENT;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ApiResponse execute(JSONObject commandJson) {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n[INFO] Starting delete document process...");
        try {
            Database database = CommandUtils.getDatabase(commandJson);
            Collection collection = CommandUtils.getCollection(commandJson);
            String documentId = CommandUtils.getDocumentId(commandJson);
            Document document = new Document(documentId);

            boolean isBroadcasted = "true".equalsIgnoreCase(commandJson.get("X-Broadcast").toString());
            System.out.println(isBroadcasted + " is broadcastedddddddddddddddddddddddd");
            int workerWithAffinity = affinityManager.getWorkerPort(document.getId());
            int currentWorkerPort = affinityManager.getCurrentWorkerPort();

            if (!isBroadcasted && currentWorkerPort != workerWithAffinity) {
                System.out.println("[REDIRECT] Current worker isn't the one with affinity. Redirecting...");
                return redirectionService.redirectToWorkerForDeletion(database, collection, document, workerWithAffinity);
            }

            if(isBroadcasted){
                System.out.println("[BROADCAST] Document received for replication...");
                return documentServices.deleteDocument(collection, document);
            }else {
                ApiResponse response = documentServices.deleteDocument(collection, document);
                if (response.getStatus() == HttpStatus.ACCEPTED) {
                    System.out.println("[BROADCAST] Broadcasting deletion to other nodes...");
                    JSONObject details = new JSONObject();
                    details.put("database", database);
                    details.put("collection", collection);
                    details.put("document", document);
                    details.put("originatingWorkerPort", affinityManager.getCurrentWorkerPort());
                    broadcastOperation(details);
                }
                return response;
            }

        } catch (Exception e) {
            System.out.println("[ERROR] Exception occurred in delete process. Message: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void broadcastOperation(JSONObject details) {

        Database database = (Database) details.get("database");
        Collection collection = (Collection) details.get("collection");
        Document document = (Document) details.get("document");
        int originatingWorkerPort = (int) details.get("originatingWorkerPort");
        System.out.println(originatingWorkerPort + " original worker portttttttttttt");
        for (int i = 1; i <= affinityManager.getNumberOfNodes(); i++) {
            if (i == originatingWorkerPort) continue; // Skip the worker which already deleted the document
            System.out.println("broadcasting the operation to worker" + i);
            String url = "http://worker" + i + ":9000/api/" + database.getDatabaseName() + "/" +
                    collection.getCollectionName() + "/deleteDoc?doc_id=" + document.getId();
            System.out.println(url + " in broadcast url");
            broadcastService.broadcast(url, HttpMethod.DELETE, true);
        }
    }
}