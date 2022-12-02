#!/usr/bin/env bash
set -x -e


if [[ "$(docker network ls | grep "111network")" == "" ]] ; then
    docker network create 111network
fi
docker-compose -f docker-compose-arm.yml build wiremock activemq rabbitmq;
docker-compose -f docker-compose-arm.yml up -d wiremock activemq rabbitmq;
docker-compose -f docker-compose-arm.yml build integration-adaptor-111;
docker-compose -f docker-compose-arm.yml up integration-adaptor-111;