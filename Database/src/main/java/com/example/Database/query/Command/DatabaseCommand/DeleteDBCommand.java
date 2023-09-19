package com.example.Database.query.Command.DatabaseCommand;

import com.example.Database.affinity.AffinityManager;
import com.example.Database.cluster.BroadcastService;
import com.example.Database.model.ApiResponse;
import com.example.Database.model.Database;
import com.example.Database.query.Command.CommandUtils;
import com.example.Database.query.Command.QueryCommand;
import com.example.Database.query.QueryType;
import com.example.Database.services.DatabaseService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DeleteDBCommand implements QueryCommand {

    @Autowired
    private DatabaseService databaseService;
    @Autowired
    AffinityManager affinityManager;
    @Autowired
    BroadcastService broadcastService;

    @Override

    public QueryType getQueryType() {
        return QueryType.DELETE_DATABASE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ApiResponse execute(JSONObject query) {
        try {
            Database database = CommandUtils.getDatabase(query);
            boolean isBroadcasted = "true".equalsIgnoreCase(query.getOrDefault("X-Broadcast", "false").toString());
            ApiResponse response = databaseService.deleteDB(database.getDatabaseName());
            System.out.println(isBroadcasted + " broadcastttttttttttttttttttttt");
            if(response.getStatus() == HttpStatus.ACCEPTED && !isBroadcasted) {
                JSONObject details = new JSONObject();
                details.put("database", database);
                details.put("originatingWorkerPort", affinityManager.getCurrentWorkerPort());
                broadcastOperation(details);
            }
            return response;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void broadcastOperation(JSONObject details) {
        System.out.println("[INFO] Starting broadcasting database deletion to others..");
        Database database = (Database) details.get("database");
        int originatingWorkerPort = (int) details.get("originatingWorkerPort");
        System.out.println(originatingWorkerPort + " ssssssssssssssssssssssssssssssssssssssssssssss");
        for (int i = 1; i <= affinityManager.getNumberOfNodes(); i++) {
            if (i == originatingWorkerPort) {
                System.out.println("[SKIP] Skipping broadcast to worker " + i + " (origin node)...");
                continue;
            }
            System.out.println("[BROADCAST] Broadcasting to worker " + i + "...");
            String url = "http://worker" + i + ":9000/api/deleteDB" + "/" + database.getDatabaseName();
            System.out.println("[BROADCAST] Broadcasting to URL: " + url);
            broadcastService.broadcast(url, HttpMethod.DELETE, true);
        }
    }
}