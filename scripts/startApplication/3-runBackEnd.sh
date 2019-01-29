#!/bin/bash
cd ../../code/BackEnd/target
java -jar gs-rest-service-0.1.0.jar &
read w
if [ "$w" = "q" ]
then
echo "shutting down..."
trap 'kill $(jobs -p)' EXIT
fi
