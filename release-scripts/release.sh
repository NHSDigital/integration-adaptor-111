#!/bin/bash 

set -e

export RELEASE_VERSION=0.2.0
cd ..

docker build -t local/111:${RELEASE_VERSION} .

docker tag local/111:${RELEASE_VERSION} nhsdev/nia-111-adaptor:${RELEASE_VERSION}

if [ "$1" == "-y" ];
then
  echo "Tagging and pushing Docker image and git tag"
  docker tag nhsdev/nia-111-adaptor:${RELEASE_VERSION} nhsdev/nia-111-adaptor:latest
  docker push nhsdev/nia-111-adaptor:${RELEASE_VERSION}
  git tag -a ${RELEASE_VERSION} -m "Release ${RELEASE_VERSION}"
  git push origin ${RELEASE_VERSION}
fi