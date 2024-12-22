package com.razvan.agentsorchestrator.service;

import com.razvan.agentsorchestrator.model.Agent;
import com.razvan.agentsorchestrator.model.Job;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Getter
@Service
public class JobExecutionService {
    private final AgentService agentService;

    @Autowired
    public JobExecutionService(AgentService agentService) {
        this.agentService = agentService;
    }

    public Boolean executeJob(Agent agent) {
        Job job = agent.getJob();
        return switch (job.getCommand()) {
            case RUN_TESTS -> {
                System.out.println("Run tests project ID: " + job.getProjectId());
                copyProjectToContainer(agent);
                yield runTests();
            }
            default -> {
                System.out.println("Unknown command: " + job.getCommand());
                yield false;
            }
        };
    }

    private boolean runTests() {
        return true;
    }

    private boolean copyProjectToContainer(Agent agent) {
        System.out.println("Copying project to container: " + agent.getContainerId());
        agentService.copyProjectToContainer(agent);
        return true;
    }
}