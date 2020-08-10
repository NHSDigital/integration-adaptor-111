#!/bin/bash 

set -e

export BUILD_TAG=latest
export RELEASE_VERSION=0.1.0
cd ..

docker build -t local/111:${BUILD_TAG} .

docker tag local/111:${BUILD_TAG} nhsdev/nia-111-adaptor:${RELEASE_VERSION}

if [ "$1" == "-y" ];
then
  echo "Tagging and pushing Docker image and git tag"
  docker push nhsdev/nia-111-adaptor:${RELEASE_VERSION}
  git tag -a ${RELEASE_VERSION} -m "Release ${RELEASE_VERSION}"
  git push origin ${RELEASE_VERSION}
fi