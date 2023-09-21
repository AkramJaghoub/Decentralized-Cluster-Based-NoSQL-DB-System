package com.example.Database.query.Command.CollectionCommand;

import com.example.Database.affinity.AffinityManager;
import com.example.Database.cluster.BroadcastService;
import com.example.Database.cluster.RedirectionService;
import com.example.Database.file.FileService;
import com.example.Database.model.ApiResponse;
import com.example.Database.model.Collection;
import com.example.Database.model.Database;
import com.example.Database.query.Command.CommandUtils;
import com.example.Database.query.Command.QueryCommand;
import com.example.Database.query.QueryType;
import com.example.Database.services.CollectionService;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CreateCollectionCommand implements QueryCommand {

    @Autowired
    private CollectionService collectionService;
    @Autowired
    AffinityManager affinityManager;
    @Autowired
    BroadcastService broadcastService;

    @Override
    public QueryType getQueryType() {
        return QueryType.CREATE_COLLECTION;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ApiResponse execute(JSONObject query) {
        try {
            Database database = CommandUtils.getDatabase(query);
            Collection collection = CommandUtils.getCollection(query);
            JSONObject jsonSchema = CommandUtils.getSchemaJson(query);
            boolean isBroadcasted = "true".equalsIgnoreCase(query.getOrDefault("X-Broadcast", "false").toString());
            ApiResponse response = collectionService.createCollection(database, collection.getCollectionName(), jsonSchema);
            System.out.println(isBroadcasted + " broadcastttttttttttttttttttttt");
            if(response.getStatus() == HttpStatus.CREATED && !isBroadcasted) {
                JSONObject details = new JSONObject();
                details.put("database", database);
                details.put("collection", collection);
                details.put("originatingWorkerPort", affinityManager.getCurrentWorkerPort());
                broadcastOperation(details);
            }
            return response;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void broadcastOperation(JSONObject details) {
        System.out.println("[INFO] Starting broadcasting collection creation to others..");
        Database database = (Database) details.get("database");
        Collection collection = (Collection) details.get("collection");
        int originatingWorkerPort = (int) details.get("originatingWorkerPort");
        for (int i = 1; i <= affinityManager.getNumberOfNodes(); i++) {
            if (i == originatingWorkerPort) {
                System.out.println("[SKIP] Skipping broadcast to worker " + i + " (origin node)...");
                continue;
            }
            System.out.println("[BROADCAST] Broadcasting to worker " + i + "...");
            String url = "http://worker" + i + ":9000/api/" + database.getDatabaseName() + "/createCol/" + collection.getCollectionName();
            System.out.println("[BROADCAST] Broadcasting to URL: " + url);
            broadcastService.broadcast(url, HttpMethod.POST, true);
        }
    }
}