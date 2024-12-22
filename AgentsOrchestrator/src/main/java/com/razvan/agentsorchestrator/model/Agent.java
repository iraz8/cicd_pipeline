package com.razvan.agentsorchestrator.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Agent {
    private Integer id;
    private String name;
    private Job job;

    public Agent(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.job = null;
    }

    public Boolean hasJobAssigned() {
        return this.job != null;
    }

    public void setJob(Job job) {
        this.job = job;
        System.out.println("Job assigned to agent " + name + ": " + job);
    }

}