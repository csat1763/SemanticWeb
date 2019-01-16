/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package helloworld;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.apache.jena.rdfconnection.RDFDatasetConnection;
import org.apache.jena.sparql.core.DatasetImpl;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import example.RecipeBase;

/**
 * <h2>Apache Jena Getting Started Guide - Step 1: Hello World</h2>
 * <p>
 * In this step, we illustrate the basic operations of getting some data into a Java program, finding some data, and showing some output.
 * </p>
 */
public class HelloWorld extends RecipeBase {

	private static final String dataSet = "src\\main\\resources\\data\\recipes";

	/***********************************/
	/* Constants */
	/***********************************/

	/***********************************/
	/* Static variables */
	/***********************************/

	@SuppressWarnings(value = "unused")
	private static final Logger log = LoggerFactory.getLogger(HelloWorld.class);

	public static String prefix = "prefix rdfs: <" + RDFS.getURI() + ">\n" + "prefix rdf: <" + RDF.getURI() + ">\n"
			+ "prefix owl: <" + OWL.getURI() + ">\n" + "PREFIX wd: <http://www.wikidata.org/entity/>\r\n"
			+ "PREFIX wds: <http://www.wikidata.org/entity/statement/>\r\n"
			+ "PREFIX wdv: <http://www.wikidata.org/value/>\r\n"
			+ "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\r\n"
			+ "PREFIX wikibase: <http://wikiba.se/ontology#>\r\n" + "PREFIX p: <http://www.wikidata.org/prop/>\r\n"
			+ "PREFIX ps: <http://www.wikidata.org/prop/statement/>\r\n"
			+ "PREFIX pq: <http://www.wikidata.org/prop/qualifier/>\r\n"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
			+ "PREFIX bd: <http://www.bigdata.com/rdf#>\r\n" + "PREFIX afn: <http://jena.hpl.hp.com/ARQ/function#>\r\n"
			+ "PREFIX dc: <http://purl.org/dc/elements/1.1/>\r\n"
			+ "PREFIX mw: <http://tools.wmflabs.org/mw2sparql/ontology#>\r\n"
			+ "PREFIX mwapi: <https://www.mediawiki.org/ontology#API/>\r\n" + "PREFIX schema: <http://schema.org/>";

	public static DatasetImpl model;

	/***********************************/
	/* Instance variables */
	/***********************************/

	/***********************************/
	/* Constructors */
	/***********************************/

	/***********************************/
	/* External signature methods */
	/***********************************/

	/**
	 * Main entry point for running this example. Since every sub-class will be {@link Runnable}, we create an instance, stash the command-line args where we can retrieve them later, and invoke
	 * {@link #run}
	 */
	public static void main(String[] args) {
		new HelloWorld().setArgs(args).run();
	}

	@Override
	public void run() {

		File datafolder = new File(dataSet);
		Model datamodel = ModelFactory.createDefaultModel();

		for (String datafile : listFilesForFolder(datafolder)) {
			System.out.println(datafile);
			FileManager.get().readModel(datamodel, datafile);
		}

		HashMap<String, Model> nameData = new HashMap<String, Model>();
		nameData.put("edamam", datamodel);
		// TODO: very slow loading into java program and pushing to fuseki; push directly to fuseki without java overhead

		// // creates a new, empty in-memory model
		// Model m = ModelFactory.createDefaultModel();
		// Model m2 = ModelFactory.createDefaultModel();
		// Model m3 = ModelFactory.createDefaultModel();
		// OntModel o = ModelFactory.createOntologyModel();
		//
		// // load some data into the model
		// FileManager.get().readModel(m, RECIPE_DATASET1);
		// FileManager.get().readModel(m2, RECIPE_DATASET2);
		// FileManager.get().readModel(m3, RECIPE_DATASET3);
		// FileManager.get().readModel(o, RECIPE_SCHEMA_FILE);
		//
		// HashMap<String, Model> nameData = new HashMap<String, Model>();
		// nameData.put("google", m2);
		// nameData.put("edamam", m);
		// nameData.put("ontology", o);

		// FusekiServer server = FusekiServer.create().add("/rdf", new DatasetImpl(m)).build();
		// server.start();

		FusekiConnection fc = new FusekiConnection("http://localhost:3030", "food");
		fc.initFuseki(nameData); // TODO: uncomment for default loading of data
		// fc.deleteAllGraphs();
		// fc.deleteDefaultModel();

		// fc.query(numberOfTriples());
		// fc.query(numberOfTriplesPerClass());
		// fc.query(numberOfDistinctClasses());
		// fc.query(numberOfDistinctProperties());
		// fc.query(classesPerDataSet());
		// fc.query(propertiesPerDataSet());
		// fc.query(instancesPerClassPerDataSet());
		// fc.query(subjectsPerPropertyPerDataSet());
		// fc.query(objectsPerPropertyPerDataSet());
		// fc.query(propertiesInTop5Classes());
		// fc.query(federatedQuery());

	}

