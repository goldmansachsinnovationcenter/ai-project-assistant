'use client';

import React, { useState } from 'react';
import { getTopicSummary } from '@/lib/api';

export default function SummaryPage() {
  const [topic, setTopic] = useState('');
  const [summary, setSummary] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!topic.trim()) return;

    setIsLoading(true);
    setError('');
    
    try {
      const summaryResponse = await getTopicSummary(topic);
      setSummary(summaryResponse);
    } catch (err) {
      setError('Failed to get summary from AI. Please try again.');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <main className="min-h-screen p-8 bg-gray-50">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold mb-8 text-center">Topic Summary Tool</h1>
        
        <div className="w-full max-w-2xl mx-auto p-4 bg-white rounded-lg shadow-md">
          <form onSubmit={handleSubmit} className="mb-4">
            <div className="mb-4">
              <label htmlFor="topic" className="block text-sm font-medium text-gray-700 mb-1">
                Topic
              </label>
              <input
                id="topic"
                type="text"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                value={topic}
                onChange={(e) => setTopic(e.target.value)}
                placeholder="Enter a topic for summary..."
              />
            </div>
            
            <button
              type="submit"
              disabled={isLoading || !topic.trim()}
              className={`px-4 py-2 rounded-md text-white font-medium ${
                isLoading || !topic.trim() 
                  ? 'bg-gray-400 cursor-not-allowed' 
                  : 'bg-blue-600 hover:bg-blue-700'
              }`}
            >
              {isLoading ? 'Generating...' : 'Get Summary'}
            </button>
          </form>
          
          {error && (
            <div className="p-3 bg-red-100 text-red-700 rounded-md mb-4">
              {error}
            </div>
          )}
          
          {summary && (
            <div className="mt-6">
              <h3 className="text-lg font-semibold mb-2">Summary:</h3>
              <div className="p-4 bg-gray-100 rounded-md whitespace-pre-wrap">
                {summary}
              </div>
            </div>
          )}
        </div>
      </div>
    </main>
  );
}
