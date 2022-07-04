set -e

LIGHT_GREEN='\033[1;32m'
RED='\033[0;31m'
NC='\033[0m'

echo "\n${LIGHT_GREEN}Building test-suite containers ${NC}\n"
docker-compose build test-server test-client;

echo "\n${LIGHT_GREEN}Running test-suite containers ${NC}\n"
docker-compose up test-server test-client;
