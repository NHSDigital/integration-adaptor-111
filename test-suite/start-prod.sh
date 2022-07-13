set -e

app_install()
{
      echo "
      
      Installing client dependencies
      
      "
      npm --prefix client/ install

      echo "
      
      Installing server dependencies
      
      "
      npm --prefix server/ install

      echo "
      
      Building client for production...
      
      "
      npm --prefix client/ run build

      echo "
      
      Building server for production...
      
      "
      npm --prefix server/ run build

      echo "
      
      Outputing client html into html folder...
      
      "
      mkdir -p html
      cp client/dist/* html/
      
      echo "
      
      Starting the test suite server...
      
      "
      npx pm2 delete TestSuiteServer  2> /dev/null || true && npx pm2 start  server/dist/index.js --name TestSuiteServer

      echo "
      
      Done! Server running on port 7000.
      
      Remember to relocate html files to your web server html folder.
      
      Use 'npx pm2 ps' to see your server instance
      Use 'npx pm2 delete/stop/start TestSuiteServer' for running the instance
      
      "
}

if ! command -v node &> /dev/null
  then
    echo "
    
    Couldn't find npm or Node.
    please install at https://nodejs.org/en/
    
    "
    exit 1  
  else
   app_install
   exit 1
  fi

  exit 1