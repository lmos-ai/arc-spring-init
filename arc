#!/usr/bin/env bash

if [[ -z $1 ]]; then
  echo "Please supply name of Agent to chat with."
  exit 1
fi

echo "Loading..."
./gradlew -q --console=plain arc -Pagent="$1"
