package com.example.springai.tools;

import com.example.springai.entity.Project;
import com.example.springai.model.StoryAnalysisResponse;
import com.example.springai.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ksuid.Ksuid;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectManagementTools {

    private final ProjectService projectService;
    private final OllamaChatModel chatModel;

    public ProjectManagementTools(ProjectService projectService, OllamaChatModel chatModel) {
        this.projectService = projectService;
        this.chatModel = chatModel;
    }

    @Tool(description = "Create a new project with a friendly approach - I'll help you set up your project with the given name and description, making sure everything is organized perfectly!")
    public Project addProject(String name, String description) {
        System.out.println("Add project tool called");
//        Project prj = new Project();
//        prj.setId(Ksuid.newKsuid().toString());
//        prj.setName(name);
//        prj.setDescription(description);
        return this.projectService.createProject(name, description);
    }

    @Tool(description = "Show you all your wonderful projects in a clear, organized way - I'll present them so you can easily see what you're working on!")
    public List<Project> listProject() {
        System.out.println("List projects tool called");
        return this.projectService.getAllProjects();
    }

    @Tool(description = "Give you a detailed, friendly overview of your specific project - I'll show you everything about it in an easy-to-understand format!")
    public String showProject(String name) {
        System.out.println("Show project tool called");
        if (name == null || name.trim().isEmpty()) {
            return "I'd love to help you! Could you please provide the project name so I can show you all the details? 😊";
        }
        
        try {
            var projectOpt = this.projectService.findProjectByName(name);
            if (projectOpt.isEmpty()) {
                return String.format("I couldn't find a project named '%s'. No worries though! Please double-check the name, or would you like me to show you all available projects instead? I'm here to help! 🔍", name);
            }
            
            var project = projectOpt.get();
            StringBuilder response = new StringBuilder();
            response.append(String.format("Project: %s\n", project.getName()));
            
            if (project.getDescription() != null && !project.getDescription().isEmpty()) {
                response.append(String.format("Description: %s\n\n", project.getDescription()));
            }
            
            if (project.getRequirements().isEmpty()) {
                response.append("This project is ready for requirements! Would you like me to help you add some? I'm excited to help you build something amazing! ✨");
            } else {
                response.append("Here are the requirements I've organized for you:\n");
                for (var req : project.getRequirements()) {
                    response.append(String.format("✓ %s\n", req.getText()));
                }
            }
            
            return response.toString();
        } catch (Exception e) {
            return "Oops! I encountered a small hiccup while trying to show your project details. Don't worry, let's try again! If this keeps happening, I'm here to help troubleshoot. 🛠️ Error: " + e.getMessage();
        }
    }

    @Tool(description = "Help you add a new requirement to your project - I'll make sure it's properly organized and easy to track!")
    public String addRequirement(String project, String requirement) {
        System.out.println("Add requirement tool called");
        if (project == null || project.trim().isEmpty() || 
            requirement == null || requirement.trim().isEmpty()) {
            return "I'm excited to help you add a requirement! Could you please provide both the project name and the requirement text? I want to make sure I organize everything perfectly for you! 📝";
        }
        
        try {
            var projectOpt = this.projectService.findProjectByName(project);
            if (projectOpt.isEmpty()) {
                return String.format("I couldn't find a project named '%s'. No worries! Let me help you - please double-check the project name, or would you like me to show you all your projects? I'm here to help! 🔍", project);
            }
            
            var projectObj = projectOpt.get();
            this.projectService.addRequirement(projectObj, requirement);
            
            return String.format("Perfect! ✅ I've successfully added the requirement '%s' to your project '%s'. Your project is looking great! Is there anything else you'd like me to help you with?", 
                                 requirement, projectObj.getName());
        } catch (Exception e) {
            return "Oops! I ran into a small issue while adding your requirement. Don't worry, let's try again! If this keeps happening, I'm here to help troubleshoot. 🛠️ Error: " + e.getMessage();
        }
    }

    @Tool(description = "Help you create a brand new project - I'll set it up perfectly with your chosen name and description!")
    public String createProject(String name, String description) {
        System.out.println("Create project tool called");
        if (name == null || name.trim().isEmpty()) {
            return "I'm excited to help you create a new project! Could you please provide a name for your project? I can't wait to help you get started! 🚀";
        }
        
        try {
            if (this.projectService.findProjectByName(name).isPresent()) {
                return String.format("Great news! You already have a project named '%s'! Would you like me to show you its details, or perhaps help you create a project with a different name? I'm here to help! 💡", name);
            }
            
            var project = this.projectService.createProject(name, description);
            return String.format("Fantastic! 🎉 Your project '%s' has been created successfully! I'm excited to help you build something amazing. What would you like to do next? Add some requirements perhaps?", project.getName());
        } catch (Exception e) {
            return "Oh no! I encountered a small hiccup while creating your project. Don't worry, let's try again! If this keeps happening, I'm here to help troubleshoot. 🛠️ Error: " + e.getMessage();
        }
    }

    @Tool(description = "Show you all the amazing things I can help you with - I'll give you friendly guidance on using all my features!")
    public String help() {
        System.out.println("Help tool called");
        StringBuilder response = new StringBuilder("Hi there! I'm so excited to help you! 🌟 Here's everything I can do for you:\n\n");
        
        response.append("🚀 **Create a new project:**\n");
        response.append("   Just say: \"create project MyAwesomeProject\"\n\n");
        
        response.append("📋 **See all your projects:**\n");
        response.append("   Simply ask: \"list projects\" or \"show me my projects\"\n\n");
        
        response.append("🔍 **Get project details:**\n");
        response.append("   Ask me: \"show project MyAwesomeProject\"\n\n");
        
        response.append("📝 **Add requirements:**\n");
        response.append("   Tell me: \"add requirement 'Users should be able to login' to project MyAwesomeProject\"\n\n");
        
        response.append("✨ **Refine and enhance requirements:**\n");
        response.append("   Say: \"refine requirements for project MyAwesomeProject\"\n\n");
        
        if (this.projectService.hasProjects()) {
            var exampleProject = this.projectService.getFirstProject();
            response.append("💡 **Try these with your existing project:**\n");
            response.append(String.format("   • \"show project %s\"\n", exampleProject.getName()));
            response.append(String.format("   • \"add requirement 'New login feature' to project %s\"\n", exampleProject.getName()));
            response.append(String.format("   • \"refine requirements for project %s\"\n\n", exampleProject.getName()));
        }
        
        response.append("I'm here to make your project management journey smooth and enjoyable! Feel free to ask me anything - I love helping you succeed! 😊");
        
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
                return String.format("I couldn't find a project named '%s'. No worries! Let me help you - please double-check the project name, or would you like me to show you all your projects? I'm here to help! 🔍", project);
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
                return String.format("I couldn't find a project named '%s'. No worries! Let me help you - please double-check the project name, or would you like me to show you all your projects? I'm here to help! 🔍", project);
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
                return String.format("I couldn't find a project named '%s'. No worries! Let me help you - please double-check the project name, or would you like me to show you all your projects? I'm here to help! 🔍", project);
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
}
