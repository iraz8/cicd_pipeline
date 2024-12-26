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
            case FETCH -> {
                System.out.println("Fetch project ID: " + job.getProjectId());
                agentService.copyProjectToContainer(agent);
                yield true;
            }
            case CLEAN -> {
                System.out.println("Clean project ID: " + job.getProjectId());
                agentService.cleanProjectInContainer(agent);
                yield true;
            }
            case BUILD -> {
                System.out.println("Build project ID: " + job.getProjectId());
                agentService.buildProjectInContainer(agent);
                yield true;
            }
            case RUN_TESTS -> {
                System.out.println("Run tests project ID: " + job.getProjectId());
                agentService.runTestsInContainer(agent);
                yield true;
            }
            case LAST_OUTPUT -> {
                System.out.println("Read output project ID: " + job.getProjectId());
                agentService.readOutput(agent);
                yield true;
            }
            case FULL_STEPS -> {
                System.out.println("Full steps project ID: " + job.getProjectId());
                agentService.cleanProjectInContainer(agent);
                agent.getJob().setErrors(null);
                agentService.copyProjectToContainer(agent);
                agentService.buildProjectInContainer(agent);
                agentService.runTestsInContainer(agent);
                agentService.readOutput(agent);
                yield true;
            }
            default -> {
                System.out.println("Unknown command: " + job.getCommand());
                yield false;
            }
        };
    }
}