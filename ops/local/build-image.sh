#!/usr/bin/env bash
set -e

TAG=${1:-uk.nhs/integration-adaptor-111:0.0.1-SNAPSHOT}

pushd ../../service/ && ./gradlew --build-cache bootJar && popd

cp ../../service/build/libs/integration-adaptor-111.jar integration-adaptor-111.jar

docker build -t $TAG .

rm integration-adaptor-111.jar
