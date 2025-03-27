#!/bin/bash

echo "=== Running Backend Tests with Coverage ==="
cd /home/ubuntu/spring-ai-project
mvn clean test jacoco:report

echo ""
echo "Backend coverage report available at: file:///home/ubuntu/spring-ai-project/target/site/jacoco/index.html"

echo ""
echo "=== Running Frontend Tests with Coverage ==="
cd /home/ubuntu/ui
npm test -- --coverage

echo ""
echo "Frontend coverage report available at: file:///home/ubuntu/ui/coverage/lcov-report/index.html"

echo ""
echo "=== Coverage Summary ==="
echo "Backend coverage: See report at file:///home/ubuntu/spring-ai-project/target/site/jacoco/index.html"
echo "Frontend coverage: See report at file:///home/ubuntu/ui/coverage/lcov-report/index.html"
