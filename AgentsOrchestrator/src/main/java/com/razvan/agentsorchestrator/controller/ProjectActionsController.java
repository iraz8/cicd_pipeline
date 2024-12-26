package com.razvan.agentsorchestrator.controller;

import com.razvan.agentsorchestrator.service.CommandsService;
import com.razvan.agentsorchestrator.service.ProjectSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class ProjectActionsController {
    private final CommandsService commandsService;
    private final ProjectSettingsService projectSettingsService;

    @Autowired
    public ProjectActionsController(CommandsService commandsService, ProjectSettingsService projectSettingsService) {
        this.commandsService = commandsService;
        this.projectSettingsService = projectSettingsService;
    }

    @GetMapping("/projectActions")
    public String projectActions(Model model) {
        model.addAttribute("projects", projectSettingsService.getActiveProjects());
        return "projectActions";
    }

    @PostMapping("/runCommand/{projectId}")
    @ResponseBody
    public Map<String, Object> runCommand(@PathVariable Long projectId, @RequestBody Map<String, String> request) {
        String commandStr = request.get("command");
        String jobId = request.get("jobId");
        return commandsService.executeCommand(projectId, commandStr, jobId);
    }
}


