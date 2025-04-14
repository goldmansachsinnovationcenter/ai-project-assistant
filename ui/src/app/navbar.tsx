'use client';

import React from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';

export default function Navbar() {
  const pathname = usePathname();

  const isActive = (path: string) => {
    return pathname === path ? 'bg-blue-700' : '';
  };

  return (
    <nav className="bg-blue-600 text-white p-4">
      <div className="container mx-auto flex flex-wrap items-center justify-between">
        <div className="flex items-center">
          <span className="font-bold text-xl">AI Project Assistant</span>
        </div>
        <div className="flex space-x-4">
          <Link href="/" className={`px-3 py-2 rounded hover:bg-blue-700 ${isActive('/')}`}>
            Home
          </Link>
          <Link href="/projects" className={`px-3 py-2 rounded hover:bg-blue-700 ${isActive('/projects')}`}>
            Projects
          </Link>
          <Link href="/chat" className={`px-3 py-2 rounded hover:bg-blue-700 ${isActive('/chat')}`}>
            Chat
          </Link>
          <Link href="/code-quality" className={`px-3 py-2 rounded hover:bg-blue-700 ${isActive('/code-quality')}`}>
            Code Quality
          </Link>
        </div>
      </div>
    </nav>
  );
}
