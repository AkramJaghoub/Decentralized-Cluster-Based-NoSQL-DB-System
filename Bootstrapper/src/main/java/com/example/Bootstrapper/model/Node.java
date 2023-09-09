package com.example.Bootstrapper.model;

import org.json.simple.JSONObject;

public class Node {
    private int nodeNumber;
    private JSONObject nodeJsonObject;
    private String nodeIP;
    private boolean isActive;


    public int getNodeNumber() {
        return nodeNumber;
    }

    public void setNodeNumber(int nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    public static Node of(JSONObject nodeObject) {
        Node node = new Node();
        node.nodeNumber = (int) ((long) nodeObject.get("nodeNumber"));
        node.nodeIP = (String) nodeObject.get("nodeIP");
        node.nodeJsonObject = nodeObject;
        return node;
    }

    public JSONObject getNodeJsonObject() {
        return nodeJsonObject;
    }

    public String getNodeIp() {
        return nodeIP;
    }

    public void setNodeIp(String nodeIP) {
        this.nodeIP = nodeIP;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nodeIP", nodeIP);
        jsonObject.put("nodeNumber", nodeNumber);
        return jsonObject;
    }
}