/**
 * API client for GSIC-TRACK onboarding platform
 */

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

// --- Types ---

export interface ConnectorType {
  type: string;
  displayName: string;
  description: string;
}

export interface ConnectorConfig {
  id: number;
  name: string;
  connectorType: string;
  enabled: boolean;
  configJson: string;
  createdAt: string;
  updatedAt: string;
}

export interface OnboardingUser {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  department: string;
  jobTitle: string;
  status: string;
  tasks: OnboardingTask[];
  createdAt: string;
  completedAt: string | null;
}

export interface OnboardingTask {
  id: number;
  connectorType: string;
  status: string;
  externalId: string | null;
  errorMessage: string | null;
  resultMessage: string | null;
  createdAt: string;
  startedAt: string | null;
  completedAt: string | null;
}

export interface OnboardingStats {
  totalUsers: number;
  pending: number;
  inProgress: number;
  completed: number;
  failed: number;
  partiallyCompleted: number;
}

export interface ValidationResult {
  valid: boolean;
  errors: string[];
}

export interface ExecutionResult {
  success: boolean;
  message: string;
  externalId?: string;
}

// --- Connector APIs ---

export async function getConnectorTypes(): Promise<ConnectorType[]> {
  const response = await fetch(`${API_URL}/api/onboarding/connectors/types`);
  if (!response.ok) throw new Error(`API error: ${response.status}`);
  return response.json();
}

export async function getConnectors(): Promise<ConnectorConfig[]> {
  const response = await fetch(`${API_URL}/api/onboarding/connectors`);
  if (!response.ok) throw new Error(`API error: ${response.status}`);
  return response.json();
}

export async function getConnectorById(id: number): Promise<ConnectorConfig> {
  const response = await fetch(`${API_URL}/api/onboarding/connectors/${id}`);
  if (!response.ok) throw new Error(`API error: ${response.status}`);
  return response.json();
}

export async function getEnabledConnectors(): Promise<ConnectorConfig[]> {
  const response = await fetch(`${API_URL}/api/onboarding/connectors/enabled`);
  if (!response.ok) throw new Error(`API error: ${response.status}`);
  return response.json();
}

export async function createConnector(config: Partial<ConnectorConfig>): Promise<ConnectorConfig> {
  const response = await fetch(`${API_URL}/api/onboarding/connectors`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(config),
  });
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.join(', ') || `API error: ${response.status}`);
  }
  return response.json();
}

export async function updateConnector(id: number, config: Partial<ConnectorConfig>): Promise<ConnectorConfig> {
  const response = await fetch(`${API_URL}/api/onboarding/connectors/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(config),
  });
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.join(', ') || `API error: ${response.status}`);
  }
  return response.json();
}

export async function deleteConnector(id: number): Promise<void> {
  const response = await fetch(`${API_URL}/api/onboarding/connectors/${id}`, {
    method: 'DELETE',
  });
  if (!response.ok) throw new Error(`API error: ${response.status}`);
}

export async function toggleConnector(id: number): Promise<ConnectorConfig> {
  const response = await fetch(`${API_URL}/api/onboarding/connectors/${id}/toggle`, {
    method: 'PUT',
  });
  if (!response.ok) throw new Error(`API error: ${response.status}`);
  return response.json();
}

export async function testConnector(id: number): Promise<ExecutionResult> {
  const response = await fetch(`${API_URL}/api/onboarding/connectors/${id}/test`, {
    method: 'POST',
  });
  if (!response.ok) throw new Error(`API error: ${response.status}`);
  return response.json();
}

export async function validateConnector(config: Partial<ConnectorConfig>): Promise<ValidationResult> {
  const response = await fetch(`${API_URL}/api/onboarding/connectors/validate`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(config),
  });
  if (!response.ok) throw new Error(`API error: ${response.status}`);
  return response.json();
}

// --- Onboarding User APIs ---

export async function getOnboardingUsers(): Promise<OnboardingUser[]> {
  const response = await fetch(`${API_URL}/api/onboarding/users`);
  if (!response.ok) throw new Error(`API error: ${response.status}`);
  return response.json();
}

export async function getOnboardingUserById(id: number): Promise<OnboardingUser> {
  const response = await fetch(`${API_URL}/api/onboarding/users/${id}`);
  if (!response.ok) throw new Error(`API error: ${response.status}`);
  return response.json();
}

export async function createOnboardingUser(user: Partial<OnboardingUser>): Promise<OnboardingUser> {
  const response = await fetch(`${API_URL}/api/onboarding/users`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(user),
  });
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error || `API error: ${response.status}`);
  }
  return response.json();
}

export async function updateOnboardingUser(id: number, user: Partial<OnboardingUser>): Promise<OnboardingUser> {
  const response = await fetch(`${API_URL}/api/onboarding/users/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(user),
  });
  if (!response.ok) throw new Error(`API error: ${response.status}`);
  return response.json();
}

export async function deleteOnboardingUser(id: number): Promise<void> {
  const response = await fetch(`${API_URL}/api/onboarding/users/${id}`, {
    method: 'DELETE',
  });
  if (!response.ok) throw new Error(`API error: ${response.status}`);
}

export async function executeOnboarding(userId: number): Promise<OnboardingUser> {
  const response = await fetch(`${API_URL}/api/onboarding/users/${userId}/execute`, {
    method: 'POST',
  });
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error || `API error: ${response.status}`);
  }
  return response.json();
}

export async function getTasksForUser(userId: number): Promise<OnboardingTask[]> {
  const response = await fetch(`${API_URL}/api/onboarding/users/${userId}/tasks`);
  if (!response.ok) throw new Error(`API error: ${response.status}`);
  return response.json();
}

export async function retryTask(taskId: number): Promise<OnboardingTask> {
  const response = await fetch(`${API_URL}/api/onboarding/users/tasks/${taskId}/retry`, {
    method: 'POST',
  });
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error || `API error: ${response.status}`);
  }
  return response.json();
}

export async function getOnboardingStats(): Promise<OnboardingStats> {
  const response = await fetch(`${API_URL}/api/onboarding/users/stats`);
  if (!response.ok) throw new Error(`API error: ${response.status}`);
  return response.json();
}
