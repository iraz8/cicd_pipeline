package com.razvan.agentsorchestrator.service;

import com.razvan.agentsorchestrator.model.Project;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;

@Getter
@Service
public class JobQueueService {
    private final Queue<Project> projectQueue = new LinkedList<>();

    public void addJob(Project repository) {
        projectQueue.add(repository);
    }

}