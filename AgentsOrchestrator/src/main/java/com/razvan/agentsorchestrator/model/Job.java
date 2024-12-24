package com.razvan.agentsorchestrator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Job {
    private Long projectId;
    private Commands command;
    private String jobId;
}
