
package fuseki;

import org.apache.jena.sparql.core.DatasetImpl;

public class FusekiLoader {

	public static String dataSet = "../TripleStore/Triple/src/main/resources/data/recipes";
	public static String ontology = "../TripleStore/Triple/src/main/resources/ontologies/currentOntology";
	public static String datasetName = "food";

	public static DatasetImpl model;

	public static void main(String[] args) {
		FusekiConnection fc = new FusekiConnection("http://localhost:3030");
		fc.dataSet = dataSet;
		fc.ontology = ontology;
		fc.datasetName = datasetName;
		fc.loadData();
	}

}
