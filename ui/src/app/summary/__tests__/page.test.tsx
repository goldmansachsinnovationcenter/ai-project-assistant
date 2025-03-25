import React from 'react';
import { render, screen, act } from '@testing-library/react';
import SummaryPage from '../page';
import * as api from '../../../lib/api';

// Mock the API module
jest.mock('../../../lib/api', () => ({
  getProjects: jest.fn(),
  getProjectDetails: jest.fn(),
  getAllProjectsWithDetails: jest.fn()
}));

describe('SummaryPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // Mock API responses
    (api.getProjects as jest.Mock).mockResolvedValue([
      { id: 1, name: 'Test Project 1', description: 'Description 1' },
      { id: 2, name: 'Test Project 2', description: 'Description 2' }
    ]);
    (api.getAllProjectsWithDetails as jest.Mock).mockResolvedValue([
      { 
        id: 1, 
        name: 'Test Project 1', 
        description: 'Description 1',
        requirements: [{ id: 1, text: 'Requirement 1' }],
        stories: [{ id: 1, title: 'Story 1', description: 'Story description' }],
        risks: [{ id: 1, description: 'Risk 1' }],
        nfrs: [{ id: 1, description: 'NFR 1' }],
        queries: [{ id: 1, question: 'Query 1' }]
      }
    ]);
  });

  test('renders summary page', async () => {
    await act(async () => {
      render(<SummaryPage />);
    });
    
    // Check page title
    expect(screen.getByText('Topic Summary Tool')).toBeInTheDocument();
  });

  test('handles API error', async () => {
    // Mock API error
    (api.getAllProjectsWithDetails as jest.Mock).mockRejectedValue(new Error('API error'));
    
    await act(async () => {
      render(<SummaryPage />);
    });
    
    // The page doesn't show an error message for API errors, so we just check that it renders
    expect(screen.getByText('Topic Summary Tool')).toBeInTheDocument();
  });
});
