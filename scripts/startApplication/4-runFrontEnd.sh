#!/bin/bash
cd ../../sw/node_modules/.bin
./ng serve &
read w
if [ "$w" = "q" ]
then
echo "shutting down..."
trap 'kill $(jobs -p)' EXIT
fi
