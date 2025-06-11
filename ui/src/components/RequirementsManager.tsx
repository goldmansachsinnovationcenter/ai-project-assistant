'use client';

import React, { useState } from 'react';
import { Project, Requirement } from '../lib/api';

interface RequirementsManagerProps {
  project: Project;
  onRequirementAdded?: (requirement: Requirement) => void;
}

export default function RequirementsManager({ project, onRequirementAdded }: RequirementsManagerProps) {
  const [newRequirement, setNewRequirement] = useState('');
  const [isAdding, setIsAdding] = useState(false);
  const [error, setError] = useState('');

  const handleAddRequirement = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newRequirement.trim()) return;

    setIsAdding(true);
    setError('');

    try {
      const response = await fetch(`http://localhost:8080/api/projects/${encodeURIComponent(project.name)}/requirements`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ text: newRequirement.trim() }),
      });

      if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
      }

      const requirement = await response.json();
      setNewRequirement('');
      
      if (onRequirementAdded) {
        onRequirementAdded(requirement);
      }
    } catch (err) {
      setError('Failed to add requirement. Please try again.');
      console.error(err);
    } finally {
      setIsAdding(false);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <h3 className="text-lg font-semibold mb-4">Requirements Management</h3>
      
      <form onSubmit={handleAddRequirement} className="mb-6">
        <div className="flex space-x-2">
          <textarea
            className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
            rows={2}
            placeholder="Enter new requirement..."
            value={newRequirement}
            onChange={(e) => setNewRequirement(e.target.value)}
            maxLength={500}
          />
          <button
            type="submit"
            disabled={isAdding || !newRequirement.trim()}
            className={`px-4 py-2 rounded-md text-white font-medium flex items-center space-x-2 ${
              isAdding || !newRequirement.trim()
                ? 'bg-gray-400 cursor-not-allowed'
                : 'bg-blue-600 hover:bg-blue-700'
            }`}
          >
            {isAdding && (
              <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
            )}
            <span>{isAdding ? 'Adding...' : 'Add'}</span>
          </button>
        </div>
        {error && (
          <div className="mt-2 p-2 bg-red-100 text-red-700 rounded-md text-sm">
            {error}
          </div>
        )}
      </form>

      <div className="space-y-2">
        <h4 className="font-medium text-gray-700">Current Requirements ({project.requirements?.length || 0})</h4>
        {project.requirements?.length > 0 ? (
          <ul className="divide-y border rounded-md">
            {project.requirements.map((req) => (
              <li key={req.id} className="p-3 hover:bg-gray-50">
                <p className="text-gray-700">{req.text}</p>
              </li>
            ))}
          </ul>
        ) : (
          <p className="text-gray-500 italic">No requirements defined yet.</p>
        )}
      </div>
    </div>
  );
}
