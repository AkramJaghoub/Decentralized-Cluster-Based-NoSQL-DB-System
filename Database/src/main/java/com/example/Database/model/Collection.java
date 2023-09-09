package com.example.Database.model;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

public class Collection {
    private final ReentrantLock documentLock;
    private boolean hasAffinity;
    private int nodeWithAffinity;
    private final String collectionName;

    public Collection(String collectionName){
//        nodeWithAffinity=AffinityDistributor.getInstance().hasAffinity();
//        hasAffinity= ClusterManager.getInstance().getNodeNumber() == nodeWithAffinity;
        this.collectionName = collectionName;
        documentLock=new ReentrantLock();
    }

    public String getCollectionName() {
        return collectionName;
    }

    public ReentrantLock getDocumentLock(){
        return documentLock;
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
}