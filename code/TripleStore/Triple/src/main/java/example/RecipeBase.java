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

package example;

// Imports
///////////////
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Base class for cheese-ontology based examples. Declares common namespaces and provides some basic utilities.
 * </p>
 */
public abstract class RecipeBase extends Base {
	/***********************************/
	/* Constants */
	/***********************************/

	// public static final String RECIPE_SCHEMA =
	// "http://data.kasabi.com/dataset/cheese/schema/";
	// public static final String RECIPE_DATA =
	// "http://data.kasabi.com/dataset/cheese/";

	public static final String RECIPE_SCHEMA_FILE = ONTOLOGIES_DIR + "all-layers.nt";
	public static final String RECIPE_DATA_FILE = DATA_DIR + "recipesFromEdamam.nt";
	public static final String RECIPE_DATASET1 = DATA_DIR + "recipesFromEdamam.jsonld";
	public static final String RECIPE_DATASET2 = DATA_DIR + "recipesFromTxt.jsonld";
	public static final String RECIPE_DATASET3 = DATA_DIR + "recipesFromEdamam2.jsonld";

	/***********************************/
	/* Static variables */
	/***********************************/

	@SuppressWarnings(value = "unused")
	private static final Logger log = LoggerFactory.getLogger(RecipeBase.class);

	/***********************************/
	/* Instance variables */
	/***********************************/

	/***********************************/
	/* Constructors */
	/***********************************/

	/***********************************/
	/* External signature methods */
	/***********************************/

	/***********************************/
	/* Internal implementation methods */
	/***********************************/

	/***********************************/
	/* Inner class definitions */
	/***********************************/

}