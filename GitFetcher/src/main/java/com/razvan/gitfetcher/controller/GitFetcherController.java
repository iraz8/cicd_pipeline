package com.razvan.gitfetcher.controller;

import com.razvan.gitfetcher.model.Project;
import com.razvan.gitfetcher.repository.ProjectRepository;
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
    private final ProjectRepository projectRepository;

    private static final Pattern GIT_URL_PATTERN = Pattern.compile(
            "^(https?|git|ssh)://.*\\.git$|^git@.*:.*\\.git$"
    );

    @Autowired
    public GitFetcherController(GitFetcherService gitFetcherService, ProjectRepository projectRepository) {
        this.gitFetcherService = gitFetcherService;
        this.projectRepository = projectRepository;
    }

    @GetMapping("/fetch-git-urls")
    public String showForm(Model model) {
        List<Project> projects = projectRepository.findAll();
        model.addAttribute("gitUrls", projects);
        return "fetch-git-urls";
    }

    @PostMapping("/fetch-git-urls")
    public String fetchGitUrls(@RequestParam("gitUrl") String gitUrl, Model model) {
        if (!isValidGitUrl(gitUrl)) {
            model.addAttribute("errorMessage", "Invalid Git URL");
            model.addAttribute("gitUrls", projectRepository.findAll());
            return "fetch-git-urls";
        }

        if (projectRepository.existsByUrl(gitUrl)) {
            model.addAttribute("errorMessage", "Repository already exists in the database");
            model.addAttribute("gitUrls", projectRepository.findAll());
            return "fetch-git-urls";
        }

        Path repoDir = Paths.get("Projects", gitFetcherService.extractRepoName(gitUrl));
        if (Files.exists(repoDir)) {
            model.addAttribute("errorMessage", "Repository folder already exists");
            model.addAttribute("gitUrls", projectRepository.findAll());
            return "fetch-git-urls";
        }

        gitFetcherService.addGitUrl(gitUrl);

        Project project = new Project();
        project.setName(gitFetcherService.extractRepoName(gitUrl));
        project.setUrl(gitUrl);
        project.setActive(true);
        projectRepository.save(project);

        return "redirect:/fetch-git-urls";
    }

    private boolean isValidGitUrl(String gitUrl) {
        return GIT_URL_PATTERN.matcher(gitUrl).matches();
    }
}