#!/bin/bash
cd ../../code/TripleStore/apache-jena-fuseki-3.9.0/
java -jar fuseki-server.jar &
read w
if [ "$w" = "q" ]
then
echo "shutting down..."
trap 'kill $(jobs -p)' EXIT
fi





