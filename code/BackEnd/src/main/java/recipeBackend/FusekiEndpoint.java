package recipeBackend;

import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import fuseki.FusekiConnection;
import fuseki.FusekiLoader;

/* Fuseki must be running and must have at least one dataset in graph <http://localhost:3030/food/data/data> 
 * and an ontology in graph <http://localhost:3030/food/data/ontology>
 * */
public class FusekiEndpoint {

	public static String fusekiAdr = "http://localhost:3030";
	public static String frontEntAdr2 = "https://httpbin.org/post";
	public static String frontEntAdr = "http://localhost:4200";
	public static String fusekiDataGraphName = "<http://uibk.org/data>";
	public static String fusekiOntGraphName = "<http://uibk.org/ontology>";

	public static String getResutlsFromFusekiFor(List<String> ings, List<String> keys) {
		FusekiConnection fc = genDefaultFuseki();
		return fc.sendQueryResultsToAdr(generateQuery(ings, keys));

	}

	public static FusekiConnection genDefaultFuseki() {
		FusekiConnection fc = new FusekiConnection(fusekiAdr);
		fc.dataSet = FusekiLoader.dataSet;
		fc.ontology = FusekiLoader.ontology;
		fc.datasetName = FusekiLoader.datasetName;
		return fc;

	}

	public static String generateQuery(List<String> ings, List<String> keywords) {
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
						// .append("FILTER regex(?" + keyw + ",\"" + key + "\") .\r\n")
						.append("FILTER(<java:fusekic.LevenshteinFilter>(?" + keyw + ", \"" + key + "\") < 6) .\r\n");

			}
		}
		query.append("  \r\n" + "  } LIMIT 5}");
		System.out.println(query.toString());
		return query.toString();
	}

	public static String prefix = "PREFIX schema: <http://schema.org/>\r\n"
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n";

}
