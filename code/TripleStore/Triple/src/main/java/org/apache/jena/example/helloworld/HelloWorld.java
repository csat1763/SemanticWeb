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

package org.apache.jena.example.helloworld;

import org.apache.jena.example.RecipeBase;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.apache.jena.sparql.core.DatasetImpl;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h2>Apache Jena Getting Started Guide - Step 1: Hello World</h2>
 * <p>
 * In this step, we illustrate the basic operations of getting some data into a Java program, finding some data, and showing some output.
 * </p>
 */
public class HelloWorld extends RecipeBase {
	/***********************************/
	/* Constants */
	/***********************************/

	/***********************************/
	/* Static variables */
	/***********************************/

	@SuppressWarnings(value = "unused")
	private static final Logger log = LoggerFactory.getLogger(HelloWorld.class);

	public static String prefix = "prefix schema: <http://schema.org/>\n" + "prefix rdfs: <" + RDFS.getURI() + ">\n"
			+ "prefix rdf: <" + RDF.getURI() + ">\n" + "prefix owl: <" + OWL.getURI() + ">\n";

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

		// creates a new, empty in-memory model
		Model m = ModelFactory.createDefaultModel();
		Model m2 = ModelFactory.createDefaultModel();
		OntModel o = ModelFactory.createOntologyModel();

		// load some data into the model
		FileManager.get().readModel(m, RECIPE_DATASET1);
		FileManager.get().readModel(m2, RECIPE_DATASET2);
		FileManager.get().readModel(o, RECIPE_SCHEMA_FILE);

		// create DataSet for with namedGraph
		DatasetImpl ds = new DatasetImpl(ModelFactory.createDefaultModel());
		ds.addNamedModel("google", m2);
		ds.addNamedModel("edamam", m);
		ds.addNamedModel("ontology", o);
		ds.setDefaultModel(ds.getUnionModel());
		model = ds;

		numberOfTriples();
		numberOfTriplesPerClass();
		numberOfDistinctClasses();
		numberOfDistinctProperties();

		classesPerDataSet();

		propertiesPerDataSet();
		instancesPerClassPerDataSet();
		subjectsPerPropertyPerDataSet();
		objectsPerPropertyPerDataSet();

