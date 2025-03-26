'use client';

import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import { getAllProjectsWithDetails, Project } from '../../lib/api';

export default function ProjectsPage() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [selectedProject, setSelectedProject] = useState<Project | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    async function fetchProjects() {
      try {
        setIsLoading(true);
        const response = await getAllProjectsWithDetails();
        setProjects(response.projects);
        if (response.projects.length > 0) {
          setSelectedProject(response.projects[0]);
        }
      } catch (err) {
        setError('Failed to load projects. Please try again.');
        console.error(err);
      } finally {
        setIsLoading(false);
      }
    }

    fetchProjects();
  }, []);

  const handleSelectProject = (project: Project) => {
    setSelectedProject(project);
  };

  return (
    <main className="min-h-screen p-8 bg-gray-50">
      <div className="max-w-7xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <h1 className="text-3xl font-bold">Project Manager</h1>
          
          <div className="flex space-x-4">
            <Link 
              href="/"
              className="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition-colors"
            >
              Home
            </Link>
            <Link 
              href="/chat"
              className="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition-colors"
            >
              Chat
            </Link>
            <Link 
              href="/projects"
              className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
            >
              Projects
            </Link>
          </div>
        </div>
        
        {isLoading ? (
          <div className="flex justify-center items-center h-64">
            <p className="text-xl text-gray-500">Loading projects...</p>
          </div>
        ) : error ? (
          <div className="p-4 bg-red-100 text-red-700 rounded-md">
            {error}
          </div>
        ) : projects.length === 0 ? (
          <div className="bg-white rounded-lg shadow-md p-6">
            <p className="text-center text-gray-500">No projects found. Use the chat to create a new project.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="md:col-span-1 bg-white rounded-lg shadow-md p-4">
              <h2 className="text-xl font-semibold mb-4">Projects</h2>
              <div className="divide-y">
                {projects.map((project) => (
                  <div 
                    key={project.id}
                    className={`py-3 px-2 cursor-pointer hover:bg-gray-50 ${
                      selectedProject?.id === project.id ? 'bg-blue-50 border-l-4 border-blue-500 pl-2' : ''
                    }`}
                    onClick={() => handleSelectProject(project)}
                  >
                    <h3 className="font-medium">{project.name}</h3>
                    <p className="text-sm text-gray-500 truncate">{project.description || 'No description'}</p>
                  </div>
                ))}
              </div>
            </div>
            
            <div className="md:col-span-2">
              {selectedProject ? (
                <div className="bg-white rounded-lg shadow-md p-6">
                  <h2 className="text-2xl font-bold mb-4">{selectedProject.name}</h2>
                  <p className="text-gray-700 mb-6">{selectedProject.description || 'No description provided.'}</p>
                  
                  <div className="space-y-8">
                    {/* Requirements Section */}
                    <div>
                      <h3 className="text-lg font-semibold mb-2 border-b pb-2">Requirements ({selectedProject.requirements?.length || 0})</h3>
                      {selectedProject.requirements?.length > 0 ? (
                        <ul className="list-disc pl-5 space-y-1">
                          {selectedProject.requirements.map((req) => (
                            <li key={req.id} className="text-gray-700">{req.text}</li>
                          ))}
                        </ul>
                      ) : (
                        <p className="text-gray-500 italic">No requirements defined yet.</p>
                      )}
                    </div>
                    
                    {/* Stories Section */}
                    <div>
                      <h3 className="text-lg font-semibold mb-2 border-b pb-2">Stories ({selectedProject.stories?.length || 0})</h3>
                      {selectedProject.stories?.length > 0 ? (
                        <div className="space-y-3">
                          {selectedProject.stories.map((story) => (
                            <div key={story.id} className="bg-gray-50 p-3 rounded">
                              <h4 className="font-medium">{story.title}</h4>
                              <p className="text-sm text-gray-700">{story.description}</p>
                            </div>
                          ))}
                        </div>
                      ) : (
                        <p className="text-gray-500 italic">No stories created yet.</p>
                      )}
                    </div>
                    
                    {/* Queries Section */}
                    <div>
                      <h3 className="text-lg font-semibold mb-2 border-b pb-2">Queries ({selectedProject.queries?.length || 0})</h3>
                      {selectedProject.queries?.length > 0 ? (
                        <div className="space-y-3">
                          {selectedProject.queries.map((query) => (
                            <div key={query.id} className="bg-gray-50 p-3 rounded">
                              <h4 className="font-medium">{query.question}</h4>
                              {query.context && <p className="text-sm text-gray-700">{query.context}</p>}
                            </div>
                          ))}
                        </div>
                      ) : (
                        <p className="text-gray-500 italic">No queries defined yet.</p>
                      )}
                    </div>
                    
                    {/* Risks Section */}
                    <div>
                      <h3 className="text-lg font-semibold mb-2 border-b pb-2">Risks ({selectedProject.risks?.length || 0})</h3>
                      {selectedProject.risks?.length > 0 ? (
                        <div className="space-y-3">
                          {selectedProject.risks.map((risk) => (
                            <div key={risk.id} className="bg-gray-50 p-3 rounded">
                              <h4 className="font-medium">{risk.description}</h4>
                              {risk.mitigation && (
                                <p className="text-sm text-gray-700">
                                  <span className="font-medium">Mitigation:</span> {risk.mitigation}
                                </p>
                              )}
                            </div>
                          ))}
                        </div>
                      ) : (
                        <p className="text-gray-500 italic">No risks identified yet.</p>
                      )}
                    </div>
                    
                    {/* NFRs Section */}
                    <div>
                      <h3 className="text-lg font-semibold mb-2 border-b pb-2">Non-Functional Requirements ({selectedProject.nfrs?.length || 0})</h3>
                      {selectedProject.nfrs?.length > 0 ? (
                        <div className="space-y-3">
                          {selectedProject.nfrs.map((nfr) => (
                            <div key={nfr.id} className="bg-gray-50 p-3 rounded">
                              <h4 className="font-medium">{nfr.category}</h4>
                              <p className="text-sm text-gray-700">{nfr.description}</p>
                            </div>
                          ))}
                        </div>
                      ) : (
                        <p className="text-gray-500 italic">No non-functional requirements defined yet.</p>
                      )}
                    </div>
                  </div>
                </div>
              ) : (
                <div className="bg-white rounded-lg shadow-md p-6 h-full flex items-center justify-center">
                  <p className="text-gray-500">Select a project to view details</p>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </main>
  );
}
