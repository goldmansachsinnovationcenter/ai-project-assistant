package com.example.springai.repository.codequality;

import com.example.springai.entity.codequality.CodeQualityMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for accessing CodeQualityMetric entities.
 */
@Repository
public interface CodeQualityMetricRepository extends JpaRepository<CodeQualityMetric, Long> {
    
    /**
     * Find metrics by file name.
     * 
     * @param fileName the name of the file
     * @return list of metrics for the file
     */
    List<CodeQualityMetric> findByFileName(String fileName);
    
    /**
     * Find metrics by metric type.
     * 
     * @param metricType the type of metric
     * @return list of metrics of the specified type
     */
    List<CodeQualityMetric> findByMetricType(String metricType);
    
    /**
     * Find metrics with score greater than or equal to the specified value.
     * 
     * @param score the minimum score
     * @return list of metrics with score >= the specified value
     */
    List<CodeQualityMetric> findByScoreGreaterThanEqual(Double score);
}