	/***********************************/
	/* Internal implementation methods */
	/***********************************/

	// total number of triples
	public static String numberOfTriples() {
		System.out.println("numberOfTriples");
		return prefix + "PREFIX schema: <http://schema.org/>\r\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" + "\r\n"
				+ "SELECT (COUNT(?s) as ?count) WHERE{\r\n" + "  graph ?g {?s ?p ?o}\r\n" + "}";

	}

	// total number of instantiations
	public static String numberOfTriplesPerClass() {
		System.out.println("numberOfTriplesPerClass");
		return prefix + "PREFIX schema: <http://schema.org/>\r\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" + "\r\n"
				+ "SELECT ?class (COUNT(?class) as ?instances) WHERE{\r\n" + "	{\r\n"
				+ "     GRAPH <http://localhost:3030/food/data/data> {?s ?p ?class} .\r\n"
				+ "    GRAPH <http://localhost:3030/food/data/ontology> {?class a owl:Class } .\r\n" + "	}\r\n"
				+ "} GROUP BY ?class ";

	}

	// ?x rdf:type schema:Recipe
	// we want the subject ?x that is connected to the object schema:Recipe by the predicate rdf:type
	// _:Nb9b78862704942d18cd2e72470615aae <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
	// <http://schema.org/Recipe> .
	// example triple for the subject above:
	// _:Nb9b78862704942d18cd2e72470615aae <http://schema.org/recipeIngredient> "25 grams paprika (about 1/4 cup)" .

	// total number of distinct classes
	public static String numberOfDistinctClasses() {
		System.out.println("numberOfDistinctClasses");
		return prefix + "PREFIX schema: <http://schema.org/>\r\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" + "\r\n"
				+ "SELECT (COUNT(DISTINCT ?o) as ?numberOfClasses) WHERE{\r\n" + "	{\r\n"
				+ "     GRAPH <http://localhost:3030/food/data/data> {?s ?p ?o} .\r\n"
				+ "    GRAPH <http://localhost:3030/food/data/ontology> {?o a owl:Class } .\r\n" + "	}\r\n" + "}";
	}

	// total number of distinct properties
	public static String numberOfDistinctProperties() {
		System.out.println("numberOfDistinctProperties");
		return prefix + "PREFIX schema: <http://schema.org/>\r\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" + "\r\n"
				+ "SELECT (COUNT(DISTINCT ?p) as ?numberOfProperties) WHERE{\r\n" + "	{\r\n"
				+ "     GRAPH <http://localhost:3030/food/data/data> {?s ?p ?o} .\r\n"
				+ "    GRAPH <http://localhost:3030/food/data/ontology> {?p a owl:ObjectProperty} .\r\n" + "	}\r\n"
				+ "}\r\n" + "";
	}

