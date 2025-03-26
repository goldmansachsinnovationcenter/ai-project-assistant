#!/bin/bash

# Script to create test data for the Spring AI Project
# Creates 5 projects with requirements using the API

BASE_URL="http://localhost:8080/api"

# Project 1: E-Commerce Platform
echo "Creating Project 1: E-Commerce Platform"
curl -X POST "${BASE_URL}/project/create" \
  -H "Content-Type: application/json" \
  -d '{"name":"E-Commerce Platform","description":"A modern e-commerce platform with advanced features"}'

# Add requirements for Project 1
echo "Adding requirements for E-Commerce Platform"
curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"E-Commerce Platform","text":"The system should allow users to browse products by category"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"E-Commerce Platform","text":"Users should be able to add items to a shopping cart"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"E-Commerce Platform","text":"The platform should support secure payment processing"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"E-Commerce Platform","text":"Users should be able to create and manage their accounts"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"E-Commerce Platform","text":"The system should provide order tracking functionality"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"E-Commerce Platform","text":"Admin users should be able to manage product inventory"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"E-Commerce Platform","text":"The platform should support product reviews and ratings"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"E-Commerce Platform","text":"The system should provide search functionality with filters"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"E-Commerce Platform","text":"The platform should support multiple languages and currencies"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"E-Commerce Platform","text":"The system should provide product recommendations based on user behavior"}'

# Generate stories for Project 1
echo "Generating stories for E-Commerce Platform"
curl -X POST "${BASE_URL}/project/stories/prepare" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"E-Commerce Platform"}'

# Project 2: Task Management App
echo "Creating Project 2: Task Management App"
curl -X POST "${BASE_URL}/project/create" \
  -H "Content-Type: application/json" \
  -d '{"name":"Task Management App","description":"A collaborative task management application for teams"}'

# Add requirements for Project 2
echo "Adding requirements for Task Management App"
curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Task Management App","text":"Users should be able to create and assign tasks"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Task Management App","text":"The app should support task prioritization and deadlines"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Task Management App","text":"Users should be able to organize tasks into projects"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Task Management App","text":"The system should provide notifications for upcoming deadlines"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Task Management App","text":"Users should be able to track time spent on tasks"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Task Management App","text":"The app should support file attachments for tasks"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Task Management App","text":"Users should be able to comment on tasks"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Task Management App","text":"The system should provide progress tracking and reporting"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Task Management App","text":"The app should support integration with calendar applications"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Task Management App","text":"Users should be able to create recurring tasks"}'

# Generate stories for Project 2
echo "Generating stories for Task Management App"
curl -X POST "${BASE_URL}/project/stories/prepare" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Task Management App"}'

# Project 3: Health Monitoring System
echo "Creating Project 3: Health Monitoring System"
curl -X POST "${BASE_URL}/project/create" \
  -H "Content-Type: application/json" \
  -d '{"name":"Health Monitoring System","description":"A system for tracking and analyzing health metrics"}'

# Add requirements for Project 3
echo "Adding requirements for Health Monitoring System"
curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Health Monitoring System","text":"The system should collect data from wearable devices"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Health Monitoring System","text":"Users should be able to view their health metrics over time"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Health Monitoring System","text":"The system should provide alerts for abnormal health readings"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Health Monitoring System","text":"Users should be able to set health goals and track progress"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Health Monitoring System","text":"The system should support sharing data with healthcare providers"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Health Monitoring System","text":"Users should be able to log meals and nutrition information"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Health Monitoring System","text":"The system should provide personalized health recommendations"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Health Monitoring System","text":"Users should be able to track medication schedules"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Health Monitoring System","text":"The system should support integration with medical record systems"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Health Monitoring System","text":"Users should be able to schedule appointments with healthcare providers"}'

# Generate stories for Project 3
echo "Generating stories for Health Monitoring System"
curl -X POST "${BASE_URL}/project/stories/prepare" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Health Monitoring System"}'

# Project 4: Smart Home Automation
echo "Creating Project 4: Smart Home Automation"
curl -X POST "${BASE_URL}/project/create" \
  -H "Content-Type: application/json" \
  -d '{"name":"Smart Home Automation","description":"A system for controlling and automating home devices"}'

# Add requirements for Project 4
echo "Adding requirements for Smart Home Automation"
curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Smart Home Automation","text":"The system should support integration with various smart home devices"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Smart Home Automation","text":"Users should be able to control devices remotely"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Smart Home Automation","text":"The system should support voice commands"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Smart Home Automation","text":"Users should be able to create automation routines"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Smart Home Automation","text":"The system should provide energy usage monitoring"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Smart Home Automation","text":"Users should be able to set schedules for device operation"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Smart Home Automation","text":"The system should support geofencing for location-based automation"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Smart Home Automation","text":"Users should be able to monitor home security cameras"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Smart Home Automation","text":"The system should provide alerts for unusual activity"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Smart Home Automation","text":"Users should be able to control multiple homes from a single account"}'

# Generate stories for Project 4
echo "Generating stories for Smart Home Automation"
curl -X POST "${BASE_URL}/project/stories/prepare" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Smart Home Automation"}'

# Project 5: Learning Management System
echo "Creating Project 5: Learning Management System"
curl -X POST "${BASE_URL}/project/create" \
  -H "Content-Type: application/json" \
  -d '{"name":"Learning Management System","description":"A platform for creating and delivering educational content"}'

# Add requirements for Project 5
echo "Adding requirements for Learning Management System"
curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Learning Management System","text":"The system should support course creation and management"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Learning Management System","text":"Users should be able to enroll in courses"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Learning Management System","text":"The system should support various content types (video, text, quizzes)"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Learning Management System","text":"Users should be able to track their progress through courses"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Learning Management System","text":"The system should provide assessment and grading functionality"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Learning Management System","text":"Users should be able to participate in discussion forums"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Learning Management System","text":"The system should support live virtual classrooms"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Learning Management System","text":"Users should be able to receive certificates upon course completion"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Learning Management System","text":"The system should provide analytics on student performance"}'

curl -X POST "${BASE_URL}/project/requirement/add" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Learning Management System","text":"Users should be able to provide feedback on courses"}'

# Generate stories for Project 5
echo "Generating stories for Learning Management System"
curl -X POST "${BASE_URL}/project/stories/prepare" \
  -H "Content-Type: application/json" \
  -d '{"projectName":"Learning Management System"}'

echo "Test data creation completed"
