package com.example.springai.controller.search;

import com.example.springai.entity.FinancialQuery;
import com.example.springai.model.search.SearchParameters;
import com.example.springai.model.search.SearchQuery;
import com.example.springai.model.search.SearchResult;
import com.example.springai.repository.FinancialQueryRepository;
import com.example.springai.service.search.QueryParserService;
import com.example.springai.service.search.SearchAggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for financial research search functionality
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final QueryParserService queryParserService;
    private final SearchAggregationService searchAggregationService;
    private final FinancialQueryRepository financialQueryRepository;

    @Autowired
    public SearchController(
            QueryParserService queryParserService,
            SearchAggregationService searchAggregationService,
            FinancialQueryRepository financialQueryRepository) {
        this.queryParserService = queryParserService;
        this.searchAggregationService = searchAggregationService;
        this.financialQueryRepository = financialQueryRepository;
    }

    /**
     * Search using natural language query
     * Examples:
     * - "David kostin's view on the s&p 500"
     * - "US gdp"
     * - "what is apple projected q2 earnings"
     */
    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> searchByQuery(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Query is required",
                    "status", "error"
            ));
        }

        try {
            SearchQuery searchQuery = queryParserService.parseQuery(query);
            
            SearchParameters parameters = queryParserService.extractSearchParameters(searchQuery);
            
            List<SearchResult> results = searchAggregationService.searchAcrossServices(parameters);
            
            FinancialQuery financialQuery = queryParserService.convertToEntity(searchQuery);
            financialQueryRepository.save(financialQuery);
            
            return ResponseEntity.ok(Map.of(
                    "results", results,
                    "parsedQuery", searchQuery,
                    "status", "success",
                    "resultCount", results.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Error processing query: " + e.getMessage(),
                    "status", "error"
            ));
        }
    }

    /**
     * Search using structured parameters
     */
    @PostMapping("/parameters")
    public ResponseEntity<Map<String, Object>> searchByParameters(@RequestBody SearchParameters parameters) {
        if (parameters == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Search parameters are required",
                    "status", "error"
            ));
        }

        try {
            List<SearchResult> results = searchAggregationService.searchAcrossServices(parameters);
            
            return ResponseEntity.ok(Map.of(
                    "results", results,
                    "status", "success",
                    "resultCount", results.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Error processing search: " + e.getMessage(),
                    "status", "error"
            ));
        }
    }

    /**
     * Get recent search queries
     */
    @GetMapping("/recent")
    public ResponseEntity<List<FinancialQuery>> getRecentQueries() {
        return ResponseEntity.ok(financialQueryRepository.findAll());
    }

    /**
     * Get search query by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<FinancialQuery> getQueryById(@PathVariable Long id) {
        return financialQueryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
