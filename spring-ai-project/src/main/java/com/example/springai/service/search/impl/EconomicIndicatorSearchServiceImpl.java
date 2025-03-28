package com.example.springai.service.search.impl;

import com.example.springai.model.search.SearchParameters;
import com.example.springai.model.search.SearchResult;
import com.example.springai.service.search.SearchService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Placeholder implementation for economic indicator search
 * Handles queries like "US gdp"
 */
@Service
public class EconomicIndicatorSearchServiceImpl implements SearchService {

    @Override
    public List<SearchResult> search(SearchParameters parameters) {
        List<SearchResult> results = new ArrayList<>();
        
        boolean isGdpQuery = parameters.getSearchTerm() != null && 
                parameters.getSearchTerm().toLowerCase().contains("gdp") ||
                (parameters.getEntities() != null && 
                 parameters.getEntities().stream()
                    .anyMatch(e -> e.toLowerCase().contains("gdp")));
        
        boolean isUsQuery = parameters.getSearchTerm() != null && 
                (parameters.getSearchTerm().toLowerCase().contains("us ") || 
                 parameters.getSearchTerm().toLowerCase().contains("u.s.") ||
                 parameters.getSearchTerm().toLowerCase().contains("united states")) ||
                (parameters.getEntities() != null && 
                 parameters.getEntities().stream()
                    .anyMatch(e -> e.toLowerCase().contains("us") || 
                             e.toLowerCase().contains("u.s.") || 
                             e.toLowerCase().contains("united states")));
        
        if (isGdpQuery && isUsQuery) {
            results.add(createEconomicResult(
                "US GDP Q4 2024 - Final Estimate",
                "The US economy grew at an annualized rate of 3.2% in Q4 2024, according to the final estimate from the Bureau of Economic Analysis. This exceeded the initial estimate of 2.9% and represents an acceleration from the 2.5% growth recorded in Q3 2024.",
                "Economic Data API",
                "gdp_report",
                0.98,
                LocalDateTime.now().minusDays(15),
                Arrays.asList("US", "GDP", "economic growth"),
                Arrays.asList("Q4 2024", "BEA", "final estimate"),
                "https://api.example.com/economic-data/us/gdp/q4-2024-final"
            ));
            
            results.add(createEconomicResult(
                "US GDP Forecast - 2025",
                "Economists project US GDP growth of 2.7% for 2025, with stronger performance expected in the second half of the year. Consumer spending remains resilient despite higher interest rates, while business investment is expected to accelerate.",
                "Economic Forecasts API",
                "gdp_forecast",
                0.90,
                LocalDateTime.now().minusDays(5),
                Arrays.asList("US", "GDP", "forecast", "economic outlook"),
                Arrays.asList("2025", "projections", "growth"),
                "https://api.example.com/economic-forecasts/us/gdp/2025"
            ));
            
            results.add(createEconomicResult(
                "US GDP Components Analysis - Q1 2025",
                "Consumer spending contributed 1.8 percentage points to GDP growth in Q1 2025, while government spending added 0.6 percentage points. Net exports subtracted 0.3 percentage points, and private investment added 0.9 percentage points.",
                "Economic Analysis API",
                "gdp_components",
                0.85,
                LocalDateTime.now().minusDays(2),
                Arrays.asList("US", "GDP", "components"),
                Arrays.asList("consumer spending", "government spending", "exports", "investment"),
                "https://api.example.com/economic-analysis/us/gdp-components/q1-2025"
            ));
        } else if (isGdpQuery) {
            results.add(createEconomicResult(
                "Global GDP Growth Trends - 2025",
                "Global GDP is projected to grow at 3.4% in 2025, with emerging markets outpacing developed economies. Asia continues to lead growth with a projected 5.1% expansion, while Europe shows modest 1.8% growth.",
                "Economic Data API",
                "global_gdp",
                0.80,
                LocalDateTime.now().minusDays(20),
                Arrays.asList("global", "GDP", "growth trends"),
                Arrays.asList("2025", "emerging markets", "developed economies"),
                "https://api.example.com/economic-data/global/gdp/2025"
            ));
        } else if (isUsQuery) {
            results.add(createEconomicResult(
                "US Economic Indicators Dashboard - March 2025",
                "Comprehensive overview of key US economic indicators including GDP, inflation, unemployment, housing starts, and consumer confidence. Updated through March 2025 with trend analysis and forecasts.",
                "Economic Dashboard API",
                "economic_dashboard",
                0.85,
                LocalDateTime.now().minusDays(1),
                Arrays.asList("US", "economic indicators", "dashboard"),
                Arrays.asList("GDP", "inflation", "unemployment", "housing", "consumer confidence"),
                "https://api.example.com/economic-dashboard/us/march-2025"
            ));
        }
        
        if (results.isEmpty()) {
            results.add(createEconomicResult(
                "Key Economic Indicators - Global Overview",
                "Summary of major economic indicators across key global economies, including GDP growth rates, inflation metrics, unemployment figures, and central bank policies.",
                "Economic Data API",
                "global_indicators",
                0.70,
                LocalDateTime.now().minusDays(3),
                Arrays.asList("global", "economic indicators"),
                Arrays.asList("GDP", "inflation", "unemployment", "central banks"),
                "https://api.example.com/economic-data/global/indicators/latest"
            ));
        }
        
        return results;
    }

    private SearchResult createEconomicResult(
            String title, 
            String content, 
            String source, 
            String sourceType, 
            double relevanceScore, 
            LocalDateTime publishDate, 
            List<String> entities, 
            List<String> keywords, 
            String url) {
        
        SearchResult result = new SearchResult();
        result.setId(String.valueOf(System.nanoTime()));
        result.setTitle(title);
        result.setContent(content);
        result.setSource(source);
        result.setSourceType(sourceType);
        result.setRelevanceScore(relevanceScore);
        result.setPublishDate(publishDate);
        result.setEntities(entities);
        result.setKeywords(keywords);
        result.setUrl(url);
        
        return result;
    }

    @Override
    public String getServiceName() {
        return "Economic Indicator Search Service";
    }

    @Override
    public boolean isApplicable(SearchParameters parameters) {
        if (parameters.getSearchTerm() != null) {
            String query = parameters.getSearchTerm().toLowerCase();
            
            boolean hasEconomicTerms = query.contains("gdp") || 
                                      query.contains("inflation") || 
                                      query.contains("unemployment") || 
                                      query.contains("economic") || 
                                      query.contains("economy") ||
                                      query.contains("interest rate") ||
                                      query.contains("cpi") ||
                                      query.contains("consumer price");
            
            return hasEconomicTerms;
        }
        
        return false;
    }
}
