package com.razvan.agentsorchestrator.service;

import com.razvan.agentsorchestrator.model.Commands;
import com.razvan.agentsorchestrator.model.Job;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CommandsService {

    private final JobQueueService jobQueueService;

    public CommandsService(JobQueueService jobQueueService) {
        this.jobQueueService = jobQueueService;
    }

    public Map<String, Object> executeCommand(Long projectId, String commandStr) {
        Commands command = Commands.valueOf(commandStr);
        System.out.println("Running commands for project ID: " + projectId + " with command: " + command);

        Map<String, Object> response = new HashMap<>();
        response.put("projectId", projectId);
        response.put("command", command);

        Job job = new Job(projectId, command);
        jobQueueService.addJob(job);

        return response;
    }
}