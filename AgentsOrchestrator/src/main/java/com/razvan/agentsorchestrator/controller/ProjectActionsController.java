package com.razvan.agentsorchestrator.controller;

import com.razvan.agentsorchestrator.model.Commands;
import com.razvan.agentsorchestrator.service.CommandsService;
import com.razvan.agentsorchestrator.service.ProjectSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
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
        if (projectSettingsService.getActiveProjects().isEmpty()) {
            return "errorPage";
        }
        model.addAttribute("projects", projectSettingsService.getActiveProjects());
        return "projectActions";
    }

    @PostMapping("/runCommand/{projectId}")
    public ResponseEntity<Map<String, Object>> runCommand(@PathVariable Long projectId, @RequestBody Map<String, String> request) {
        String commandStr = request.get("command");
        String jobId = request.get("jobId");
        try {
            Map<String, Object> result = commandsService.executeCommand(projectId, commandStr, jobId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
    }

    @GetMapping("/commands")
    public List<String> getCommands() {
        return Arrays.stream(Commands.values())
                .map(Enum::name)
                .toList();
    }
}

