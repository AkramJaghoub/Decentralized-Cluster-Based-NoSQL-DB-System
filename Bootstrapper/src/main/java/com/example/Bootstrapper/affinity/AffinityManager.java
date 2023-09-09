package com.example.Bootstrapper.affinity;

import org.springframework.stereotype.Service;

@Service
public class AffinityManager {
    private String currentWorkerName;
    public String getCurrentWorkerName() {
        return currentWorkerName;
    }

    public void setCurrentWorkerName(String currentWorkerName) {
        this.currentWorkerName = currentWorkerName;
    }
}
