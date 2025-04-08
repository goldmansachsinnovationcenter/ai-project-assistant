# Devin AI Audit Logs Utility

A JavaScript utility for fetching Devin AI audit logs and uploading them to an S3 bucket.

## Features

- Fetches audit logs from the Devin AI API
- Implements retry logic with exponential backoff (max 3 retries)
- Formats the API response as JSON
- Uploads the JSON file to an S3 bucket
- Can be used as a module or run directly from the command line
- Includes comprehensive test suite with mock data

## Installation

```bash
npm install
```

## Usage

### As a module

```javascript
import { main } from './devin-audit-logs.js';

// API key for Devin AI
const apiKey = 'your-api-key';

// Optional query parameters
const queryParams = {
  limit: 100,
  startDate: '2025-01-01'
};

main(apiKey, queryParams)
  .then(result => console.log('Success:', result))
  .catch(error => console.error('Error:', error));
```

### From the command line

```bash
# Set your API key as an environment variable
export DEVIN_API_KEY='your-api-key'

# Run the script with optional query parameters
node devin-audit-logs.js limit=100 startDate=2025-01-01
```

## Functions

- `fetchDevinAuditLogs(apiKey, params, maxRetries)`: Fetches audit logs from the Devin AI API
- `uploadToS3(data, bucketName, fileName)`: Uploads data to an S3 bucket
- `formatResponseAsJson(data)`: Formats the API response as a structured JSON
- `main(apiKey, queryParams)`: Main function that orchestrates the process

## Testing

The utility comes with a comprehensive test suite that uses Jest and mock data:

```bash
# Run the tests
npm test
```

The test suite includes:
- Tests for the API fetch function with retry logic
- Tests for JSON formatting
- Tests for S3 upload functionality
- End-to-end test of the main function

A mock audit logs file is included at `mock-audit-logs.json` for testing purposes.
