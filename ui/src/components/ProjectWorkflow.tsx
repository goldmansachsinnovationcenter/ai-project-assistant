'use client';

import React from 'react';
import { Project } from '../lib/api';

interface ProjectWorkflowProps {
  project: Project;
}

export default function ProjectWorkflow({ project }: ProjectWorkflowProps) {
  const getCompletionPercentage = (current: number, total: number) => {
    if (total === 0) return 0;
    return Math.round((current / total) * 100);
  };

  const requirementsCount = project.requirements?.length || 0;
  const storiesCount = project.stories?.length || 0;
  const risksCount = project.risks?.length || 0;
  const nfrsCount = project.nfrs?.length || 0;

  const workflowSteps = [
    {
      title: 'Requirements Definition',
      count: requirementsCount,
      status: requirementsCount > 0 ? 'completed' : 'pending',
      description: 'Define project requirements and specifications'
    },
    {
      title: 'User Stories Creation',
      count: storiesCount,
      status: storiesCount > 0 ? 'completed' : requirementsCount > 0 ? 'available' : 'pending',
      description: 'Generate user stories from requirements'
    },
    {
      title: 'Risk Assessment',
      count: risksCount,
      status: risksCount > 0 ? 'completed' : 'available',
      description: 'Identify and assess project risks'
    },
    {
      title: 'NFR Definition',
      count: nfrsCount,
      status: nfrsCount > 0 ? 'completed' : 'available',
      description: 'Define non-functional requirements'
    }
  ];

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'completed': return 'bg-green-500';
      case 'available': return 'bg-blue-500';
      case 'pending': return 'bg-gray-300';
      default: return 'bg-gray-300';
    }
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'completed': return 'Completed';
      case 'available': return 'Available';
      case 'pending': return 'Pending';
      default: return 'Unknown';
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <h3 className="text-lg font-semibold mb-6">Project Workflow</h3>
      
      <div className="space-y-4">
        {workflowSteps.map((step, index) => (
          <div key={step.title} className="flex items-center space-x-4 p-4 border rounded-lg hover:bg-gray-50">
            <div className="flex-shrink-0">
              <div className={`w-8 h-8 rounded-full ${getStatusColor(step.status)} flex items-center justify-center text-white font-bold text-sm`}>
                {index + 1}
              </div>
            </div>
            
            <div className="flex-1">
              <div className="flex items-center justify-between">
                <h4 className="font-medium text-gray-900">{step.title}</h4>
                <div className="flex items-center space-x-2">
                  <span className="text-sm font-medium text-gray-600">
                    {step.count} items
                  </span>
                  <span className={`px-2 py-1 text-xs rounded-full text-white ${getStatusColor(step.status)}`}>
                    {getStatusText(step.status)}
                  </span>
                </div>
              </div>
              <p className="text-sm text-gray-500 mt-1">{step.description}</p>
            </div>
          </div>
        ))}
      </div>

      <div className="mt-6 p-4 bg-blue-50 rounded-lg">
        <h4 className="font-medium text-blue-900 mb-2">Quick Actions</h4>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
          <button 
            className="px-3 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors text-sm backdrop-blur transition-backdrop"
            onClick={() => console.log('Generate stories action')}
          >
            Generate Stories from Requirements
          </button>
          <button 
            className="px-3 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors text-sm backdrop-blur transition-backdrop"
            onClick={() => console.log('Assess risks action')}
          >
            Assess Project Risks
          </button>
          <button 
            className="px-3 py-2 bg-purple-600 text-white rounded-md hover:bg-purple-700 transition-colors text-sm backdrop-blur transition-backdrop"
            onClick={() => console.log('Define NFRs action')}
          >
            Define NFRs
          </button>
          <button 
            className="px-3 py-2 bg-yellow-600 text-white rounded-md hover:bg-yellow-700 transition-colors text-sm backdrop-blur transition-backdrop"
            onClick={() => console.log('Export summary action')}
          >
            Export Project Summary
          </button>
        </div>
      </div>
    </div>
  );
}
