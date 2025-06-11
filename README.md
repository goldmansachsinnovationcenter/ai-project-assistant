# AI Project Assistant

This application provides AI-powered project management capabilities and S&P 500 market data analysis. It includes both a Java Spring Boot backend for project management and a Python FastAPI backend for market data analysis.

## Features

- AI-powered chat interface for S&P 500 market data
- Market data retrieval and analysis
- Market news aggregation with sentiment analysis
- Market trend predictions and chart generation
- Project management with SQLite database
- RESTful API endpoints
- CORS configuration for frontend integration

## Architecture

- **Frontend**: Next.js React application (port 3000)
- **Python Backend**: FastAPI with SQLModel for market data (port 8000)
- **Java Backend**: Spring Boot for project management (port 8080)

## Prerequisites

### For Python Backend (Market Data)
- Python 3.12+
- Poetry for dependency management
- Alpha Vantage API key (optional - demo key included)

### For Java Backend (Project Management)
- Java 21
- Maven
- Ollama (with a small model like TinyLlama)

### For Frontend
- Node.js 18+
- npm or yarn

## Getting Started

### Python Backend Setup (Market Data)

1. Navigate to the Python backend directory:
   ```bash
   cd python-backend
   ```

2. Install dependencies using Poetry:
   ```bash
   poetry install
   ```

3. Set up environment variables (optional):
   ```bash
   cp .env.example .env
   # Edit .env with your Alpha Vantage API key
   ```

4. Run the Python backend:
   ```bash
   poetry run uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
   ```

### Java Backend Setup (Project Management)

1. Navigate to the Java backend directory:
   ```bash
   cd spring-ai-project
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   java -jar target/spring-ai-0.0.1-SNAPSHOT.jar
   ```

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd ui
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Run the development server:
   ```bash
   npm run dev
   ```

4. Open [http://localhost:3000](http://localhost:3000) in your browser

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
