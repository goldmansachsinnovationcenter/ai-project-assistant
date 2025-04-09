# Spring AI Project

This Spring Boot application integrates with Ollama to provide AI-powered project management and research search capabilities. It allows users to create and manage projects, add requirements, generate user stories using AI, and perform intelligent financial research searches.

## Features

- AI-powered chat interface
- Project management with SQLite database
- User story generation from requirements
- Intelligent financial research search
- Natural language query understanding
- Multi-source search aggregation
- Result reranking by relevance
- RESTful API endpoints
- CORS configuration for frontend integration

## Prerequisites

- Java 21
- Maven
- Ollama (with a small model like TinyLlama)

## Getting Started

1. Clone the repository
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   java -jar target/spring-ai-0.0.1-SNAPSHOT.jar
   ```

## API Endpoints

All API endpoints are prefixed with `/api` to distinguish them from UI routes:

- `/api/ai/chat` - Chat with the AI
- `/api/ai/template` - Use AI templates
- `/api/projects` - Project management
- `/api/health` - Health check endpoint
- `/api/db` - Database verification endpoints
- `/api/search/query` - Natural language financial search
- `/api/search/parameters` - Structured parameter search
- `/api/search/recent` - Get recent search queries
- `/api/search/{id}` - Get search query by ID

## Financial Research Search

The application provides an AI-powered financial research search capability that can understand natural language queries, search across multiple data sources, and rerank results by relevance.

### Example Queries

```
"David kostin's view on the s&p 500"
"US gdp"
"what is apple projected q2 earnings"
```

### Using the Search API

#### Natural Language Search

```bash
curl -X POST http://localhost:8080/api/search/query \
  -H "Content-Type: application/json" \
  -d '{"query": "David kostin'\''s view on the s&p 500"}'
```

#### Structured Parameter Search

```bash
curl -X POST http://localhost:8080/api/search/parameters \
  -H "Content-Type: application/json" \
  -d '{
    "searchTerm": "s&p 500 outlook",
    "entities": ["S&P 500", "Goldman Sachs"],
    "timePeriod": "2025",
    "keywords": ["market outlook", "price target"]
  }'
```

### Search Capabilities

The search functionality supports:

1. **Query Understanding** - Extracts entities, time periods, and intent from natural language
2. **Multi-Source Search** - Searches across market data, economic indicators, and earnings projections
3. **Result Reranking** - Uses AI to prioritize the most relevant results

## Test Coverage

To run tests with coverage reporting:

```bash
mvn clean test jacoco:report
```

The coverage report will be generated at `target/site/jacoco/index.html`.

Coverage requirements:
- Line coverage: 90%
- Branch coverage: 90% 
- Method coverage: 90%

## Configuration

The application is configured to use:

- SQLite database for persistence
- Ollama for AI integration
- Spring Web for REST endpoints
- Spring Actuator for monitoring

## Integrated Testing

A combined test script is available at the root of the project:

```bash
/home/ubuntu/run_coverage_tests.sh
```

This script runs both backend and frontend tests with coverage reporting.
