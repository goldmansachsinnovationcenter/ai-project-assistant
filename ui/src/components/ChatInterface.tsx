"use client";

import React, { useState, useEffect, useRef } from 'react';
import { sendMessage, getChatHistory, chatWithAI } from '../lib/api';

const ChatInterface: React.FC = () => {
  const [messages, setMessages] = useState<{ prompt: string; response: string }[]>([]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isLoadingHistory, setIsLoadingHistory] = useState(true);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    loadChatHistory();
  }, []);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const loadChatHistory = async () => {
    setIsLoadingHistory(true);
    try {
      const history = await getChatHistory(20);
      setMessages(history);
    } catch (err) {
      console.error('Failed to load chat history:', err);
      setError('Failed to load chat history. Please refresh the page.');
    } finally {
      setIsLoadingHistory(false);
    }
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim() || isLoading) return;

    const userMessage = input;
    setInput('');
    setError(null); // Clear any previous errors
    
    // Check if the message looks like a command
    const isCommand = /^(create project|list projects|show project|add requirement|prepare stories|help)/i.test(userMessage);
    
    // Add user message to the chat
    setMessages(prev => [...prev, { prompt: userMessage, response: isCommand ? 'Executing command...' : 'Thinking...' }]);
    
    setIsLoading(true);
    try {
      console.log('Sending message to API:', userMessage);
      
      const response = await chatWithAI(userMessage);
      
      console.log('Received response from API:', response);
      
      // Update the response in the messages array
      setMessages(prev => 
        prev.map((msg, idx) => 
          idx === prev.length - 1 ? { ...msg, response } : msg
        )
      );
    } catch (err: unknown) {
      console.error('Error sending message:', err);
      const errorMessage = err instanceof Error ? err.message : 'Unknown error';
      console.error('Error details:', errorMessage);
      setError('Failed to get response from AI. Please try again.');
      
      // Update the error in the messages array
      setMessages(prev => 
        prev.map((msg, idx) => 
          idx === prev.length - 1 ? { ...msg, response: `Failed to get response from AI: ${errorMessage}. Please try again.` } : msg
        )
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="w-full max-w-2xl mx-auto p-4 bg-white rounded-lg shadow-md flex flex-col h-[80vh]">
      <h2 className="text-2xl font-bold mb-4">Chat with AI</h2>
      
      <div className="flex-grow overflow-y-auto mb-4 p-2">
        {isLoadingHistory ? (
          <div className="text-center text-gray-500 my-8">Loading chat history...</div>
        ) : (
          <>
            {messages.length === 0 ? (
              <div className="text-center text-gray-500 my-8">
                No messages yet. Start a conversation!
              </div>
            ) : (
              messages.map((msg, idx) => (
                <div key={idx} className="mb-4">
                  <div className="bg-blue-100 p-3 rounded-lg mb-2">
                    <p className="font-semibold">You:</p>
                    <p>{msg.prompt}</p>
                  </div>
                  <div className="bg-gray-100 p-3 rounded-lg">
                    <p className="font-semibold">AI:</p>
                    <p className="whitespace-pre-wrap">{msg.response}</p>
                  </div>
                </div>
              ))
            )}
            <div ref={messagesEndRef} />
          </>
        )}
      </div>
      
      {error && !error.includes('Failed to get response') && (
        <div className="bg-red-100 text-red-700 p-3 rounded-lg mb-4">
          {error}
        </div>
      )}
      
      <form onSubmit={handleSubmit} className="mt-auto" data-testid="chat-form">
        <div className="flex items-center">
          <textarea
            id="message"
            className="flex-grow px-3 py-2 border border-gray-300 rounded-l-md focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
            placeholder="Type your message here..."
            value={input}
            onChange={(e) => setInput(e.target.value)}
            rows={2}
          />
          <button
            type="submit"
            disabled={isLoading || !input.trim()}
            className={`px-4 py-2 h-full rounded-r-md text-white font-medium ${
              isLoading || !input.trim() 
                ? 'bg-gray-400 cursor-not-allowed' 
                : 'bg-blue-600 hover:bg-blue-700'
            }`}
          >
            {isLoading ? 'Sending...' : 'Send'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default ChatInterface;
