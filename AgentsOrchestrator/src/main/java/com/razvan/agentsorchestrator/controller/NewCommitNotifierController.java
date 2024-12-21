package com.razvan.agentsorchestrator.controller;

import com.razvan.agentsorchestrator.model.Repository;
import com.razvan.agentsorchestrator.repository.IRepository;
import com.razvan.agentsorchestrator.service.JobQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
public class NewCommitNotifierController {

    private final JobQueueService jobQueueService;
    private final IRepository iRepository;

    @Autowired
    public NewCommitNotifierController(JobQueueService jobQueueService, IRepository iRepository) {
        this.jobQueueService = jobQueueService;
        this.iRepository = iRepository;
    }

    @PostMapping("/new-commit")
    public void notifyNewCommit(@RequestBody Map<String, String> repository) {
        String repoName = repository.get("name");
        String repoUrl = repository.get("url");
        Optional<Repository> repositoryOptional = iRepository.findByName(repoName);
        if (repositoryOptional.isPresent()) {
            Repository repositoryDB = repositoryOptional.get();
        } else {
            System.out.println("Repository with name " + repoName + " not found.");
        }
        System.out.println("Received repository " + repoName + " with URL: " + repoUrl);
    }
}