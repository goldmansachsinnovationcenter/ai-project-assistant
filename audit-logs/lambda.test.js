import assert from 'assert';
import { fileURLToPath } from 'url';
import { dirname, join } from 'path';
import fs from 'fs/promises';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const mockAuditLogs = {
  logs: [
    {
      id: "log-001",
      timestamp: "2025-04-07T18:30:00Z",
      user: "user123",
      action: "login",
      resource: "devin-platform",
      status: "success",
      ip_address: "192.168.1.1",
      user_agent: "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
    },
    {
      id: "log-002",
      timestamp: "2025-04-07T18:35:10Z",
      user: "user123",
      action: "create_project",
      resource: "project-xyz",
      status: "success",
      ip_address: "192.168.1.1",
      user_agent: "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
    },
    {
      id: "log-003",
      timestamp: "2025-04-07T18:40:22Z",
      user: "user456",
      action: "login",
      resource: "devin-platform",
      status: "failed",
      reason: "invalid_credentials",
      ip_address: "10.0.0.5",
      user_agent: "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)"
    }
  ],
  pagination: {
    total: 3,
    limit: 100,
    offset: 0,
    has_more: false
  }
};

async function setupMockedModule() {
  const mockModulePath = join(__dirname, 'mocked-devin-audit-logs.js');
  
  const originalCode = await fs.readFile(join(__dirname, 'devin-audit-logs.js'), 'utf8');
  
  const mockedCode = originalCode
    .replace(
      "import fetch from 'node-fetch';",
      `// Mock fetch implementation
const fetch = async (url, options) => {
  console.log('Mock fetch called with URL:', url.toString());
  
  return {
    ok: true,
    status: 200,
    statusText: 'OK',
    json: async () => (${JSON.stringify(mockAuditLogs)})
  };
};`
    )
    .replace(
      "import fs from 'fs/promises';",
      `// Mock fs implementation
const fs = {
  writeFile: async (path, content) => {
    console.log('Mock writeFile called with path:', path);
    return undefined;
  }
};`
    )
    .replace(
      "import { S3Client, PutObjectCommand } from '@aws-sdk/client-s3';",
      `// Mock S3 implementation
class S3Client {
  constructor(config) {
    console.log('Mock S3Client constructor called');
  }
  
  async send(command) {
    console.log('Mock S3 send called with bucket:', command.Bucket);
    return { ETag: 'mock-etag-12345' };
  }
}

class PutObjectCommand {
  constructor(params) {
    Object.assign(this, params);
  }
}
`
    );
  
  await fs.writeFile(mockModulePath, mockedCode);
  
  return import(mockModulePath);
}

async function runTests() {
  console.log('Setting up mocked module...');
  const mockedModule = await setupMockedModule();
  
  console.log('Starting tests...');
  let allTestsPassed = true;
  
  try {
    console.log('\n--- Testing fetchDevinAuditLogs ---');
    const result = await mockedModule.fetchDevinAuditLogs('test-api-key', { limit: 10 });
    
    assert.strictEqual(result.logs.length, 3, 'Should return 3 logs');
    assert.strictEqual(result.logs[0].id, 'log-001', 'First log should have ID log-001');
    assert.strictEqual(result.pagination.total, 3, 'Should have 3 total logs');
    
    console.log('✅ fetchDevinAuditLogs test passed');
  } catch (error) {
    console.error('❌ fetchDevinAuditLogs test failed:', error);
    allTestsPassed = false;
  }
  
  try {
    console.log('\n--- Testing formatResponseAsJson ---');
    const formattedData = mockedModule.formatResponseAsJson(mockAuditLogs);
    
    assert.strictEqual(typeof formattedData.metadata, 'object', 'Should have metadata object');
    assert.strictEqual(typeof formattedData.metadata.timestamp, 'string', 'Should have timestamp string');
    assert.strictEqual(formattedData.metadata.source, 'Devin AI Audit Logs', 'Should have correct source');
    assert.strictEqual(formattedData.metadata.version, '1.0.0', 'Should have correct version');
    assert.deepStrictEqual(formattedData.data, mockAuditLogs, 'Should contain original data');
    
    console.log('✅ formatResponseAsJson test passed');
  } catch (error) {
    console.error('❌ formatResponseAsJson test failed:', error);
    allTestsPassed = false;
  }
  
  try {
    console.log('\n--- Testing uploadToS3 ---');
    const testData = { test: 'data' };
    const result = await mockedModule.uploadToS3(testData, 'test-bucket', 'test-file.json');
    
    assert.strictEqual(result.success, true, 'Should return success: true');
    assert.strictEqual(result.bucket, 'test-bucket', 'Should return correct bucket');
    assert.strictEqual(result.key, 'test-file.json', 'Should return correct key');
    assert.strictEqual(result.etag, 'mock-etag-12345', 'Should return mock etag');
    
    console.log('✅ uploadToS3 test passed');
  } catch (error) {
    console.error('❌ uploadToS3 test failed:', error);
    allTestsPassed = false;
  }
  
  try {
    console.log('\n--- Testing main function ---');
    const result = await mockedModule.main('test-api-key', { limit: 10 });
    
    assert.strictEqual(result.success, true, 'Should return success: true');
    assert.strictEqual(typeof result.fileName, 'string', 'Should return fileName string');
    assert.strictEqual(typeof result.localFilePath, 'string', 'Should return localFilePath string');
    
    console.log('✅ main function test passed');
  } catch (error) {
    console.error('❌ main function test failed:', error);
    allTestsPassed = false;
  }
  
  try {
    await fs.unlink(join(__dirname, 'mocked-devin-audit-logs.js'));
  } catch (error) {
    console.error('Error cleaning up:', error);
  }
  
  if (allTestsPassed) {
    console.log('\n✅ All tests passed!');
    process.exit(0);
  } else {
    console.error('\n❌ Some tests failed!');
    process.exit(1);
  }
}

runTests().catch(error => {
  console.error('Error running tests:', error);
  process.exit(1);
});
