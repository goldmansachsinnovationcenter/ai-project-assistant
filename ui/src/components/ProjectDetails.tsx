'use client';

import React, { useState } from 'react';
import { Project } from '../lib/api';

interface ProjectDetailsProps {
  project: Project;
}

export default function ProjectDetails({ project }: ProjectDetailsProps) {
  const [activeTab, setActiveTab] = useState('requirements');
  
  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden">
      <div className="p-6 border-b">
        <h2 className="text-2xl font-bold mb-2">{project.name}</h2>
        <p className="text-gray-700">{project.description || 'No description provided.'}</p>
      </div>
      
      <div className="border-b">
        <nav className="flex overflow-x-auto">
          <button
            className={`px-4 py-2 text-sm font-medium ${
              activeTab === 'requirements' ? 'border-b-2 border-blue-500 text-blue-600' : 'text-gray-500 hover:text-gray-700'
            }`}
            onClick={() => setActiveTab('requirements')}
          >
            Requirements ({project.requirements?.length || 0})
          </button>
          <button
            className={`px-4 py-2 text-sm font-medium ${
              activeTab === 'stories' ? 'border-b-2 border-blue-500 text-blue-600' : 'text-gray-500 hover:text-gray-700'
            }`}
            onClick={() => setActiveTab('stories')}
          >
            Stories ({project.stories?.length || 0})
          </button>
          <button
            className={`px-4 py-2 text-sm font-medium ${
              activeTab === 'risks' ? 'border-b-2 border-blue-500 text-blue-600' : 'text-gray-500 hover:text-gray-700'
            }`}
            onClick={() => setActiveTab('risks')}
          >
            Risks ({project.risks?.length || 0})
          </button>
          <button
            className={`px-4 py-2 text-sm font-medium ${
              activeTab === 'queries' ? 'border-b-2 border-blue-500 text-blue-600' : 'text-gray-500 hover:text-gray-700'
            }`}
            onClick={() => setActiveTab('queries')}
          >
            Queries ({project.queries?.length || 0})
          </button>
          <button
            className={`px-4 py-2 text-sm font-medium ${
              activeTab === 'nfrs' ? 'border-b-2 border-blue-500 text-blue-600' : 'text-gray-500 hover:text-gray-700'
            }`}
            onClick={() => setActiveTab('nfrs')}
          >
            NFRs ({project.nfrs?.length || 0})
          </button>
        </nav>
      </div>
      
      <div className="p-6">
        {activeTab === 'requirements' && (
          <div>
            <h3 className="text-lg font-semibold mb-4">Requirements</h3>
            {project.requirements?.length ? (
              <ul className="divide-y">
                {project.requirements.map((req) => (
                  <li key={req.id} className="py-3">
                    <p>{req.text}</p>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="text-gray-500">No requirements defined yet.</p>
            )}
          </div>
        )}
        
        {activeTab === 'stories' && (
          <div>
            <h3 className="text-lg font-semibold mb-4">User Stories</h3>
            {project.stories?.length ? (
              <ul className="divide-y">
                {project.stories.map((story) => (
                  <li key={story.id} className="py-3">
                    <h4 className="font-medium">{story.title}</h4>
                    <p className="text-gray-700 mt-1">{story.description}</p>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="text-gray-500">No stories defined yet.</p>
            )}
          </div>
        )}
        
        {activeTab === 'risks' && (
          <div>
            <h3 className="text-lg font-semibold mb-4">Risks</h3>
            {project.risks?.length ? (
              <ul className="divide-y">
                {project.risks.map((risk) => (
                  <li key={risk.id} className="py-3">
                    <h4 className="font-medium">Risk: {risk.description}</h4>
                    {risk.mitigation && (
                      <p className="text-gray-700 mt-1">Mitigation: {risk.mitigation}</p>
                    )}
                  </li>
                ))}
              </ul>
            ) : (
              <p className="text-gray-500">No risks identified yet.</p>
            )}
          </div>
        )}
        
        {activeTab === 'queries' && (
          <div>
            <h3 className="text-lg font-semibold mb-4">Queries</h3>
            {project.queries?.length ? (
              <ul className="divide-y">
                {project.queries.map((query) => (
                  <li key={query.id} className="py-3">
                    <h4 className="font-medium">{query.question}</h4>
                    {query.context && (
                      <p className="text-gray-700 mt-1">Context: {query.context}</p>
                    )}
                  </li>
                ))}
              </ul>
            ) : (
              <p className="text-gray-500">No queries defined yet.</p>
            )}
          </div>
        )}
        
        {activeTab === 'nfrs' && (
          <div>
            <h3 className="text-lg font-semibold mb-4">Non-Functional Requirements</h3>
            {project.nfrs?.length ? (
              <ul className="divide-y">
                {project.nfrs.map((nfr) => (
                  <li key={nfr.id} className="py-3">
                    <h4 className="font-medium">{nfr.category}</h4>
                    <p className="text-gray-700 mt-1">{nfr.description}</p>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="text-gray-500">No NFRs defined yet.</p>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
