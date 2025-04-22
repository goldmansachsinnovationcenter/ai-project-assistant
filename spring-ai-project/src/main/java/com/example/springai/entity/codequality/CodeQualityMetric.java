package com.example.springai.entity.codequality;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a code quality metric measurement.
 */
@Entity
@Table(name = "code_quality_metrics")
public class CodeQualityMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String metricType;

    @Column(nullable = false)
    private Double score;

    @Column(length = 1000)
    private String details;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(length = 255)
    private String reportPath;

    public CodeQualityMetric() {
        this.createdAt = LocalDateTime.now();
    }

    public CodeQualityMetric(String fileName, String metricType, Double score, String details) {
        this.fileName = fileName;
        this.metricType = metricType;
        this.score = score;
        this.details = details;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    @Override
    public String toString() {
        return "CodeQualityMetric{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", metricType='" + metricType + '\'' +
                ", score=" + score +
                ", details='" + details + '\'' +
                ", createdAt=" + createdAt +
                ", reportPath='" + reportPath + '\'' +
                '}';
    }
}
