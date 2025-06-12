/**
 * API client for interacting with the Spring AI backend
 */

const API_URL = 'http://localhost:8080';

/**
 * Send a message to the AI chat endpoint
 * @param message The message to send to the AI
 * @returns The AI's response
 */
export async function chatWithAI(message: string): Promise<string> {
  try {
    const response = await fetch(`${API_URL}/ai/chat?message=${encodeURIComponent(message)}`, {
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      throw new Error(`API error: ${response.status}`);
    }
    
    const data = await response.json();
    return data.generation || "No response from AI";
  } catch (error) {
    console.error('Error chatting with AI:', error);
    throw error;
  }
}

/**
 * Send a message to the AI chat endpoint
 * @param message The message to send to the AI
 * @returns The AI's response
 */
export async function sendMessage(message: string): Promise<string> {
  return chatWithMcp(message);
}

/**
 * Send a message to the AI chat endpoint using MCP
 * @param message The message to send to the AI
 * @returns The AI's response
 */
export async function chatWithMcp(message: string): Promise<string> {
  try {
    const response = await fetch(`${API_URL}/ai/chat?message=${encodeURIComponent(message)}`, {
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      throw new Error(`API error: ${response.status}`);
    }
    
    const data = await response.json();
    return data.generation || "No response from AI";
  } catch (error) {
    console.error('Error chatting with MCP AI:', error);
    throw error;
  }
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
    const response = await fetch(`${API_URL}/ai/chat-history?limit=${limit}`, {
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

export interface Conversation {
  id: string;
  userId: string;
  personality: string;
  context: string;
  startTime: string;
  lastActivity: string;
  messages: ChatMessage[];
}

export interface ChatbotAnalytics {
  id: string;
  conversationId: string;
  intent: string;
  sentiment: string;
  responseTime: number;
  timestamp: string;
}

export interface ChatbotSettings {
  personality: string;
  responseStyle: string;
  contextRetention: boolean;
}

export interface ConversationSummary {
  summary: string;
  analyticsCount: number;
  conversation: Conversation;
}

export interface AnalyticsData {
  timeRange: string;
  topIntents: [string, number][];
  averageResponseTime: number;
  sentimentDistribution: [string, number][];
}

export async function startConversation(userId: string = 'default-user', personality: string = 'helpful'): Promise<Conversation> {
  try {
    const response = await fetch(`${API_URL}/api/ai/chat/conversation`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ userId, personality }),
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('Error starting conversation:', error);
    throw error;
  }
}

export async function getConversation(conversationId: string): Promise<Conversation> {
  try {
    const response = await fetch(`${API_URL}/api/ai/chat/conversation/${conversationId}`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching conversation:', error);
    throw error;
  }
}

export async function getConversationSummary(conversationId: string): Promise<ConversationSummary> {
  try {
    const response = await fetch(`${API_URL}/api/ai/chat/conversation/${conversationId}/summary`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching conversation summary:', error);
    throw error;
  }
}

export async function updateChatbotPersonality(settings: ChatbotSettings): Promise<void> {
  try {
    const response = await fetch(`${API_URL}/api/ai/chat/personality`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        personality: settings.personality,
        responseStyle: settings.responseStyle,
      }),
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
  } catch (error) {
    console.error('Error updating chatbot personality:', error);
    throw error;
  }
}

export async function getUserConversations(userId: string = 'default-user'): Promise<Conversation[]> {
  try {
    const response = await fetch(`${API_URL}/api/ai/chat/conversations?userId=${encodeURIComponent(userId)}`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching user conversations:', error);
    throw error;
  }
}

export async function updateConversationContext(conversationId: string, context: string): Promise<void> {
  try {
    const response = await fetch(`${API_URL}/api/ai/chat/conversation/${conversationId}/context`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ context }),
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
  } catch (error) {
    console.error('Error updating conversation context:', error);
    throw error;
  }
}

export async function getChatbotAnalytics(timeRange: string = 'day'): Promise<AnalyticsData> {
  try {
    const response = await fetch(`${API_URL}/api/ai/chat/analytics?timeRange=${encodeURIComponent(timeRange)}`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching chatbot analytics:', error);
    throw error;
  }
}
