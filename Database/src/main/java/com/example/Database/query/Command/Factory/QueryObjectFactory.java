package com.example.Database.query.Command.Factory;

import com.example.Database.query.Command.QueryCommand;
import com.example.Database.query.QueryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class QueryObjectFactory {

    private final Map<QueryType, QueryCommand> queryMap = new HashMap<>();

    @Autowired
    public QueryObjectFactory(List<QueryCommand> commandList) {
        for (QueryCommand command : commandList) {
            queryMap.put(command.getQueryType(), command);
        }
    }

    public Map<QueryType, QueryCommand> queryMap() {
        return Collections.unmodifiableMap(queryMap);
    }
}