		propertiesInTop5Classes();

	}

	/***********************************/
	/* Internal implementation methods */
	/***********************************/

	// total number of triples
	public static void numberOfTriples() {
		System.out.println("numberOfTriples");
		showQuery(model, prefix + "SELECT (COUNT(?x) as ?triples)	\r\n" + "WHERE { ?x ?y ?s . ?x a schema:Recipe}");

	}

	// total number of instantiations
	public static void numberOfTriplesPerClass() {
		System.out.println("numberOfTriplesPerClass");
		showQuery(model, prefix + "SELECT  ?class (COUNT(?x) as ?instances)	\r\n"
				+ "WHERE { ?x ?y ?class . ?class a rdfs:Class . ?x a schema:Recipe} GROUP BY ?class ");

	}

	// ?x rdf:type schema:Recipe
	// we want the subject ?x that is connected to the object schema:Recipe by the predicate rdf:type
	// _:Nb9b78862704942d18cd2e72470615aae <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
	// <http://schema.org/Recipe> .
	// example triple for the subject above:
	// _:Nb9b78862704942d18cd2e72470615aae <http://schema.org/recipeIngredient> "25 grams paprika (about 1/4 cup)" .

	// total number of distinct classes
	public static void numberOfDistinctClasses() {
		System.out.println("numberOfDistinctClasses");
		showQuery(model,
				prefix + "SELECT (COUNT(*) as ?numberOfDistinctClasses) \r\n WHERE{"
						+ "SELECT (COUNT(distinct ?y) as ?xcount) \r\n"
						+ "WHERE { ?x ?y ?s . ?s a rdfs:Class . ?x a schema:Recipe} GROUP BY ?y}");
	}

	// total number of distinct properties
	public static void numberOfDistinctProperties() {
		System.out.println("numberOfDistinctProperties");
		showQuery(model, prefix
				+ "SELECT (COUNT(*) as ?numberOfDistinctProperties) WHERE{ SELECT DISTINCT ?Properties WHERE {\r\n"
				+ " ?x ?Properties ?z. ?Properties a rdf:Property . ?x a schema:Recipe }GROUP BY ?Properties} ");
	}

	// list of all classes used in your dataset per data source (see named graphs)
	public static void classesPerDataSet() {
		System.out.println("classesPerDataSet");
		showQuery(model, prefix + "SELECT DISTINCT ?graph ?class \r\n" + "WHERE{\r\n"
				+ " { ?s ?p ?class . ?class a rdfs:Class . ?s a schema:Recipe  } . GRAPH ?graph { ?s ?p ?class } \r\n"
				+ "} ");

	}

	// list of all properties used in your dataset per data source
	public static void propertiesPerDataSet() {
		System.out.println("propertiesPerDataSet");
		showQuery(model, prefix + "SELECT DISTINCT ?namedGraph ?class \r\n" + "{\r\n"
				+ " { ?s ?class ?o . ?class a rdf:Property . ?s a schema:Recipe  } . { GRAPH ?namedGraph { ?s ?class ?o  } }\r\n"
				+ "} GROUP BY ?class ?namedGraph ORDER BY ?namedGraph");

	}

	// total number of instances per class per data source (reasoning on and off)
	public static void instancesPerClassPerDataSet() {
		System.out.println("instancesPerClassPerDataSet");
		showQuery(model, prefix + "SELECT DISTINCT ?namedGraph ?class (COUNT(?class) as ?instances)	 \r\n" + "{\r\n"
				+ " { ?s ?p ?class . ?class a rdfs:Class . ?s a schema:Recipe } . { GRAPH ?namedGraph { ?s ?p ?class } }\r\n"
				+ "} GROUP BY ?class ?namedGraph ORDER BY ?namedGraph");
	}

	// total number of distinct subjects per property per data source
	public static void subjectsPerPropertyPerDataSet() {
		System.out.println("subjectsPerPropertyPerDataSet");
		showQuery(model, prefix
				+ "SELECT ?namedGraph ?class (COUNT(?subjectCount) as ?subjects) WHERE{SELECT ?namedGraph ?class (COUNT(?s) as ?subjectCount) \r\n"
				+ "{\r\n"
				+ " { ?s ?class ?o . ?class a rdf:Property . ?s a schema:Recipe  } . { GRAPH ?namedGraph { ?s ?class ?o } }\r\n"
				+ "} GROUP BY ?s ?class ?namedGraph ORDER BY ?namedGraph} GROUP BY ?s ?class ?namedGraph ORDER BY ?namedGraph");
	}

	// total number of distinct objects per property per data source
	public static void objectsPerPropertyPerDataSet() {
		System.out.println("objectsPerPropertyPerDataSet");
		showQuery(model, prefix
				+ "SELECT ?namedGraph ?class (COUNT(?subjectCount) as ?objects) WHERE{SELECT ?namedGraph ?class (COUNT(?o) as ?subjectCount) \r\n"
				+ "{\r\n"
				+ " { ?s ?class ?o . ?class a rdf:Property . ?s a schema:Recipe  } . { GRAPH ?namedGraph { ?s ?class ?o  } }\r\n"
				+ "} GROUP BY ?o ?class ?namedGraph ORDER BY ?namedGraph} GROUP BY ?o ?class ?namedGraph ORDER BY ?namedGraph");
	}

	// distinct properties used on top 5 classes in terms of amount of instances
	// (reasoning on and off)
	public static void propertiesInTop5Classes() {
		System.out.println("propertiesInTop5Classes");

		// added dummy movie rdf object to demonstrate this query
		showQuery(model, prefix
				+ "SELECT DISTINCT ?properties \r\n WHERE { ?properties schema:domainIncludes ?o . ?sub ?properties ?obj . FILTER(?o = ?class)"
				+ "{SELECT ?class \r\n WHERE { ?properties ?p ?class . ?class a rdfs:Class "
				+ ". ?properties a ?classtype . FILTER (?classtype IN (schema:Recipe) ) } "
				+ "GROUP BY ?class ORDER BY DESC(?instances) LIMIT 5}}");
	}

	public static void namedGraphTest() {
		showQuery(model, "SELECT *\r\n" + "{\r\n" + " { ?s ?p ?o } UNION { GRAPH ?g { ?s ?p ?o } }\r\n" + "}");
	}

	public static void showQuery(Model m, String q) {
		Query query = QueryFactory.create(q);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet results = qexec.execSelect();
			ResultSetFormatter.out(results, m);
		} finally {
			qexec.close();
		}

	}

	public static void showQuery(Dataset m, String q) {
		Query query = QueryFactory.create(q);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet results = qexec.execSelect();
			ResultSetFormatter.out(results);
		} finally {
			qexec.close();
		}

		System.out.println();

	}

	public static void execSelectAndProcess(String query) {
		QueryExecution q = QueryExecutionFactory.sparqlService("http://localhost:3030/edamam/query", query);
		ResultSet results = q.execSelect();

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			// assumes that you have an "?x" in your query
			RDFNode x = soln.get("x");
			System.out.println(x);
		}
	}

	public static void rdfCon() {

		// UpdateExecutionFactory.createStreaming(ds);

		// DatasetAccessorFactory.createHTTP("http://localhost:3030/food/update").add("google", m2);

		RDFConnection conn0 = RDFConnectionRemote.create().destination("http://localhost:3030/")
				.queryEndpoint("/food/sparql")
				// Set a specific accept header; here, sparql-results+json (preferred) and text/tab-separated-values
				// The default is "application/sparql-results+json, application/sparql-results+xml;q=0.9, text/tab-separated-values;q=0.7, text/csv;q=0.5, application/json;q=0.2, application/xml;q=0.2, */*;q=0.1"
				.acceptHeaderSelectQuery("application/sparql-results+json, application/sparql-results+xml;q=0.9")
				.build();
		// conn0.loadDataset(model);

		Query query = QueryFactory.create(prefix
				+ "SELECT DISTINCT ?namedGraph ?class (COUNT(?class) as ?instances)	 \r\n" + "{\r\n"
				+ " {{?s ?p ?class} UNION {GRAPH ?namedGraph { ?s ?p ?class }}} . { ?s ?p ?class . ?class a rdfs:Class . ?s a schema:Recipe } . { GRAPH ?namedGraph { ?s ?p ?class } }\r\n"
				+ "} GROUP BY ?class ?namedGraph ORDER BY ?namedGraph");

		// Whether the connection can be reused depends on the details of the implementation.
		// See example 5.
		try (RDFConnection conn = conn0) {
			conn.queryResultSet(query, ResultSetFormatter::out);
		}

		/*PREFIX schema: <http://schema.org/>
		SELECT distinct ?g ?o
		FROM  <http://localhost:3030/food2/data/ont>
		FROM  NAMED <http://localhost:3030/food2/data/edamama>
		FROM  NAMED <http://localhost:3030/food2/data/google>
		WHERE {
			
		GRAPH ?g{?s ?p ?o  . ?s a schema:Recipe}. ?o a rdfs:Class .

		}*/}

}