	// list of all classes used in your dataset per data source (see named graphs)
	public static String classesPerDataSet() {
		System.out.println("classesPerDataSet");
		return prefix + "PREFIX schema: <http://schema.org/>\r\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" + "\r\n" + "SELECT\r\n" + "DISTINCT ?g ?class \r\n"
				+ "WHERE{\r\n" + "	{\r\n" + "      GRAPH ?g {?s ?p ?class } .\r\n"
				+ "      GRAPH <http://localhost:3030/food/data/ontology> {?class  a owl:Class } . \r\n"
				+ "      FILTER(?g != <http://localhost:3030/food/data/ontology>) .\r\n" + "	}\r\n" + "}";

	}

	// GRAPH ?graph { ?s ?p ?class . ?s a schema:Recipe} . GRAPH ?j {?class a rdfs:Class}
	// also a valid query in case the default graph is empty or not a union of all named graphs

	// list of all properties used in your dataset per data source
	public static String propertiesPerDataSet() {
		System.out.println("propertiesPerDataSet");
		return prefix + "PREFIX schema: <http://schema.org/>\r\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" + "\r\n" + "SELECT\r\n"
				+ "DISTINCT ?g ?property\r\n" + "WHERE{\r\n" + "	{\r\n" + "      GRAPH ?g {?s ?property ?o } .\r\n"
				+ "      GRAPH <http://localhost:3030/food/data/ontology> {?property a owl:ObjectProperty } .\r\n"
				+ "      FILTER(?g != <http://localhost:3030/food/data/ontology>) .\r\n" + "	}\r\n" + "  \r\n"
				+ "  \r\n" + "}";

		// GRAPH ?namedGraph { ?s ?class ?o . ?s a schema:Recipe }. ?class a rdf:Property
		// Explanation: keyword GRAPH binds the set to 1 named graph and within this named graph certain propertiess can be accessed
		// "?s a schema:Recipe" is valid because the dataset includes the triple "xxxx rdf:type schema:Recipe"
		// ". ?class a rdf:Property" has to be outside because once the set is bound to a named graph, no other named graph can be accessed within the boundary
		// but since all named graphs are unified in the default graph, other named graph triples can be accessed outside the set.
		// The ontology graph which holds the information schema:recipeIngridient rdf:type rdf:Property is accessed this way.

	}

	// total number of instances per class per data source (reasoning on and off)
	public static String instancesPerClassPerDataSet() {
		System.out.println("instancesPerClassPerDataSet");
		return prefix + "PREFIX schema: <http://schema.org/>\r\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" + "\r\n" + "SELECT\r\n"
				+ "?g ?class (COUNT(?class ) as ?instances)\r\n" + "WHERE{\r\n" + "	{\r\n"
				+ "      GRAPH ?g {?s ?p ?class  } .\r\n"
				+ "      GRAPH <http://localhost:3030/food/data/ontology> {?class  a owl:Class } . \r\n"
				+ "      FILTER(?g != <http://localhost:3030/food/data/ontology>) .\r\n" + "	} \r\n"
				+ "}GROUP BY ?g ?class ";
	}

	// total number of distinct subjects per property per data source
	public static String subjectsPerPropertyPerDataSet() {
		System.out.println("subjectsPerPropertyPerDataSet");
		return prefix + "PREFIX schema: <http://schema.org/>\r\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" + "\r\n" + "SELECT\r\n"
				+ "DISTINCT ?g ?property (COUNT(DISTINCT ?s) as ?subjects)\r\n" + "WHERE{\r\n" + "	{\r\n"
				+ "      GRAPH ?g {?s ?property ?o } .\r\n"
				+ "      GRAPH <http://localhost:3030/food/data/ontology> {?property a owl:ObjectProperty } .\r\n"
				+ "      FILTER(?g != <http://localhost:3030/food/data/ontology>) .\r\n" + "    \r\n" + "	}\r\n"
				+ " \r\n" + "} GROUP BY ?g ?property";
	}

