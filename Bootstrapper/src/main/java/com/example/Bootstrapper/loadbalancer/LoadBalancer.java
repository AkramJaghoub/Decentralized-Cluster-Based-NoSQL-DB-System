package com.example.Bootstrapper.loadbalancer;

import com.example.Bootstrapper.model.Node;
import com.example.Bootstrapper.services.NodesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoadBalancer {

    @Autowired
    private NodesService nodesService;

    private final Map<Node, List<String>> nodeUsers;
    private int nextNodeIndex = 0;

    public LoadBalancer() {
        nodeUsers = new HashMap<>();
    }

    public Node assignUserToNextNode(String accountNumber) {
        Node node = getNextNode();
        if (!nodeUsers.containsKey(node)) {
            nodeUsers.put(node, new ArrayList<>());
        }
        nodeUsers.get(node).add(accountNumber);
        System.out.println("user is assigned to node " + node.getNodeNumber() + " and the ip address is " + node.getNodeIp());
        return node;
    }


    public synchronized Node getNextNode() {
        if (nodesService.getNodes().isEmpty()) {
            throw new IllegalStateException("No nodes available for balancing");
        }
        Node nextNode = nodesService.getNodes().get(nextNodeIndex);
        System.out.println(nextNodeIndex + " before updating");
        updateNextNodeIndex();
        System.out.println(nextNodeIndex + " after updating");
        return nextNode;
    }

    private void updateNextNodeIndex() {
        nextNodeIndex = (nextNodeIndex + 1) % nodesService.getNodes().size();
    }
}