package com.example.Database.schema;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.Map;

@SuppressWarnings("unchecked")
public class Schema {
    private String type;
    private Map<String, String> properties;
    private String[] required;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public String[] getRequired() {
        return required;
    }

    public void setRequired(String[] required) {
        this.required = required;
    }

    public JSONObject getSchemaAsJSON() {
        JSONObject jsonSchema = new JSONObject();
        jsonSchema.put("type", getType());
        JSONObject props = new JSONObject();
        props.putAll(getProperties());
        jsonSchema.put("properties", props);
        JSONArray reqArray = new JSONArray();
        Collections.addAll(reqArray, getRequired());
        jsonSchema.put("required", reqArray);
        return jsonSchema;
    }
}