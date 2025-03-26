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

  it('handles API errors gracefully', async () => {
    fetchMock.mockRejectOnce(new Error('API error'));
    await expect(api.getProjects()).rejects.toThrow('API error');
  });
});
