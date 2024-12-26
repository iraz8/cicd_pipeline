package com.razvan.agentsorchestrator.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JobStatusService {

    private final Map<String, String> jobStatuses = new HashMap<>();
    private final Map<String, String> jobErrors = new HashMap<>();
    private final Map<String, String> jobOutput = new HashMap<>();

    public void updateJobStatus(String jobId, String status, String errors, String output) {
        jobStatuses.put(jobId, status);
        jobErrors.put(jobId, errors);
        jobOutput.put(jobId, output);
    }

    public String getJobStatus(String jobId) {
        return jobStatuses.getOrDefault(jobId, "PENDING");
    }

    public String getJobErrors(String jobId) {
        return jobErrors.getOrDefault(jobId, null);
    }

    public String getJobOutput(String jobId) {
        return jobOutput.getOrDefault(jobId, null);
    }
}