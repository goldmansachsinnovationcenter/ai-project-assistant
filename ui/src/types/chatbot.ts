export interface ChatMessage {
  id: number;
  prompt: string;
  response: string;
  timestamp: string;
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
