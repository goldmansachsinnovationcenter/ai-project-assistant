import { JiraTicket } from "../components/JiraTicketCard";

export interface ParsedJiraResponse {
  hasTickets: boolean;
  tickets: JiraTicket[];
  textContent: string;
}

export function parseJiraResponse(response: string): ParsedJiraResponse {
  const result: ParsedJiraResponse = {
    hasTickets: false,
    tickets: [],
    textContent: response
  };

  try {
    const jsonMatch = response.match(/\{[\s\S]*"key"[\s\S]*\}/g);
    if (jsonMatch) {
      for (const match of jsonMatch) {
        try {
          const ticketData = JSON.parse(match);
          if (ticketData.key && ticketData.summary) {
            result.tickets.push({
              key: ticketData.key,
              summary: ticketData.summary,
              description: ticketData.description,
              status: ticketData.status || 'Unknown',
              assignee: ticketData.assignee,
              priority: ticketData.priority,
              issueType: ticketData.issueType || ticketData.type,
              projectKey: ticketData.projectKey || ticketData.project,
              created: ticketData.created,
              updated: ticketData.updated
            });
            result.hasTickets = true;
          }
        } catch (e) {
        }
      }
    }

    const ticketKeyPattern = /([A-Z]{2,10}-\d+)/g;
    const ticketKeys = response.match(ticketKeyPattern);
    
    if (ticketKeys && !result.hasTickets) {
      for (const key of [...new Set(ticketKeys)]) {
        result.tickets.push({
          key,
          summary: `Ticket ${key}`,
          status: 'Unknown',
          projectKey: key.split('-')[0]
        });
      }
      result.hasTickets = ticketKeys.length > 0;
    }

    const ticketInfoPattern = /Ticket:\s*([A-Z]{2,10}-\d+)[\s\S]*?Summary:\s*([^\n]+)[\s\S]*?Status:\s*([^\n]+)/gi;
    let match;
    while ((match = ticketInfoPattern.exec(response)) !== null) {
      result.tickets.push({
        key: match[1],
        summary: match[2].trim(),
        status: match[3].trim(),
        projectKey: match[1].split('-')[0]
      });
      result.hasTickets = true;
    }

  } catch (error) {
    console.error('Error parsing Jira response:', error);
  }

  return result;
}

export function formatJiraCommand(command: string): string {
  const lowerCommand = command.toLowerCase().trim();
  
  if (lowerCommand.startsWith('create jira')) {
    return `${command}\n\nTip: You can specify details like "create jira ticket with summary 'Fix login bug' and description 'Users cannot log in' for project PROJ"`;
  }
  
  if (lowerCommand.startsWith('search jira') || lowerCommand.startsWith('list jira')) {
    return `${command}\n\nTip: You can search by project, status, assignee, or keywords like "search jira tickets in project PROJ with status 'In Progress'"`;
  }
  
  if (lowerCommand.startsWith('update jira')) {
    return `${command}\n\nTip: You can update status, assignee, or other fields like "update jira ticket PROJ-123 status to 'Done'"`;
  }
  
  return command;
}
