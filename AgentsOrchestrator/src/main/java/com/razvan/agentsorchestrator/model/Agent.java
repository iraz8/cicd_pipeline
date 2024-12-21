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
    }
}