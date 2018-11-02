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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.jena.ontology.OntModel;
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
		Model m3 = ModelFactory.createDefaultModel();
		OntModel o = ModelFactory.createOntologyModel();

		// load some data into the model
		FileManager.get().readModel(m, RECIPE_DATASET1);
		FileManager.get().readModel(m2, RECIPE_DATASET2);
		FileManager.get().readModel(m3, RECIPE_DATASET3);
		FileManager.get().readModel(o, RECIPE_SCHEMA_FILE);

		HashMap<String, Model> nameData = new HashMap<String, Model>();
		nameData.put("google", m2);
		nameData.put("edamam", m);
		nameData.put("ontology", o);

		// FusekiServer server = FusekiServer.create().add("/rdf", new DatasetImpl(m)).build();
		// server.start();

		FusekiConnection fc = new FusekiConnection("http://localhost:3030", "food2");
		fc.initFuseki(nameData);
		// fc.deleteDefaultModel();

		fc.query(numberOfTriples());
		fc.query(numberOfTriplesPerClass());
		fc.query(numberOfDistinctClasses());
		fc.query(numberOfDistinctProperties());
		fc.query(classesPerDataSet());
		fc.query(propertiesPerDataSet());
		fc.query(instancesPerClassPerDataSet());
		fc.query(subjectsPerPropertyPerDataSet());
		fc.query(objectsPerPropertyPerDataSet());
		fc.query(propertiesInTop5Classes());

	}

	/***********************************/
	/* Internal implementation methods */
	/***********************************/

	// total number of triples
	public static String numberOfTriples() {
		System.out.println("numberOfTriples");
		return prefix + "SELECT (COUNT(?x) as ?triples)	\r\n" + "WHERE { ?x ?y ?s . ?x a schema:Recipe}";

	}

	// total number of instantiations
	public static String numberOfTriplesPerClass() {
		System.out.println("numberOfTriplesPerClass");
		return prefix + "SELECT  ?class (COUNT(?x) as ?instances)	\r\n"
				+ "WHERE { ?x ?y ?class . ?class a rdfs:Class . ?x a schema:Recipe} GROUP BY ?class ";

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
		return prefix + "SELECT (COUNT(*) as ?numberOfDistinctClasses) \r\n WHERE{"
				+ "SELECT (COUNT(distinct ?y) as ?xcount) \r\n"
				+ "WHERE { ?x ?y ?s . ?s a rdfs:Class . ?x a schema:Recipe} GROUP BY ?y}";
	}

	// total number of distinct properties
	public static String numberOfDistinctProperties() {
		System.out.println("numberOfDistinctProperties");
		return prefix
				+ "SELECT (COUNT(*) as ?numberOfDistinctProperties) WHERE{ SELECT DISTINCT ?Properties WHERE {\r\n"
				+ " ?x ?Properties ?z. ?Properties a rdf:Property . ?x a schema:Recipe }GROUP BY ?Properties} ";
	}

	// list of all classes used in your dataset per data source (see named graphs)
	public static String classesPerDataSet() {
		System.out.println("classesPerDataSet");
		return prefix + "SELECT DISTINCT ?graph ?class WHERE{\r\n"
				+ "				GRAPH ?graph { ?s ?p ?class . ?s a schema:Recipe}  . GRAPH ?j {?class a rdfs:Class} \r\n"
				+ "				} ";

	}

	// GRAPH ?graph { ?s ?p ?class . ?s a schema:Recipe} . GRAPH ?j {?class a rdfs:Class}
	// also a valid query in case the default graph is empty or not a union of all named graphs

	// list of all properties used in your dataset per data source
	public static String propertiesPerDataSet() {
		System.out.println("propertiesPerDataSet");
		return prefix + "SELECT DISTINCT ?namedGraph ?class {\r\n"
				+ "				  GRAPH ?namedGraph { ?s ?class ?o  . ?s a schema:Recipe }. ?class a rdf:Property \r\n"
				+ "				} GROUP BY ?class ?namedGraph ORDER BY ?namedGraph";

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
		return prefix + "SELECT DISTINCT ?namedGraph ?class (COUNT(?class) as ?instances)	 \r\n" + "{\r\n"
				+ "  GRAPH ?namedGraph { ?s ?p ?class . ?s a schema:Recipe}  . ?class a rdfs:Class  \r\n"
				+ "} GROUP BY ?class ?namedGraph ORDER BY ?namedGraph";
	}

	// total number of distinct subjects per property per data source
	public static String subjectsPerPropertyPerDataSet() {
		System.out.println("subjectsPerPropertyPerDataSet");
		return prefix
				+ "SELECT ?namedGraph ?class (COUNT(?subjectCount) as ?subjects) WHERE{SELECT ?namedGraph ?class (COUNT(?s) as ?subjectCount) \r\n"
				+ "{\r\n" + "  GRAPH ?namedGraph { ?s ?class ?o . ?s a schema:Recipe } . ?class a rdf:Property   \r\n"
				+ "} GROUP BY ?s ?class ?namedGraph ORDER BY ?namedGraph} GROUP BY ?s ?class ?namedGraph ORDER BY ?namedGraph";
	}

	// total number of distinct objects per property per data source
	public static String objectsPerPropertyPerDataSet() {
		System.out.println("objectsPerPropertyPerDataSet");
		return prefix
				+ "SELECT ?namedGraph ?class (COUNT(?subjectCount) as ?objects) WHERE{SELECT ?namedGraph ?class (COUNT(?o) as ?subjectCount) \r\n"
				+ "{\r\n" + " GRAPH ?namedGraph { ?s ?class ?o . ?s a schema:Recipe  } . ?class a rdf:Property\r\n"
				+ "} GROUP BY ?o ?class ?namedGraph ORDER BY ?namedGraph} GROUP BY ?o ?class ?namedGraph ORDER BY ?namedGraph";

	}

	// distinct properties used on top 5 classes in terms of amount of instances
	// (reasoning on and off)
	public static String propertiesInTop5Classes() {
		System.out.println("propertiesInTop5Classes");
		return prefix
				+ "SELECT DISTINCT ?properties \r\n WHERE { ?properties schema:domainIncludes ?o . ?sub ?properties ?obj . FILTER(?o = ?class)"
				+ "{SELECT ?class \r\n WHERE { ?properties ?p ?class . ?class a rdfs:Class "
				+ ". ?properties a ?classtype . FILTER (?classtype IN (schema:Recipe) ) } "
				+ "GROUP BY ?class ORDER BY DESC(?instances) LIMIT 5}}";
	}

	class FusekiConnection {

		private String connectionUrl;

		private String dataName;

		private RDFConnection readConnection;

		private RDFConnection queryConnection;

		public Dataset dataset;

		public FusekiConnection(String connectionUrl, String dataName) {
			this.dataName = dataName;
			this.connectionUrl = connectionUrl;
			this.readConnection = RDFConnectionRemote.create().destination(connectionUrl + "/" + dataName + "/data")
					.build();
			this.queryConnection = RDFConnectionRemote.create().destination(connectionUrl + "/")
					.queryEndpoint(dataName + "/sparql")
					.acceptHeaderSelectQuery("application/sparql-results+json, application/sparql-results+xml;q=0.9")
					.build();
			this.dataset = readConnection.fetchDataset();

		}

		private void reInit() {
			this.queryConnection = RDFConnectionRemote.create().destination(connectionUrl + "/")
					.queryEndpoint(dataName + "/sparql")
					.acceptHeaderSelectQuery("application/sparql-results+json, application/sparql-results+xml;q=0.9")
					.build();
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
			Iterator<String> names = readConnection.fetchDataset().listNames();

			ArrayList<String> namesAsStrings = new ArrayList<String>();
			while (names.hasNext()) {
				namesAsStrings.add(names.next());
			}

			return namesAsStrings;

		}

		public void query(String queryString) {

			Query query = QueryFactory.create(queryString);

			reInit();
			try (

					RDFConnection conn = queryConnection) {
				conn.queryResultSet(query, ResultSetFormatter::out);

			}

		}

		public void initFuseki(HashMap<String, Model> nameData) {
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
				System.out.println("Not isomorph");
				dataset.setDefaultModel(ModelFactory.createDefaultModel());
				dataset.setDefaultModel(dataset.getUnionModel());

				a.delete();
				a.load(dataset.getDefaultModel());

			}
		}

	}

}
