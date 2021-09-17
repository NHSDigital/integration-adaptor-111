#!/usr/bin/env bash
set -x

make build-adapter

docker-compose stop -t 1 integration-adaptor-111
docker-compose rm --force integration-adaptor-111
docker-compose up --build --force-recreate --detach integration-adaptor-111
