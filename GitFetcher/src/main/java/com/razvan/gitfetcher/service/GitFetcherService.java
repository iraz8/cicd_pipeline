package com.razvan.gitfetcher.service;

import com.razvan.gitfetcher.model.Project;
import com.razvan.gitfetcher.repository.ProjectRepository;
import com.razvan.gitfetcher.util.UrlUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevWalk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GitFetcherService {

    private final Map<String, String> repoLastCommitMap = new HashMap<>();
    private final ProjectRepository projectRepository;
    private final String orchestratorUrl;
    private final String orchestratorNewCommitNotifierPath;

    public GitFetcherService(ProjectRepository projectRepository,
                             @Value("${agents.orchestrator.url}") String orchestratorUrl,
                             @Value("${agents.orchestrator.path.new-commit}") String orchestratorNewCommitNotifierPath) {
        this.projectRepository = projectRepository;
        this.orchestratorUrl = orchestratorUrl;
        this.orchestratorNewCommitNotifierPath = orchestratorNewCommitNotifierPath;
        initializeRepoLastCommitMap();
    }
    private void initializeRepoLastCommitMap() {
        List<Project> repositories = projectRepository.findAll();
        for (Project repo : repositories) {
            repoLastCommitMap.put(repo.getUrl(), null);
        }
    }

    public void addGitUrl(String gitUrl) {
        repoLastCommitMap.put(gitUrl, null);
    }

    @Scheduled(fixedRate = 15000)
    public void checkForUpdates() {
        for (String gitUrl : repoLastCommitMap.keySet()) {
            var newCommitHash = fetchLatestCommitHash(gitUrl);
            var lastCommitHash = repoLastCommitMap.get(gitUrl);
            if (newCommitHash != null && !newCommitHash.equals(lastCommitHash)) {
                System.out.println("New update found for repo: " + gitUrl);
                repoLastCommitMap.put(gitUrl, newCommitHash);
                String repoName = extractRepoName(gitUrl);
                notifyAgentsOrchestrator(repoName, gitUrl);
            } else {
                System.out.println("No new updates found for repo: " + gitUrl);
            }
        }
    }

    private void notifyAgentsOrchestrator(String name, String url) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("url", url);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        String fullUrl = UrlUtils.buildUrl(orchestratorUrl, orchestratorNewCommitNotifierPath);

        int maxRetries = 5;
        int retryCount = 0;
        boolean success = false;

        while (retryCount < maxRetries && !success) {
            try {
                restTemplate.postForEntity(fullUrl, request, String.class);
                success = true;
            } catch (Exception e) {
                retryCount++;
                if (retryCount < maxRetries) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Thread was interrupted", ie);
                    }
                }
            }
        }
    }

    private String fetchLatestCommitHash(String gitUrl) {
        String repoName = extractRepoName(gitUrl);
        var repoDir = Paths.get("Projects", repoName);
        try {
            if (Files.exists(repoDir)) {
                return fetchFromExistingRepo(gitUrl, repoDir);
            } else {
                return cloneNewRepo(gitUrl, repoDir);
            }
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String fetchFromExistingRepo(String gitUrl, Path repoDir) throws IOException, GitAPIException {
        if (!Files.exists(repoDir)) {
            try (var git = Git.cloneRepository()
                    .setURI(gitUrl)
                    .setDirectory(repoDir.toFile())
                    .call()) {
                return git.getRepository().findRef("HEAD").getObjectId().getName();
            }
        } else {
            try (var git = Git.open(repoDir.toFile())) {
                git.pull().call();
                return git.getRepository().findRef("HEAD").getObjectId().getName();
            }
        }
    }

    private String cloneNewRepo(String gitUrl, Path repoDir) throws GitAPIException, IOException {
        Files.createDirectories(repoDir);
        try (var git = Git.cloneRepository()
                .setURI(gitUrl)
                .setDirectory(repoDir.toFile())
                .setCloneAllBranches(false)
                .call()) {
            System.out.println("Cloned new repo: " + gitUrl);
            return getHeadCommitHash(git);
        }
    }

    private String getHeadCommitHash(Git git) throws IOException {
        var head = git.getRepository().findRef("HEAD");
        if (head == null) {
            System.err.println("HEAD reference not found");
            return null;
        }

        try (var walk = new RevWalk(git.getRepository())) {
            var commit = walk.parseCommit(head.getObjectId());
            System.out.println("Latest commit hash: " + commit.getName());
            return commit.getName();
        }
    }

    public String extractRepoName(String gitUrl) {
        String[] parts = gitUrl.split("/");
        String repoName = parts[parts.length - 1];
        if (repoName.endsWith(".git")) {
            repoName = repoName.substring(0, repoName.length() - 4);
        }
        return repoName;
    }
}