package com.example.springai.repository;

import com.example.springai.entity.FinancialQuery;
import com.example.springai.model.search.SearchQuery.QueryIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for financial research queries
 */
@Repository
public interface FinancialQueryRepository extends JpaRepository<FinancialQuery, Long> {
    
    /**
     * Find financial queries by intent
     */
    List<FinancialQuery> findByIntent(QueryIntent intent);
    
    /**
     * Find financial queries by entity
     */
    @Query("SELECT fq FROM FinancialQuery fq JOIN fq.entities e WHERE e = :entity")
    List<FinancialQuery> findByEntity(@Param("entity") String entity);
    
    /**
     * Find financial queries by time period
     */
    List<FinancialQuery> findByTimePeriod(String timePeriod);
    
    /**
     * Find financial queries by analyst name
     */
    List<FinancialQuery> findByAnalystName(String analystName);
    
    /**
     * Find financial queries by company name
     */
    List<FinancialQuery> findByCompanyName(String companyName);
    
    /**
     * Find financial queries by indicator name
     */
    List<FinancialQuery> findByIndicatorName(String indicatorName);
    
    /**
     * Find financial queries containing keyword in question
     */
    List<FinancialQuery> findByQuestionContainingIgnoreCase(String keyword);
}
