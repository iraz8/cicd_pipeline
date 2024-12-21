package com.razvan.agentsorchestrator.controller;

import com.razvan.agentsorchestrator.service.ProjectSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class ProjectSettingsController {

    private final ProjectSettingsService projectSettingsService;

    @Autowired
    public ProjectSettingsController(ProjectSettingsService projectSettingsService) {
        this.projectSettingsService = projectSettingsService;
    }

    @GetMapping("/projectSettings")
    public String showProjectSettings(Model model) {
        model.addAttribute("projects", projectSettingsService.getAllProjects());
        return "projectSettings";
    }

    @PostMapping("/saveProjectSettings")
    public String saveProjectSettings(@RequestParam Map<String, String> params) {
        projectSettingsService.saveProjectSettings(params);
        return "redirect:/projectSettings";
    }
}