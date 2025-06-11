'use client';

import React from 'react';
import { Project } from '../lib/api';

interface ProjectAnalyticsProps {
  projects: Project[];
}

export default function ProjectAnalytics({ projects }: ProjectAnalyticsProps) {
  const getProjectProgress = (project: Project) => {
    const weights = {
      requirements: 0.3,
      stories: 0.3,
      risks: 0.2,
      nfrs: 0.2
    };
    
    const hasRequirements = (project.requirements?.length || 0) > 0 ? 1 : 0;
    const hasStories = (project.stories?.length || 0) > 0 ? 1 : 0;
    const hasRisks = (project.risks?.length || 0) > 0 ? 1 : 0;
    const hasNfrs = (project.nfrs?.length || 0) > 0 ? 1 : 0;
    
    return Math.round(
      (hasRequirements * weights.requirements +
       hasStories * weights.stories +
       hasRisks * weights.risks +
       hasNfrs * weights.nfrs) * 100
    );
  };

  const projectsWithProgress = projects.map(project => ({
    ...project,
    progress: getProjectProgress(project)
  }));

  const averageProgress = projects.length > 0 
    ? Math.round(projectsWithProgress.reduce((sum, p) => sum + p.progress, 0) / projects.length)
    : 0;

  const completedProjects = projectsWithProgress.filter(p => p.progress === 100).length;
  const inProgressProjects = projectsWithProgress.filter(p => p.progress > 0 && p.progress < 100).length;
  const notStartedProjects = projectsWithProgress.filter(p => p.progress === 0).length;

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <h3 className="text-lg font-semibold mb-6">Project Analytics</h3>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <div className="bg-green-50 p-4 rounded-lg">
          <div className="text-2xl font-bold text-green-600">{completedProjects}</div>
          <div className="text-sm text-green-700">Completed</div>
        </div>
        <div className="bg-blue-50 p-4 rounded-lg">
          <div className="text-2xl font-bold text-blue-600">{inProgressProjects}</div>
          <div className="text-sm text-blue-700">In Progress</div>
        </div>
        <div className="bg-gray-50 p-4 rounded-lg">
          <div className="text-2xl font-bold text-gray-600">{notStartedProjects}</div>
          <div className="text-sm text-gray-700">Not Started</div>
        </div>
        <div className="bg-purple-50 p-4 rounded-lg">
          <div className="text-2xl font-bold text-purple-600">{averageProgress}%</div>
          <div className="text-sm text-purple-700">Avg Progress</div>
        </div>
      </div>

      <div className="space-y-3">
        <h4 className="font-medium text-gray-700">Project Progress Overview</h4>
        {projectsWithProgress.length > 0 ? (
          <div className="space-y-2">
            {projectsWithProgress.map((project) => (
              <div key={project.id} className="flex items-center space-x-3">
                <div className="flex-1">
                  <div className="flex justify-between items-center mb-1">
                    <span className="text-sm font-medium text-gray-700">{project.name}</span>
                    <span className="text-sm text-gray-500">{project.progress}%</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div 
                      className={`h-2 rounded-full ${
                        project.progress === 100 ? 'bg-green-500' :
                        project.progress > 50 ? 'bg-blue-500' :
                        project.progress > 0 ? 'bg-yellow-500' : 'bg-gray-300'
                      }`}
                      style={{ width: `${project.progress}%` }}
                    ></div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-gray-500 italic">No projects to analyze.</p>
        )}
      </div>
    </div>
  );
}
