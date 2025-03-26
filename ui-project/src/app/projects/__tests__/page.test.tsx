import React from 'react';
import { render, screen, waitFor, act } from '@testing-library/react';
import ProjectsPage from '../page';
import * as api from '../../../lib/api';

// Mock the API module
jest.mock('../../../lib/api', () => ({
  getProjects: jest.fn(),
  getProjectDetails: jest.fn(),
  getAllProjectsWithDetails: jest.fn()
}));

// Mock next/navigation
jest.mock('next/navigation', () => ({
  useRouter: () => ({
    push: jest.fn(),
  }),
}));

describe('ProjectsPage', () => {
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
    (api.getProjectDetails as jest.Mock).mockResolvedValue({
      id: 1,
      name: 'Test Project 1',
      description: 'Description 1',
      requirements: [{ id: 1, text: 'Requirement 1' }],
      stories: [{ id: 1, title: 'Story 1', description: 'Story description' }],
      risks: [{ id: 1, description: 'Risk 1' }],
      nfrs: [{ id: 1, description: 'NFR 1' }],
      queries: [{ id: 1, question: 'Query 1' }]
    });
  });

  test('renders projects page', async () => {
    await act(async () => {
      render(<ProjectsPage />);
    });
    
    // Check page title
    expect(screen.getByText('Project Manager')).toBeInTheDocument();
    
    // Check API was called
    expect(api.getAllProjectsWithDetails).toHaveBeenCalled();
  });

  test('handles API error', async () => {
    // Mock API error
    (api.getAllProjectsWithDetails as jest.Mock).mockRejectedValue(new Error('API error'));
    
    await act(async () => {
      render(<ProjectsPage />);
    });
    
    // Wait for error message
    await waitFor(() => {
      expect(screen.getByText(/Failed to load projects/)).toBeInTheDocument();
    });
  });
});
