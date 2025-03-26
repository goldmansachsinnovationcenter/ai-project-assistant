import React from 'react';
import { render, screen } from '@testing-library/react';
import ProjectList from '../ProjectList';
import { Project } from '../../lib/api';

describe('ProjectList Additional Tests', () => {
  it('renders risks and queries when they exist', () => {
    const handleSelectProject = jest.fn();
    const projectWithRisksAndQueries: Project = {
      id: 5,
      name: 'Project with Risks and Queries',
      description: 'Test description',
      requirements: [],
      stories: [],
      risks: [{ id: 1, description: 'Risk 1', mitigation: 'Mitigation 1' }],
      queries: [{ id: 1, question: 'Query 1', context: 'Context 1' }],
      nfrs: []
    };
    
    render(
      <ProjectList 
        projects={[projectWithRisksAndQueries]} 
        selectedProject={null} 
        onSelectProject={handleSelectProject} 
      />
    );
    
    expect(screen.getByText('1 risks')).toBeInTheDocument();
    expect(screen.getByText('1 queries')).toBeInTheDocument();
  });
  
  it('handles project with empty description', () => {
    const handleSelectProject = jest.fn();
    const projectWithEmptyDescription: Project = {
      id: 6,
      name: 'Empty Description Project',
      description: '',
      requirements: [],
      stories: [],
      risks: [],
      queries: [],
      nfrs: []
    };
    
    render(
      <ProjectList 
        projects={[projectWithEmptyDescription]} 
        selectedProject={null} 
        onSelectProject={handleSelectProject} 
      />
    );
    
    expect(screen.getByText('Empty Description Project')).toBeInTheDocument();
    expect(screen.getByText('No description')).toBeInTheDocument();
  });
});
