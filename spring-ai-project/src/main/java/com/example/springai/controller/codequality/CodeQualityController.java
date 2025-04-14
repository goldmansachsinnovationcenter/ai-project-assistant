package com.example.springai.controller.codequality;

import com.example.springai.entity.codequality.CodeQualityMetric;
import com.example.springai.service.codequality.CodeQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * REST controller for code quality analysis.
 */
@RestController
@RequestMapping("/api/code-quality")
public class CodeQualityController {

    @Autowired
    private CodeQualityService codeQualityService;

    /**
     * Upload a file for code quality analysis.
     *
     * @param file the file to analyze
     * @return response with the report path
     */
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeCode(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Please upload a file"));
            }

            String reportPath = codeQualityService.analyzeCode(file);
            return ResponseEntity.ok(Map.of(
                    "message", "Code analysis completed successfully",
                    "reportPath", reportPath
            ));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to process file: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "An error occurred: " + e.getMessage()));
        }
    }

    /**
     * Get all code quality metrics.
     *
     * @return list of all metrics
     */
    @GetMapping("/metrics")
    public ResponseEntity<List<CodeQualityMetric>> getAllMetrics() {
        List<CodeQualityMetric> metrics = codeQualityService.getAllMetrics();
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics for a specific file.
     *
     * @param fileName name of the file
     * @return list of metrics for the file
     */
    @GetMapping("/metrics/{fileName}")
    public ResponseEntity<List<CodeQualityMetric>> getMetricsForFile(@PathVariable String fileName) {
        List<CodeQualityMetric> metrics = codeQualityService.getMetricsForFile(fileName);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Download a generated report.
     *
     * @param reportPath path to the report
     * @return the report file
     */
    @GetMapping("/report")
    public ResponseEntity<Resource> getReport(@RequestParam String reportPath) {
        File file = new File(reportPath);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }
}
