'use client';

import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import { getAllProjectsWithDetails, Project } from '../../lib/api';
import ProjectStats from '../../components/ProjectStats';
import ProjectAnalytics from '../../components/ProjectAnalytics';
import RequirementsManager from '../../components/RequirementsManager';
import ProjectWorkflow from '../../components/ProjectWorkflow';
import SearchFilter from '../../components/SearchFilter';
import Breadcrumb from '../../components/Breadcrumb';
import ProjectActions from '../../components/ProjectActions';
import KeyboardShortcuts from '../../components/KeyboardShortcuts';
import MobileMenu from '../../components/MobileMenu';

export default function ProjectsPage() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [filteredProjects, setFilteredProjects] = useState<Project[]>([]);
  const [selectedProject, setSelectedProject] = useState<Project | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    fetchProjects();
  }, []);

  useEffect(() => {
    if (searchQuery.trim() === '') {
      setFilteredProjects(projects);
    } else {
      const filtered = projects.filter(project =>
        project.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (project.description && project.description.toLowerCase().includes(searchQuery.toLowerCase()))
      );
      setFilteredProjects(filtered);
    }
  }, [projects, searchQuery]);

  const handleSelectProject = (project: Project) => {
    setSelectedProject(project);
  };

  const handleRequirementAdded = () => {
    fetchProjects();
  };

  async function fetchProjects() {
    try {
      setIsLoading(true);
      const response = await getAllProjectsWithDetails();
      setProjects(response.projects);
      setFilteredProjects(response.projects);
      if (response.projects.length > 0 && !selectedProject) {
        setSelectedProject(response.projects[0]);
      }
    } catch (err) {
      setError('Failed to load projects. Please try again.');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  }

  const handleSearch = (query: string) => {
    setSearchQuery(query);
  };

  const handleEditProject = (project: Project) => {
    window.location.href = `/projects/edit/${encodeURIComponent(project.name)}`;
  };

  const handleDeleteProject = async (project: Project) => {
    try {
      const response = await fetch(`http://localhost:8080/api/projects/${encodeURIComponent(project.name)}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        await fetchProjects();
        if (selectedProject?.id === project.id) {
          setSelectedProject(null);
        }
      } else {
        setError('Failed to delete project. Please try again.');
      }
    } catch (err) {
      setError('Failed to delete project. Please try again.');
      console.error(err);
    }
  };

  const handleCreateProject = () => {
    window.location.href = '/projects/create';
  };

  const handleGoHome = () => {
    window.location.href = '/';
  };

  return (
    <main className="min-h-screen p-8 bg-gray-50">
      <KeyboardShortcuts 
        onCreateProject={handleCreateProject}
        onHome={handleGoHome}
      />
      <div className="max-w-7xl mx-auto">
        <Breadcrumb items={[
          { label: 'Home', href: '/' },
          { label: 'Projects' }
        ]} />
        
        <div className="flex items-center justify-between mb-8">
          <h1 className="text-3xl font-bold">Project Manager</h1>
          
          <div className="hidden md:flex space-x-4">
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
              href="/projects/create"
              className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors"
            >
              Create Project
            </Link>
            <Link 
              href="/projects"
              className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
            >
              Projects
            </Link>
          </div>
          
          <MobileMenu currentPage="projects" />
        </div>
        
        {!isLoading && !error && <ProjectStats projects={projects} />}
        
        {isLoading ? (
          <div className="flex justify-center items-center h-64">
            <div className="flex items-center space-x-3">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
              <p className="text-xl text-gray-500">Loading projects...</p>
            </div>
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
          <>
            <div className="grid grid-cols-1 lg:grid-cols-4 gap-6 mb-6">
              <div className="lg:col-span-2">
                <ProjectAnalytics projects={projects} />
              </div>
              <div className="lg:col-span-2">
                {selectedProject && <ProjectWorkflow project={selectedProject} />}
              </div>
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div className="md:col-span-1 bg-white rounded-lg shadow-md p-4">
                <div className="flex items-center justify-between mb-4">
                  <h2 className="text-xl font-semibold">Projects</h2>
                  <Link 
                    href="/projects/create"
                    className="w-10 h-10 bg-blue-600 text-white rounded-full flex items-center justify-center hover:bg-blue-700 transition-colors shadow-lg hover:shadow-xl backdrop-blur transition-backdrop"
                    title="Create New Project"
                  >
                    +
                  </Link>
                </div>
                
                <div className="mb-4">
                  <SearchFilter onSearch={handleSearch} />
                </div>
                
                <div className="divide-y">
                  {filteredProjects.map((project) => (
                    <div 
                      key={project.id}
                      className={`py-3 px-2 cursor-pointer hover:bg-gray-50 ${
                        selectedProject?.id === project.id ? 'bg-blue-50 border-l-4 border-blue-500 pl-2' : ''
                      }`}
                      onClick={() => handleSelectProject(project)}
                    >
                      <div className="flex justify-between items-start">
                        <div className="flex-1">
                          <h3 className="font-medium">{project.name}</h3>
                          <p className="text-sm text-gray-500 truncate">{project.description || 'No description'}</p>
                          <div className="flex mt-1 text-xs text-gray-400 space-x-2">
                            <span>{project.requirements?.length || 0} req</span>
                            <span>•</span>
                            <span>{project.stories?.length || 0} stories</span>
                            <span>•</span>
                            <span>{project.risks?.length || 0} risks</span>
                          </div>
                        </div>
                        <div className="ml-2" onClick={(e) => e.stopPropagation()}>
                          <ProjectActions
                            project={project}
                            onEdit={handleEditProject}
                            onDelete={handleDeleteProject}
                          />
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
              
              <div className="md:col-span-2 space-y-6">
                {selectedProject ? (
                  <>
                    <div className="bg-white rounded-lg shadow-md p-6">
                      <h2 className="text-2xl font-bold mb-4">{selectedProject.name}</h2>
                      <p className="text-gray-700 mb-6">{selectedProject.description || 'No description provided.'}</p>
                      
                      <div className="space-y-8">
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
                        
                        <div>
                          <h3 className="text-lg font-semibold mb-2 border-b pb-2">NFRs ({selectedProject.nfrs?.length || 0})</h3>
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
                    
                    <RequirementsManager 
                      project={selectedProject} 
                      onRequirementAdded={handleRequirementAdded}
                    />
                  </>
                ) : (
                  <div className="bg-white rounded-lg shadow-md p-6 h-full flex items-center justify-center">
                    <div className="text-center">
                      <p className="text-gray-500 mb-4">Select a project to view details</p>
                      <Link 
                        href="/projects/create"
                        className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
                      >
                        Create Your First Project
                      </Link>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </>
        )}
      </div>
    </main>
  );
}
