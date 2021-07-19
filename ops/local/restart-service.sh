#!/usr/bin/env bash
set -e

./build-image.sh

docker-compose up -d --no-deps integration-adaptor-111