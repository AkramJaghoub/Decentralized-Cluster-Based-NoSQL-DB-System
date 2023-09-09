package com.example.Database.controllers;

import com.example.Database.affinity.AffinityManager;
import com.example.Database.services.AuthenticationService;
import com.example.Database.services.UserService;
import com.example.Database.services.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class WorkerController {

    @Autowired
    WorkerService workerService;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    AffinityManager affinityManager;

    @Autowired
    UserService userService;
    @GetMapping("/getWorker/{username}")
    public String getWorker(@PathVariable String username) {
       return workerService.getWorkerIdentity(username);
    }

    @GetMapping("/setCurrentWorkerName/{worker_name}")
    public ResponseEntity<String> setCurrentWorkerName(
            @PathVariable("worker_name") String workerName,
            @RequestHeader("username") String username,
            @RequestHeader("password") String password) {
             System.out.println("Received request to set worker name to: " + workerName + " with user: " + username);
        if (!"admin".equals(username) || !"admin@12345".equals(password)) {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
        try {
            affinityManager.setCurrentWorkerName(workerName);
            System.out.println(affinityManager.getCurrentWorkerName());
            return new ResponseEntity<>("The current worker name is set to: " + workerName, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error setting worker name: " + e.getMessage());
            return new ResponseEntity<>("Error setting worker name: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @GetMapping("/add/user")
//    public String verifyCredentials(@RequestHeader String username,
//                                    @RequestHeader String token) {
//        return authenticationService.verifyCredentials(username, token);
//    }

    @GetMapping("/add/user")
    public String addUser(@RequestHeader("adminUsername") String adminUsername,
                          @RequestHeader("adminPassword") String adminPassword,
                          @RequestHeader("accountNumber") String accountNumber,
                          @RequestHeader("password") String password) {
        if(!authenticationService.isAdmin(adminUsername, adminPassword)){
            return "User is not authorized";
        }
        return userService.addUser(accountNumber, password);
    }
}