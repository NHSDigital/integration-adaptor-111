#!/usr/bin/env bash
set -e

make build-adapter

docker-compose down
docker-compose up activemq integration-adaptor-111
