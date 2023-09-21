package com.example.Database.cluster;

import com.example.Database.file.FileService;
import com.example.Database.model.Document;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class BroadcastService {

    private void broadcast(String url, HttpMethod method, boolean isBroadcasted, Optional<JSONObject> dataOpt, Map<String, String> additionalHeaders) {
        try {
            JsonNode adminCredentials = FileService.readAdminCredentialsFromJson();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("username", adminCredentials.get("username").asText());
            headers.set("password", adminCredentials.get("password").asText());
            headers.set("X-Broadcast", String.valueOf(isBroadcasted));
            additionalHeaders.forEach(headers::set);
            if (dataOpt.isPresent()) {
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

    public void broadcast(String url, HttpMethod method, JSONObject data, boolean isBroadcasted) {    //for creating a document (data is required)
        broadcast(url, method, isBroadcasted, Optional.of(data), Collections.emptyMap());
    }

    public void broadcast(String url, HttpMethod method, boolean isBroadcasted) {
        broadcast(url, method, isBroadcasted, Optional.empty(), Collections.emptyMap());
    }

    public void broadcast(String url, HttpMethod method, boolean isBroadcasted, Map<String, String> additionalHeaders) {
        broadcast(url, method, isBroadcasted, Optional.empty(), additionalHeaders);
    }
}