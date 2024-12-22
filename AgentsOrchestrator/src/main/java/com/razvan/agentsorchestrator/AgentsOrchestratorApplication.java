package com.razvan.agentsorchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AgentsOrchestratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentsOrchestratorApplication.class, args);
    }

}
