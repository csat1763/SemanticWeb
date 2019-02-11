Application for recipe search based on ingredients and healthlabels.

## Prerequisite
You need Node.JS. See https://nodejs.org/en/download/ .
When installed go into the folder *SemanticWeb/sw/* and execute the command

	$ npm install
	
in a shell.

## Run
To start the application do following:

	$ ./run.sh
	
To gather data do following:

1.	$ ./gather
	
2.	in *\SemanticWeb\ner-tools* execute (NLP):

	$ find ../code/TripleStore/Triple/src/main/resources/data/recipes/ -name "*.jsonld" -print0 | xargs -0 -L1 python apply_ner.py	

The crawling is done based on the file *SemanticWeb/code/SemanticWeb/src/Cuisines.txt*.
Edit the entries as you wish.

or follow these steps:

1. in *\TripleStore\apache-jena-fuseki-3.9.0* either run _ for the Triplestore to start
	
		$ fuseki-server.bat 
				
		$ fuseki-server.jar
	
2. in *\SemanticWeb\code\BackEnd* run _ to load the data into the Fuseki Triplestore

		$ java -jar floader.jar
	
3.  in *\SemanticWeb\code\BackEnd\target* run _ to start the Back-End
	
		$ java -jar gs-rest-service-0.1.0.jar
	
4.  in *\SemanticWeb\sw* to start the Front-End
	
		$ node_modules/.bin/ng serve
		
## Application

There are 3 sites that can be accessed:

1. Front-End: *http://localhost:4200*

1. TripleStore-Server: *http://localhost:3030*

1. Back-End example response from Fuseki: *http://localhost:8080/recipeRequest?tags=Low-Carb&ingredients=salt,butter*
	

