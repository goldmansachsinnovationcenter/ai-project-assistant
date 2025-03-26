import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import ProjectList from '../ProjectList';
import { Project } from '../../lib/api';

// Mock project data
const mockProjects: Project[] = [
  {
    id: 1,
    name: 'Test Project 1',
    description: 'Description for test project 1',
    requirements: [{ id: 1, text: 'Requirement 1' }],
    stories: [],
    queries: [],
    risks: [],
    nfrs: []
  },
  {
    id: 2,
    name: 'Test Project 2',
    description: 'Description for test project 2',
    requirements: [],
    stories: [{ id: 1, title: 'Story 1', description: 'Story description' }],
    queries: [],
    risks: [],
    nfrs: []
  },
  {
    id: 3,
    name: 'Test Project 3',
    description: 'Project with risks and queries',
    requirements: [],
    stories: [],
    queries: [{ id: 1, question: 'Query 1', context: 'Context 1' }],
    risks: [{ id: 1, description: 'Risk 1', mitigation: 'Mitigation 1' }],
    nfrs: []
  },
  {
    id: 4,
    name: 'Test Project 4',
    description: '',
    requirements: [],
    stories: [],
    queries: [],
    risks: [],
    nfrs: []
  }
];

describe('ProjectList', () => {
  it('renders project list correctly', () => {
    const handleSelectProject = jest.fn();
    render(
      <ProjectList 
        projects={mockProjects} 
        selectedProject={mockProjects[0]} 
        onSelectProject={handleSelectProject} 
      />
    );
    
    expect(screen.getByText('Test Project 1')).toBeInTheDocument();
    expect(screen.getByText('Description for test project 1')).toBeInTheDocument();
    expect(screen.getByText('Test Project 2')).toBeInTheDocument();
    expect(screen.getByText('Description for test project 2')).toBeInTheDocument();
    expect(screen.getByText('1 requirements')).toBeInTheDocument();
    expect(screen.getByText('1 stories')).toBeInTheDocument();
  });
  
  it('calls onSelectProject when a project is clicked', () => {
    const handleSelectProject = jest.fn();
    render(
      <ProjectList 
        projects={mockProjects} 
        selectedProject={mockProjects[0]} 
        onSelectProject={handleSelectProject} 
      />
    );
    
    fireEvent.click(screen.getByText('Test Project 2'));
    expect(handleSelectProject).toHaveBeenCalledWith(mockProjects[1]);
  });
  
  it('displays "No projects found" when projects array is empty', () => {
    const handleSelectProject = jest.fn();
    render(
      <ProjectList 
        projects={[]} 
        selectedProject={null} 
        onSelectProject={handleSelectProject} 
      />
    );
    
    expect(screen.getByText('No projects found')).toBeInTheDocument();
  });
  
  it('highlights the selected project', () => {
    const handleSelectProject = jest.fn();
    const { container } = render(
      <ProjectList 
        projects={mockProjects} 
        selectedProject={mockProjects[0]} 
        onSelectProject={handleSelectProject} 
      />
    );
    
    // Check that the first project has the highlight class
    const projectElements = container.querySelectorAll('.cursor-pointer');
    expect(projectElements[0]).toHaveClass('bg-blue-50');
    expect(projectElements[1]).not.toHaveClass('bg-blue-50');
  });
});
