/**
 * API client for interacting with the Spring AI backend
 */

const apiUrlWithoutCredentials = process.env.NEXT_PUBLIC_API_URL ? 
  process.env.NEXT_PUBLIC_API_URL.replace(/^(https?:\/\/).*?@/, '$1') : 
  'http://localhost:8080';
const API_URL = apiUrlWithoutCredentials;

/**
 * Send a message to the AI chat endpoint
 * @param message The message to send to the AI
 * @returns The AI's response
 */
export async function chatWithAI(message: string): Promise<string> {
  try {
    const response = await fetch(`${API_URL}/api/ai/chat?message=${encodeURIComponent(message)}`, {
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      throw new Error(`API error: ${response.status}`);
    }
    
    return await response.text();
  } catch (error) {
    console.error('Error chatting with AI:', error);
    throw error;
  }
}

/**
 * Send a message to the AI chat endpoint (alias for chatWithAI)
 * @param message The message to send to the AI
 * @returns The AI's response
 */
export async function sendMessage(message: string): Promise<string> {
  return chatWithAI(message);
}

/**
 * Get a summary about a topic using the AI template endpoint
 * @param topic The topic to get a summary about
 * @returns The AI's summary response
 */
export async function getTopicSummary(topic: string): Promise<string> {
  try {
    const response = await fetch(`${API_URL}/api/ai/template?topic=${encodeURIComponent(topic)}`, {
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      throw new Error(`API error: ${response.status}`);
    }
    
    return await response.text();
  } catch (error) {
    console.error('Error getting topic summary:', error);
    throw error;
  }
}

/**
 * Check the health of the backend API
 * @returns The health status response
 */
export async function checkApiHealth(): Promise<{status: string, message: string}> {
  try {
    const response = await fetch(`${API_URL}/api/health`, {
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      throw new Error(`API error: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error checking API health:', error);
    throw error;
  }
}

/**
 * Project interface with all related entities
 */
export interface Project {
  id: number;
  name: string;
  description: string;
  requirements: Requirement[];
  stories: Story[];
  queries: Query[];
  risks: Risk[];
  nfrs: NFR[];
}

/**
 * Requirement interface
 */
export interface Requirement {
  id: number;
  text: string;
}

/**
 * Story interface
 */
export interface Story {
  id: number;
  title: string;
  description: string;
}

/**
 * Query interface
 */
export interface Query {
  id: number;
  question: string;
  context: string;
}

/**
 * Risk interface
 */
export interface Risk {
  id: number;
  description: string;
  mitigation: string;
}

/**
 * NFR (Non-Functional Requirement) interface
 */
export interface NFR {
  id: number;
  category: string;
  description: string;
}

/**
 * ChatMessage interface for chat history
 */
export interface ChatMessage {
  id: number;
  prompt: string;
  response: string;
  timestamp: string;
}

/**
 * Get chat history from the API
 * @param limit Number of messages to retrieve (default: 20)
 * @returns List of chat messages
 */
export async function getChatHistory(limit: number = 20): Promise<ChatMessage[]> {
  try {
    console.log('Fetching chat history from:', `${API_URL}/api/ai/chat-history?limit=${limit}`);
    const response = await fetch(`${API_URL}/api/ai/chat-history?limit=${limit}`, {
      // credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      console.error(`API error status: ${response.status}`);
      throw new Error(`API error: ${response.status}`);
    }
    
    const data = await response.json();
    console.log('Chat history data received:', data);
    return data;
  } catch (error) {
    console.error('Error getting chat history:', error);
    throw error;
  }
}

/**
 * Create a new project
 * @param name Project name
 * @param description Optional project description
 * @returns The created project data
 */
export async function createProject(name: string, description?: string): Promise<Project> {
  try {
    const params = new URLSearchParams();
    params.append('name', name);
    if (description) {
      params.append('description', description);
    }
    
    const response = await fetch(`${API_URL}/api/projects?${params.toString()}`, {
      method: 'POST',
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      throw new Error(`API error: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error creating project:', error);
    throw error;
  }
}

/**
 * Get all projects
 * @returns List of projects
 */
export async function getProjects(): Promise<Project[]> {
  try {
    const response = await fetch(`${API_URL}/api/projects`, {
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      throw new Error(`API error: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error getting projects:', error);
    throw error;
  }
}

/**
 * Get a project by name
 * @param name Project name
 * @returns Project details
 */
export async function getProjectByName(name: string): Promise<Project> {
  try {
    const response = await fetch(`${API_URL}/api/db/projects/${encodeURIComponent(name)}`, {
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      throw new Error(`API error: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error getting project details:', error);
    throw error;
  }
}

/**
 * Get all projects with detailed information
 * @returns List of all projects with all related entities
 */
export async function getAllProjectsWithDetails(): Promise<{count: number, projects: Project[]}> {
  try {
    const response = await fetch(`${API_URL}/api/db/projects`, {
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      throw new Error(`API error: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error getting detailed projects:', error);
    throw error;
  }
}
