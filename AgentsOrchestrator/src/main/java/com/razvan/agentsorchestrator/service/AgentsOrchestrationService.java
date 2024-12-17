package com.razvan.agentsorchestrator.service;

import com.razvan.agentsorchestrator.model.Agent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AgentsOrchestrationService {

    private final List<Agent> agents;

    public AgentsOrchestrationService(@Value("${agents.number}") int agentsNumber) {
        this.agents = new ArrayList<>();
        for (int i = 0; i < agentsNumber; i++) {
            agents.add(new Agent());
        }
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public void updateAgentsNumber(int newNumber) {
        int currentNumber = agents.size();
        if (newNumber > currentNumber) {
            for (int i = currentNumber; i < newNumber; i++) {
                agents.add(new Agent());
            }
        } else if (newNumber < currentNumber) {
            for (int i = currentNumber - 1; i >= newNumber; i--) {
                agents.remove(i);
            }
        }
        System.out.println("Updated agents number to: " + newNumber);
    }
}