package com.example.springai.config;

import com.example.springai.entity.*;
import com.example.springai.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class TestDataInitializer {

    @Bean
    public CommandLineRunner initTestData(
            ProjectRepository projectRepository,
            RequirementRepository requirementRepository,
            StoryRepository storyRepository,
            RiskRepository riskRepository,
            QueryRepository queryRepository,
            NFRRepository nfrRepository) {
        
        return args -> {
            // Only add test data if no projects exist
            if (projectRepository.count() == 0) {
                System.out.println("Initializing test data...");
                
                // Project 1: E-Commerce Platform
                Project ecommerce = new Project();
                ecommerce.setName("E-Commerce Platform");
                ecommerce.setDescription("A modern e-commerce platform with advanced features");
                projectRepository.save(ecommerce);
                
                addRequirement(requirementRepository, ecommerce, "The system should allow users to browse products by category");
                addRequirement(requirementRepository, ecommerce, "Users should be able to add items to a shopping cart");
                addRequirement(requirementRepository, ecommerce, "The platform should support secure payment processing");
                addRequirement(requirementRepository, ecommerce, "Users should be able to create and manage their accounts");
                addRequirement(requirementRepository, ecommerce, "The system should provide order tracking functionality");
                addRequirement(requirementRepository, ecommerce, "Admin users should be able to manage product inventory");
                addRequirement(requirementRepository, ecommerce, "The platform should support product reviews and ratings");
                addRequirement(requirementRepository, ecommerce, "The system should provide search functionality with filters");
                addRequirement(requirementRepository, ecommerce, "The platform should support multiple languages and currencies");
                addRequirement(requirementRepository, ecommerce, "The system should provide product recommendations based on user behavior");
                
                addStory(storyRepository, ecommerce, "Browse Products", "As a customer, I want to browse products by category so that I can find items I'm interested in quickly");
                addStory(storyRepository, ecommerce, "Add to Cart", "As a customer, I want to add items to my shopping cart so that I can purchase them later");
                addStory(storyRepository, ecommerce, "Checkout Process", "As a customer, I want a secure checkout process so that my payment information is protected");
                
                addRisk(riskRepository, ecommerce, "Payment Gateway Integration", "Integration with payment gateways may be complex and require additional security measures");
                addNFR(nfrRepository, ecommerce, "Performance", "The system should handle at least 1000 concurrent users without performance degradation");
                addQuery(queryRepository, ecommerce, "Multi-vendor Support", "Will the platform need to support multiple vendors/sellers?");
                
                // Project 2: Task Management App
                Project taskApp = new Project();
                taskApp.setName("Task Management App");
                taskApp.setDescription("A collaborative task management application for teams");
                projectRepository.save(taskApp);
                
                addRequirement(requirementRepository, taskApp, "Users should be able to create and assign tasks");
                addRequirement(requirementRepository, taskApp, "The app should support task prioritization and deadlines");
                addRequirement(requirementRepository, taskApp, "Users should be able to organize tasks into projects");
                addRequirement(requirementRepository, taskApp, "The system should provide notifications for upcoming deadlines");
                addRequirement(requirementRepository, taskApp, "Users should be able to track time spent on tasks");
                addRequirement(requirementRepository, taskApp, "The app should support file attachments for tasks");
                addRequirement(requirementRepository, taskApp, "Users should be able to comment on tasks");
                addRequirement(requirementRepository, taskApp, "The system should provide progress tracking and reporting");
                addRequirement(requirementRepository, taskApp, "The app should support integration with calendar applications");
                addRequirement(requirementRepository, taskApp, "Users should be able to create recurring tasks");
                
                addStory(storyRepository, taskApp, "Create Task", "As a team member, I want to create tasks so that I can track my work");
                addStory(storyRepository, taskApp, "Assign Task", "As a team leader, I want to assign tasks to team members so that work is distributed effectively");
                addStory(storyRepository, taskApp, "Set Deadlines", "As a project manager, I want to set deadlines for tasks so that projects stay on schedule");
                
                addRisk(riskRepository, taskApp, "User Adoption", "Team members may resist adopting a new task management system");
                addNFR(nfrRepository, taskApp, "Usability", "The app should be intuitive enough that new users can learn it in less than 30 minutes");
                addQuery(queryRepository, taskApp, "Mobile Support", "Will the app need to support mobile devices with offline capabilities?");
                
                // Project 3: Health Monitoring System
                Project healthSystem = new Project();
                healthSystem.setName("Health Monitoring System");
                healthSystem.setDescription("A system for tracking and analyzing health metrics");
                projectRepository.save(healthSystem);
                
                addRequirement(requirementRepository, healthSystem, "The system should collect data from wearable devices");
                addRequirement(requirementRepository, healthSystem, "Users should be able to view their health metrics over time");
                addRequirement(requirementRepository, healthSystem, "The system should provide alerts for abnormal health readings");
                addRequirement(requirementRepository, healthSystem, "Users should be able to set health goals and track progress");
                addRequirement(requirementRepository, healthSystem, "The system should support sharing data with healthcare providers");
                addRequirement(requirementRepository, healthSystem, "Users should be able to log meals and nutrition information");
                addRequirement(requirementRepository, healthSystem, "The system should provide personalized health recommendations");
                addRequirement(requirementRepository, healthSystem, "Users should be able to track medication schedules");
                addRequirement(requirementRepository, healthSystem, "The system should support integration with medical record systems");
                addRequirement(requirementRepository, healthSystem, "Users should be able to schedule appointments with healthcare providers");
                
                addStory(storyRepository, healthSystem, "Connect Device", "As a user, I want to connect my wearable device so that my health data is automatically tracked");
                addStory(storyRepository, healthSystem, "View Health Trends", "As a user, I want to view my health metrics over time so that I can track my progress");
                addStory(storyRepository, healthSystem, "Receive Alerts", "As a user, I want to receive alerts for abnormal health readings so that I can take action quickly");
                
                addRisk(riskRepository, healthSystem, "Data Privacy", "Health data is sensitive and requires strict privacy controls");
                addNFR(nfrRepository, healthSystem, "Security", "The system must comply with HIPAA regulations for health data protection");
                addQuery(queryRepository, healthSystem, "Device Compatibility", "Which wearable device brands will be supported initially?");
                
                // Project 4: Smart Home Automation
                Project smartHome = new Project();
                smartHome.setName("Smart Home Automation");
                smartHome.setDescription("A system for controlling and automating home devices");
                projectRepository.save(smartHome);
                
                addRequirement(requirementRepository, smartHome, "The system should support integration with various smart home devices");
                addRequirement(requirementRepository, smartHome, "Users should be able to control devices remotely");
                addRequirement(requirementRepository, smartHome, "The system should support voice commands");
                addRequirement(requirementRepository, smartHome, "Users should be able to create automation routines");
                addRequirement(requirementRepository, smartHome, "The system should provide energy usage monitoring");
                addRequirement(requirementRepository, smartHome, "Users should be able to set schedules for device operation");
                addRequirement(requirementRepository, smartHome, "The system should support geofencing for location-based automation");
                addRequirement(requirementRepository, smartHome, "Users should be able to monitor home security cameras");
                addRequirement(requirementRepository, smartHome, "The system should provide alerts for unusual activity");
                addRequirement(requirementRepository, smartHome, "Users should be able to control multiple homes from a single account");
                
                addStory(storyRepository, smartHome, "Device Control", "As a homeowner, I want to control my smart devices remotely so that I can manage my home from anywhere");
                addStory(storyRepository, smartHome, "Create Routines", "As a user, I want to create automation routines so that multiple devices can be controlled with a single command");
                addStory(storyRepository, smartHome, "Energy Monitoring", "As a homeowner, I want to monitor energy usage so that I can reduce my electricity bills");
                
                addRisk(riskRepository, smartHome, "Device Compatibility", "Different smart home devices use different protocols which may complicate integration");
                addNFR(nfrRepository, smartHome, "Reliability", "The system should have 99.9% uptime to ensure home devices remain accessible");
                addQuery(queryRepository, smartHome, "Local Processing", "Will the system support local processing to function during internet outages?");
                
                // Project 5: Learning Management System
                Project lms = new Project();
                lms.setName("Learning Management System");
                lms.setDescription("A platform for creating and delivering educational content");
                projectRepository.save(lms);
                
                addRequirement(requirementRepository, lms, "The system should support course creation and management");
                addRequirement(requirementRepository, lms, "Users should be able to enroll in courses");
                addRequirement(requirementRepository, lms, "The system should support various content types (video, text, quizzes)");
                addRequirement(requirementRepository, lms, "Users should be able to track their progress through courses");
                addRequirement(requirementRepository, lms, "The system should provide assessment and grading functionality");
                addRequirement(requirementRepository, lms, "Users should be able to participate in discussion forums");
                addRequirement(requirementRepository, lms, "The system should support live virtual classrooms");
                addRequirement(requirementRepository, lms, "Users should be able to receive certificates upon course completion");
                addRequirement(requirementRepository, lms, "The system should provide analytics on student performance");
                addRequirement(requirementRepository, lms, "Users should be able to provide feedback on courses");
                
                addStory(storyRepository, lms, "Create Course", "As an instructor, I want to create courses so that I can share educational content");
                addStory(storyRepository, lms, "Enroll in Course", "As a student, I want to enroll in courses so that I can access learning materials");
                addStory(storyRepository, lms, "Track Progress", "As a student, I want to track my progress through courses so that I know what I've completed");
                
                addRisk(riskRepository, lms, "Content Management", "Managing large volumes of educational content may require significant storage and CDN integration");
                addNFR(nfrRepository, lms, "Scalability", "The system should support up to 10,000 concurrent users during peak periods");
                addQuery(queryRepository, lms, "Certification Integration", "Will the system need to integrate with external certification authorities?");
                
                System.out.println("Test data initialization completed successfully");
            } else {
                System.out.println("Database already contains projects, skipping test data initialization");
            }
        };
    }
    
    private void addRequirement(RequirementRepository repository, Project project, String text) {
        Requirement requirement = new Requirement();
        requirement.setText(text);
        requirement.setProject(project);
        repository.save(requirement);
    }
    
    private void addStory(StoryRepository repository, Project project, String title, String description) {
        Story story = new Story();
        story.setTitle(title);
        story.setDescription(description);
        story.setProject(project);
        repository.save(story);
    }
    
    private void addRisk(RiskRepository repository, Project project, String description, String mitigation) {
        Risk risk = new Risk();
        risk.setDescription(description);
        risk.setMitigation(mitigation);
        risk.setProject(project);
        repository.save(risk);
    }
    
    private void addNFR(NFRRepository repository, Project project, String category, String description) {
        NFR nfr = new NFR();
        nfr.setCategory(category);
        nfr.setDescription(description);
        nfr.setProject(project);
        repository.save(nfr);
    }
    
    private void addQuery(QueryRepository repository, Project project, String question, String context) {
        Query query = new Query();
        query.setQuestion(question);
        query.setContext(context);
        query.setProject(project);
        repository.save(query);
    }
}
