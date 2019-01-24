#!/bin/bash

startFuseki(){
cd code/TripleStore/apache-jena-fuseki-3.9.0/
java -jar fuseki-server.jar
}

loadFuseki(){
cd code/BackEnd
java -jar floader.jar
}

startBackEnd(){
cd code/BackEnd/target
java -jar gs-rest-service-0.1.0.jar
}

startFrontEnd(){
cd ./sw
node_modules/.bin/ng serve
}



startFuseki &
sleep 5
loadFuseki &
startBackEnd &
startFrontEnd &

