package com.razvan.agentsorchestrator.service;

import com.razvan.agentsorchestrator.model.Job;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;

@Getter
@Service
public class JobQueueService {
    private final Queue<Job> projectQueue = new LinkedList<>();

    public void addJob(Job job) {
        projectQueue.add(job);
    }
}