package com.razvan.agentsorchestrator.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Job {
    private String name;
    private String url;

    public Job(String name, String url) {
        this.name = name;
        this.url = url;
    }

}