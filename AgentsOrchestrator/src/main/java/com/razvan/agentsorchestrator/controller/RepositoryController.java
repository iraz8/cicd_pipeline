package com.razvan.agentsorchestrator.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RepositoryController {

    @PostMapping("/repository")
    public void receiveRepository(@RequestBody Map<String, String> repository) {
        String name = repository.get("name");
        String url = repository.get("url");
        System.out.println("Received repository " + name + " with URL: " + url);
    }
}