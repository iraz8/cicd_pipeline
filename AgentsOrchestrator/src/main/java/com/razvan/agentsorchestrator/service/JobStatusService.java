package com.razvan.agentsorchestrator.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JobStatusService {

    private final Map<String, String> jobStatuses = new HashMap<>();

    public void updateJobStatus(String jobId, String status) {
        jobStatuses.put(jobId, status);
    }

    public String getJobStatus(Long projectId, String command, String jobId) {
        return jobStatuses.getOrDefault(jobId, "PENDING");
    }
}