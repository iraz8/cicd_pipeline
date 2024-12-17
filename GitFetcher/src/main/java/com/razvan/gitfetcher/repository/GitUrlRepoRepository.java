package com.razvan.gitfetcher.repository;

import com.razvan.gitfetcher.model.GitUrlRepo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GitUrlRepoRepository extends JpaRepository<GitUrlRepo, Long> {
        boolean existsByUrl(String url);
}