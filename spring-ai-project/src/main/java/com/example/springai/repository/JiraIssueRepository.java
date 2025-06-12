package com.example.springai.repository;

import com.example.springai.entity.JiraIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JiraIssueRepository extends JpaRepository<JiraIssue, String> {
    
    List<JiraIssue> findByProjectKey(String projectKey);
    
    List<JiraIssue> findByStatus(String status);
    
    List<JiraIssue> findByAssignee(String assignee);
    
    List<JiraIssue> findByReporter(String reporter);
    
    @Query("SELECT j FROM JiraIssue j WHERE j.summary LIKE %:keyword% OR j.description LIKE %:keyword%")
    List<JiraIssue> findByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT j FROM JiraIssue j WHERE j.projectKey = :projectKey AND j.status = :status")
    List<JiraIssue> findByProjectKeyAndStatus(@Param("projectKey") String projectKey, @Param("status") String status);
    
    @Query("SELECT j FROM JiraIssue j WHERE j.projectKey = :projectKey AND (j.summary LIKE %:keyword% OR j.description LIKE %:keyword%)")
    List<JiraIssue> findByProjectKeyAndKeyword(@Param("projectKey") String projectKey, @Param("keyword") String keyword);
    
    Optional<JiraIssue> findByKey(String key);
}
