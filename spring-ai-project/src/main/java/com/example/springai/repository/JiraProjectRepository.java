package com.example.springai.repository;

import com.example.springai.entity.JiraProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JiraProjectRepository extends JpaRepository<JiraProject, String> {
    
    Optional<JiraProject> findByKey(String key);
    
    Optional<JiraProject> findByName(String name);
}
