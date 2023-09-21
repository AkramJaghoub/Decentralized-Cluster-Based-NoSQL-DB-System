package com.example.Bootstrapper.services.network;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BootstrapperRunner implements CommandLineRunner {

    @Autowired
    private NetworkService networkService;

    @Override
    public void run(String... args) {
        System.out.println("initializing nodes.................");
        networkService.run();
    }
}