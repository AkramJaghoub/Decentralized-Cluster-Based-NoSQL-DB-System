package com.example.Database.cluster;

import com.example.Database.file.FileService;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class BroadcastService {

    private void broadcast(String url, HttpMethod method, boolean isBroadcasted, Optional<JSONObject> dataOpt) {
        try {
            JsonNode adminCredentials = FileService.readAdminCredentialsFromJson();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("username", adminCredentials.get("username").asText());
            headers.set("password", adminCredentials.get("password").asText());
            headers.set("X-Broadcast", String.valueOf(isBroadcasted));

            if (dataOpt.isPresent()) {    // check if json data were sent within the call then set content type as application/json
                headers.setContentType(MediaType.APPLICATION_JSON);
            }

            HttpEntity<String> requestEntity = dataOpt.map(jsonObject ->
                    new HttpEntity<>(jsonObject.toJSONString(), headers)).orElseGet(() -> new HttpEntity<>(headers));
            restTemplate.exchange(url, method, requestEntity, String.class);
        } catch (Exception e) {
            System.out.println("[ERROR] Broadcasting failed. URL: " + url);
            e.printStackTrace();
        }
    }

    public void broadcast(String url, HttpMethod method, boolean isBroadcasted, JSONObject data) {
        broadcast(url, method, isBroadcasted, Optional.of(data));
    }

    public void broadcast(String url, HttpMethod method, boolean isBroadcasted) {
        broadcast(url, method, isBroadcasted, Optional.empty());
    }
}