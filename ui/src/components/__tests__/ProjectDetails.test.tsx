import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import ProjectDetails from '../ProjectDetails';
import { Project } from '../../lib/api';

// Mock project data with all entity types
const mockProject: Project = {
  id: 1,
  name: 'Test Project',
  description: 'Test Description',
  requirements: [{ id: 1, text: 'Test Requirement' }],
  stories: [{ id: 1, title: 'Test Story', description: 'Story Description' }],
  risks: [{ id: 1, description: 'Test Risk', mitigation: 'Test Mitigation' }],
  queries: [{ id: 1, question: 'Test Question', context: 'Test Context' }],
  nfrs: [{ id: 1, category: 'Performance', description: 'Test NFR' }]
};

describe('ProjectDetails', () => {
  it('renders project details correctly', () => {
    render(<ProjectDetails project={mockProject} />);
    
    expect(screen.getByText('Test Project')).toBeInTheDocument();
    expect(screen.getByText('Test Description')).toBeInTheDocument();
    
    // Requirements should be visible by default
    expect(screen.getByText('Requirements')).toBeInTheDocument();
    expect(screen.getByText('Test Requirement')).toBeInTheDocument();
  });
  
  it('switches between tabs correctly', () => {
    render(<ProjectDetails project={mockProject} />);
    
    // Check requirements tab (default)
    expect(screen.getByText('Test Requirement')).toBeInTheDocument();
    
    // Switch to stories tab
    fireEvent.click(screen.getByText('Stories (1)'));
    expect(screen.getByText('User Stories')).toBeInTheDocument();
    expect(screen.getByText('Test Story')).toBeInTheDocument();
    expect(screen.getByText('Story Description')).toBeInTheDocument();
    
    // Switch to risks tab
    fireEvent.click(screen.getByText('Risks (1)'));
    expect(screen.getByText('Risks')).toBeInTheDocument();
    expect(screen.getByText('Risk: Test Risk')).toBeInTheDocument();
    expect(screen.getByText('Mitigation: Test Mitigation')).toBeInTheDocument();
    
    // Switch to queries tab
    fireEvent.click(screen.getByText('Queries (1)'));
    expect(screen.getByText('Queries')).toBeInTheDocument();
    expect(screen.getByText('Test Question')).toBeInTheDocument();
    expect(screen.getByText('Context: Test Context')).toBeInTheDocument();
    
    // Switch to NFRs tab
    fireEvent.click(screen.getByText('NFRs (1)'));
    expect(screen.getByText('Non-Functional Requirements')).toBeInTheDocument();
    expect(screen.getByText('Performance')).toBeInTheDocument();
    expect(screen.getByText('Test NFR')).toBeInTheDocument();
  });
  
  it('handles empty collections gracefully', () => {
    const emptyProject = {
      id: 1,
      name: 'Empty Project',
      description: 'No Details',
      requirements: [],
      stories: [],
      risks: [],
      queries: [],
      nfrs: []
    };
    
    render(<ProjectDetails project={emptyProject} />);
    
    // Check requirements tab (default)
    expect(screen.getByText('No requirements defined yet.')).toBeInTheDocument();
    
    // Switch to stories tab
    fireEvent.click(screen.getByText('Stories (0)'));
    expect(screen.getByText('No stories defined yet.')).toBeInTheDocument();
    
    // Switch to risks tab
    fireEvent.click(screen.getByText('Risks (0)'));
    expect(screen.getByText('No risks identified yet.')).toBeInTheDocument();
    
    // Switch to queries tab
    fireEvent.click(screen.getByText('Queries (0)'));
    expect(screen.getByText('No queries defined yet.')).toBeInTheDocument();
    
    // Switch to NFRs tab
    fireEvent.click(screen.getByText('NFRs (0)'));
    expect(screen.getByText('No NFRs defined yet.')).toBeInTheDocument();
  });
});
