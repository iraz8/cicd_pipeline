package com.razvan.agentsorchestrator.service;

import com.razvan.agentsorchestrator.model.Agent;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AgentsOrchestrationService {

    private final List<Agent> agents;
    private final AgentService agentService;

    @Autowired
    public AgentsOrchestrationService(@Value("${agents.number}") int agentsNumber, AgentService agentService) {
        this.agents = new ArrayList<>();
        this.agentService = agentService;
        for (int i = 0; i < agentsNumber; i++) {
            agents.add(new Agent(i, "Agent_" + i));
        }
    }

    @PostConstruct
    public void initializeAgents() {
        for (Agent agent : agents) {
            agentService.startDockerContainer(agent);
        }
    }

    public void updateAgentsNumber(int newNumber) {
        int currentNumber = agents.size();
        if (newNumber > currentNumber) {
            for (int i = currentNumber; i < newNumber; i++) {
                agents.add(new Agent(i, "Agent_" + i));
            }
        } else if (newNumber < currentNumber) {
            for (int i = currentNumber - 1; i >= newNumber; i--) {
                agents.remove(i);
            }
        }
        System.out.println("Updated agents number to: " + newNumber);
    }
}