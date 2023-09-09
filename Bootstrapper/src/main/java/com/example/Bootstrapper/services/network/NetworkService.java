package com.example.Bootstrapper.services.network;

import com.example.Bootstrapper.model.Node;
import com.example.Bootstrapper.services.NodesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NetworkService {

    @Autowired
    private NodesService nodesService;

    public void run() {
        createNetwork();
        checkClusterStatus();
    }

    private void createNetwork() {
        for (int i = 1; i <= 4; i++) {
            Node node = new Node();
            node.setNodeNumber(i);
            node.setNodeIp("192.168.1.10" + i);
            nodesService.addNode(node);
        }
        setUpWorkersNames();
    }

    private void setUpWorkersNames() {
        for (Node node : nodesService.getNodes()) {
            System.out.println(node.getNodeIp());
            try {
                String workerName = "worker" + node.getNodeNumber();
                String url = "http://" + node.getNodeIp() + ":9000/api/setCurrentWorkerName/" + workerName;
                HttpHeaders headers = new HttpHeaders();
                headers.set("username", "admin");
                headers.set("password", "admin@12345");
                HttpEntity<String> requestEntity = new HttpEntity<>(headers);
                RestTemplate restTemplate = new RestTemplate();
                SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
                requestFactory.setConnectTimeout(5000);  // 5 seconds
                requestFactory.setReadTimeout(5000);     // 5 seconds
                restTemplate.setRequestFactory(requestFactory);
                System.out.println("sending request to add worker with " + node.getNodeIp());
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
                if (response.getStatusCode() == HttpStatus.OK) {
                    System.out.println("Successfully set worker name for node: " + node.getNodeIp());
                    node.setActive(true);
                } else {
                    System.out.println("Failed to set worker name for node: " + node.getNodeIp() + ". HTTP Status: " + response.getStatusCode());
                }
            } catch (Exception e) {
                System.out.println("Error setting up worker name for node: " + node.getNodeIp());
                e.printStackTrace();
            }
        }
    }

    private void checkClusterStatus() {
        for (Node node : nodesService.getNodes()) {
            if (node.isActive()) {
                System.out.println("Node " + node.getNodeNumber() + " is active.");
            } else {
                System.out.println("Node " + node.getNodeNumber() + " is not active.");
            }
        }
    }
}