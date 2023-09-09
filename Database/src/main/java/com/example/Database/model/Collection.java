package com.example.Database.model;

import com.example.Database.affinity.AffinityStatus;
import com.example.Database.index.Index;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Collection {
    private final ReentrantLock documentLock;
    private AffinityStatus affinityStatus;

    public ReentrantLock getDocumentLock(){
        return documentLock;
    }
    public Collection() {
//        this.affinityStatus = AffinityHandler.getInstance().getAffinityStatus();
        this.documentLock = new ReentrantLock();
    }

    // Placeholder for other methods to manage data and indices...

    public AffinityStatus getAffinityStatus() {
        return affinityStatus;
    }

    public void updateAffinityStatus(AffinityStatus status) {
        this.affinityStatus = status;
    }
}
