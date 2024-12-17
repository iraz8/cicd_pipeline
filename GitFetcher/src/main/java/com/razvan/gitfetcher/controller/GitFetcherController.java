package com.razvan.gitfetcher.controller;

import com.razvan.gitfetcher.model.GitUrlRepo;
import com.razvan.gitfetcher.repository.GitUrlRepoRepository;
import com.razvan.gitfetcher.service.GitFetcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

@Controller
public class GitFetcherController {

    private final GitFetcherService gitFetcherService;
    private final GitUrlRepoRepository gitUrlRepoRepository;

    private static final Pattern GIT_URL_PATTERN = Pattern.compile(
            "^(https?|git|ssh)://.*\\.git$|^git@.*:.*\\.git$"
    );

    @Autowired
    public GitFetcherController(GitFetcherService gitFetcherService, GitUrlRepoRepository gitUrlRepoRepository) {
        this.gitFetcherService = gitFetcherService;
        this.gitUrlRepoRepository = gitUrlRepoRepository;
    }

    @GetMapping("/fetch-git-urls")
    public String showForm(Model model) {
        List<GitUrlRepo> gitUrlRepos = gitUrlRepoRepository.findAll();
        model.addAttribute("gitUrls", gitUrlRepos);
        return "fetch-git-urls";
    }

    @PostMapping("/fetch-git-urls")
    public String fetchGitUrls(@RequestParam("gitUrl") String gitUrl, Model model) {
        if (!isValidGitUrl(gitUrl)) {
            model.addAttribute("errorMessage", "Invalid Git URL");
            model.addAttribute("gitUrls", gitUrlRepoRepository.findAll());
            return "fetch-git-urls";
        }

        if (gitUrlRepoRepository.existsByUrl(gitUrl)) {
            model.addAttribute("errorMessage", "Repository already exists in the database");
            model.addAttribute("gitUrls", gitUrlRepoRepository.findAll());
            return "fetch-git-urls";
        }

        Path repoDir = Paths.get("Repositories", gitFetcherService.extractRepoName(gitUrl));
        if (Files.exists(repoDir)) {
            model.addAttribute("errorMessage", "Repository folder already exists");
            model.addAttribute("gitUrls", gitUrlRepoRepository.findAll());
            return "fetch-git-urls";
        }

        gitFetcherService.addGitUrl(gitUrl);

        GitUrlRepo gitUrlRepo = new GitUrlRepo();
        gitUrlRepo.setName(gitFetcherService.extractRepoName(gitUrl));
        gitUrlRepo.setUrl(gitUrl);
        gitUrlRepoRepository.save(gitUrlRepo);

        return "redirect:/fetch-git-urls";
    }

    private boolean isValidGitUrl(String gitUrl) {
        return GIT_URL_PATTERN.matcher(gitUrl).matches();
    }
}