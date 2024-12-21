package com.razvan.agentsorchestrator.repository;

import com.razvan.agentsorchestrator.model.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IRepository extends JpaRepository<Repository, Long> {
    Optional<Repository> findByName(String name);
}