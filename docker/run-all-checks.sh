#!/usr/bin/env bash
set -x -e

docker network create 111network || true
docker-compose -f docker-compose.yml -f docker-compose-checks.yml build integration-adaptor-111 activemq wiremock
docker-compose -f docker-compose.yml -f docker-compose-checks.yml up integration-adaptor-111