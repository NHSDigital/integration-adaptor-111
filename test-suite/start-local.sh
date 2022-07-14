set -e

LIGHT_GREEN='\033[1;32m'
RED='\033[0;31m'
NC='\033[0m'

if command -v node &> /dev/null
  then
      echo "
      
      Installing client dependencies
      
      "
      npm --prefix client/ install

      echo "
      
      Installing server dependencies
      
      "
      npm --prefix server/ install

      echo "
      
      Starting services...
      
      "
      npm --prefix server/ run all
  else
    echo "
    
    Couldn't find npm or Node.
    please install at https://nodejs.org/en/download/
    
    "
    exit 1
  fi

  exit 1