	// total number of distinct objects per property per data source
	public static String objectsPerPropertyPerDataSet() {
		System.out.println("objectsPerPropertyPerDataSet");
		return prefix + "PREFIX schema: <http://schema.org/>\r\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" + "\r\n" + "SELECT\r\n"
				+ "DISTINCT ?g ?property (COUNT(DISTINCT ?o) as ?objects)\r\n" + "WHERE{\r\n" + "	{\r\n"
				+ "      GRAPH ?g {?s ?property ?o } .\r\n"
				+ "      GRAPH <http://localhost:3030/food/data/ontology> {?property a owl:ObjectProperty } .\r\n"
				+ "      FILTER(?g != <http://localhost:3030/food/data/ontology>) .\r\n" + "    \r\n" + "	}\r\n"
				+ " \r\n" + "} GROUP BY ?g ?property";

	}

	// distinct properties used on top 5 classes in terms of amount of instances
	// (reasoning on and off)
	public static String propertiesInTop5Classes() {
		System.out.println("propertiesInTop5Classes");
		return prefix + "PREFIX schema: <http://schema.org/>\r\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" + "\r\n"
				+ "SELECT DISTINCT ?property ?class WHERE {\r\n" + "  \r\n"
				+ "   GRAPH <http://localhost:3030/food/data/data> {?s a ?class . ?s ?property ?o} .\r\n"
				+ "   FILTER(?property != rdf:type) .\r\n" + "\r\n"
				+ "{SELECT ?class (COUNT(?class) as ?instances) WHERE{\r\n" + "	{\r\n"
				+ "     GRAPH <http://localhost:3030/food/data/data> {?s ?property ?class} .\r\n"
				+ "    GRAPH <http://localhost:3030/food/data/ontology> {?class a owl:Class } .\r\n" + "	}\r\n"
				+ "    } GROUP BY ?class ORDER BY DESC(?instances) LIMIT 5}}";
	}

	// distinct properties used on top 5 classes in terms of amount of instances
	// (reasoning on and off)
	public static String federatedQuery() {
		System.out.println("wikiDataAlignment");
		return prefix + "\r\n" + "PREFIX wds: <http://www.wikidata.org/entity/statement/>\r\n"
				+ "PREFIX wdv: <http://www.wikidata.org/value/>\r\n"
				+ "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\r\n"
				+ "PREFIX wikibase: <http://wikiba.se/ontology#>\r\n" + "PREFIX p: <http://www.wikidata.org/prop/>\r\n"
				+ "PREFIX ps: <http://www.wikidata.org/prop/statement/>\r\n"
				+ "PREFIX pq: <http://www.wikidata.org/prop/qualifier/>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX bd: <http://www.bigdata.com/rdf#> \r\n"
				+ "PREFIX afn: <http://jena.hpl.hp.com/ARQ/function#>\r\n"
				+ "PREFIX dc: <http://purl.org/dc/elements/1.1/>\r\n"
				+ "PREFIX mw: <http://tools.wmflabs.org/mw2sparql/ontology#>\r\n"
				+ "PREFIX mwapi: <https://www.mediawiki.org/ontology#API/> \r\n"
				+ "PREFIX schema: <http://schema.org/>\r\n" + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n"
				+ "\r\n" + "SELECT DISTINCT ?item ?itemLabel ?localClass WHERE {    {\r\n"
				+ "    SELECT DISTINCT ?localClass ?z WHERE {\r\n"
				+ "     graph ?g { ?s ?p ?localClass . ?localClass a owl:Class . ?s a schema:Recipe . ?localClass rdfs:label ?z }.\r\n"
				+ "    }   }   SERVICE <https://query.wikidata.org/sparql> { \r\n"
				+ "    SERVICE wikibase:mwapi {    bd:serviceParam wikibase:api \"EntitySearch\" .\r\n"
				+ "      bd:serviceParam wikibase:endpoint \"www.wikidata.org\" .\r\n"
				+ "      bd:serviceParam mwapi:search ?z .    bd:serviceParam mwapi:language \"en\" .\r\n"
				+ "      bd:serviceParam mwapi:limit 5 .    ?item wikibase:apiOutputItem mwapi:item .  \r\n"
				+ "    }    ?item rdfs:label ?itemLabel .\r\n"
				+ "    FILTER(LANG(?itemLabel) = \"\" || LANGMATCHES(LANG(?itemLabel), \"en\"))      } }";
	}

