import Image from "next/image";
import Link from "next/link";

export default function Home() {
  return (
    <main className="min-h-screen p-8 bg-gray-50">
      <div className="max-w-4xl mx-auto">
        <div className="flex items-center justify-center mb-8">
          <Image
            className="dark:invert mr-4"
            src="/next.svg"
            alt="Next.js logo"
            width={120}
            height={30}
            priority
          />
          <h1 className="text-3xl font-bold">Spring AI Chat</h1>
        </div>
        
        <div className="bg-white rounded-lg shadow-md p-6">
          <p className="mb-4">
            This application demonstrates integration between Next.js and Spring AI.
            The UI is built with Next.js 15 and Tailwind CSS, while the backend uses
            Spring Boot with Spring AI and Ollama integration.
          </p>
          
          <div className="bg-blue-50 p-4 rounded-md mb-6">
            <h2 className="text-xl font-semibold mb-2">Getting Started</h2>
            <p>
              Use the links below to access the chat interface or topic summary tool.
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <Link 
              href="/chat" 
              className="block p-4 bg-blue-600 text-white rounded-md text-center hover:bg-blue-700 transition-colors"
            >
              Go to Chat Interface
            </Link>
            <Link 
              href="/projects" 
              className="block p-4 bg-green-600 text-white rounded-md text-center hover:bg-green-700 transition-colors"
            >
              Project Manager
            </Link>
            <Link 
              href="/summary" 
              className="block p-4 bg-gray-200 text-gray-800 rounded-md text-center hover:bg-gray-300 transition-colors"
            >
              Topic Summary Tool
            </Link>
          </div>
        </div>
      </div>
    </main>
  );
}
