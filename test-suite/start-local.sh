set -e

LIGHT_GREEN='\033[1;32m'
RED='\033[0;31m'
NC='\033[0m'

if which node > /dev/null
  then
      echo "\n${LIGHT_GREEN}Installing client dependencies${NC}\n"
      npm --prefix client/ install

      echo "\n${LIGHT_GREEN}Installing server dependencies${NC}\n"
      npm --prefix server/ install

      echo "\n${LIGHT_GREEN}Starting services...${NC}\n"
      npm --prefix server/ run dev
  else
    echo : "\n${RED}Couldn't find npm or Node.
    please install at https://nodejs.org/en/download/${NC}\n"
    exit 1
  fi

  exit 1