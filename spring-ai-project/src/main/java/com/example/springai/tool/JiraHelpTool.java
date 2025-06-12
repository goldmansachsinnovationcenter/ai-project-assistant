package com.example.springai.tool;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Tool for providing help with Jira commands
 */
@Component
public class JiraHelpTool extends JiraManagementTool {

    public JiraHelpTool() {
        super("jira-help", "Get help with available Jira commands and their usage");
    }

    @Override
    public ToolResult execute(Map<String, String> parameters) {
        StringBuilder help = new StringBuilder();
        help.append("🎫 **Jira Integration Help**\n\n");
        help.append("Available Jira commands:\n\n");
        
        help.append("**📋 Project Management:**\n");
        help.append("• `list jira projects` - List all available Jira projects\n\n");
        
        help.append("**🎯 Ticket Operations:**\n");
        help.append("• `create jira ticket` - Create a new Jira ticket\n");
        help.append("  Example: \"Create a jira ticket with summary 'Fix login bug' and description 'Users cannot login'\"\n\n");
        
        help.append("• `search jira tickets` - Search for existing tickets\n");
        help.append("  Example: \"Search jira tickets for 'login bug'\"\n");
        help.append("  Example: \"Search jira tickets in project PROJ with status 'In Progress'\"\n\n");
        
        help.append("**💡 Tips:**\n");
        help.append("• You can specify project key, priority, and issue type when creating tickets\n");
        help.append("• Search supports filtering by project, status, and assignee\n");
        help.append("• Use natural language - the AI will understand your intent\n\n");
        
        help.append("**🔧 Parameters:**\n");
        help.append("• **Project Key**: PROJ, DEV, TEST (use `list jira projects` to see all)\n");
        help.append("• **Issue Types**: Task, Bug, Story, Epic\n");
        help.append("• **Priorities**: Low, Medium, High, Critical\n");
        help.append("• **Status**: To Do, In Progress, Done, Closed\n\n");
        
        help.append("Need more help? Just ask: \"How do I create a Jira ticket?\" or \"Show me Jira projects\"");
        
        return ToolResult.success(help.toString());
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{};
    }
}
