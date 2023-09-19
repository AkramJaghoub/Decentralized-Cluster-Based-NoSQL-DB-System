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
public class CreateDocumentCommand implements QueryCommand {

    @Autowired
    DocumentService documentService;
    @Autowired
    AffinityManager affinityManager;
    @Autowired
    BroadcastService broadcastService;
    @Autowired
    RedirectionService redirectionService;


    @Override
    public QueryType getQueryType() {
        return QueryType.CREATE_DOCUMENT;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ApiResponse execute(JSONObject commandJson) {
        System.out.println("[INFO] Starting execute method...");

        try {
            System.out.println("[INFO] Extracting database from commandJson...");
            Database database = CommandUtils.getDatabase(commandJson);

            System.out.println("[INFO] Extracting collection from commandJson...");
            Collection collection = CommandUtils.getCollection(commandJson);

            System.out.println("[INFO] Extracting documentJson from commandJson...");
            JSONObject documentJson = CommandUtils.getDocumentJson(commandJson);

            System.out.println("[INFO] Converting documentJson to Document object...");
            documentJson = FileService.addIdToDocument(documentJson);

            System.out.println(documentJson + " in execute");
            Document document = new Document(documentJson);
            document.setId((String) documentJson.get("_id"));

            System.out.println("[INFO] Checking broadcast condition...");
            boolean isBroadcasted = "true".equalsIgnoreCase(documentJson.get("X-Broadcast").toString());
            int workerWithAffinity = affinityManager.getWorkerPort(document.getId());
            int currentWorkerPort = affinityManager.getCurrentWorkerPort();

            if (!isBroadcasted && currentWorkerPort != workerWithAffinity) {
                System.out.println("[REDIRECT] The current worker is not the one with affinity. Redirecting...");
                document.setReplication(true); //set replication to true to not change on the data and avoid duplications
                return redirectionService.redirectToWorkerForCreation(database, collection, document, workerWithAffinity);
            }
            System.out.println("[INFO] Checking broadcast flag...");
            if (isBroadcasted) {
                System.out.println("[BROADCAST] Document received for replication...");
                return documentService.createDocument(collection, document);
            } else {
                System.out.println("[INFO] Not broadcasting. Creating document on the current node...");
                ApiResponse response = documentService.createDocument(collection, document);
                if (response.getStatus() == HttpStatus.CREATED) {
                    System.out.println("[INFO] Document created successfully. Broadcasting creation to other nodes...");
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
            System.out.println("[ERROR] Exception occurred in execute method. Message: " + e.getMessage());
            throw new RuntimeException("[ERROR] Exception occurred in execute method.", e);
        }
    }

    @Override
    public void broadcastOperation(JSONObject details) {
        Database database = (Database) details.get("database");
        Collection collection = (Collection) details.get("collection");
        Document document = (Document) details.get("document");
        int originatingWorkerPort = (int) details.get("originatingWorkerPort");
        System.out.println(originatingWorkerPort + " original worker portttttttttttt");
        JSONObject documentJson = new JSONObject(document.getData());
        for (int i = 1; i <= affinityManager.getNumberOfNodes(); i++) {
            if (i == originatingWorkerPort)
                continue; // skip the worker who has done the operation
            String url = "http://worker" + i + ":9000/api/" + database.getDatabaseName() + "/" + collection.getCollectionName() + "/createDoc";
            broadcastService.broadcast(url, HttpMethod.POST, true, documentJson);
        }
    }
}