package com.example.springai.tools;

import com.example.springai.entity.Project;
import com.example.springai.entity.Conversation;
import com.example.springai.model.StoryAnalysisResponse;
import com.example.springai.service.ProjectService;
import com.example.springai.service.ConversationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ksuid.Ksuid;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectManagementTools {

    private final ProjectService projectService;
    private final ConversationService conversationService;
    private final OllamaChatModel chatModel;

    public ProjectManagementTools(ProjectService projectService, ConversationService conversationService, OllamaChatModel chatModel) {
        this.projectService = projectService;
        this.conversationService = conversationService;
        this.chatModel = chatModel;
    }

    @Tool(description = "Create or Add Project for the given Project name and description as strings")
    public Project addProject(String name, String description) {
        System.out.println("Add project tool called");
//        Project prj = new Project();
//        prj.setId(Ksuid.newKsuid().toString());
//        prj.setName(name);
//        prj.setDescription(description);
        return this.projectService.createProject(name, description);
    }

    @Tool(description = "list or show all available Projects")
    public List<Project> listProject() {
        System.out.println("List projects tool called");
        return this.projectService.getAllProjects();
    }

    @Tool(description = "Show details of a specific project by name")
    public String showProject(String name) {
        System.out.println("Show project tool called");
        if (name == null || name.trim().isEmpty()) {
            return "Project name is required";
        }
        
        try {
            var projectOpt = this.projectService.findProjectByName(name);
            if (projectOpt.isEmpty()) {
                return String.format("Project '%s' not found. Please check the name and try again.", name);
            }
            
            var project = projectOpt.get();
            StringBuilder response = new StringBuilder();
            response.append(String.format("Project: %s\n", project.getName()));
            
            if (project.getDescription() != null && !project.getDescription().isEmpty()) {
                response.append(String.format("Description: %s\n\n", project.getDescription()));
            }
            
            if (project.getRequirements().isEmpty()) {
                response.append("This project doesn't have any requirements yet.");
            } else {
                response.append("Requirements:\n");
                for (var req : project.getRequirements()) {
                    response.append(String.format("- %s\n", req.getText()));
                }
            }
            
            return response.toString();
        } catch (Exception e) {
            return "Failed to show project: " + e.getMessage();
        }
    }

    @Tool(description = "Add a requirement to a specific project")
    public String addRequirement(String project, String requirement) {
        System.out.println("Add requirement tool called");
        if (project == null || project.trim().isEmpty() || 
            requirement == null || requirement.trim().isEmpty()) {
            return "Project name and requirement text are required";
        }
        
        try {
            var projectOpt = this.projectService.findProjectByName(project);
            if (projectOpt.isEmpty()) {
                return String.format("Project '%s' not found. Please check the name and try again.", project);
            }
            
            var projectObj = projectOpt.get();
            this.projectService.addRequirement(projectObj, requirement);
            
            return String.format("Requirement '%s' has been added to project '%s'.", 
                                 requirement, projectObj.getName());
        } catch (Exception e) {
            return "Failed to add requirement: " + e.getMessage();
        }
    }

    @Tool(description = "Create a new project with the given name and description")
    public String createProject(String name, String description) {
        System.out.println("Create project tool called");
        if (name == null || name.trim().isEmpty()) {
            return "Project name is required";
        }
        
        try {
            if (this.projectService.findProjectByName(name).isPresent()) {
                return String.format("Project '%s' already exists", name);
            }
            
            var project = this.projectService.createProject(name, description);
            return String.format("Project '%s' has been created successfully", project.getName());
        } catch (Exception e) {
            return "Failed to create project: " + e.getMessage();
        }
    }

    @Tool(description = "Display available commands and usage examples")
    public String help() {
        System.out.println("Help tool called");
        StringBuilder response = new StringBuilder("Available commands:\n\n");
        
        response.append("1. Create a new project:\n");
        response.append("   Example: create project ProjectName\n\n");
        
        response.append("2. List all projects:\n");
        response.append("   Example: list projects\n\n");
        
        response.append("3. Show project details:\n");
        response.append("   Example: show project ProjectName\n\n");
        
        response.append("4. Add requirement to a project:\n");
        response.append("   Example: add requirement The system should allow user login to project ProjectName\n\n");
        
        response.append("5. Refine requirements for a project:\n");
        response.append("   Example: refine requirements for project ProjectName\n\n");
        
        if (this.projectService.hasProjects()) {
            var exampleProject = this.projectService.getFirstProject();
            response.append("Example with existing project:\n");
            response.append(String.format("- show project %s\n", exampleProject.getName()));
            response.append(String.format("- add requirement New login feature to project %s\n", exampleProject.getName()));
            response.append(String.format("- refine requirements for project %s\n", exampleProject.getName()));
        }
        
        return response.toString();
    }
    
    @Tool(description = "Analyze project requirements and generate stories, NFRs, risks, and queries")
    public String analyzeProjectRequirements(String project) {
        System.out.println("Analyze project requirements tool called");
        if (project == null || project.trim().isEmpty()) {
            return "Project name is required";
        }
        
        try {
            var projectOpt = this.projectService.findProjectByName(project);
            if (projectOpt.isEmpty()) {
                return String.format("Project '%s' not found. Please check the name and try again.", project);
            }
            
            var projectObj = projectOpt.get();
            if (projectObj.getRequirements().isEmpty()) {
                return String.format("Project '%s' doesn't have any requirements yet. Please add some requirements first.", projectObj.getName());
            }
            
            StringBuilder requirementsText = new StringBuilder();
            requirementsText.append("Project Name: ").append(projectObj.getName()).append("\n");
            requirementsText.append("Project Description: ").append(projectObj.getDescription()).append("\n\n");
            requirementsText.append("Current Requirements:\n");
            
            for (var req : projectObj.getRequirements()) {
                requirementsText.append("- ").append(req.getText()).append("\n");
            }
            
            String prompt = String.format(
                "Based on the following project details and requirements, please analyze and enhance them. " +
                "Generate user stories, identify non-functional requirements (NFRs), highlight potential risks, " +
                "and suggest queries that need clarification.\n\n" +
                "%s\n\n" +
                "Please format your response in the following JSON structure:\n" +
                "{\n" +
                "  \"stories\": [\"user story 1\", \"user story 2\", ...],\n" +
                "  \"nfrs\": [\"NFR 1\", \"NFR 2\", ...],\n" +
                "  \"risks\": [\"risk 1\", \"risk 2\", ...],\n" +
                "  \"queries\": [\"query 1\", \"query 2\", ...],\n" +
                "  \"summary\": \"brief summary of the analysis\"\n" +
                "}\n\n" +
                "Each user story should follow the format: 'As a [role], I want [feature] so that [benefit]'.\n" +
                "NFRs should cover aspects like performance, security, usability, etc.\n" +
                "Risks should highlight potential challenges or issues.\n" +
                "Queries should identify areas that need clarification or more details.", 
                requirementsText.toString());
            
            String response = ChatClient.create(chatModel)
                    .prompt(prompt)
                    .call()
                    .content();
            
            ObjectMapper objectMapper = new ObjectMapper();
            StoryAnalysisResponse storyAnalysis = objectMapper.readValue(
                response, StoryAnalysisResponse.class);
            
            StringBuilder result = new StringBuilder();
            result.append(String.format("Requirements for project '%s' have been analyzed successfully.\n\n", projectObj.getName()));
            
            result.append("Generated User Stories:\n");
            for (String story : storyAnalysis.getStories()) {
                result.append("- ").append(story).append("\n");
            }
            result.append("\n");
            
            result.append("Non-Functional Requirements:\n");
            for (String nfr : storyAnalysis.getNfrs()) {
                result.append("- ").append(nfr).append("\n");
            }
            result.append("\n");
            
            result.append("Potential Risks:\n");
            for (String risk : storyAnalysis.getRisks()) {
                result.append("- ").append(risk).append("\n");
            }
            result.append("\n");
            
            result.append("Queries for Clarification:\n");
            for (String query : storyAnalysis.getQueries()) {
                result.append("- ").append(query).append("\n");
            }
            result.append("\n");
            
            result.append("To save these refined requirements, use the 'save refined requirements' command with the project name.");
            
            return result.toString();
        } catch (Exception e) {
            return "Failed to analyze project requirements: " + e.getMessage();
        }
    }
    
    @Tool(description = "Save the most recently analyzed requirements for a project")
    public String saveRefinedRequirements(String project) {
        System.out.println("Save refined requirements tool called");
        if (project == null || project.trim().isEmpty()) {
            return "Project name is required";
        }
        
        try {
            var projectOpt = this.projectService.findProjectByName(project);
            if (projectOpt.isEmpty()) {
                return String.format("Project '%s' not found. Please check the name and try again.", project);
            }
            
            var projectObj = projectOpt.get();
            
            String analysisJson = ChatClient.create(chatModel)
                    .prompt("Return the most recent analysis you provided in JSON format")
                    .call()
                    .content();
            
            ObjectMapper objectMapper = new ObjectMapper();
            StoryAnalysisResponse storyAnalysis;
            
            try {
                storyAnalysis = objectMapper.readValue(analysisJson, StoryAnalysisResponse.class);
            } catch (Exception e) {
                return "Failed to parse the most recent analysis. Please analyze the project requirements first.";
            }
            
            this.projectService.saveStoryAnalysisResult(projectObj, storyAnalysis);
            
            return String.format("Refined requirements for project '%s' have been saved successfully.", projectObj.getName());
        } catch (Exception e) {
            return "Failed to save refined requirements: " + e.getMessage();
        }
    }
    
    @Tool(description = "Analyze and refine requirements for a project, generating and saving stories, NFRs, risks, and queries")
    public String refineRequirements(String project) {
        System.out.println("Refine requirements tool called");
        if (project == null || project.trim().isEmpty()) {
            return "Project name is required";
        }
        
        try {
            var projectOpt = this.projectService.findProjectByName(project);
            if (projectOpt.isEmpty()) {
                return String.format("Project '%s' not found. Please check the name and try again.", project);
            }
            
            var projectObj = projectOpt.get();
            if (projectObj.getRequirements().isEmpty()) {
                return String.format("Project '%s' doesn't have any requirements yet. Please add some requirements first.", projectObj.getName());
            }
            
            StringBuilder requirementsText = new StringBuilder();
            requirementsText.append("Project Name: ").append(projectObj.getName()).append("\n");
            requirementsText.append("Project Description: ").append(projectObj.getDescription()).append("\n\n");
            requirementsText.append("Current Requirements:\n");
            
            for (var req : projectObj.getRequirements()) {
                requirementsText.append("- ").append(req.getText()).append("\n");
            }
            
            String prompt = String.format(
                "Based on the following project details and requirements, please analyze and enhance them. " +
                "Generate user stories, identify non-functional requirements (NFRs), highlight potential risks, " +
                "and suggest queries that need clarification.\n\n" +
                "%s\n\n" +
                "Please format your response in the following JSON structure:\n" +
                "{\n" +
                "  \"stories\": [\"user story 1\", \"user story 2\", ...],\n" +
                "  \"nfrs\": [\"NFR 1\", \"NFR 2\", ...],\n" +
                "  \"risks\": [\"risk 1\", \"risk 2\", ...],\n" +
                "  \"queries\": [\"query 1\", \"query 2\", ...],\n" +
                "  \"summary\": \"brief summary of the analysis\"\n" +
                "}\n\n" +
                "Each user story should follow the format: 'As a [role], I want [feature] so that [benefit]'.\n" +
                "NFRs should cover aspects like performance, security, usability, etc.\n" +
                "Risks should highlight potential challenges or issues.\n" +
                "Queries should identify areas that need clarification or more details.", 
                requirementsText.toString());
            
            String response = ChatClient.create(chatModel)
                    .prompt(prompt)
                    .call()
                    .content();
            
            ObjectMapper objectMapper = new ObjectMapper();
            StoryAnalysisResponse storyAnalysis = objectMapper.readValue(
                response, StoryAnalysisResponse.class);
            
            this.projectService.saveStoryAnalysisResult(projectObj, storyAnalysis);
            
            StringBuilder result = new StringBuilder();
            result.append(String.format("Requirements for project '%s' have been refined successfully.\n\n", projectObj.getName()));
            
            result.append("Generated User Stories:\n");
            for (String story : storyAnalysis.getStories()) {
                result.append("- ").append(story).append("\n");
            }
            result.append("\n");
            
            result.append("Non-Functional Requirements:\n");
            for (String nfr : storyAnalysis.getNfrs()) {
                result.append("- ").append(nfr).append("\n");
            }
            result.append("\n");
            
            result.append("Potential Risks:\n");
            for (String risk : storyAnalysis.getRisks()) {
                result.append("- ").append(risk).append("\n");
            }
            result.append("\n");
            
            result.append("Queries for Clarification:\n");
            for (String query : storyAnalysis.getQueries()) {
                result.append("- ").append(query).append("\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            return "Failed to refine requirements: " + e.getMessage();
        }
    }

    @Tool(description = "Set chatbot personality and response style for conversations")
    public String setChatbotPersonality(String personality, String responseStyle) {
        System.out.println("Set chatbot personality tool called");
        if (personality == null || personality.trim().isEmpty()) {
            return "Personality is required. Available options: helpful, professional, friendly, technical, creative";
        }
        
        try {
            String validPersonalities = "helpful,professional,friendly,technical,creative";
            if (!validPersonalities.contains(personality.toLowerCase())) {
                return String.format("Invalid personality '%s'. Available options: %s", personality, validPersonalities);
            }
            
            String style = responseStyle != null ? responseStyle : "balanced";
            return String.format("Chatbot personality set to '%s' with response style '%s'. This will apply to new conversations.", personality, style);
        } catch (Exception e) {
            return "Failed to set chatbot personality: " + e.getMessage();
        }
    }

    @Tool(description = "Start a new conversation with specified personality")
    public String startConversation(String userId, String personality) {
        System.out.println("Start conversation tool called");
        if (userId == null || userId.trim().isEmpty()) {
            userId = "default-user";
        }
        
        try {
            String chatbotPersonality = personality != null ? personality : "helpful";
            Conversation conversation = conversationService.startConversation(userId, chatbotPersonality);
            
            return String.format("New conversation started with ID: %s. Personality: %s. You can now chat with enhanced context retention.", 
                                 conversation.getId(), conversation.getPersonality());
        } catch (Exception e) {
            return "Failed to start conversation: " + e.getMessage();
        }
    }

    @Tool(description = "Analyze conversation context and suggest next actions")
    public String analyzeConversationContext(String conversationId) {
        System.out.println("Analyze conversation context tool called");
        if (conversationId == null || conversationId.trim().isEmpty()) {
            return "Conversation ID is required";
        }
        
        try {
            var conversationOpt = conversationService.findConversationById(conversationId);
            if (conversationOpt.isEmpty()) {
                return String.format("Conversation '%s' not found", conversationId);
            }
            
            var conversation = conversationOpt.get();
            StringBuilder analysis = new StringBuilder();
            analysis.append("Conversation Context Analysis:\n\n");
            analysis.append(String.format("Conversation ID: %s\n", conversation.getId()));
            analysis.append(String.format("Personality: %s\n", conversation.getPersonality()));
            analysis.append(String.format("Duration: %s to %s\n", conversation.getStartTime(), conversation.getLastActivity()));
            analysis.append(String.format("Message Count: %d\n\n", conversation.getMessages().size()));
            
            if (!conversation.getContext().isEmpty()) {
                analysis.append("Current Context:\n").append(conversation.getContext()).append("\n\n");
            }
            
            analysis.append("Suggested Next Actions:\n");
            if (conversation.getMessages().size() < 3) {
                analysis.append("- Continue building rapport and understanding user needs\n");
            } else {
                analysis.append("- Summarize key discussion points\n");
                analysis.append("- Identify action items or next steps\n");
                analysis.append("- Ask clarifying questions if needed\n");
            }
            
            return analysis.toString();
        } catch (Exception e) {
            return "Failed to analyze conversation context: " + e.getMessage();
        }
    }

    @Tool(description = "Generate conversation summary and action items")
    public String generateConversationSummary(String conversationId) {
        System.out.println("Generate conversation summary tool called");
        if (conversationId == null || conversationId.trim().isEmpty()) {
            return "Conversation ID is required";
        }
        
        try {
            String summary = conversationService.generateConversationSummary(conversationId);
            var analytics = conversationService.getConversationAnalytics(conversationId);
            
            StringBuilder result = new StringBuilder(summary);
            result.append("\nAnalytics Summary:\n");
            result.append(String.format("Total Analytics Records: %d\n", analytics.size()));
            
            if (!analytics.isEmpty()) {
                var recentAnalytics = analytics.get(0);
                result.append(String.format("Latest Intent: %s\n", recentAnalytics.getIntent()));
                result.append(String.format("Latest Sentiment: %s\n", recentAnalytics.getSentiment()));
                if (recentAnalytics.getResponseTime() != null) {
                    result.append(String.format("Latest Response Time: %dms\n", recentAnalytics.getResponseTime()));
                }
            }
            
            return result.toString();
        } catch (Exception e) {
            return "Failed to generate conversation summary: " + e.getMessage();
        }
    }

    @Tool(description = "Get chatbot analytics and performance metrics")
    public String getChatbotAnalytics(String timeRange) {
        System.out.println("Get chatbot analytics tool called");
        try {
            LocalDateTime since;
            switch (timeRange != null ? timeRange.toLowerCase() : "day") {
                case "hour":
                    since = LocalDateTime.now().minusHours(1);
                    break;
                case "week":
                    since = LocalDateTime.now().minusWeeks(1);
                    break;
                case "month":
                    since = LocalDateTime.now().minusMonths(1);
                    break;
                default:
                    since = LocalDateTime.now().minusDays(1);
            }
            
            StringBuilder analytics = new StringBuilder();
            analytics.append(String.format("Chatbot Analytics (Last %s):\n\n", timeRange != null ? timeRange : "day"));
            
            var topIntents = conversationService.getTopIntents(since);
            analytics.append("Top Intents:\n");
            for (Object[] intent : topIntents) {
                analytics.append(String.format("- %s: %d occurrences\n", intent[0], intent[1]));
            }
            analytics.append("\n");
            
            var avgResponseTime = conversationService.getAverageResponseTime(since);
            if (avgResponseTime != null) {
                analytics.append(String.format("Average Response Time: %.2fms\n\n", avgResponseTime));
            }
            
            var sentimentDist = conversationService.getSentimentDistribution(since);
            analytics.append("Sentiment Distribution:\n");
            for (Object[] sentiment : sentimentDist) {
                analytics.append(String.format("- %s: %d occurrences\n", sentiment[0], sentiment[1]));
            }
            
            return analytics.toString();
        } catch (Exception e) {
            return "Failed to get chatbot analytics: " + e.getMessage();
        }
    }
}
