import React from 'react';
import { render, screen, waitFor, act, fireEvent } from '@testing-library/react';
import ProjectsPage from '../page';
import * as api from '../../../lib/api';

// Mock the API module
jest.mock('../../../lib/api', () => ({
  getAllProjectsWithDetails: jest.fn()
}));

// Mock next/navigation
jest.mock('next/navigation', () => ({
  useRouter: () => ({
    push: jest.fn(),
  }),
}));

describe('ProjectsPage', () => {
  const mockProjects = {
    count: 2,
    projects: [
      { 
        id: 1, 
        name: 'Test Project 1', 
        description: 'Description 1',
        requirements: [{ id: 1, text: 'Requirement 1' }],
        stories: [{ id: 1, title: 'Story 1', description: 'Story description' }],
        risks: [{ id: 1, description: 'Risk 1', mitigation: 'Mitigation 1' }],
        nfrs: [{ id: 1, category: 'Performance', description: 'NFR 1' }],
        queries: [{ id: 1, question: 'Query 1', context: 'Context 1' }]
      },
      { 
        id: 2, 
        name: 'Test Project 2', 
        description: 'Description 2',
        requirements: [],
        stories: [],
        risks: [],
        nfrs: [],
        queries: []
      }
    ]
  };

  beforeEach(() => {
    jest.clearAllMocks();
    // Mock API responses
    (api.getAllProjectsWithDetails as jest.Mock).mockResolvedValue(mockProjects);
  });

  test('renders projects page with loading state', async () => {
    render(<ProjectsPage />);
    
    expect(screen.getByText('Loading projects...')).toBeInTheDocument();
    
    await waitFor(() => {
      expect(screen.getByText('Project Manager')).toBeInTheDocument();
    });
    
    // Check API was called
    expect(api.getAllProjectsWithDetails).toHaveBeenCalled();
  });

  test('displays projects after loading', async () => {
    render(<ProjectsPage />);
    
    await waitFor(() => {
      expect(screen.getAllByText('Test Project 1')[0]).toBeInTheDocument();
      expect(screen.getByText('Test Project 2')).toBeInTheDocument();
    });
    
    expect(screen.getByText('Requirements (1)')).toBeInTheDocument();
  });

  test('allows selecting a different project', async () => {
    render(<ProjectsPage />);
    
    await waitFor(() => {
      expect(screen.getAllByText('Test Project 1')[0]).toBeInTheDocument();
      expect(screen.getByText('Test Project 2')).toBeInTheDocument();
    });
    
    fireEvent.click(screen.getByText('Test Project 2'));
    
    expect(screen.getByText('Test Project 2')).toBeInTheDocument();
    expect(screen.getByText('No requirements defined yet.')).toBeInTheDocument();
  });

  test('displays project details with all sections', async () => {
    render(<ProjectsPage />);
    
    await waitFor(() => {
      expect(screen.getAllByText('Test Project 1')[0]).toBeInTheDocument();
    });
    
    expect(screen.getByText('Requirements (1)')).toBeInTheDocument();
    expect(screen.getByText('Stories (1)')).toBeInTheDocument();
    expect(screen.getByText('Queries (1)')).toBeInTheDocument();
    expect(screen.getByText('Risks (1)')).toBeInTheDocument();
    expect(screen.getByText('Non-Functional Requirements (1)')).toBeInTheDocument();
    
    expect(screen.getByText('Requirement 1')).toBeInTheDocument();
    expect(screen.getByText('Story 1')).toBeInTheDocument();
    expect(screen.getByText('Query 1')).toBeInTheDocument();
    expect(screen.getByText('Risk 1')).toBeInTheDocument();
    expect(screen.getByText('Performance')).toBeInTheDocument();
    expect(screen.getByText('NFR 1')).toBeInTheDocument();
    expect(screen.getByText('Mitigation:')).toBeInTheDocument();
    expect(screen.getByText('Context 1')).toBeInTheDocument();
  });

  test('handles API error', async () => {
    // Mock API error
    (api.getAllProjectsWithDetails as jest.Mock).mockRejectedValue(new Error('API error'));
    
    render(<ProjectsPage />);
    
    // Wait for error message
    await waitFor(() => {
      expect(screen.getByText('Failed to load projects. Please try again.')).toBeInTheDocument();
    });
  });

  test('displays empty state when no projects are found', async () => {
    (api.getAllProjectsWithDetails as jest.Mock).mockResolvedValue({ count: 0, projects: [] });
    
    render(<ProjectsPage />);
    
    await waitFor(() => {
      expect(screen.getByText('No projects found. Use the chat to create a new project.')).toBeInTheDocument();
    });
  });
});
