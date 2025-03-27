#!/bin/bash

echo "=== Running Backend Tests with Coverage ==="
cd /home/ubuntu/repos/ai-project-assistant/spring-ai-project
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
mvn clean test jacoco:report

echo ""
echo "Backend coverage report available at: file:///home/ubuntu/repos/ai-project-assistant/spring-ai-project/target/site/jacoco/index.html"

echo ""
echo "=== Running Frontend Tests with Coverage ==="
cd /home/ubuntu/repos/ai-project-assistant/ui
npm test -- --coverage

echo ""
echo "Frontend coverage report available at: file:///home/ubuntu/repos/ai-project-assistant/ui/coverage/lcov-report/index.html"

echo ""
echo "=== Coverage Summary ==="
echo "Backend coverage: See report at file:///home/ubuntu/repos/ai-project-assistant/spring-ai-project/target/site/jacoco/index.html"
echo "Frontend coverage: See report at file:///home/ubuntu/repos/ai-project-assistant/ui/coverage/lcov-report/index.html"
