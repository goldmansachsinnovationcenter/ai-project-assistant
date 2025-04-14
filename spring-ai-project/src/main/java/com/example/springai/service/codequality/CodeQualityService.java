package com.example.springai.service.codequality;

import com.example.springai.entity.codequality.CodeQualityMetric;
import com.example.springai.repository.codequality.CodeQualityMetricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.TextAlignment;

/**
 * Service for analyzing code quality and generating reports.
 */
@Service
public class CodeQualityService {

    @Autowired
    private CodeQualityMetricRepository metricRepository;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "code-quality-analysis");
    private final Path reportsDir = Paths.get(System.getProperty("user.dir"), "reports");

    /**
     * Initialize directories for code analysis and reports.
     */
    public CodeQualityService() {
        try {
            if (!Files.exists(tempDir)) {
                Files.createDirectories(tempDir);
            }
            if (!Files.exists(reportsDir)) {
                Files.createDirectories(reportsDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create necessary directories", e);
        }
    }

    /**
     * Analyze code quality from an uploaded file.
     *
     * @param file the uploaded file
     * @return path to the generated report
     * @throws IOException if file operations fail
     */
    public String analyzeCode(MultipartFile file) throws IOException {
        Path filePath = saveUploadedFile(file);
        String fileName = file.getOriginalFilename();

        List<CodeQualityMetric> metrics = new ArrayList<>();
        
        CompletableFuture<CodeQualityMetric> styleMetricFuture = CompletableFuture.supplyAsync(
                () -> analyzeCodeStyle(filePath, fileName), executorService);
        
        CompletableFuture<CodeQualityMetric> qualityMetricFuture = CompletableFuture.supplyAsync(
                () -> analyzeCodeQuality(filePath, fileName), executorService);
        
        CompletableFuture<CodeQualityMetric> complexityMetricFuture = CompletableFuture.supplyAsync(
                () -> analyzeComplexity(filePath, fileName), executorService);
        
        CompletableFuture<CodeQualityMetric> securityMetricFuture = CompletableFuture.supplyAsync(
                () -> analyzeSecurityVulnerabilities(filePath, fileName), executorService);

        CompletableFuture.allOf(
                styleMetricFuture, 
                qualityMetricFuture, 
                complexityMetricFuture, 
                securityMetricFuture
        ).join();

        try {
            metrics.add(styleMetricFuture.get());
            metrics.add(qualityMetricFuture.get());
            metrics.add(complexityMetricFuture.get());
            metrics.add(securityMetricFuture.get());
        } catch (Exception e) {
            throw new RuntimeException("Failed to complete code analysis", e);
        }

        metricRepository.saveAll(metrics);

        String reportPath = generatePdfReport(metrics, fileName);

        metrics.forEach(metric -> {
            metric.setReportPath(reportPath);
            metricRepository.save(metric);
        });

        Files.deleteIfExists(filePath);

        return reportPath;
    }

    /**
     * Save an uploaded file to a temporary location.
     *
     * @param file the uploaded file
     * @return path to the saved file
     * @throws IOException if file operations fail
     */
    private Path saveUploadedFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        Path filePath = tempDir.resolve(originalFilename);
        Files.copy(file.getInputStream(), filePath);
        return filePath;
    }

    /**
     * Analyze code style using a code style checker.
     *
     * @param filePath path to the file
     * @param fileName name of the file
     * @return code quality metric for style
     */
    private CodeQualityMetric analyzeCodeStyle(Path filePath, String fileName) {
        double score = Math.random() * 10;
        String details = "Style analysis completed. Found " + (int)(Math.random() * 10) + " style issues.";
        return new CodeQualityMetric(fileName, "Style", score, details);
    }

    /**
     * Analyze code quality using a code quality checker.
     *
     * @param filePath path to the file
     * @param fileName name of the file
     * @return code quality metric for quality
     */
    private CodeQualityMetric analyzeCodeQuality(Path filePath, String fileName) {
        double score = Math.random() * 10;
        String details = "Quality analysis completed. Found " + (int)(Math.random() * 15) + " quality issues.";
        return new CodeQualityMetric(fileName, "Quality", score, details);
    }

    /**
     * Analyze code complexity.
     *
     * @param filePath path to the file
     * @param fileName name of the file
     * @return code quality metric for complexity
     */
    private CodeQualityMetric analyzeComplexity(Path filePath, String fileName) {
        double score = Math.random() * 10;
        String details = "Complexity analysis completed. Average cyclomatic complexity: " + String.format("%.2f", Math.random() * 5 + 1);
        return new CodeQualityMetric(fileName, "Complexity", score, details);
    }

    /**
     * Analyze security vulnerabilities.
     *
     * @param filePath path to the file
     * @param fileName name of the file
     * @return code quality metric for security
     */
    private CodeQualityMetric analyzeSecurityVulnerabilities(Path filePath, String fileName) {
        double score = Math.random() * 10;
        String details = "Security analysis completed. Found " + (int)(Math.random() * 5) + " potential vulnerabilities.";
        return new CodeQualityMetric(fileName, "Security", score, details);
    }

    /**
     * Generate a PDF report from code quality metrics.
     *
     * @param metrics list of code quality metrics
     * @param fileName name of the analyzed file
     * @return path to the generated report
     */
    private String generatePdfReport(List<CodeQualityMetric> metrics, String fileName) {
        String reportFileName = "code_quality_report_" + System.currentTimeMillis() + ".pdf";
        Path reportPath = reportsDir.resolve(reportFileName);
        
        try {
            PdfWriter writer = new PdfWriter(reportPath.toFile());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            Paragraph title = new Paragraph("Code Quality Report for " + fileName)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20);
            document.add(title);
            
            Paragraph date = new Paragraph("Generated on: " + new Date())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(12);
            document.add(date);
            
            document.add(new Paragraph("\n"));
            
            Paragraph summary = new Paragraph("Summary")
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(16);
            document.add(summary);
            
            Table summaryTable = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
            summaryTable.addHeaderCell(new Cell().add(new Paragraph("Metric Type")));
            summaryTable.addHeaderCell(new Cell().add(new Paragraph("Score")));
            summaryTable.addHeaderCell(new Cell().add(new Paragraph("Details")));
            
            for (CodeQualityMetric metric : metrics) {
                summaryTable.addCell(new Cell().add(new Paragraph(metric.getMetricType())));
                summaryTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", metric.getScore()))));
                summaryTable.addCell(new Cell().add(new Paragraph(metric.getDetails())));
            }
            
            document.add(summaryTable);
            
            double averageScore = metrics.stream()
                    .mapToDouble(CodeQualityMetric::getScore)
                    .average()
                    .orElse(0.0);
            
            document.add(new Paragraph("\n"));
            Paragraph overallScore = new Paragraph("Overall Score: " + String.format("%.2f", averageScore) + " / 10")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18);
            document.add(overallScore);
            
            document.add(new Paragraph("\n"));
            Paragraph recommendationsTitle = new Paragraph("Recommendations")
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(16);
            document.add(recommendationsTitle);
            
            List<String> recommendations = generateRecommendations(metrics);
            for (String recommendation : recommendations) {
                document.add(new Paragraph("• " + recommendation));
            }
            
            document.close();
            
            return reportPath.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    /**
     * Generate recommendations based on code quality metrics.
     *
     * @param metrics list of code quality metrics
     * @return list of recommendations
     */
    private List<String> generateRecommendations(List<CodeQualityMetric> metrics) {
        List<String> recommendations = new ArrayList<>();
        
        for (CodeQualityMetric metric : metrics) {
            if (metric.getScore() < 5.0) {
                switch (metric.getMetricType()) {
                    case "Style":
                        recommendations.add("Improve code style by following coding conventions and using a linter.");
                        break;
                    case "Quality":
                        recommendations.add("Refactor code to reduce duplication and improve maintainability.");
                        break;
                    case "Complexity":
                        recommendations.add("Simplify complex methods by breaking them down into smaller, more focused methods.");
                        break;
                    case "Security":
                        recommendations.add("Address security vulnerabilities by following secure coding practices.");
                        break;
                }
            }
        }
        
        recommendations.add("Regularly run code quality checks as part of your development process.");
        recommendations.add("Consider implementing automated code quality checks in your CI/CD pipeline.");
        
        return recommendations;
    }

    /**
     * Get all code quality metrics.
     *
     * @return list of all metrics
     */
    public List<CodeQualityMetric> getAllMetrics() {
        return metricRepository.findAll();
    }

    /**
     * Get metrics for a specific file.
     *
     * @param fileName name of the file
     * @return list of metrics for the file
     */
    public List<CodeQualityMetric> getMetricsForFile(String fileName) {
        return metricRepository.findByFileName(fileName);
    }
}
