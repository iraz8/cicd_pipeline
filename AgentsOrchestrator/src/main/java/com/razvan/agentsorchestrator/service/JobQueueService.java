package com.razvan.agentsorchestrator.service;

import com.razvan.agentsorchestrator.model.Repository;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;

@Getter
@Service
public class JobQueueService {
    private final Queue<Repository> repositoryQueue = new LinkedList<>();

    public void addJob(Repository repository) {
        repositoryQueue.add(repository);
    }

}