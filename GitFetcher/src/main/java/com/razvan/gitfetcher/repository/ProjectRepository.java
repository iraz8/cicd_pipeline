package com.razvan.gitfetcher.repository;

import com.razvan.gitfetcher.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
        boolean existsByUrl(String url);
}