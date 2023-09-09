package com.example.Bootstrapper.services;

import com.example.Bootstrapper.model.Node;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class NodesService {
    private final List<Node> nodes;

    public NodesService() {
        nodes = new ArrayList<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public Node getNode(int nodeNumber) {
        return nodes.get(nodeNumber - 1);
    }
}