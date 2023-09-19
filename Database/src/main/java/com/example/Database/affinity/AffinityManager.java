package com.example.Database.affinity;

import org.springframework.stereotype.Service;

@Service
public class AffinityManager {
    private String currentWorkerName;
    private int currentWorkerNumber;
    private static final int NUMBER_OF_NODES = 4;

    public int getNumberOfNodes(){
        return NUMBER_OF_NODES;
    }

    public int getCurrentWorkerPort() {
        return currentWorkerNumber;
    }

    public synchronized void setCurrentWorkerPort(String currentWorkerName) {
        this.currentWorkerName = currentWorkerName;
        System.out.println(currentWorkerName);
        try {
            this.currentWorkerNumber = Integer.parseInt(currentWorkerName.trim().replace("worker", ""));
            System.out.println(currentWorkerNumber);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid worker name format.");
        }
    }

    public int getNextWorker() {
        int nextWorkerNumber = (currentWorkerNumber % NUMBER_OF_NODES) + 1;
        System.out.println("workerrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr " + nextWorkerNumber);
        return nextWorkerNumber;
    }

    public int getWorkerPort(String documentId) {
        int index = calculateWorkerIndex(documentId);
        return index + 1;
    }

    public int calculateWorkerIndex(String documentId) {
        int hashCode = documentId.hashCode();
        return Math.abs(hashCode) % NUMBER_OF_NODES;
    }



//    public String documentAffinity(String db_name , String collection_name,
//                                   String json){
//        String port = "w" + getValue();
//        return broadcast.buildDocument(db_name,collection_name,json,port);
//    }
//
//    public String deleteDocumentAffinity(String db_name, String collection_name, String value){
//        String port = "w" + getValue();
//        return broadcast.deleteDocument(db_name,collection_name,value,port);
//    }
//
//    public String updateDocumentAffinity(String db_name, String collection_name, String id, String prop, String value){
//        String port = "w" + getValue();
//        return broadcast.updateDocument(db_name,collection_name,id,prop,value,port);
//    }
}

