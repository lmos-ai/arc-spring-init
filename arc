
#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
# 
# SPDX-License-Identifier: Apache-2.0
PURPLE='\033[0;35m'
NC='\033[0m'

function usage() {
  echo "Usage:"
  echo "arc [command] [agent]"
  echo ""
  echo "Available Commands:"
  echo -e "${PURPLE}start${NC}   Start Arc Demo Application"
  echo -e "${PURPLE}chat${NC}    Start conversation with agent"
  exit 1
}

if [ "$1" != "start" ] && [ "$1" != "chat" ]; then
    usage
fi

if [ "$1" == "start" ]; then
    echo "Starting Arc Demo Application..."
     ./gradlew -q --console=plain bootRun
elif [ "$1" == "chat" ]; then
   if [ -z "$2" ]; then
      echo -e "${PURPLE}>> Please provide an Agent${NC}"
      usage
   fi
   echo "Starting conversation with Agent $2..."
   ./gradlew -q --console=plain arc -Pagent="$2"
fi


