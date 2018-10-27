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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
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
		Model m = ModelFactory.createDefaultModel();

		// load some data into the model
		FileManager.get().readModel(m, RECIPE_DATA_FILE);

		// generate some output
		showModelSize(m);

		// System.out.println(m.listStatements(null, RDF.type,
		// m.getResource("http://schema.org/recipeIngredient")).next()
		// .getSubject());

	}

	/***********************************/
	/* Internal implementation methods */
	/***********************************/

	/**
	 * Show the size of the model on stdout
	 */
	protected void showModelSize(Model m) {
		System.out.println(String.format("The model contains %d triples", m.size()));
	}

}
