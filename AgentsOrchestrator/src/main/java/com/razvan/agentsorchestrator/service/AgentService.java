package com.razvan.agentsorchestrator.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.exception.DockerClientException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.razvan.agentsorchestrator.model.Agent;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AgentService {

    public void startDockerContainer(Agent agent) {
        String imageTag = "iraz/common-languages";

        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

        try {
            dockerClient.pullImageCmd(imageTag)
                    .exec(new PullImageResultCallback())
                    .awaitCompletion();


            CreateContainerResponse container = dockerClient.createContainerCmd(imageTag)
                    .withName("common-languages-" + agent.getId())
                    .withExposedPorts(ExposedPort.tcp(8080 + agent.getId()))
                    .withPortBindings(PortBinding.parse(8080 + agent.getId() + ":8080"))
                    .exec();

            dockerClient.startContainerCmd(container.getId()).exec();
            System.out.println("Container started: " + container.getId());

        } catch (DockerClientException | NotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}