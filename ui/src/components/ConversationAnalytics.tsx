'use client';

import React, { useState, useEffect } from 'react';
import { getChatbotAnalytics, AnalyticsData } from '../lib/api';

export default function ConversationAnalytics() {
  const [analytics, setAnalytics] = useState<AnalyticsData | null>(null);
  const [timeRange, setTimeRange] = useState('day');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const timeRanges = [
    { value: 'hour', label: 'Last Hour' },
    { value: 'day', label: 'Last Day' },
    { value: 'week', label: 'Last Week' },
    { value: 'month', label: 'Last Month' },
  ];

  const fetchAnalytics = async () => {
    setIsLoading(true);
    setError('');

    try {
      const data = await getChatbotAnalytics(timeRange);
      setAnalytics(data);
    } catch (err) {
      setError('Failed to fetch analytics data');
      console.error('Error fetching analytics:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchAnalytics();
  }, [timeRange]);

  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-xl font-semibold">Conversation Analytics</h2>
        <select
          value={timeRange}
          onChange={(e) => setTimeRange(e.target.value)}
          className="p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
        >
          {timeRanges.map((range) => (
            <option key={range.value} value={range.value}>
              {range.label}
            </option>
          ))}
        </select>
      </div>

      {isLoading && (
        <div className="flex justify-center items-center py-8">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        </div>
      )}

      {error && (
        <div className="bg-red-100 text-red-700 p-3 rounded-md mb-4">
          {error}
        </div>
      )}

      {analytics && !isLoading && (
        <div className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="bg-gray-50 p-4 rounded-lg">
              <h3 className="text-lg font-medium mb-3">Top Intents</h3>
              {analytics.topIntents.length > 0 ? (
                <div className="space-y-2">
                  {analytics.topIntents.map(([intent, count], index) => (
                    <div key={index} className="flex justify-between items-center">
                      <span className="text-sm text-gray-700">{intent}</span>
                      <span className="bg-blue-100 text-blue-800 px-2 py-1 rounded-full text-xs">
                        {count}
                      </span>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-gray-500 text-sm">No intent data available</p>
              )}
            </div>

            <div className="bg-gray-50 p-4 rounded-lg">
              <h3 className="text-lg font-medium mb-3">Sentiment Distribution</h3>
              {analytics.sentimentDistribution.length > 0 ? (
                <div className="space-y-2">
                  {analytics.sentimentDistribution.map(([sentiment, count], index) => (
                    <div key={index} className="flex justify-between items-center">
                      <span className="text-sm text-gray-700 capitalize">{sentiment}</span>
                      <span className={`px-2 py-1 rounded-full text-xs ${
                        sentiment === 'positive' ? 'bg-green-100 text-green-800' :
                        sentiment === 'negative' ? 'bg-red-100 text-red-800' :
                        'bg-yellow-100 text-yellow-800'
                      }`}>
                        {count}
                      </span>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-gray-500 text-sm">No sentiment data available</p>
              )}
            </div>
          </div>

          <div className="bg-gray-50 p-4 rounded-lg">
            <h3 className="text-lg font-medium mb-2">Performance Metrics</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <span className="text-sm text-gray-600">Average Response Time</span>
                <p className="text-2xl font-semibold text-blue-600">
                  {analytics.averageResponseTime.toFixed(2)}ms
                </p>
              </div>
              <div>
                <span className="text-sm text-gray-600">Time Range</span>
                <p className="text-lg font-medium text-gray-800 capitalize">
                  {analytics.timeRange}
                </p>
              </div>
            </div>
          </div>
        </div>
      )}

      <button
        onClick={fetchAnalytics}
        disabled={isLoading}
        className="mt-4 bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50"
      >
        Refresh Analytics
      </button>
    </div>
  );
}