	class FusekiConnection {

		private String connectionUrl;

		private String dataName;

		public Dataset dataset;

		public FusekiConnection(String connectionUrl, String dataName) {
			this.dataName = dataName;
			this.connectionUrl = connectionUrl;

		}

		public void deleteModel(String graphName) {
			DatasetAccessorFactory.createHTTP(connectionUrl + "/" + dataName + "/data").deleteModel(graphName);
		}

		public void deleteDefaultModel() {
			DatasetAccessorFactory.createHTTP(connectionUrl + "/" + dataName + "/data").deleteDefault();
		}

		public void addModel(String graphName, Model model) {
			DatasetAccessorFactory.createHTTP(connectionUrl + "/" + dataName + "/data").add(graphName, model);
		}

		public void addDefaultModel(Model model) {
			DatasetAccessorFactory.createHTTP(connectionUrl + "/" + dataName + "/data").add(model);
		}

		public ArrayList<String> getGraphNames() {
			Iterator<String> names = RDFConnectionRemote.create().destination(connectionUrl + "/" + dataName + "/data")
					.build().fetchDataset().listNames();

			ArrayList<String> namesAsStrings = new ArrayList<String>();
			while (names.hasNext()) {
				namesAsStrings.add(names.next());
			}

			return namesAsStrings;

		}

		public void query(String queryString) {

			Query query = QueryFactory.create(queryString);

			RDFConnection queryConnection = RDFConnectionRemote.create().destination(connectionUrl + "/")
					.queryEndpoint(dataName + "/sparql")
					.acceptHeaderSelectQuery("application/sparql-results+json, application/sparql-results+xml;q=0.9")
					.build();
			try (RDFConnection conn = queryConnection) {
				conn.queryResultSet(query, ResultSetFormatter::out);

			}

		}

		public void initFuseki(HashMap<String, Model> nameData) {
			System.out.println("Initializing dataset...");
			this.dataset = RDFConnectionRemote.create().destination(connectionUrl + "/" + dataName + "/data").build()
					.fetchDataset();
			RDFDatasetConnection a = RDFConnectionRemote.create().destination(connectionUrl + "/" + dataName + "/data")
					.build();
			ArrayList<String> graphNames = getGraphNames();
			for (Entry<String, Model> entry : nameData.entrySet()) {

				if (!graphNames.contains(connectionUrl + "/" + dataName + "/data/" + entry.getKey())
						&& !dataset.getNamedModel(entry.getKey()).isIsomorphicWith(entry.getValue())) {
					dataset.addNamedModel(entry.getKey(), entry.getValue());
					a.load(entry.getKey(), entry.getValue());
				}

			}
			if (!dataset.getDefaultModel().isIsomorphicWith(dataset.getUnionModel())) {
				// System.out.println("Not isomorph");
				dataset.setDefaultModel(ModelFactory.createDefaultModel());
				dataset.setDefaultModel(dataset.getUnionModel());

				a.delete();
				a.load(dataset.getDefaultModel());

			}
		}

		public void deleteAllGraphs() {

			for (String name : getGraphNames()) {
				deleteModel(name);
			}
			deleteDefaultModel();
			System.out.println("All graphs deleted!");

		}

	}

	public ArrayList<String> listFilesForFolder(final File folder) {
		ArrayList<String> files = new ArrayList<String>();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				files.add(fileEntry.getPath().replace("\\", "/"));
			}

		}

		return files;
	}

}

// DESCRIBE ?s0 WHERE{
// {SELECT DISTINCT ?s0 WHERE{
// GRAPH ?g { ?s0 a schema:Recipe } .
// } LIMIT 1}
// }
