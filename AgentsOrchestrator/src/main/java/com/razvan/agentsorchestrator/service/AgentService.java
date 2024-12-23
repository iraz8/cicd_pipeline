package com.razvan.agentsorchestrator.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.exception.DockerClientException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.razvan.agentsorchestrator.model.Agent;
import com.razvan.agentsorchestrator.model.Project;
import com.razvan.agentsorchestrator.repository.ProjectRepository;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

@Service
public class AgentService {
    private static final String IMAGE_TAG = "iraz/common-languages";
    private final ProjectRepository projectRepository;

    public AgentService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public String startDockerContainer(Agent agent) {
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

            return createAndStartContainer(dockerClient, agent);

        } catch (DockerClientException | NotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private String createAndStartContainer(DockerClient dockerClient, Agent agent) throws InterruptedException {
        boolean containerCreated = false;
        while (!containerCreated) {
            try {
                CreateContainerResponse container = dockerClient.createContainerCmd(IMAGE_TAG)
                        .withName("common-languages-" + agent.getId())
                        .withExposedPorts(ExposedPort.tcp(8080 + agent.getId()))
                        .withPortBindings(PortBinding.parse(8080 + agent.getId() + ":8080"))
                        .exec();

                dockerClient.startContainerCmd(container.getId()).exec();
                System.out.println("Container started: " + container.getId());
                containerCreated = true;
                return container.getId();
            } catch (ConflictException e) {
                String containerName = "common-languages-" + agent.getId();
                String containerId = dockerClient.listContainersCmd()
                        .withNameFilter(Collections.singletonList(containerName))
                        .withShowAll(true)
                        .exec()
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Container not found"))
                        .getId();

                boolean isRunning = dockerClient.inspectContainerCmd(containerId).exec().getState().getRunning();
                if (isRunning) {
                    dockerClient.stopContainerCmd(containerId).exec();
                }
                dockerClient.removeContainerCmd(containerId).exec();
                System.out.println("Stopped and removed existing container: " + containerId);
            }
        }
        return null;
    }

    public void copyProjectToContainer(Agent agent) {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

        String containerId = agent.getContainerId();
        Long projectId = agent.getJob().getProjectId();
        Optional<Project> project = projectRepository.findById(projectId);
        String projectPath = "";
        if (project.isPresent()) {
            projectPath = "Projects/" + project.get().getName();
        } else {
            System.out.println("Project not found: " + containerId);
            return;
        }

        Path tempTarFile = null;
        try {
            tempTarFile = Files.createTempFile("project-", ".tar");
            createTarFile(projectPath, tempTarFile.toString());

            dockerClient.copyArchiveToContainerCmd(containerId)
                    .withHostResource(tempTarFile.toString())
                    .withRemotePath("/home/")
                    .exec();

            System.out.println("Project copied to container: " + containerId);

            uncompressTarArchive(dockerClient, containerId, tempTarFile);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tempTarFile != null) {
                try {
                    Files.delete(tempTarFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createTarFile(String sourceDirPath, String tarFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(tarFilePath);
             TarArchiveOutputStream tarOut = new TarArchiveOutputStream(fos)) {
            Path sourceDir = Paths.get(sourceDirPath);
            Path parentDir = sourceDir.getParent();
            Files.walk(sourceDir).forEach(path -> {
                File file = path.toFile();
                if (file.isFile()) {
                    TarArchiveEntry entry = new TarArchiveEntry(file, parentDir.relativize(path).toString());
                    try {
                        tarOut.putArchiveEntry(entry);
                        Files.copy(path, tarOut);
                        tarOut.closeArchiveEntry();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            tarOut.finish();
        }
    }

    private void uncompressTarArchive(DockerClient dockerClient, String containerId, Path tempTarFile) {
        try {
            String[] command = {"/bin/sh", "-c", "tar -xvf /home/" + tempTarFile.getFileName() + " -C /home"};

            String execId = dockerClient.execCreateCmd(containerId)
                    .withCmd(command)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec()
                    .getId();

            dockerClient.execStartCmd(execId).exec(new ResultCallback.Adapter<>() {
                @Override
                public void onNext(Frame item) {
                    System.out.println(new String(item.getPayload()));
                }
            });

            System.out.println("Uncompressed project archive inside container: " + containerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}