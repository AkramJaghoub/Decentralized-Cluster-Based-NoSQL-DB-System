package com.example.Database.model;

import com.example.Database.schema.SchemaValidator;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
public class Document {

    private String id;
    private JSONObject data;
    private String propertyName;
    private Object propertyValue;
    private boolean hasAffinity;
    private int nodeWithAffinity;
    private boolean replicated;
    private long version;

    public Document(JSONObject data) {
        this.data = data;
        if (data.containsKey("_version")) {
            this.version = Integer.parseInt(data.get("_version").toString());
        }
    }

    public Document(String id) {
        this.id = id;
    }

    public boolean isValidDocument(SchemaValidator validator, String collectionName) {
        return validator.schemaValidator(collectionName, this.data.toJSONString());
    }
}