'use client';

import React, { useState } from 'react';
import { updateChatbotPersonality, ChatbotSettings as ChatbotSettingsType } from '../lib/api';

interface ChatbotSettingsProps {
  onSettingsUpdate?: (settings: ChatbotSettingsType) => void;
}

export default function ChatbotSettings({ onSettingsUpdate }: ChatbotSettingsProps) {
  const [settings, setSettings] = useState<ChatbotSettingsType>({
    personality: 'helpful',
    responseStyle: 'balanced',
    contextRetention: true,
  });
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState('');

  const personalities = [
    { value: 'helpful', label: 'Helpful' },
    { value: 'professional', label: 'Professional' },
    { value: 'friendly', label: 'Friendly' },
    { value: 'technical', label: 'Technical' },
    { value: 'creative', label: 'Creative' },
  ];

  const responseStyles = [
    { value: 'concise', label: 'Concise' },
    { value: 'balanced', label: 'Balanced' },
    { value: 'detailed', label: 'Detailed' },
  ];

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setMessage('');

    try {
      await updateChatbotPersonality(settings);
      setMessage('Chatbot settings updated successfully!');
      onSettingsUpdate?.(settings);
    } catch (error) {
      setMessage('Failed to update settings. Please try again.');
      console.error('Error updating chatbot settings:', error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <h2 className="text-xl font-semibold mb-4">Chatbot Settings</h2>
      
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label htmlFor="personality" className="block text-sm font-medium text-gray-700 mb-2">
            Personality
          </label>
          <select
            id="personality"
            value={settings.personality}
            onChange={(e) => setSettings({ ...settings, personality: e.target.value })}
            className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            {personalities.map((personality) => (
              <option key={personality.value} value={personality.value}>
                {personality.label}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label htmlFor="responseStyle" className="block text-sm font-medium text-gray-700 mb-2">
            Response Style
          </label>
          <select
            id="responseStyle"
            value={settings.responseStyle}
            onChange={(e) => setSettings({ ...settings, responseStyle: e.target.value })}
            className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            {responseStyles.map((style) => (
              <option key={style.value} value={style.value}>
                {style.label}
              </option>
            ))}
          </select>
        </div>

        <div className="flex items-center">
          <input
            id="contextRetention"
            type="checkbox"
            checked={settings.contextRetention}
            onChange={(e) => setSettings({ ...settings, contextRetention: e.target.checked })}
            className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
          />
          <label htmlFor="contextRetention" className="ml-2 block text-sm text-gray-700">
            Enable context retention across conversations
          </label>
        </div>

        <button
          type="submit"
          disabled={isLoading}
          className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {isLoading ? 'Updating...' : 'Update Settings'}
        </button>
      </form>

      {message && (
        <div className={`mt-4 p-3 rounded-md ${
          message.includes('successfully') 
            ? 'bg-green-100 text-green-700' 
            : 'bg-red-100 text-red-700'
        }`}>
          {message}
        </div>
      )}
    </div>
  );
}
