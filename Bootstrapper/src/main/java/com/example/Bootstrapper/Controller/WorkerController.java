package com.example.Bootstrapper.Controller;

import com.example.Bootstrapper.services.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/bootstrapper")
public class WorkerController {

    @Autowired
    WorkerService workerService;

    @GetMapping("/getWorker/{identity}")
    public String getWorker(@PathVariable String identity) {
        String workerPort = workerService.getWorker(identity);
        System.out.println(identity + " is in worker: " + workerPort);
        return Objects.requireNonNullElse(workerPort, "could not find worker port");
    }
}
