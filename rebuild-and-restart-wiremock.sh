#!/usr/bin/env bash
set -x

docker-compose stop -t 1 wiremock
docker-compose rm --force wiremock
docker-compose up --build --force-recreate --detach wiremock
