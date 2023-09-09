package com.example.Database.schema;

import com.example.Database.file.FileService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Set;

public class SchemaValidator {

    public boolean schemaValidator(String collectionName, String json) {
        try {
            JSONObject schema = FileService.readSchema(collectionName);
            JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.getInstance();
            JsonSchema jsonSchema = jsonSchemaFactory.getSchema(schema.toString());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            Set<ValidationMessage> validationMessageSet = jsonSchema.validate(jsonNode);
            return validationMessageSet.isEmpty();
        } catch (IOException e) {
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }
    }

}