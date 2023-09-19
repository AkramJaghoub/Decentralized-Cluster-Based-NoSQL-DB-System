package com.example.Database.model;

import com.example.Database.schema.SchemaValidator;
import org.json.simple.JSONObject;

public class Document {

    private String id;
    private JSONObject data;
    private String propertyName;
    private Object propertyValue;
    private boolean hasAffinity;
    private int nodeWithAffinity;
    private boolean replication;


    public Document(JSONObject data) {
        this.data = data;
    }


    public Document(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public JSONObject getData() {
        return data;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Object getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(Object propertyValue) {
        this.propertyValue = propertyValue;
    }


    public boolean hasAffinity() {
        return hasAffinity;
    }

    public void setHasAffinity(boolean hasAffinity) {
        this.hasAffinity = hasAffinity;
    }

    public int getNodeWithAffinity() {
        return nodeWithAffinity;
    }

    public void setNodeWithAffinity(int nodeWithAffinity) {
        this.nodeWithAffinity = nodeWithAffinity;
    }

    public boolean isReplicated(){
        return replication;
    }

    public void setReplication(boolean replication){
        this.replication = replication;
    }

    public boolean isValidDocument(SchemaValidator validator, String collectionName) {
        return validator.schemaValidator(collectionName, this.data.toJSONString());
    }
}