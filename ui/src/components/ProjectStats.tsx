'use client';

import React from 'react';
import { Project } from '../lib/api';

interface ProjectStatsProps {
  projects: Project[];
}

export default function ProjectStats({ projects }: ProjectStatsProps) {
  const totalProjects = projects.length;
  const totalRequirements = projects.reduce((sum, p) => sum + (p.requirements?.length || 0), 0);
  const totalStories = projects.reduce((sum, p) => sum + (p.stories?.length || 0), 0);
  const totalRisks = projects.reduce((sum, p) => sum + (p.risks?.length || 0), 0);
  const totalQueries = projects.reduce((sum, p) => sum + (p.queries?.length || 0), 0);
  const totalNfrs = projects.reduce((sum, p) => sum + (p.nfrs?.length || 0), 0);

  const stats = [
    { label: 'Total Projects', value: totalProjects, color: 'bg-blue-500' },
    { label: 'Requirements', value: totalRequirements, color: 'bg-green-500' },
    { label: 'User Stories', value: totalStories, color: 'bg-purple-500' },
    { label: 'Risks', value: totalRisks, color: 'bg-red-500' },
    { label: 'Queries', value: totalQueries, color: 'bg-yellow-500' },
    { label: 'NFRs', value: totalNfrs, color: 'bg-indigo-500' },
  ];

  return (
    <div className="bg-white rounded-lg shadow-md p-6 mb-6">
      <h2 className="text-xl font-semibold mb-4">Project Statistics</h2>
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
        {stats.map((stat) => (
          <div key={stat.label} className="text-center">
            <div className={`${stat.color} text-white rounded-lg p-4 mb-2`}>
              <div className="text-2xl font-bold">{stat.value}</div>
            </div>
            <div className="text-sm text-gray-600">{stat.label}</div>
          </div>
        ))}
      </div>
    </div>
  );
}
