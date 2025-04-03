package com.example.springai.service.search;

import com.example.springai.entity.FinancialQuery;
import com.example.springai.model.search.SearchQuery;
import com.example.springai.model.search.SearchParameters;

/**
 * Service interface for parsing natural language queries into structured search parameters
 */
public interface QueryParserService {
    
    /**
     * Parse a natural language query into a structured SearchQuery object
     * 
     * @param query The natural language query string
     * @return A structured SearchQuery with extracted parameters
     */
    SearchQuery parseQuery(String query);
    
    /**
     * Convert a SearchQuery into a FinancialQuery entity for persistence
     * 
     * @param searchQuery The structured search query
     * @return A FinancialQuery entity
     */
    FinancialQuery convertToEntity(SearchQuery searchQuery);
    
    /**
     * Extract search parameters from a SearchQuery for use in search APIs
     * 
     * @param searchQuery The structured search query
     * @return SearchParameters for API calls
     */
    SearchParameters extractSearchParameters(SearchQuery searchQuery);
}
