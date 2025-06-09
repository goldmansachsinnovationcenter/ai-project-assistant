"use client";

import React, { useState, useEffect, useRef } from "react";
import { sendMessage, getChatHistory, chatWithAI } from "../lib/api";

interface Message {
  prompt: string;
  response: string;
  timestamp?: string;
}

const ChatInterface: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isLoadingHistory, setIsLoadingHistory] = useState(true);
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
    setMessages((prev: Message[]) => [
      ...prev,
      {
        prompt: userMessage,
        response: isCommand ? "Executing command..." : "Thinking...",
        timestamp: new Date().toLocaleTimeString(),
      },
    ]);

    setIsLoading(true);
    try {
      console.log("Sending message to API:", userMessage);

      const response = isCommand
        ? await sendMessage(userMessage)
        : await chatWithAI(userMessage);

      console.log("Received response from API:", response);

      // Update the response in the messages array
      setMessages((prev: Message[]) =>
        prev.map((msg: Message, idx: number) =>
          idx === prev.length - 1 ? { ...msg, response } : msg
        )
      );
    } catch (err: unknown) {
      console.error("Error sending message:", err);
      const errorMessage = err instanceof Error ? err.message : "Unknown error";
      console.error("Error details:", errorMessage);
      setError("Failed to get response from AI. Please try again.");

      // Update the error in the messages array
      setMessages((prev: Message[]) =>
        prev.map((msg: Message, idx: number) =>
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
    <div className="w-full max-w-4xl mx-auto p-4 sm:p-6 bg-white rounded-xl shadow-lg flex flex-col h-[85vh] sm:h-[80vh]">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl sm:text-3xl font-bold text-gray-800">
          💬 Chat with AI Assistant
        </h2>
        <div className="text-sm text-gray-500 hidden sm:block">
          Project Management Helper
        </div>
      </div>

      <div className="flex-grow overflow-y-auto mb-4 p-2 sm:p-4 bg-gray-50 rounded-lg">
        {isLoadingHistory ? (
          <div className="text-center text-gray-500 my-8">
            <div className="animate-pulse">Loading chat history...</div>
          </div>
        ) : (
          <>
            {messages.length === 0 ? (
              <div className="text-center text-gray-500 my-8 p-6">
                <div className="text-4xl mb-4">👋</div>
                <h3 className="text-lg font-semibold mb-2">Welcome! I'm here to help</h3>
                <p className="text-sm">
                  Start a conversation by asking me to create projects, add requirements, or get help with project management!
                </p>
              </div>
            ) : (
              messages.map((msg, idx) => (
                <div key={idx} className="mb-6">
                  <div className="bg-blue-50 border-l-4 border-blue-400 p-4 rounded-r-lg mb-3">
                    <div className="flex items-center justify-between mb-2">
                      <p className="font-semibold text-blue-800 flex items-center">
                        <span className="mr-2">👤</span>You
                      </p>
                      {msg.timestamp && (
                        <span className="text-xs text-blue-600">{msg.timestamp}</span>
                      )}
                    </div>
                    <p className="text-gray-700 leading-relaxed">{msg.prompt}</p>
                  </div>
                  <div className="bg-green-50 border-l-4 border-green-400 p-4 rounded-r-lg">
                    <div className="flex items-center mb-2">
                      <p className="font-semibold text-green-800 flex items-center">
                        <span className="mr-2">🤖</span>AI Assistant
                      </p>
                    </div>
                    <p className="whitespace-pre-wrap text-gray-700 leading-relaxed">
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
        <div className="bg-red-50 border-l-4 border-red-400 text-red-700 p-4 rounded-r-lg mb-4">
          <div className="flex items-center">
            <span className="mr-2">⚠️</span>
            <span>{error}</span>
          </div>
        </div>
      )}

      <form onSubmit={handleSubmit} className="mt-auto" data-testid="chat-form">
        <div className="flex flex-col sm:flex-row gap-2 sm:gap-0">
          <textarea
            id="message"
            className="flex-grow px-4 py-3 border-2 border-gray-300 rounded-lg sm:rounded-l-lg sm:rounded-r-none focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 resize-none text-gray-700 placeholder-gray-400 transition-colors"
            placeholder="Ask me to create projects, add requirements, or type 'help' for commands..."
            value={input}
            onChange={(e) => setInput(e.target.value)}
            rows={2}
            onKeyDown={(e) => {
              if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                handleSubmit(e);
              }
            }}
          />
          <button
            type="submit"
            disabled={isLoading || !input.trim()}
            className={`px-6 py-3 rounded-lg sm:rounded-l-none sm:rounded-r-lg text-white font-medium transition-all duration-200 ${
              isLoading || !input.trim()
                ? "bg-gray-400 cursor-not-allowed"
                : "bg-blue-600 hover:bg-blue-700 hover:shadow-md active:transform active:scale-95"
            }`}
          >
            {isLoading ? (
              <span className="flex items-center">
                <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Sending...
              </span>
            ) : (
              <span className="flex items-center">
                Send <span className="ml-1">📤</span>
              </span>
            )}
          </button>
        </div>
        <div className="text-xs text-gray-500 mt-2 text-center sm:text-left">
          Press Enter to send, Shift+Enter for new line
        </div>
      </form>
    </div>
  );
};

export default ChatInterface;
