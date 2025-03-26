# Spring AI Project

This Spring Boot application integrates with Ollama to provide AI-powered project management capabilities. It allows users to create and manage projects, add requirements, and generate user stories using AI.

## Features

- AI-powered chat interface
- Project management with SQLite database
- User story generation from requirements
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
