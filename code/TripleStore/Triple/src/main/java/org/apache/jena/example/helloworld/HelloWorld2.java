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
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h2>Apache Jena Getting Started Guide - Step 1: Hello World</h2>
 * <p>
 * In this step, we illustrate the basic operations of getting some data into a
 * Java program, finding some data, and showing some output.
 * </p>
 */
public class HelloWorld2 extends RecipeBase {
	/***********************************/
	/* Constants */
	/***********************************/

	/***********************************/
	/* Static variables */
	/***********************************/

	@SuppressWarnings(value = "unused")
	private static final Logger log = LoggerFactory.getLogger(HelloWorld2.class);

	public static String prefix = "prefix schema: <http://schema.org/>\n" + "prefix rdfs: <" + RDFS.getURI() + ">\n"
			+ "prefix rdf: <" + RDF.getURI() + ">\n" + "prefix owl: <" + OWL.getURI() + ">\n";

	public static Model model;

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
	 * Main entry point for running this example. Since every sub-class will be
	 * {@link Runnable}, we create an instance, stash the command-line args where we
	 * can retrieve them later, and invoke {@link #run}
	 */
	public static void main(String[] args) {
		new HelloWorld2().setArgs(args).run();
	}

	public void run() {
		// creates a new, empty in-memory model
		model = ModelFactory.createDefaultModel();

		OntModel o = ModelFactory.createOntologyModel();

		FileManager.get().readModel(o, RECIPE_SCHEMA_FILE);

		// load some data into the model
		FileManager.get().readModel(model, RECIPE_DATA_FILE);

		// generate some output

		// StmtIterator a = model.listStatements();

		// while (a.hasNext()) {
		// System.out.println(a.next());
		// }

		// showQuery(model, "SELECT ?x ?y \r\n"
		// + "WHERE { ?x <http://schema.org/identifier> ?y .FILTER regex(str(?y),
		// \"trap\") }");

		// numberOfTriples();
		// numberOfTriplesPerClass();
		// numberOfDistinctClasses();
		// numberOfDistinctProperties();
		// classesPerDataSet();

		showQuery(model, prefix + " SELECT ?entity ?type\r\n"
				+ "WHERE { ?entity rdf:type/rdfs:subClassOf* schema:Thing:Property }");

		showQuery(model,
				prefix + "SELECT DISTINCT ?type\r\n" + "WHERE {\r\n" + "  ?s schema:Thing:Property ?type.\r\n" + "}");

		showQuery(model, prefix + "SELECT ?x WHERE {\r\n"
				+ "  ?x a schema:recipeIngredient . ?x schema:authot \"John Smith\" \r\n" + "}");
	}

	/***********************************/
	/* Internal implementation methods */
	/***********************************/

	/**
	 * Show the size of the model on stdout
	 */
	public static void numberOfTriples() {
		showQuery(model, "SELECT (COUNT(?x) as ?triples)	\r\n" + "WHERE { ?x ?y ?s}");
	}

	public static void numberOfTriplesPerClass() {
		showQuery(model, "SELECT  ?class (COUNT(?x) as ?count)	\r\n" + "WHERE { ?x ?class ?s} GROUP BY ?class ");
	}

	public static void numberOfDistinctClasses() {
		showQuery(model, "SELECT (COUNT(*) as ?numberOfDistinctClasses) \r\n WHERE{"
				+ "SELECT (COUNT(distinct ?y) as ?xcount) \r\n" + "WHERE { ?x ?y ?s} GROUP BY ?y}");
	}

	public static void numberOfDistinctProperties() {
		showQuery(model,
				"SELECT  ?class (COUNT(distinct ?s) as ?count)	\r\n" + "WHERE { ?x ?class ?s} GROUP BY ?class ");
	}

	public static void classesPerDataSet() {
		showQuery(model, " SELECT ?x ?y ?z \r\n" + "WHERE { ?x ?y ?z }");

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

}
