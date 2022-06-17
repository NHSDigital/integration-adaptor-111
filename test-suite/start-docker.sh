set -e

LIGHT_GREEN='\033[1;32m'
RED='\033[0;31m'
NC='\033[0m'

#echo "\n${LIGHT_GREEN}Building server Docker container ${NC}\n"
#(cd ./server; docker build -t 111-adaptor-test-server .   )
#
#echo "\n${LIGHT_GREEN}Building client Docker container ${NC}\n"
#(cd ./client; docker build -t 111-adaptor-test-client .   )

docker-compose build test-server test-client;
docker-compose up test-server test-client;