#!/bin/bash

LIGHT_GREEN='\033[1;32m' 
NC='\033[0m' 

echo -e "${LIGHT_GREEN}Stopping running containers${NC}" 
docker-compose -f docker-compose.yml down

echo -e "${LIGHT_GREEN}Build the docker image for 111${NC}"
cd ../..
docker build -t local/111:latest .
cd test-scripts/0.1.0

echo -e "${LIGHT_GREEN}Build and starting containers${NC}" 
docker-compose -f docker-compose.yml up -d --no-build