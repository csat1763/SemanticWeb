start cmd /k "cd code/TripleStore/apache-jena-fuseki-3.9.0/ & java -jar fuseki-server.jar & title Fuseki"
ping 127.0.0.1 -n 6 > nul
start cmd /k "cd code/BackEnd & java -jar floader.jar & title FusekiLoader"
start cmd /k "cd code/BackEnd/target & java -jar gs-rest-service-0.1.0.jar & title BackEnd"
start cmd /k "cd ./sw/node_modules/.bin & ng.cmd serve & title FrontEnd"

:startFuseki
cd code/TripleStore/apache-jena-fuseki-3.9.0/
java -jar fuseki-server.jar


:loadFuseki
cd code/BackEnd
java -jar floader.jar


:startBackEnd
cd code/BackEnd/target
java -jar gs-rest-service-0.1.0.jar


:startFrontEnd
cd ./sw
node_modules/.bin/ng serve






