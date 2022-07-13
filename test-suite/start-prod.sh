set -e

LIGHT_GREEN='\033[1;32m'
RED='\033[0;31m'
NC='\033[0m'

app_install()
{
      echo "\n${LIGHT_GREEN}Installing client dependencies${NC}\n"
      npm --prefix client/ install

      echo "\n${LIGHT_GREEN}Installing server dependencies${NC}\n"
      npm --prefix server/ install

      echo "\n${LIGHT_GREEN}Building client for production...${NC}\n"
      npm --prefix client/ run build

      echo "\n${LIGHT_GREEN}Building server for production...${NC}\n"
      npm --prefix server/ run build

      echo "\n${LIGHT_GREEN}Outputing client html into html folder...${NC}\n"
      mkdir -p html
      cp client/dist/* html/
      
      echo "\n${LIGHT_GREEN}Starting the test suite server...${NC}\n"
      npx pm2 delete TestSuiteServer  2> /dev/null || true && npx pm2 start  server/dist/index.js --name TestSuiteServer

      echo "\n${LIGHT_GREEN}Done! Server running on port 7000. \n Remember to relocate html files to your web server html folder. \n \n Use 'npx pm2 ps' to see your server instance\n Use 'npx pm2 delete/stop/start TestSuiteServer' for running the instance${NC}\n"
}

if ! command -v node &> /dev/null
  then
    echo "\n${RED}Couldn't find npm or Node.
    please install at https://nodejs.org/en/${NC}\n"
    exit 1  
  else
   app_install
   exit 1
  fi

  exit 1