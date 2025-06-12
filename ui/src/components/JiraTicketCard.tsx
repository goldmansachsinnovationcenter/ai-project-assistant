"use client";

import React from "react";

export interface JiraTicket {
  key: string;
  summary: string;
  description?: string;
  status: string;
  assignee?: string;
  priority?: string;
  issueType?: string;
  projectKey: string;
  created?: string;
  updated?: string;
}

interface JiraTicketCardProps {
  ticket: JiraTicket;
  onClick?: () => void;
}

const JiraTicketCard: React.FC<JiraTicketCardProps> = ({ ticket, onClick }) => {
  const getStatusColor = (status: string) => {
    switch (status.toLowerCase()) {
      case 'to do':
      case 'open':
        return 'bg-gray-100 text-gray-800';
      case 'in progress':
      case 'in review':
        return 'bg-blue-100 text-blue-800';
      case 'done':
      case 'closed':
      case 'resolved':
        return 'bg-green-100 text-green-800';
      case 'blocked':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getPriorityColor = (priority?: string) => {
    if (!priority) return 'bg-gray-100 text-gray-600';
    switch (priority.toLowerCase()) {
      case 'highest':
      case 'critical':
        return 'bg-red-100 text-red-800';
      case 'high':
        return 'bg-orange-100 text-orange-800';
      case 'medium':
        return 'bg-yellow-100 text-yellow-800';
      case 'low':
        return 'bg-green-100 text-green-800';
      case 'lowest':
        return 'bg-blue-100 text-blue-800';
      default:
        return 'bg-gray-100 text-gray-600';
    }
  };

  return (
    <div 
      className={`border rounded-lg p-4 mb-3 bg-white shadow-sm hover:shadow-md transition-shadow ${
        onClick ? 'cursor-pointer' : ''
      }`}
      onClick={onClick}
    >
      <div className="flex items-start justify-between mb-2">
        <div className="flex items-center space-x-2">
          <span className="font-mono text-sm text-blue-600 font-semibold">
            {ticket.key}
          </span>
          <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(ticket.status)}`}>
            {ticket.status}
          </span>
          {ticket.priority && (
            <span className={`px-2 py-1 rounded-full text-xs font-medium ${getPriorityColor(ticket.priority)}`}>
              {ticket.priority}
            </span>
          )}
        </div>
        {ticket.issueType && (
          <span className="text-xs text-gray-500 bg-gray-50 px-2 py-1 rounded">
            {ticket.issueType}
          </span>
        )}
      </div>
      
      <h3 className="font-semibold text-gray-900 mb-2 line-clamp-2">
        {ticket.summary}
      </h3>
      
      {ticket.description && (
        <p className="text-sm text-gray-600 mb-3 line-clamp-3">
          {ticket.description}
        </p>
      )}
      
      <div className="flex items-center justify-between text-xs text-gray-500">
        <div className="flex items-center space-x-4">
          {ticket.assignee && (
            <span>Assignee: {ticket.assignee}</span>
          )}
          <span>Project: {ticket.projectKey}</span>
        </div>
        {ticket.updated && (
          <span>Updated: {new Date(ticket.updated).toLocaleDateString()}</span>
        )}
      </div>
    </div>
  );
};

export default JiraTicketCard;
