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
        Map<Long, Boolean> activeProjectSettings = new HashMap<>();
        Map<Long, Boolean> enabledPipelineProjectSettings = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("active_") || key.startsWith("enable_pipeline_")) {
                Long projectId = Long.parseLong(key.substring(key.lastIndexOf('_') + 1));
                if (key.startsWith("active_")) {
                    Boolean isActive = "on".equals(entry.getValue());
                    activeProjectSettings.put(projectId, isActive);
                } else if (key.startsWith("enable_pipeline_")) {
                    Boolean isEnablePipeline = "on".equals(entry.getValue());
                    enabledPipelineProjectSettings.put(projectId, isEnablePipeline);
                }
            }
        }

        List<Project> projects = projectRepository.findAll();
        for (Project project : projects) {
            Boolean isActive = activeProjectSettings.get(project.getId());
            if (isActive != null) {
                project.setActive(isActive);
                projectRepository.save(project);
            }

            Boolean isEnablePipeline = enabledPipelineProjectSettings.get(project.getId());
            if (isEnablePipeline != null) {
                project.setEnablePipeline(isEnablePipeline);
                projectRepository.save(project);
            }
        }
    }
}