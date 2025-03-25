'use client';

import React from 'react';
import { Project } from '../lib/api';

interface ProjectListProps {
  projects: Project[];
  selectedProject: Project | null;
  onSelectProject: (project: Project) => void;
}

export default function ProjectList({ projects, selectedProject, onSelectProject }: ProjectListProps) {
  return (
    <div className="bg-white rounded-lg shadow-md p-4">
      <h2 className="text-xl font-semibold mb-4">Projects</h2>
      
      {projects.length === 0 ? (
        <p className="text-gray-500">No projects found</p>
      ) : (
        <div className="divide-y">
          {projects.map((project) => (
            <div 
              key={project.id}
              className={`py-3 px-2 cursor-pointer hover:bg-gray-50 ${
                selectedProject?.id === project.id ? 'bg-blue-50 border-l-4 border-blue-500 pl-1' : ''
              }`}
              onClick={() => onSelectProject(project)}
            >
              <h3 className="font-medium">{project.name}</h3>
              <p className="text-sm text-gray-500 truncate">{project.description || 'No description'}</p>
              <div className="flex mt-1 text-xs text-gray-400 space-x-2">
                <span>{project.requirements?.length || 0} requirements</span>
                <span>•</span>
                <span>{project.stories?.length || 0} stories</span>
                {project.risks?.length > 0 && (
                  <>
                    <span>•</span>
                    <span>{project.risks?.length || 0} risks</span>
                  </>
                )}
                {project.queries?.length > 0 && (
                  <>
                    <span>•</span>
                    <span>{project.queries?.length || 0} queries</span>
                  </>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
