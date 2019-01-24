Application for recipe search based on ingredients and healthlabels.

## Run
Either run

	$ ./run.sh

or follow these steps:

1. in *\TripleStore\apache-jena-fuseki-3.9.0* either run 
	
		$ fuseki-server.bat 
				
		$ fuseki-server.jar
	
2. in *\SemanticWeb\code\BackEnd*

		$ java -jar floader.jar
	
3.  in *\SemanticWeb\code\BackEnd\target*
	
		$ java -jar gs-rest-service-0.1.0.jar
	
4.  in *\SemanticWeb\sw*
	
		$ node_modules/.bin/ng serve
	
## Application

There are 3 sites that can be accessed:

1. Front-End: *http://localhost:4200*

1. TripleStore-Server: *http://localhost:3030*

1. Back-End example response from Fuseki: *http://localhost:8080*
	

