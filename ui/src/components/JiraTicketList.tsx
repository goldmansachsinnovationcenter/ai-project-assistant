"use client";

import React from "react";
import JiraTicketCard, { JiraTicket } from "./JiraTicketCard";

interface JiraTicketListProps {
  tickets: JiraTicket[];
  title?: string;
  onTicketClick?: (ticket: JiraTicket) => void;
  emptyMessage?: string;
}

const JiraTicketList: React.FC<JiraTicketListProps> = ({ 
  tickets, 
  title = "Jira Tickets", 
  onTicketClick,
  emptyMessage = "No tickets found"
}) => {
  if (tickets.length === 0) {
    return (
      <div className="text-center py-8">
        <div className="text-gray-400 mb-2">
          <svg className="mx-auto h-12 w-12" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
        </div>
        <p className="text-gray-500">{emptyMessage}</p>
      </div>
    );
  }

  return (
    <div className="space-y-1">
      {title && (
        <h3 className="text-lg font-semibold text-gray-800 mb-3 flex items-center">
          <svg className="w-5 h-5 mr-2 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          {title} ({tickets.length})
        </h3>
      )}
      <div className="space-y-2">
        {tickets.map((ticket) => (
          <JiraTicketCard
            key={ticket.key}
            ticket={ticket}
            onClick={onTicketClick ? () => onTicketClick(ticket) : undefined}
          />
        ))}
      </div>
    </div>
  );
};

export default JiraTicketList;
