"use client";

import React, { useState, useEffect, useRef } from "react";
import { sendMessage, getChatHistory, chatWithAI, chatWithMcp } from "../lib/api";

const ChatInterface: React.FC = () => {
  const [messages, setMessages] = useState<
    { prompt: string; response: string; provider?: string }[]
  >([]);
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isLoadingHistory, setIsLoadingHistory] = useState(true);
  const [selectedProvider, setSelectedProvider] = useState<'OLLAMA' | 'COHERE'>('COHERE');
  const [useMcp, setUseMcp] = useState(true);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    loadChatHistory();
  }, []);

  useEffect(() => {
    scrollToBottom();
    if (messages.length > 0) {
      localStorage.setItem('chatHistory', JSON.stringify(messages));
    }
  }, [messages]);

  const loadChatHistory = () => {
    setIsLoadingHistory(true);
    try {
      const savedHistory = localStorage.getItem('chatHistory');
      if (savedHistory) {
        setMessages(JSON.parse(savedHistory));
      }
    } catch (err) {
      console.error("Failed to load chat history:", err);
    } finally {
      setIsLoadingHistory(false);
    }
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim() || isLoading) return;

    const userMessage = input;
    setInput("");
    setError(null); // Clear any previous errors

    // Check if the message looks like a command
    const isCommand =
      /^(create project|list projects|show project|add requirement|prepare stories|help)/i.test(
        userMessage
      );

    // Add user message to the chat
    setMessages((prev) => [
      ...prev,
      {
        prompt: userMessage,
        response: isCommand ? "Executing command..." : "Thinking...",
      },
    ]);

    setIsLoading(true);
    try {
      console.log("Sending message to API:", userMessage);

      const response = isCommand
        ? await sendMessage(userMessage)
        : useMcp 
          ? await chatWithMcp(userMessage, selectedProvider)
          : await chatWithAI(userMessage, selectedProvider);

      console.log("Received response from API:", response);

      // Update the response in the messages array
      setMessages((prev) =>
        prev.map((msg, idx) =>
          idx === prev.length - 1 ? { ...msg, response, provider: selectedProvider } : msg
        )
      );
    } catch (err: unknown) {
      console.error("Error sending message:", err);
      const errorMessage = err instanceof Error ? err.message : "Unknown error";
      console.error("Error details:", errorMessage);
      setError("Failed to get response from AI. Please try again.");

      // Update the error in the messages array
      setMessages((prev) =>
        prev.map((msg, idx) =>
          idx === prev.length - 1
            ? {
                ...msg,
                response: `Failed to get response from AI: ${errorMessage}. Please try again.`,
              }
            : msg
        )
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="w-full max-w-2xl mx-auto p-4 bg-white rounded-lg shadow-md flex flex-col h-[80vh]">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-2xl font-bold text-gray-800">Chat with AI</h2>
        <div className="flex items-center space-x-4">
          <div className="flex items-center space-x-2">
            <label className="text-sm font-medium text-gray-700">Provider:</label>
            <select
              value={selectedProvider}
              onChange={(e) => setSelectedProvider(e.target.value as 'OLLAMA' | 'COHERE')}
              className="border border-gray-300 rounded px-2 py-1 text-sm"
            >
              <option value="OLLAMA">Ollama (Local)</option>
              <option value="COHERE">Cohere (Cloud)</option>
            </select>
          </div>
          <div className="flex items-center space-x-2">
            <label className="text-sm font-medium text-gray-700">MCP:</label>
            <input
              type="checkbox"
              checked={useMcp}
              onChange={(e) => setUseMcp(e.target.checked)}
              className="rounded"
            />
          </div>
        </div>
      </div>

      <div className="flex-grow overflow-y-auto mb-4 p-2">
        {isLoadingHistory ? (
          <div className="text-center text-gray-500 my-8">
            Loading chat history...
          </div>
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
                    <p className="font-semibold text-gray-800">You:</p>
                    <p className="text-gray-500">{msg.prompt}</p>
                  </div>
                  <div className="bg-gray-100 p-3 rounded-lg">
                    <div className="flex justify-between items-center mb-1">
                      <p className="font-semibold text-gray-800">AI:</p>
                      {msg.provider && (
                        <span className="text-xs bg-gray-200 px-2 py-1 rounded">
                          {msg.provider}
                        </span>
                      )}
                    </div>
                    <p className="whitespace-pre-wrap text-gray-500">
                      {msg.response}
                    </p>
                  </div>
                </div>
              ))
            )}
            <div ref={messagesEndRef} />
          </>
        )}
      </div>

      {error && !error.includes("Failed to get response") && (
        <div className="bg-red-100 text-red-700 p-3 rounded-lg mb-4">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="mt-auto" data-testid="chat-form">
        <div className="flex items-center">
          <textarea
            id="message"
            className="flex-grow px-3 py-2 border border-gray-300 rounded-l-md focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none text-gray-500"
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
                ? "bg-gray-400 cursor-not-allowed"
                : "bg-blue-600 hover:bg-blue-700"
            }`}
          >
            {isLoading ? "Sending..." : "Send"}
          </button>
        </div>
      </form>
    </div>
  );
};

export default ChatInterface;
