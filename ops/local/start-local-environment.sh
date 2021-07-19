#!/usr/bin/env bash
set -e

./build-image.sh

docker-compose down
docker-compose up