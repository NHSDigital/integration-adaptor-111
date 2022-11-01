#!/usr/bin/env bash
set -x -e


if [[ "$(docker network ls | grep "111network")" == "" ]] ; then
    docker network create 111network
fi
docker-compose build wiremock activemq rabbitmq;
docker-compose up -d wiremock activemq rabbitmq;
docker-compose build integration-adaptor-111;
docker-compose up integration-adaptor-111;