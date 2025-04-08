import fetch from 'node-fetch';
import fs from 'fs/promises';
import { S3Client, PutObjectCommand } from '@aws-sdk/client-s3';

/**
 * Fetches audit logs from Devin AI API with retry logic
 * @param {string} apiKey - API key for authentication
 * @param {Object} params - Query parameters for the API
 * @param {number} maxRetries - Maximum number of retry attempts
 * @returns {Promise<Object>} - The API response as a JSON object
 */
async function fetchDevinAuditLogs(apiKey, params = {}, maxRetries = 3) {
  const url = new URL('https://api.devin.ai/v1/audit-logs');
  
  Object.entries(params).forEach(([key, value]) => {
    url.searchParams.append(key, value);
  });
  
  let retries = 0;
  let lastError;
  
  while (retries < maxRetries) {
    try {
      console.log(`Attempt ${retries + 1} of ${maxRetries} to fetch audit logs...`);
      
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${apiKey}`,
          'Content-Type': 'application/json'
        }
      });
      
      if (!response.ok) {
        throw new Error(`API request failed with status ${response.status}: ${response.statusText}`);
      }
      
      const data = await response.json();
      console.log(`Successfully fetched audit logs with ${Object.keys(data).length} entries`);
      return data;
    } catch (error) {
      lastError = error;
      retries++;
      
      if (retries < maxRetries) {
        const delay = Math.pow(2, retries) * 1000;
        console.log(`Attempt ${retries} failed: ${error.message}. Retrying in ${delay}ms...`);
        await new Promise(resolve => setTimeout(resolve, delay));
      } else {
        console.error(`All ${maxRetries} attempts failed. Last error: ${error.message}`);
      }
    }
  }
  
  throw new Error(`Failed to fetch audit logs after ${maxRetries} attempts. Last error: ${lastError.message}`);
}

/**
 * Uploads data to an S3 bucket
 * @param {Object} data - The data to upload
 * @param {string} bucketName - The name of the S3 bucket
 * @param {string} fileName - The name of the file to create in S3
 * @returns {Promise<Object>} - The S3 upload response
 */
async function uploadToS3(data, bucketName, fileName) {
  console.log(`Initializing S3 client for bucket: ${bucketName}`);
  
  const s3Client = new S3Client({
    region: process.env.AWS_REGION || 'us-east-1'
  });
  
  const params = {
    Bucket: bucketName,
    Key: fileName,
    Body: JSON.stringify(data, null, 2),
    ContentType: 'application/json'
  };
  
  console.log(`Uploading file: ${fileName} to bucket: ${bucketName}`);
  
  try {
    const command = new PutObjectCommand(params);
    const result = await s3Client.send(command);
    
    console.log(`Upload successful. ETag: ${result.ETag}`);
    return {
      success: true,
      bucket: bucketName,
      key: fileName,
      etag: result.ETag
    };
  } catch (error) {
    console.error(`S3 upload failed: ${error.message}`);
    throw new Error(`Failed to upload to S3: ${error.message}`);
  }
}

/**
 * Formats the API response data as a structured JSON object
 * @param {Object} data - Raw API response data
 * @returns {Object} - Formatted JSON object with metadata
 */
function formatResponseAsJson(data) {
  const formattedData = {
    metadata: {
      timestamp: new Date().toISOString(),
      source: 'Devin AI Audit Logs',
      version: '1.0.0'
    },
    data: data
  };
  
  return formattedData;
}

/**
 * Main function to fetch audit logs and upload to S3
 * @param {string} apiKey - API key for Devin AI
 * @param {Object} queryParams - Parameters for the audit logs API
 * @returns {Promise<Object>} - Result object with success status and filename
 */
async function main(apiKey, queryParams = {}) {
  try {
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
    const fileName = `devin-audit-logs-${timestamp}.json`;
    const localFilePath = `/tmp/${fileName}`;
    
    const defaultParams = { ...queryParams };
    if (!defaultParams.after && !defaultParams.before) {
      const now = new Date();
      const oneHourAgo = new Date(now.getTime() - 60 * 60 * 1000);
      defaultParams.after = oneHourAgo.toISOString();
      defaultParams.before = now.toISOString();
      console.log(`Setting default time range: after=${defaultParams.after}, before=${defaultParams.before}`);
    }
    
    console.log('Fetching audit logs from Devin AI...');
    const rawAuditLogs = await fetchDevinAuditLogs(apiKey, defaultParams);
    
    console.log('Formatting API response as JSON...');
    const formattedLogs = formatResponseAsJson(rawAuditLogs);
    
    console.log(`Writing formatted JSON to ${localFilePath}...`);
    await fs.writeFile(localFilePath, JSON.stringify(formattedLogs, null, 2));
    
    console.log(`Uploading logs to S3 bucket: gs-devin-audit-logs...`);
    await uploadToS3(formattedLogs, 'gs-devin-audit-logs', fileName);
    
    console.log('Successfully uploaded audit logs to S3');
    return { success: true, fileName, localFilePath };
  } catch (error) {
    console.error('Error processing audit logs:', error);
    throw error;
  }
}

if (process.argv[1] === import.meta.url) {
  const apiKey = process.env.DEVIN_API_KEY;
  
  if (!apiKey) {
    console.error('Error: DEVIN_API_KEY environment variable is required');
    process.exit(1);
  }
  
  const queryParams = {};
  process.argv.slice(2).forEach(arg => {
    if (arg.includes('=')) {
      const [key, value] = arg.split('=');
      queryParams[key] = value;
    }
  });
  
  main(apiKey, queryParams)
    .then(result => {
      console.log(`Completed successfully. File: ${result.fileName}`);
      process.exit(0);
    })
    .catch(error => {
      console.error('Failed to process audit logs:', error);
      process.exit(1);
    });
}

export { fetchDevinAuditLogs, uploadToS3, formatResponseAsJson, main };
