package com.razvan.agentsorchestrator.repository;

import com.razvan.agentsorchestrator.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByName(String name);
    List<Project> findByActive(boolean active);
}