package com.example.Database.schema;

import java.util.Map;
import java.util.HashMap;

public final class SchemaBuilder {

    private SchemaBuilder(){}
    public static Schema buildSchema() {
        Schema schema = new Schema();
        schema.setType("object");
        Map<String, String> properties = new HashMap<>();
        properties.put("customerNumber", DataTypes.LONG.getValue());
        properties.put("clientName", DataTypes.STRING.getValue());
        properties.put("accountNumber", DataTypes.LONG.getValue());
        properties.put("balance", DataTypes.DOUBLE.getValue());
        properties.put("hasInsurance", DataTypes.BOOLEAN.getValue());
        properties.put("accountType", DataTypes.STRING.getValue());
        schema.setProperties(properties);
        return schema;
    }
}