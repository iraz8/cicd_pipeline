package com.razvan.gitfetcher.repository;

import com.razvan.gitfetcher.model.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GitUrlRepoRepository extends JpaRepository<Repository, Long> {
        boolean existsByUrl(String url);
}