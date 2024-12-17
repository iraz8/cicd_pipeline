package com.razvan.agentsorchestrator.controller;

import com.razvan.agentsorchestrator.service.AgentsOrchestrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SettingsController {

    private final AgentsOrchestrationService agentsOrchestrationService;

    @Autowired
    public SettingsController(AgentsOrchestrationService agentsOrchestrationService) {
        this.agentsOrchestrationService = agentsOrchestrationService;
    }

    @GetMapping("/orchestratorSettings")
    public String showSettingsForm(Model model) {
        return "orchestratorSettings";
    }

    @PostMapping("/updateSettings")
    public String updateAgentsNumber(@RequestParam("agentsNumber") int agentsNumber) {
        agentsOrchestrationService.updateAgentsNumber(agentsNumber);
        return "redirect:/orchestratorSettings";
    }
}