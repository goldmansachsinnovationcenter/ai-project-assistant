/**
 * API client for interacting with the Spring AI backend
 */

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8000';

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
 * Send a message to the AI chat endpoint
 * @param message The message to send to the AI
 * @returns The AI's response (can be text or structured market data)
 */
export async function sendMessage(message: string): Promise<string | MarketResponse> {
  return chatWithMcp(message);
}

/**
 * Send a message to the AI chat endpoint using MCP
 * @param message The message to send to the AI
 * @returns The AI's response (can be text or structured JSON with chart data)
 */
export async function chatWithMcp(message: string): Promise<string | MarketResponse> {
  try {
    if (message.toLowerCase().includes('market') || 
        message.toLowerCase().includes('s&p') || 
        message.toLowerCase().includes('stock') ||
        message.toLowerCase().includes('chart') ||
        message.toLowerCase().includes('trend') ||
        message.toLowerCase().includes('news')) {
      
      const marketResponse = await getMarketDataResponse(message);
      if (marketResponse) {
        return marketResponse;
      }
    }
    
    // Fallback to original MCP chat endpoint
    const response = await fetch(`${API_URL}/api/ai/mcp-chat?message=${encodeURIComponent(message)}`, {
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
    const response = await fetch(`${API_URL}/api/ai/chat-history?limit=${limit}`, {
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

/**
 * Market data interfaces for S&P 500 chatbot
 */
export interface MarketData {
  symbol: string;
  current_price: string;
  volume: string;
  change_percent: string;
  timestamp: string;
}

export interface MarketNews {
  title: string;
  summary: string;
  source: string;
  sentiment: string;
  relevance_score: string;
  published_at: string;
  url: string;
}

export interface MarketPrediction {
  symbol: string;
  prediction_type: string;
  predicted_price: string;
  confidence_score: string;
  time_horizon: string;
  model_used: string;
  prediction_date: string;
  target_date: string;
}

export interface ChartData {
  chart_data: any;
  chart_type: string;
  symbol: string;
  days: number;
  date_range: {
    start: string;
    end: string;
  };
}

export interface MarketResponse {
  type: 'market_data' | 'market_news' | 'market_prediction' | 'chart_data' | 'text';
  data?: MarketData | MarketNews[] | MarketPrediction | ChartData;
  message?: string;
  status: 'success' | 'error';
}

/**
 * Get current S&P 500 market data
 * @param symbol Stock symbol (default: SPY)
 * @returns Current market data
 */
export async function getCurrentMarketData(symbol: string = 'SPY'): Promise<MarketData> {
  try {
    const response = await fetch(`${API_URL}/api/v1/market/data/current?symbol=${symbol}`, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      throw new Error(`API error: ${response.status}`);
    }
    
    const result = await response.json();
    return result.data;
  } catch (error) {
    console.error('Error getting current market data:', error);
    throw error;
  }
}

/**
 * Get market news with sentiment analysis
 * @param limit Number of news articles (default: 5)
 * @returns Market news array
 */
export async function getMarketNews(limit: number = 5): Promise<MarketNews[]> {
  try {
    const response = await fetch(`${API_URL}/api/v1/market/news?limit=${limit}`, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      throw new Error(`API error: ${response.status}`);
    }
    
    const result = await response.json();
    return result.data;
  } catch (error) {
    console.error('Error getting market news:', error);
    throw error;
  }
}

/**
 * Get market trend prediction
 * @param symbol Stock symbol (default: SPY)
 * @returns Market prediction
 */
export async function getMarketPrediction(symbol: string = 'SPY'): Promise<MarketPrediction> {
  try {
    const response = await fetch(`${API_URL}/api/v1/market/trends/prediction?symbol=${symbol}`, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      throw new Error(`API error: ${response.status}`);
    }
    
    const result = await response.json();
    return result.data;
  } catch (error) {
    console.error('Error getting market prediction:', error);
    throw error;
  }
}

/**
 * Get chart data for market visualization
 * @param symbol Stock symbol (default: SPY)
 * @param days Number of days (default: 30)
 * @param chartType Chart type (default: line)
 * @returns Chart data
 */
export async function getChartData(symbol: string = 'SPY', days: number = 30, chartType: string = 'line'): Promise<ChartData> {
  try {
    const response = await fetch(`${API_URL}/api/v1/market/chart/data?symbol=${symbol}&days=${days}&chart_type=${chartType}`, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      throw new Error(`API error: ${response.status}`);
    }
    
    const result = await response.json();
    return result.data;
  } catch (error) {
    console.error('Error getting chart data:', error);
    throw error;
  }
}

/**
 * Analyze message and return appropriate market data response
 * @param message User message
 * @returns Market response or null if not market-related
 */
async function getMarketDataResponse(message: string): Promise<MarketResponse | null> {
  try {
    const lowerMessage = message.toLowerCase();
    
    if (lowerMessage.includes('chart') || lowerMessage.includes('graph')) {
      const chartData = await getChartData();
      return {
        type: 'chart_data',
        data: chartData,
        status: 'success'
      };
    } else if (lowerMessage.includes('news')) {
      const news = await getMarketNews();
      return {
        type: 'market_news',
        data: news,
        status: 'success'
      };
    } else if (lowerMessage.includes('prediction') || lowerMessage.includes('forecast') || lowerMessage.includes('trend')) {
      const prediction = await getMarketPrediction();
      return {
        type: 'market_prediction',
        data: prediction,
        status: 'success'
      };
    } else if (lowerMessage.includes('price') || lowerMessage.includes('current') || lowerMessage.includes('data')) {
      const marketData = await getCurrentMarketData();
      return {
        type: 'market_data',
        data: marketData,
        status: 'success'
      };
    }
    
    return null;
  } catch (error) {
    console.error('Error getting market data response:', error);
    return {
      type: 'text',
      message: `Error fetching market data: ${error}`,
      status: 'error'
    };
  }
}
