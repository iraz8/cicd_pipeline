package com.razvan.agentsorchestrator.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.exception.ConflictException;
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
import java.util.Collections;
import java.util.List;

@Service
public class AgentService {
    private static final String IMAGE_TAG = "iraz/common-languages";

    public void startDockerContainer(Agent agent) {
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
            dockerClient.pullImageCmd(IMAGE_TAG)
                    .exec(new PullImageResultCallback())
                    .awaitCompletion();

            createAndStartContainer(dockerClient, agent);

        } catch (DockerClientException | NotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void createAndStartContainer(DockerClient dockerClient, Agent agent) throws InterruptedException {
        try {
            CreateContainerResponse container = dockerClient.createContainerCmd(IMAGE_TAG)
                    .withName("common-languages-" + agent.getId())
                    .withExposedPorts(ExposedPort.tcp(8080 + agent.getId()))
                    .withPortBindings(PortBinding.parse(8080 + agent.getId() + ":8080"))
                    .exec();

            dockerClient.startContainerCmd(container.getId()).exec();
            System.out.println("Container started: " + container.getId());

        } catch (ConflictException e) {
            String containerName = "common-languages-" + agent.getId();
            String containerId = dockerClient.listContainersCmd()
                    .withNameFilter(List.of(containerName))
                    .withShowAll(true)
                    .exec()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Container not found"))
                    .getId();

            dockerClient.stopContainerCmd(containerId).exec();
            dockerClient.removeContainerCmd(containerId).exec();
            System.out.println("Stopped and removed existing container: " + containerId);

            createAndStartContainer(dockerClient, agent);
        }
    }
}