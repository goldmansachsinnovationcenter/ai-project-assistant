import * as api from '../api';
import fetchMock from 'jest-fetch-mock';

// Enable fetch mocks
fetchMock.enableMocks();

describe('API Client', () => {
  beforeEach(() => {
    fetchMock.resetMocks();
  });

  it('chatWithAI sends request to correct endpoint', async () => {
    fetchMock.mockResponseOnce('AI response');
    const response = await api.chatWithAI('Hello');
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining('/api/ai/chat?message=Hello'),
      expect.any(Object)
    );
    expect(response).toBe('AI response');
  });

  it('chatWithAI handles API errors', async () => {
    fetchMock.mockRejectOnce(new Error('Network error'));
    await expect(api.chatWithAI('Hello')).rejects.toThrow('Network error');
  });

  it('chatWithAI handles non-OK responses', async () => {
    fetchMock.mockResponseOnce('Not Found', { status: 404 });
    await expect(api.chatWithAI('Hello')).rejects.toThrow('API error: 404');
  });

  it('sendMessage calls chatWithAI', async () => {
    fetchMock.mockResponseOnce('AI response');
    const response = await api.sendMessage('Hello');
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining('/api/ai/chat?message=Hello'),
      expect.any(Object)
    );
    expect(response).toBe('AI response');
  });

  it('getTopicSummary sends request to correct endpoint', async () => {
    fetchMock.mockResponseOnce('Topic summary');
    const response = await api.getTopicSummary('AI');
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining('/api/ai/template?topic=AI'),
      expect.any(Object)
    );
    expect(response).toBe('Topic summary');
  });

  it('getTopicSummary handles API errors', async () => {
    fetchMock.mockRejectOnce(new Error('Network error'));
    await expect(api.getTopicSummary('AI')).rejects.toThrow('Network error');
  });

  it('getTopicSummary handles non-OK responses', async () => {
    fetchMock.mockResponseOnce('Not Found', { status: 404 });
    await expect(api.getTopicSummary('AI')).rejects.toThrow('API error: 404');
  });

  it('checkApiHealth fetches health status', async () => {
    const mockHealth = { status: 'UP', message: 'Service is healthy' };
    fetchMock.mockResponseOnce(JSON.stringify(mockHealth));
    const health = await api.checkApiHealth();
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining('/api/health'),
      expect.any(Object)
    );
    expect(health).toEqual(mockHealth);
  });

  it('checkApiHealth handles API errors', async () => {
    fetchMock.mockRejectOnce(new Error('Network error'));
    await expect(api.checkApiHealth()).rejects.toThrow('Network error');
  });

  it('checkApiHealth handles non-OK responses', async () => {
    fetchMock.mockResponseOnce('Not Found', { status: 404 });
    await expect(api.checkApiHealth()).rejects.toThrow('API error: 404');
  });

  it('getChatHistory fetches chat messages', async () => {
    const mockMessages = [
      { id: 1, prompt: 'Hello', response: 'Hi there', timestamp: '2023-01-01T12:00:00Z' }
    ];
    fetchMock.mockResponseOnce(JSON.stringify(mockMessages));
    const messages = await api.getChatHistory();
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining('/api/ai/chat-history?limit=20'),
      expect.any(Object)
    );
    expect(messages).toEqual(mockMessages);
  });

  it('getChatHistory with custom limit', async () => {
    const mockMessages = [
      { id: 1, prompt: 'Hello', response: 'Hi there', timestamp: '2023-01-01T12:00:00Z' }
    ];
    fetchMock.mockResponseOnce(JSON.stringify(mockMessages));
    const messages = await api.getChatHistory(10);
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining('/api/ai/chat-history?limit=10'),
      expect.any(Object)
    );
    expect(messages).toEqual(mockMessages);
  });

  it('getChatHistory handles API errors', async () => {
    fetchMock.mockRejectOnce(new Error('Network error'));
    await expect(api.getChatHistory()).rejects.toThrow('Network error');
  });

  it('getChatHistory handles non-OK responses', async () => {
    fetchMock.mockResponseOnce('Not Found', { status: 404 });
    await expect(api.getChatHistory()).rejects.toThrow('API error: 404');
  });

  it('createProject sends correct data', async () => {
    const mockProject = { id: 1, name: 'New Project', description: 'Test project' };
    fetchMock.mockResponseOnce(JSON.stringify(mockProject));
    const project = await api.createProject('New Project', 'Test project');
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining('/api/projects?name=New%20Project&description=Test%20project'),
      expect.objectContaining({
        method: 'POST'
      })
    );
    expect(project).toEqual(mockProject);
  });

  it('createProject without description', async () => {
    const mockProject = { id: 1, name: 'New Project' };
    fetchMock.mockResponseOnce(JSON.stringify(mockProject));
    const project = await api.createProject('New Project');
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining('/api/projects?name=New%20Project'),
      expect.objectContaining({
        method: 'POST'
      })
    );
    expect(project).toEqual(mockProject);
  });

  it('createProject handles API errors', async () => {
    fetchMock.mockRejectOnce(new Error('Network error'));
    await expect(api.createProject('New Project')).rejects.toThrow('Network error');
  });

  it('createProject handles non-OK responses', async () => {
    fetchMock.mockResponseOnce('Not Found', { status: 404 });
    await expect(api.createProject('New Project')).rejects.toThrow('API error: 404');
  });

  it('getProjects fetches projects from API', async () => {
    const mockProjects = [{ id: 1, name: 'Test Project' }];
    fetchMock.mockResponseOnce(JSON.stringify(mockProjects));
    const projects = await api.getProjects();
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining('/api/projects'),
      expect.any(Object)
    );
    expect(projects).toEqual(mockProjects);
  });

  it('getProjects handles API errors', async () => {
    fetchMock.mockRejectOnce(new Error('Network error'));
    await expect(api.getProjects()).rejects.toThrow('Network error');
  });

  it('getProjects handles non-OK responses', async () => {
    fetchMock.mockResponseOnce('Not Found', { status: 404 });
    await expect(api.getProjects()).rejects.toThrow('API error: 404');
  });

  it('getProjectByName fetches project details', async () => {
    const mockProject = { id: 1, name: 'Test Project', description: 'A test project' };
    fetchMock.mockResponseOnce(JSON.stringify(mockProject));
    const project = await api.getProjectByName('Test Project');
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining('/api/db/projects/Test%20Project'),
      expect.any(Object)
    );
    expect(project).toEqual(mockProject);
  });

  it('getProjectByName handles API errors', async () => {
    fetchMock.mockRejectOnce(new Error('Network error'));
    await expect(api.getProjectByName('Test Project')).rejects.toThrow('Network error');
  });

  it('getProjectByName handles non-OK responses', async () => {
    fetchMock.mockResponseOnce('Not Found', { status: 404 });
    await expect(api.getProjectByName('Test Project')).rejects.toThrow('API error: 404');
  });

  it('getAllProjectsWithDetails fetches detailed projects', async () => {
    const mockResponse = { 
      count: 1, 
      projects: [{ id: 1, name: 'Test Project', description: 'A test project' }] 
    };
    fetchMock.mockResponseOnce(JSON.stringify(mockResponse));
    const result = await api.getAllProjectsWithDetails();
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining('/api/db/projects'),
      expect.any(Object)
    );
    expect(result).toEqual(mockResponse);
  });

  it('getAllProjectsWithDetails handles API errors', async () => {
    fetchMock.mockRejectOnce(new Error('Network error'));
    await expect(api.getAllProjectsWithDetails()).rejects.toThrow('Network error');
  });

  it('getAllProjectsWithDetails handles non-OK responses', async () => {
    fetchMock.mockResponseOnce('Not Found', { status: 404 });
    await expect(api.getAllProjectsWithDetails()).rejects.toThrow('API error: 404');
  });
});
