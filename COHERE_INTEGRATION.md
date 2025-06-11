# Cohere NLP Engine Integration

This document describes the implementation of Cohere NLP Engine integration for the AI Project Assistant.

## Overview

The integration adds Cohere as an additional AI provider alongside the existing Ollama setup, providing cloud-based AI capabilities with enhanced NLP features.

## Architecture

### Backend Changes

1. **CohereConfig.java** - Configuration for Cohere chat model
2. **AIProviderService.java** - Service to manage multiple AI providers (Ollama and Cohere)
3. **ChatController.java** - Updated to support provider selection and MCP integration
4. **application.properties** - Added Cohere configuration properties

### Frontend Changes

1. **ChatInterface.tsx** - Added provider selection UI and MCP toggle
2. **api.ts** - Added provider parameter support for all API calls

### FastMCP Integration

1. **my_server.py** - Added Cohere-specific tools (generate, embed, rerank)
2. **my_client.py** - Added test methods for Cohere tools
3. **requirements.txt** - Added Cohere dependency

## Configuration

### Environment Variables

```bash
COHERE_API_KEY=your_cohere_api_key_here
```

### Application Properties

```properties
spring.ai.cohere.api-key=${COHERE_API_KEY:your_cohere_api_key_here}
spring.ai.cohere.model=command-r
spring.ai.cohere.chat.options.temperature=0.7
spring.ai.cohere.chat.options.max-tokens=1024
```

## Usage

### Provider Selection

Users can select between:
- **Ollama (Local)** - Local LLM for privacy-sensitive operations
- **Cohere (Cloud)** - Cloud-based AI with enhanced capabilities

### MCP Integration

The MCP (Model Context Protocol) integration enables:
- Enhanced tool calling capabilities
- Structured command processing
- Better integration with project management tools

### API Endpoints

- `/api/ai/chat` - Standard chat with provider selection
- `/api/ai/mcp-chat` - MCP-enhanced chat with tool calling

## Cohere Capabilities

### Text Generation
- Model: command-r
- Configurable temperature and max tokens
- Optimized for conversational AI

### Embeddings
- Model: embed-english-v3.0
- Vector representations for semantic search
- Batch processing support

### Reranking
- Model: rerank-english-v3.0
- Document relevance scoring
- Query-document matching

## Security Considerations

1. **API Key Management** - Store Cohere API keys securely using environment variables
2. **Rate Limiting** - Implement usage monitoring to control API costs
3. **Error Handling** - Robust error handling for network and API failures
4. **Data Privacy** - Consider data residency requirements for sensitive information

## Testing

### Backend Testing
```bash
cd spring-ai-project
mvn test
```

### Frontend Testing
```bash
cd ui
npm test
```

### FastMCP Testing
```bash
cd cohere-mcp
python my_client.py
```

## Deployment

### Local Development
1. Set environment variables in `.env` file
2. Start Ollama service (if using local provider)
3. Start Spring Boot backend
4. Start Next.js frontend

### Production Deployment
1. Configure secure environment variable management
2. Set up monitoring for API usage and costs
3. Implement rate limiting and error handling
4. Deploy FastMCP server to AWS (as mentioned in repository description)

## Cost Management

- Monitor Cohere API usage through their dashboard
- Implement usage limits in application configuration
- Consider caching strategies for repeated requests
- Use appropriate model sizes for different use cases

## Troubleshooting

### Common Issues

1. **API Key Not Set** - Ensure COHERE_API_KEY environment variable is configured
2. **Model Not Found** - Verify model names match Cohere's available models
3. **Rate Limiting** - Implement exponential backoff for API calls
4. **Network Issues** - Add retry logic for transient failures

### Logs

Check application logs for:
- Cohere API response errors
- Authentication failures
- Network connectivity issues
- Model configuration problems
