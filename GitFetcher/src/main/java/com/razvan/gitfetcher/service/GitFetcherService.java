package com.razvan.gitfetcher.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevWalk;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class GitFetcherService {

    private final Map<String, String> repoLastCommitMap = new HashMap<>();

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
            } else {
                System.out.println("No new updates found for repo: " + gitUrl);
            }
        }
    }

    private String fetchLatestCommitHash(String gitUrl) {
        String repoName = extractRepoName(gitUrl);
        var repoDir = Paths.get("Repositories", repoName);
        try {
            if (Files.exists(repoDir)) {
                return fetchFromExistingRepo(repoDir);
            } else {
                return cloneNewRepo(gitUrl, repoDir);
            }
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String fetchFromExistingRepo(Path repoDir) throws IOException, GitAPIException {
        try (var git = Git.open(repoDir.toFile())) {
            git.pull().call();
            System.out.println("Pulled latest changes for repo: " + repoDir);
            return getHeadCommitHash(git);
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