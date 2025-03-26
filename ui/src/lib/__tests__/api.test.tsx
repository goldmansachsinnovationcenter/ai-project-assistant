import { sendMessage, getChatHistory, getProjects, getProjectByName, getAllProjectsWithDetails } from '../api';

// Mock fetch
global.fetch = jest.fn();

describe('API Client', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (global.fetch as jest.Mock).mockResolvedValue({
      ok: true,
      text: jest.fn().mockResolvedValue('test response'),
      json: jest.fn().mockResolvedValue({ data: 'test' })
    });
  });

  test('sendMessage calls the correct endpoint', async () => {
    await sendMessage('Test message');
    
    expect(global.fetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/ai/chat?message=Test%20message'),
      expect.objectContaining({
        credentials: 'include',
        headers: expect.objectContaining({
          'Content-Type': 'application/json'
        })
      })
    );
  });

  test('getChatHistory calls the correct endpoint', async () => {
    await getChatHistory(10);
    
    expect(global.fetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/ai/chat-history?limit=10'),
      expect.objectContaining({
        credentials: 'include',
        headers: expect.objectContaining({
          'Content-Type': 'application/json'
        })
      })
    );
  });

  test('getProjects calls the correct endpoint', async () => {
    await getProjects();
    
    expect(global.fetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/projects'),
      expect.objectContaining({
        credentials: 'include',
        headers: expect.objectContaining({
          'Content-Type': 'application/json'
        })
      })
    );
  });

  test('getProjectByName calls the correct endpoint', async () => {
    await getProjectByName('TestProject');
    
    expect(global.fetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/db/projects/TestProject'),
      expect.objectContaining({
        credentials: 'include',
        headers: expect.objectContaining({
          'Content-Type': 'application/json'
        })
      })
    );
  });

  test('getAllProjectsWithDetails calls the correct endpoint', async () => {
    await getAllProjectsWithDetails();
    
    expect(global.fetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/db/projects'),
      expect.objectContaining({
        credentials: 'include',
        headers: expect.objectContaining({
          'Content-Type': 'application/json'
        })
      })
    );
  });

  test('handles fetch errors', async () => {
    (global.fetch as jest.Mock).mockRejectedValue(new Error('Network error'));
    
    await expect(sendMessage('Test')).rejects.toThrow('Network error');
  });

  test('handles non-ok responses', async () => {
    (global.fetch as jest.Mock).mockResolvedValue({
      ok: false,
      status: 500,
      statusText: 'Server Error'
    });
    
    await expect(sendMessage('Test')).rejects.toThrow('API error: 500');
  });
});
