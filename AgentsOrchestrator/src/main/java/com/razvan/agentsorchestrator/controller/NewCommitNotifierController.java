package com.razvan.agentsorchestrator.controller;

import com.razvan.agentsorchestrator.model.Commands;
import com.razvan.agentsorchestrator.model.Project;
import com.razvan.agentsorchestrator.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@RestController
public class NewCommitNotifierController {

    private final ProjectRepository projectRepository;
    private final String baseUrl;

    @Autowired
    public NewCommitNotifierController(ProjectRepository projectRepository, @Value("${base.url}") String baseUrl) {
        this.projectRepository = projectRepository;
        this.baseUrl = baseUrl;
    }

    @PostMapping("/new-commit")
    public void notifyNewCommit(@RequestBody Map<String, String> project) {
        String projectName = project.get("name");
        String projectUrl = project.get("url");
        Optional<Project> projectOptional = projectRepository.findByName(projectName);
        if (projectOptional.isPresent()) {
            Project projectDB = projectOptional.get();
            Long projectId = projectDB.getId();
            String jobId = java.util.UUID.randomUUID().toString();
            String command = Commands.FULL_STEPS.toString();

            RestTemplate restTemplate = new RestTemplate();
            String url = baseUrl + "/runCommand/" + projectId;
            Map<String, String> request = Map.of("command", command, "jobId", jobId);
            restTemplate.postForEntity(url, request, Map.class);
        } else {
            System.out.println("Project with name " + projectName + " not found.");
        }
        System.out.println("Received repository " + projectName + " with URL: " + projectUrl);
    }
}