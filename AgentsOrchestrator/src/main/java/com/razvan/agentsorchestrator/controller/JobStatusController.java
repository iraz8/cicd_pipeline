package com.razvan.agentsorchestrator.controller;

import com.razvan.agentsorchestrator.service.JobStatusService;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/jobStatus")
public class JobStatusController {

    private final JobStatusService jobStatusService;

    public JobStatusController(JobStatusService jobStatusService) {
        this.jobStatusService = jobStatusService;
    }

    @GetMapping("/{projectId}/{command}/{jobId}")
    public Map<String, String> getJobStatus(@PathVariable String jobId) {
        String status = jobStatusService.getJobStatus(jobId);
        String errors = jobStatusService.getJobErrors(jobId);
        Map<String, String> response = new HashMap<>();
        response.put("status", status);
        response.put("errors", errors);
        return response;
    }
}