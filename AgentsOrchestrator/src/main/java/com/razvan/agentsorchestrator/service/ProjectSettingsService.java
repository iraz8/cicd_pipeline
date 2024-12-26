package com.razvan.agentsorchestrator.service;

import com.razvan.agentsorchestrator.model.Project;
import com.razvan.agentsorchestrator.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectSettingsService {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectSettingsService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getActiveProjects() {
        return projectRepository.findByActive(true);
    }

    public void saveProjectSettings(Map<String, String> params) {
        Map<Long, Boolean> projectSettings = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().startsWith("active_")) {
                Long projectId = Long.parseLong(entry.getKey().substring(7));
                Boolean isActive = "on".equals(entry.getValue());
                projectSettings.put(projectId, isActive);
            }
        }

        List<Project> projects = projectRepository.findAll();
        for (Project project : projects) {
            Boolean isActive = projectSettings.get(project.getId());
            if (isActive != null) {
                project.setActive(isActive);
                projectRepository.save(project);
            }
        }
    }
}