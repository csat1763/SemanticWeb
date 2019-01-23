package recipeBackend;

import java.util.ArrayList;

import org.apache.commons.codec.digest.DigestUtils;

import fuseki.FusekiConnection;
import fuseki.FusekiLoader;

/* Fuseki must be running and must have at least one dataset in graph <http://localhost:3030/food/data/data> 
 * and an ontology in graph <http://localhost:3030/food/data/ontology>
 * */
public class FusekiEndpoint {

	public static String fusekiAdr = "http://localhost:3030";
	public static String frontEntAdr = "https://httpbin.org/post";
	public static String fusekiDataGraphName = "<http://localhost:3030/food/data/data>";
	public static String fusekiOntGraphName = "<http://localhost:3030/food/data/ontology>";

	public static void main(String args[]) {
		runExample();
	}

	public static void runExample() {
		FusekiConnection fc = genDefaultFuseki();
		ArrayList<String> ings = new ArrayList<String>();
		ings.add("salt");
		ings.add("pepper");
		ings.add("butter");
		ArrayList<String> keys = new ArrayList<String>();
		keys.add("Alcohol-Free");
		// System.out.println(query(ings, keys));
		fc.sendQueryResultsToAdr(generateQuery(ings, keys), frontEntAdr);
	}

	public static FusekiConnection genDefaultFuseki() {
		FusekiConnection fc = new FusekiConnection(fusekiAdr);
		fc.dataSet = FusekiLoader.dataSet;
		fc.ontology = FusekiLoader.ontology;
		fc.datasetName = FusekiLoader.datasetName;
		return fc;

	}

	public static String generateQuery(ArrayList<String> ings, ArrayList<String> keywords) {
		StringBuilder query = new StringBuilder();
		query.append(prefix)
				.append("DESCRIBE ?s " + "FROM " + fusekiOntGraphName + " \r\n" + "FROM " + fusekiDataGraphName
						+ " \r\n" + "{ SELECT \r\n" + "DISTINCT ?s \r\n" + "WHERE{")
				.append("\r\n?s ?p ?o . \r\n" + "?s a schema:Recipe . ");
		if (ings != null && !ings.isEmpty()) {
			for (String inge : ings) {
				String ing = DigestUtils.sha256Hex(inge);
				query.append("?s schema:recipeIngredient ?" + ing + ".\r\n")
						.append("?" + ing + " schema:ingredientName ?" + ing + "1 .\r\n")
						.append("?" + ing + "1 schema:name ?" + ing + "2 .\r\n")
						.append("FILTER regex(?" + ing + "2,\"" + inge + "\") .");

			}
		} else {
			return null;
		}
		if (keywords != null && !keywords.isEmpty()) {
			for (String key : keywords) {
				String keyw = DigestUtils.sha256Hex(key);
				query.append("?s schema:keywords ?" + keyw + ".\r\n")
						.append("FILTER regex(?" + keyw + ",\"" + key + "\") .\r\n");

			}
		}
		query.append("  \r\n" + "  } LIMIT 5}");
		return query.toString();
	}

	public static String prefix = "PREFIX schema: <http://schema.org/>\r\n"
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n";

}
