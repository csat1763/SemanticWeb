package api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

public class EdmanAPI {

	public static void main(String[] args) throws IOException, InterruptedException {
		// getData();
		translateJasonLdToN3();

	}

	// TODO: 5 workers; 5 requests per minute; 100 results per request; variate from
	// and to in request-query;
	@SuppressWarnings("unchecked")
	public static void getData() throws IOException {

		URL url = new URL("https://api.edamam.com/search?q=chicken&app_id=XXX&app_key=XXX&from=0&to=100");

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
			rep.append(output);
		}

		// Transform output to json
		LinkedTreeMap<String, Object> jsonResult = new Gson().fromJson(rep.toString(), LinkedTreeMap.class);

		List<LinkedTreeMap<String, Object>> hits = (ArrayList<LinkedTreeMap<String, Object>>) jsonResult.get("hits");

		if (null != hits && !hits.isEmpty()) {

			StringBuilder recipiesAsString = new StringBuilder();
			recipiesAsString.append("[\n");
			for (LinkedTreeMap<String, Object> hit : hits) {

				StringBuilder ingredientsAsString = new StringBuilder();

				// Publishing Date (YYYY-MM-DD) begin
				LinkedTreeMap<String, Object> recipe = (LinkedTreeMap<String, Object>) hit.get("recipe");

				String uri, label, recipeUrl;
				List<String> ingredients = new ArrayList<String>();
				double calories;
				// TODO: totalNutrients, healthLabels, source(author)

				uri = (String) recipe.get("uri");
				label = (String) recipe.get("label");
				recipeUrl = (String) recipe.get("url");

				calories = (double) recipe.get("calories");

				ingredients = (ArrayList<String>) recipe.get("ingredientLines");

				/*
				 * System.out.println("\nnew entry:\n" + " uri: " + uri + "\nlabel: " + label +
				 * "\nurl: " + recipeUrl + "\ncalories: " + calories + "\ningredients" +
				 * ingredients);
				 */

				ingredientsAsString.append("[\n");

				for (String i : ingredients) {
					ingredientsAsString.append("\t\t\"").append(i).append("\",\n");
				}
				ingredientsAsString.delete(ingredientsAsString.length() - 2, ingredientsAsString.length() - 1);

				ingredientsAsString.append("\t]");

				// System.out.println("\njson-ld:\n");

				recipiesAsString.append("{\n" + "\t\"@context\": \"http://schema.org\",\n"
						+ "\t\"@type\": \"Recipe\",\n" + "\t\"author\": \"John Smith\", \n" + "\t\"name\": \"" + label
						+ "\",\n" + "\t\"identifier\": \"" + uri + "\",\n" + "\t\"url\": \"" + recipeUrl + "\",\n"
						+ "\t\"recipeIngredient\": " + ingredientsAsString.toString() + " \n" + "},\n");

			}
			recipiesAsString.delete(recipiesAsString.length() - 2, recipiesAsString.length() - 1);
			recipiesAsString.append("]");

			// System.out.println(recipiesAsString.toString());

			File recipesFromEdamam = new File("recipesFromEdamam.json");

			PrintWriter tempWriter = new PrintWriter(recipesFromEdamam);
			tempWriter.print(recipiesAsString.toString());
			tempWriter.flush();
			tempWriter.close();

		}

		// Close connection instance
		conn.disconnect();

	}

	public static void translateJasonLdToN3() throws IOException {
		URL url = new URL("http://rdf-translator.appspot.com/convert/json-ld/nt/content");

		File file = new File("recipesFromEdamam.json");
		InputStream fileInputStream = new FileInputStream(file);

		byte[] fileContent = new byte[(int) file.length()];
		fileInputStream.read(fileContent);
		fileInputStream.close();

		String str = new String(fileContent, "UTF-8");
		// System.out.println(str);

		String newStr = URLEncoder.encode(str, "UTF-8");

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setInstanceFollowRedirects(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", Integer.toString(newStr.getBytes("UTF-8").length));
		conn.setRequestProperty("Expect", "100-continue");

		try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
			wr.writeBytes("content=");
			wr.write(newStr.getBytes("UTF-8"));
			wr.flush();
			wr.close();
		}

		System.out.println("[DEBUG]--------------- Converting JSON-LD file into N3: Request to Converter sent.");

		File n3file = new File("recipesFromEdamam.nt");
		PrintWriter n3fileWriter = new PrintWriter(new FileWriter(n3file));

		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()), "UTF-8"));

		StringBuilder rep = new StringBuilder();
		String output = "";
		while ((output = br.readLine()) != null) {
			n3fileWriter.println(output);
			n3fileWriter.flush();
			rep.append(output);
		}
		n3fileWriter.close();
		System.out.println("[DEBUG]--------------- Converting JSON-LD file into N3: DONE.");
	}

}
