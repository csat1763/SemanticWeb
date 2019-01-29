#!/bin/bash
echo "collecting data..."
cd code/SemanticWeb
java -jar dataCrawler.jar
echo "transforming data..."
java -jar dataTranslator.jar
cd ../../
cp "code/TripleStore/Triple/src/main/resources/data/misc/AAdummyForShacl.jsonld" "code/TripleStore/Triple/src/main/resources/data/recipes"
rm -rf "code/TripleStore/Triple/src/main/resources/shaclReports/*"
rm -rf "code/TripleStore/Triple/src/main/resources/data/invalidRecipes/*"
cd "code/Backend"
java -jar shacl.jar
echo "Reports at: ../code/TripleStore/Triple/src/main/resources/shaclReports/"
read -p "Press enter to continue"







