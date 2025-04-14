'use client';

import React, { useState } from 'react';
import { CodeQualityUpload } from '@/components/codequality/CodeQualityUpload';

export default function CodeQualityPage() {
  const [reportUrl, setReportUrl] = useState<string | null>(null);

  const handleAnalysisComplete = (url: string) => {
    setReportUrl(url);
  };

  return (
    <div className="container mx-auto py-8">
      <h1 className="text-3xl font-bold mb-8 text-center">Code Quality Metrics</h1>
      
      {!reportUrl ? (
        <CodeQualityUpload onAnalysisComplete={handleAnalysisComplete} />
      ) : (
        <div className="w-full max-w-3xl mx-auto bg-white p-6 rounded-lg shadow-md">
          <h2 className="text-2xl font-bold mb-4 text-green-600">Analysis Complete!</h2>
          <p className="mb-6">Your code quality report has been generated successfully.</p>
          
          <div className="flex justify-center">
            <a 
              href={reportUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="py-2 px-4 bg-blue-600 text-white font-semibold rounded-md shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
            >
              Download PDF Report
            </a>
          </div>
        </div>
      )}
    </div>
  );
}
