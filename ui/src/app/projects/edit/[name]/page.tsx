'use client';

import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import { useRouter, useParams } from 'next/navigation';
import { getProjectByName, Project } from '../../../../lib/api';
import Breadcrumb from '../../../../components/Breadcrumb';

export default function EditProjectPage() {
  const [project, setProject] = useState<Project | null>(null);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState('');
  const router = useRouter();
  const params = useParams();
  const projectName = params.name as string;

  useEffect(() => {
    async function loadProject() {
      try {
        setIsLoading(true);
        const projectData = await getProjectByName(decodeURIComponent(projectName));
        setProject(projectData);
        setName(projectData.name);
        setDescription(projectData.description || '');
      } catch (err) {
        setError('Failed to load project. Please try again.');
        console.error(err);
      } finally {
        setIsLoading(false);
      }
    }

    if (projectName) {
      loadProject();
    }
  }, [projectName]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim() || !project) return;

    setIsSaving(true);
    setError('');
    
    try {
      const response = await fetch(`http://localhost:8080/api/projects/${encodeURIComponent(project.name)}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          name: name.trim(),
          description: description.trim() || undefined
        }),
      });

      if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
      }

      router.push('/projects');
    } catch (err) {
      setError('Failed to update project. Please try again.');
      console.error(err);
    } finally {
      setIsSaving(false);
    }
  };

  if (isLoading) {
    return (
      <main className="min-h-screen p-8 bg-gray-50">
        <div className="max-w-2xl mx-auto">
          <div className="flex justify-center items-center h-64">
            <p className="text-xl text-gray-500">Loading project...</p>
          </div>
        </div>
      </main>
    );
  }

  if (!project) {
    return (
      <main className="min-h-screen p-8 bg-gray-50">
        <div className="max-w-2xl mx-auto">
          <div className="bg-red-100 text-red-700 p-4 rounded-md">
            Project not found.
          </div>
        </div>
      </main>
    );
  }

  return (
    <main className="min-h-screen p-8 bg-gray-50">
      <div className="max-w-2xl mx-auto">
        <Breadcrumb items={[
          { label: 'Home', href: '/' },
          { label: 'Projects', href: '/projects' },
          { label: `Edit ${project.name}` }
        ]} />
        
        <div className="flex items-center justify-between mb-8">
          <h1 className="text-3xl font-bold">Edit Project</h1>
          
          <div className="flex space-x-4">
            <Link 
              href="/projects"
              className="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition-colors"
            >
              Back to Projects
            </Link>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow-md p-6">
          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-2">
                Project Name *
              </label>
              <input
                id="name"
                type="text"
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Enter project name..."
                maxLength={100}
              />
            </div>
            
            <div>
              <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-2">
                Description
              </label>
              <textarea
                id="description"
                rows={4}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 resize-none"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Enter project description..."
                maxLength={500}
              />
              <p className="text-sm text-gray-500 mt-1">
                {description.length}/500 characters
              </p>
            </div>
            
            {error && (
              <div className="p-3 bg-red-100 text-red-700 rounded-md">
                {error}
              </div>
            )}
            
            <div className="flex justify-end space-x-4">
              <Link
                href="/projects"
                className="px-6 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
              >
                Cancel
              </Link>
              <button
                type="submit"
                disabled={isSaving || !name.trim()}
                className={`px-6 py-2 rounded-md text-white font-medium ${
                  isSaving || !name.trim()
                    ? 'bg-gray-400 cursor-not-allowed'
                    : 'bg-blue-600 hover:bg-blue-700'
                }`}
              >
                {isSaving ? 'Saving...' : 'Save Changes'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </main>
  );
}
