package com.razvan.agentsorchestrator.controller;

import com.razvan.agentsorchestrator.model.Job;
import com.razvan.agentsorchestrator.service.JobQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RepositoryController {

    private final JobQueueService jobQueueService;

    @Autowired
    public RepositoryController(JobQueueService jobQueueService) {
        this.jobQueueService = jobQueueService;
    }

    @PostMapping("/repository")
    public void receiveRepository(@RequestBody Map<String, String> repository) {
        String name = repository.get("name");
        String url = repository.get("url");
        Job job = new Job(name, url);
        jobQueueService.addJob(job);
        System.out.println("Received repository " + name + " with URL: " + url);
    }
}