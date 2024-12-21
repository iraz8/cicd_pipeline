package com.razvan.agentsorchestrator.controller;

import com.razvan.agentsorchestrator.model.Project;
import com.razvan.agentsorchestrator.repository.ProjectRepository;
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
    private final ProjectRepository projectRepository;

    @Autowired
    public NewCommitNotifierController(JobQueueService jobQueueService, ProjectRepository projectRepository) {
        this.jobQueueService = jobQueueService;
        this.projectRepository = projectRepository;
    }

    @PostMapping("/new-commit")
    public void notifyNewCommit(@RequestBody Map<String, String> project) {
        String projectName = project.get("name");
        String projectUrl = project.get("url");
        Optional<Project> projectOptional = projectRepository.findByName(projectName);
        if (projectOptional.isPresent()) {
            Project projectDB = projectOptional.get();
        } else {
            System.out.println("Project with name " + projectName + " not found.");
        }
        System.out.println("Received repository " + projectName + " with URL: " + projectUrl);
    }
}