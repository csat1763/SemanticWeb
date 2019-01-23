package fuseki;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.apache.jena.rdfconnection.RDFDatasetConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

public class FusekiConnection {
	public String dataSet;
	public String ontology;
	public String datasetName;

	private String connectionUrl;

	private String dataName = datasetName;

	private Dataset dataset;

	public FusekiConnection(String connectionUrl) {
		this.connectionUrl = connectionUrl;

	}

	public void loadData() {

		File datafolder = new File(dataSet);
		for (String datafile : listFilesForFolder(datafolder)) {
			try {
				uploadFileToGraph(datafile, "data");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		File ontologyFolder = new File(ontology);
		for (String ontFile : listFilesForFolder(ontologyFolder)) {
			try {
				uploadFileToGraph(ontFile, "ontology");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void uploadFileToGraph(String filename, String graphName) throws IOException {
		URL url = new URL(connectionUrl + "/" + dataName + "/data?graph=" + URLEncoder.encode(graphName, "UTF-8"));

		File file = new File(filename);
		InputStream fileInputStream = new FileInputStream(file);

		byte[] fileContent = new byte[(int) file.length()];
		fileInputStream.read(fileContent);
		fileInputStream.close();

		String str = new String(fileContent, "UTF-8");
		// System.out.println(str);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setInstanceFollowRedirects(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("Content-Type", "application/ld+json");
		conn.setRequestProperty("Content-Length", Integer.toString(str.getBytes("UTF-8").length));
		conn.setRequestProperty("Expect", "100-continue");
		try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {

			wr.write(str.getBytes("UTF-8"));
			wr.flush();
			wr.close();
		}

		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()), "UTF-8"));
		String output;
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}

		br.close();

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

	public void sendQueryResultsToAdr(String query, String address) {
		QueryExecution q = QueryExecutionFactory.sparqlService(connectionUrl + "/" + dataName + "/sparql", query);

		Model model = q.execDescribe();
		// File testFile = new File("testFileaa.jsonld");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		model.write(bos, "JSONLD");
		StringBuilder rep = new StringBuilder();
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader((new ByteArrayInputStream(bos.toByteArray())), "UTF-8"));

			String output = "";
			while ((output = br.readLine()) != null) {
				rep.append(output).append("\n");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println(rep.toString());

		// Transform output to json
		LinkedTreeMap<String, Object> jsonResult = new Gson().fromJson(rep.toString(), LinkedTreeMap.class);
		// System.out.println(jsonResult.toString());

		List<LinkedTreeMap<String, Object>> graph = (ArrayList<LinkedTreeMap<String, Object>>) jsonResult.get("@graph");
		HashMap<String, LinkedTreeMap<String, Object>> idEntries = new HashMap<String, LinkedTreeMap<String, Object>>();
		HashMap<String, ArrayList<LinkedTreeMap<String, Object>>> map = new HashMap<String, ArrayList<LinkedTreeMap<String, Object>>>();
		String type;
		for (LinkedTreeMap<String, Object> a : graph) {
			idEntries.put((String) a.get("@id"), a);
			type = (String) a.get("@type");
			if (!map.containsKey(type)) {
				map.put(type, new ArrayList<LinkedTreeMap<String, Object>>());
				// System.out.println(type);
			}
			map.get(type).add(a);

		}
		// System.out.println(idEntries);
		// System.out.println(map);
		// System.out.println(map.get("schema:Recipe"));
		ArrayList<String> fieldsToReplace = new ArrayList<String>();
		fieldsToReplace.add("creator");
		fieldsToReplace.add("image");
		fieldsToReplace.add("nutrition");
		fieldsToReplace.add("recipeInstructions");
		fieldsToReplace.add("recipeYield");

		for (LinkedTreeMap<String, Object> b : map.get("schema:Recipe")) {
			for (String field : fieldsToReplace) {
				b.put(field, idEntries.get(b.get(field)));
			}
			HashSet<String> ingredients = new HashSet<String>((ArrayList<String>) b.get("recipeIngredient"));

			ArrayList<LinkedTreeMap<String, Object>> toAdd = new ArrayList<LinkedTreeMap<String, Object>>();
			for (String id : ingredients) {

				toAdd.add(idEntries.get(id));

			}
			b.put("recipeIngredient", toAdd);

			for (LinkedTreeMap<String, Object> ingred : (ArrayList<LinkedTreeMap<String, Object>>) b
					.get("recipeIngredient")) {

				ingred.put("ingredientName", idEntries.get(ingred.get("ingredientName")));
				ingred.put("ingridientAmount", idEntries.get(ingred.get("ingridientAmount")));

			}

			// System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(b));

		}

		try {
			ArrayList<LinkedTreeMap<String, Object>> b = map.get("schema:Recipe");
			URL url = new URL(address);

			JsonElement element = new Gson().toJsonTree(b, new TypeToken<ArrayList<LinkedTreeMap<String, Object>>>() {
			}.getType());
			JsonArray jsonArray = element.getAsJsonArray();

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Content-Type", "application/ld+json");
			conn.setRequestProperty("Content-Length", Integer.toString(jsonArray.toString().getBytes("UTF-8").length));
			conn.setRequestProperty("Expect", "100-continue");
			try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {

				wr.write(new GsonBuilder().setPrettyPrinting().create().toJson(jsonArray).getBytes("UTF-8"));
				wr.flush();
				wr.close();
			}

			BufferedReader brr = new BufferedReader(new InputStreamReader((conn.getInputStream()), "UTF-8"));
			String output;
			while ((output = brr.readLine()) != null) {
				System.out.println(output);
			}

			brr.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void replaceIdByContent(LinkedTreeMap<String, Object> entry,
			HashMap<String, LinkedTreeMap<String, Object>> idEntries) {
		Set<String> keys = entry.keySet();
		for (String key : keys) {
			System.out.println(entry.get(key).toString());
			if (entry.get(key).toString().contains("_:"))
				entry.put("recipeInstructions", idEntries.get(entry.get("recipeInstructions")));
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

	public ArrayList<String> listFilesForFolder(final File folder) {
		ArrayList<String> files = new ArrayList<String>();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				files.add(fileEntry.getAbsolutePath().replace("\\", "/"));
			}

		}

		return files;
	}

}
