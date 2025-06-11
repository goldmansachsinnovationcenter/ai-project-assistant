'use client';

import React from 'react';
import Link from 'next/link';
import ChatInterface from '../../components/ChatInterface';
import Breadcrumb from '../../components/Breadcrumb';

export default function ChatPage() {
  return (
    <main className="min-h-screen p-8 bg-gray-50">
      <div className="max-w-4xl mx-auto">
        <Breadcrumb items={[
          { label: 'Home', href: '/' },
          { label: 'Chat' }
        ]} />
        
        <div className="flex items-center justify-between mb-8">
          <h1 className="text-3xl font-bold">Chat with AI</h1>
          
          <div className="flex space-x-4">
            <Link 
              href="/"
              className="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition-colors"
            >
              Home
            </Link>
            <Link 
              href="/projects"
              className="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition-colors"
            >
              Projects
            </Link>
          </div>
        </div>
        
        <ChatInterface />
      </div>
    </main>
  );
}
