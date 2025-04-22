import axios from 'axios';

const API_BASE_URL = '/api';

/**
 * Analyze code quality by uploading a file.
 * 
 * @param file The file to analyze
 * @returns Promise with the analysis result
 */
export async function analyzeCodeQuality(file: File) {
  const formData = new FormData();
  formData.append('file', file);

  try {
    const response = await axios.post(`${API_BASE_URL}/code-quality/analyze`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return {
      message: response.data.message,
      reportPath: response.data.reportPath,
      downloadUrl: `/api/code-quality/report?reportPath=${response.data.reportPath}`,
    };
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.error || 'Failed to analyze code quality');
    }
    throw new Error('Failed to connect to the server');
  }
}

/**
 * Get all code quality metrics.
 * 
 * @returns Promise with all metrics
 */
export async function getAllCodeQualityMetrics() {
  try {
    const response = await axios.get(`${API_BASE_URL}/code-quality/metrics`);
    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.error || 'Failed to fetch code quality metrics');
    }
    throw new Error('Failed to connect to the server');
  }
}

/**
 * Get metrics for a specific file.
 * 
 * @param fileName The name of the file
 * @returns Promise with metrics for the file
 */
export async function getCodeQualityMetricsForFile(fileName: string) {
  try {
    const response = await axios.get(`${API_BASE_URL}/code-quality/metrics/${encodeURIComponent(fileName)}`);
    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.error || 'Failed to fetch code quality metrics');
    }
    throw new Error('Failed to connect to the server');
  }
}
