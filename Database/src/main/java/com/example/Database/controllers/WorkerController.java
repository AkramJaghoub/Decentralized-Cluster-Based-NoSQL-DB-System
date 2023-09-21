package com.example.Database.controllers;

import com.example.Database.affinity.AffinityManager;
import com.example.Database.services.AuthenticationService;
import com.example.Database.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class WorkerController {

    AuthenticationService authenticationService;

    AffinityManager affinityManager;

    UserService userService;

    @Autowired
    public WorkerController(AuthenticationService authenticationService, UserService userService, AffinityManager affinityManager){
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.affinityManager = affinityManager;
    }

    @GetMapping("/setCurrentWorkerName/{worker_name}")
    public ResponseEntity<String> setCurrentWorkerName(@PathVariable("worker_name") String workerName) {
        System.out.println("Received request to set worker name to: " + workerName);
        try {
            affinityManager.setCurrentWorkerPort(workerName);
            System.out.println(affinityManager.getCurrentWorkerPort());
            return new ResponseEntity<>("The current worker name is set to: " + workerName, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error setting worker name: " + e.getMessage());
            return new ResponseEntity<>("Error setting worker name: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}