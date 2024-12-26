package com.razvan.agentsorchestrator.service;

import com.razvan.agentsorchestrator.model.Agent;
import com.razvan.agentsorchestrator.model.Job;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AgentsOrchestrationService {

    private final List<Agent> agents;
    private final AgentService agentService;
    private final JobQueueService jobQueueService;
    private final JobExecutionService jobExecutionService;
    private final JobStatusService jobStatusService;

    @Autowired
    public AgentsOrchestrationService(@Value("${agents.number}") int agentsNumber, AgentService agentService, JobQueueService jobQueueService, JobExecutionService jobExecutionService, JobStatusService jobStatusService) {
        this.agents = new ArrayList<>();
        this.agentService = agentService;
        this.jobQueueService = jobQueueService;
        this.jobExecutionService = jobExecutionService;
        this.jobStatusService = jobStatusService;
        for (int i = 0; i < agentsNumber; i++) {
            agents.add(new Agent(i, "Agent_" + i));
        }
    }

    @PostConstruct
    public void initializeAgents() {
        for (Agent agent : agents) {
            agent.setContainerId(agentService.startDockerContainer(agent));
        }
    }

    public void updateAgentsNumber(int newNumber) {
        int currentNumber = agents.size();
        if (newNumber > currentNumber) {
            for (int i = currentNumber; i < newNumber; i++) {
                agents.add(new Agent(i, "Agent_" + i));
            }
        } else if (newNumber < currentNumber) {
            agents.subList(newNumber, currentNumber).clear();
        }
        System.out.println("Updated agents number to: " + newNumber);
    }

    @Scheduled(fixedRate = 10000)
    public void assignJobsToAgents() {
        for (Agent agent : agents) {
            if (!agent.hasJobAssigned() && !jobQueueService.getProjectQueue().isEmpty()) {
                Job job = jobQueueService.getProjectQueue().poll();
                if (job != null) {
                    agent.setJob(job);
                    System.out.println("Assigned job to agent: " + agent.getName());
                    boolean result = jobExecutionService.executeJob(agent);
                    System.out.println("Job execution result for agent " + agent.getName() + ": " + result);
                    jobStatusService.updateJobStatus(job.getJobId(), result ? "COMPLETED" : "FAILED", job.getErrors());
                    agent.setJob(null);
                }
            }
        }
    }
}