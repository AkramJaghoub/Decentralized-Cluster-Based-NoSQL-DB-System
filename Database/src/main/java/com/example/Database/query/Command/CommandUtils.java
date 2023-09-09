package com.example.Database.query.Command;

import org.json.simple.JSONObject;
import com.example.Database.exceptions.*;
import java.util.Optional;

public class CommandUtils {
    public static String getDatabaseName(JSONObject commandJson) throws DatabaseNotFoundException {
        Optional<String> databaseName = Optional.ofNullable((String) commandJson.get("databaseName"));
        return databaseName.orElseThrow(DatabaseNotFoundException::new);
    }

    public static String getCollectionName(JSONObject commandJson) throws CollectionNotFoundException {
        Optional<String> collectionName = Optional.ofNullable((String) commandJson.get("collectionName"));
        return collectionName.orElseThrow(CollectionNotFoundException::new);
    }

    public static JSONObject getDocumentJson(JSONObject commandJson) throws DocumentNotFoundException {
        Optional<JSONObject> documentJson = Optional.ofNullable((JSONObject) commandJson.get("document"));
        return documentJson.orElseThrow(DocumentNotFoundException::new);
    }

    public static String getDocumentId(JSONObject commandJson) throws DocumentIdNotFoundException {
        Optional<String> documentId = Optional.ofNullable((String) commandJson.get("documentId"));
        return documentId.orElseThrow(DocumentIdNotFoundException::new);
    }

    public static String getPropertyName(JSONObject commandJson) throws PropertyNameNotFound {
        Optional<String> indexProperty = Optional.ofNullable((String) commandJson.get("propertyName"));
        return indexProperty.orElseThrow(PropertyNameNotFound::new);
    }

    public static String getNewValue(JSONObject commandJson) throws NewValueNotFound {
        Optional<String> indexProperty = Optional.ofNullable((String) commandJson.get("newValue"));
        return indexProperty.orElseThrow(NewValueNotFound::new);
    }

    public static JSONObject getIndexProperty(JSONObject commandJson) throws IndexPropertyNotFoundException {
        Optional<JSONObject> indexProperty = Optional.ofNullable((JSONObject) commandJson.get("indexProperty"));
        return indexProperty.orElseThrow(IndexPropertyNotFoundException::new);
    }

    public static JSONObject getSchemaJson(JSONObject commandJson) throws SchemaNotFoundException {
        Optional<JSONObject> schema = Optional.ofNullable((JSONObject) commandJson.get("schema"));
        return schema.orElseThrow(SchemaNotFoundException::new);
    }
}
