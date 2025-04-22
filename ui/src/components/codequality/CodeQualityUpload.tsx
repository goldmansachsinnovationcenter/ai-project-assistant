import React, { useState } from 'react';

interface CodeQualityUploadProps {
  onAnalysisComplete: (reportUrl: string) => void;
}

export function CodeQualityUpload({ onAnalysisComplete }: CodeQualityUploadProps) {
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setFile(e.target.files[0]);
      setError(null);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!file) {
      setError('Please select a file to analyze');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      // const response = await analyzeCodeQuality(file);
      setTimeout(() => {
        onAnalysisComplete("/api/code-quality/report?reportPath=sample-report.pdf");
        setLoading(false);
      }, 2000);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred during analysis');
      setLoading(false);
    }
  };

  return (
    <div className="w-full max-w-3xl mx-auto bg-white p-6 rounded-lg shadow-md">
      <h2 className="text-2xl font-bold mb-4">Code Quality Analysis</h2>
      <p className="text-gray-600 mb-6">
        Upload your code file to analyze its quality and generate a comprehensive report.
      </p>
      
      <form onSubmit={handleSubmit}>
        <div className="mb-4">
          <label htmlFor="file" className="block text-sm font-medium text-gray-700 mb-2">
            Upload Code File
          </label>
          <input 
            id="file" 
            type="file" 
            onChange={handleFileChange}
            accept=".java,.py,.js,.ts,.jsx,.tsx,.html,.css"
            className="block w-full text-sm text-gray-500
                      file:mr-4 file:py-2 file:px-4
                      file:rounded-md file:border-0
                      file:text-sm file:font-semibold
                      file:bg-blue-50 file:text-blue-700
                      hover:file:bg-blue-100"
          />
          <p className="mt-1 text-sm text-gray-500">
            Supported file types: .java, .py, .js, .ts, .jsx, .tsx, .html, .css
          </p>
        </div>
        
        {error && (
          <div className="mb-4 p-3 bg-red-50 text-red-700 rounded-md">
            <p className="font-medium">Error</p>
            <p>{error}</p>
          </div>
        )}
        
        <button 
          type="submit" 
          className="w-full py-2 px-4 bg-blue-600 text-white font-semibold rounded-md shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50"
          disabled={loading || !file}
        >
          {loading ? 'Analyzing...' : 'Analyze Code'}
        </button>
      </form>
      
      <div className="mt-6">
        <h4 className="font-semibold mb-2">Metrics Analyzed:</h4>
        <ul className="list-disc pl-5 space-y-1 text-sm text-gray-600">
          <li>Code Style: Adherence to coding conventions and style guidelines</li>
          <li>Code Quality: Maintainability, readability, and best practices</li>
          <li>Complexity: Cyclomatic complexity and code structure</li>
          <li>Security: Potential security vulnerabilities and risks</li>
        </ul>
      </div>
    </div>
  );
}
