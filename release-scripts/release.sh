#!/bin/bash 

set -e

export RELEASE_VERSION=1.0.3
cd ..

docker build -t local/111:${RELEASE_VERSION} -f docker/service/Dockerfile .
docker build -t local/111-nginx:${RELEASE_VERSION} -f docker/nginx/Dockerfile .

docker tag local/111:${RELEASE_VERSION} nhsdev/nia-111-adaptor:${RELEASE_VERSION}
docker tag local/111-nginx:${RELEASE_VERSION} nhsdev/nia-111-nginx-adaptor:${RELEASE_VERSION}

if [ "$1" == "-y" ];
then
  echo "Tagging and pushing Docker image and git tag"
  docker tag nhsdev/nia-111-adaptor:${RELEASE_VERSION} nhsdev/nia-111-adaptor:latest
  docker tag nhsdev/nia-111-nginx-adaptor:${RELEASE_VERSION} nhsdev/nia-111-nginx-adaptor:latest
  docker push nhsdev/nia-111-adaptor:${RELEASE_VERSION}
  docker push nhsdev/nia-111-nginx-adaptor:${RELEASE_VERSION}
  git tag -a ${RELEASE_VERSION} -m "Release ${RELEASE_VERSION}"
  git push origin ${RELEASE_VERSION}
fi
