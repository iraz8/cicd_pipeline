package com.razvan.agentsorchestrator.controller;

import com.razvan.agentsorchestrator.service.CommandsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/runCommand")
public class CommandsController {

    private final CommandsService commandsService;

    public CommandsController(CommandsService commandsService) {
        this.commandsService = commandsService;
    }

    @PostMapping("/{projectId}")
    public Map<String, Object> runCommand(@PathVariable Long projectId, @RequestBody Map<String, String> request) {
        String commandStr = request.get("command");
        String jobId = request.get("jobId");
        return commandsService.executeCommand(projectId, commandStr, jobId);
    }
}