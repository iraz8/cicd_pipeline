package com.razvan.agentsorchestrator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class Job {
    private Long projectId;
    private Commands command;
    private String jobId;
    @Setter
    private String errors;
    @Setter
    private String output;
}
