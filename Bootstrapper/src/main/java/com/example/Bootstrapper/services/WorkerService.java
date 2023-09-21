package com.example.Bootstrapper.services;

import com.example.Bootstrapper.loadbalancer.LoadBalancer;
import com.example.Bootstrapper.model.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkerService {

    @Autowired
    LoadBalancer loadBalancer;

    public String getWorker(String identity){
        Node node = loadBalancer.getUserNode(identity);
        if (node != null) {
            System.out.println(node.getNodeNumber() + " worker nodeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
            return String.valueOf(node.getNodeNumber());
        } else {
            return "Customer not found on any worker";
        }
    }
}
