package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

public class DataCrawler {

	private static Collection<String> searchTerms = new ArrayList<String>();

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		RDFMisc.initSearchTerms(searchTerms);
		getRawData();
	}

	@SuppressWarnings({ "unused", "unchecked" })
	public static void getRawData() throws IOException {

		String searchTerm = "";
		String filename = "rawRecipes/" + searchTerm + ".json";

		for (String s : searchTerms) {

			searchTerm = s;
			filename = "rawRecipes/" + searchTerm + ".json";

			URL url = new URL("https://api.edamam.com/search?q=" + searchTerm
					+ "&app_id=6362f010&app_key=0a2cfb0cce312b298bf239c7c37790a8&from=0&to=100");

			// Create instance of connection to the API URL
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			// We will get the result in json format
			conn.setRequestProperty("Accept", "application/json");
			conn.setDoOutput(true);
			// Read response body from the stream returned by getInputStream()
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()), "UTF-8"));

			StringBuilder rep = new StringBuilder();
			String output = "";
			while ((output = br.readLine()) != null) {
				rep.append(output).append("\n");
			}

			// Transform output to json
			LinkedTreeMap<String, Object> jsonResult = new Gson().fromJson(rep.toString(), LinkedTreeMap.class);

			File recipesFromEdamam = new File(filename);

			try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(recipesFromEdamam),
					StandardCharsets.UTF_8)) {
				writer.write(rep.toString());
				writer.flush();
				writer.close();

			}

			// Close connection instance
			conn.disconnect();
			System.out.println(s + " done.");
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
