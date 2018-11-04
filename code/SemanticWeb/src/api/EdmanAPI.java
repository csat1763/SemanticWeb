package api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

public class EdmanAPI {

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		// getData();
		// translateJsonLdToN3();
		// translateTxtToJson();
		crawl();

	}

	public static void crawl() throws InterruptedException, FileNotFoundException, ExecutionException {
		ExecutorService pool = Executors.newFixedThreadPool(5);
		ArrayList<Future<String>> results = new ArrayList<Future<String>>();
		int i = 0;
		boolean endNotReached = true;
		while (endNotReached) {
			results.add(pool.submit(new EdamamCrawler(i, i + 100)));
			results.add(pool.submit(new EdamamCrawler(i + 100, i + 200)));
			results.add(pool.submit(new EdamamCrawler(i + 200, i + 300)));
			results.add(pool.submit(new EdamamCrawler(i + 300, i + 400)));
			results.add(pool.submit(new EdamamCrawler(i + 400, i + 500)));
			i += 500;
			Thread.sleep(1200);

			for (Future<String> res : results) {
				try {
					if (res.get() == null) {
						endNotReached = false;
					}
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		File recipesFromEdamam = new File("recipesFromEdamam.jsonld");

		PrintWriter tempWriter = new PrintWriter(recipesFromEdamam);

		for (Future<String> res : results) {
			tempWriter.print(res.get());
			tempWriter.flush();
		}

		tempWriter.close();

	}

	// TODO: 5 workers; 5 requests per minute; 100 results per request; variate from
	// and to in request-query;
	@SuppressWarnings("unchecked")
	public static void getData() throws IOException {

		URL url = new URL("https://api.edamam.com/search?q=chicken&app_id=XXX&app_key=XXX&from=100&to=200");

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

				String uri, label, recipeUrl, imageUrl;
				List<String> ingredients = new ArrayList<String>();
				double calories, yield, totalTime;
				// TODO: totalNutrients, healthLabels, source(author)

				uri = (String) recipe.get("uri");
				label = (String) recipe.get("label");
				recipeUrl = (String) recipe.get("url");
				imageUrl = (String) recipe.get("image");
				yield = (double) recipe.get("yield");
				calories = (double) recipe.get("calories");
				totalTime = (double) recipe.get("totalTime");
				calories = Math.round(calories);

				ingredients = (ArrayList<String>) recipe.get("ingredientLines");

				/*
				 * System.out.println("\nnew entry:\n" + " uri: " + uri + "\nlabel: " + label + "\nurl: " + recipeUrl + "\ncalories: " + calories + "\ningredients" + ingredients);
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
						+ "\t\"image\": \"" + imageUrl + "\",\n" + "\t\"recipeYield\": \"" + yield + "\",\n"
						+ "\t\"totalTime\": \"" + totalTime + "\",\n"
						+ "\t\"nutrition\": {\n\t\t\"@type\": \"NutritionInformation\",\n\t\t\"calories\" : \""
						+ calories + " calories\"\n\t},\n" + "\t\"recipeIngredient\": " + ingredientsAsString.toString()
						+ " \n" + "},\n");

			}

			recipiesAsString.delete(recipiesAsString.length() - 2, recipiesAsString.length() - 1);
			recipiesAsString.append("]");

			// System.out.println(recipiesAsString.toString());

			File recipesFromEdamam = new File("recipesFromEdamam.jsonld");

			PrintWriter tempWriter = new PrintWriter(recipesFromEdamam);
			tempWriter.print(recipiesAsString.toString());
			tempWriter.flush();
			tempWriter.close();

		}

		// Close connection instance
		conn.disconnect();

	}

	public static void translateJsonLdToN3() throws IOException {
		URL url = new URL("http://rdf-translator.appspot.com/convert/json-ld/nt/content");

		File file = new File("recipesFromEdamam.jsonld");
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

	public static void translateTxtToJson() throws IOException {

		JSONParser parser = new JSONParser();

		StringBuilder recipiesAsString = new StringBuilder();
		recipiesAsString.append("[\n");

		try {

			Object obj = parser.parse(new FileReader("data.txt"));

			JSONArray jsonArray = (JSONArray) obj;

			for (int i = 0; i < jsonArray.size(); i++) {

				StringBuilder ingredientsAsString = new StringBuilder();

				ingredientsAsString.append("[\n");

				JSONObject jsonObj = (JSONObject) jsonArray.get(i);
				String label = (String) jsonObj.get("name");

				JSONArray ingredientsJson = (JSONArray) jsonObj.get("recipeIngredient");

				ArrayList<String> ingredients = new ArrayList<String>();

				for (int j = 0; j < ingredientsJson.size(); j++) {
					ingredients.add((String) ingredientsJson.get(j));
				}

				for (String ing : ingredients) {
					ingredientsAsString.append("\t\t\"").append(ing).append("\",\n");
				}
				ingredientsAsString.delete(ingredientsAsString.length() - 2, ingredientsAsString.length() - 1);

				ingredientsAsString.append("\t]");

				String cookTime = (String) jsonObj.get("cookTime");
				String prepTime = (String) jsonObj.get("prepTime");
				String url = (String) jsonObj.get("url");
				String imageUrl = (String) jsonObj.get("image");
				String yield = (String) jsonObj.get("recipeYield");

				recipiesAsString.append("{\n" + "\t\"@context\": \"http://schema.org\",\n"
						+ "\t\"@type\": \"Recipe\",\n" + "\t\"author\": \"John Smith\", \n" + "\t\"name\": \"" + label
						+ "\",\n" + "\t\"recipeYield\": \"" + yield + "\",\n" + "\t\"image\": \"" + imageUrl + "\",\n"
						+ "\t\"cookTime\": \"" + cookTime + "\",\n" + "\t\"prepTime\": \"" + prepTime + "\",\n"
						+ "\t\"url\": \"" + url + "\",\n" + "\t\"recipeIngredient\": " + ingredientsAsString.toString()
						+ " \n" + "},\n");

			}
			recipiesAsString.delete(recipiesAsString.length() - 2, recipiesAsString.length() - 1);
			recipiesAsString.append("]");

			File recipesFromTxt = new File("recipesFromTxt.jsonld");

			PrintWriter tempWriter = new PrintWriter(recipesFromTxt);
			tempWriter.print(recipiesAsString.toString());
			tempWriter.flush();
			tempWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Source 2 done.");

	}

}

class EdamamCrawler implements Callable<String> {

	private int from;
	private int to;

	public EdamamCrawler(int from, int to) {
		this.from = from;
		this.to = to;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String call() {
		URL url;
		try {
			url = new URL("https://api.edamam.com/search?q=pasta&app_id=XXX&app_key=XXX&from=" + from + "&to=" + to);

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

			File recipesFromTxt = new File("edamamResult" + from + "-" + to + ".jsonld");

			PrintWriter tempWriter = new PrintWriter(recipesFromTxt);
			tempWriter.print(rep.toString());
			tempWriter.flush();
			tempWriter.close();

			// Transform output to json
			LinkedTreeMap<String, Object> jsonResult = new Gson().fromJson(rep.toString(), LinkedTreeMap.class);

			List<LinkedTreeMap<String, Object>> hits = (ArrayList<LinkedTreeMap<String, Object>>) jsonResult
					.get("hits");

			if (null != hits && !hits.isEmpty()) {

				StringBuilder recipiesAsString = new StringBuilder();
				System.out.println("Entering result!");
				// recipiesAsString.append("[\n");
				for (LinkedTreeMap<String, Object> hit : hits) {

					StringBuilder ingredientsAsString = new StringBuilder();

					// Publishing Date (YYYY-MM-DD) begin
					LinkedTreeMap<String, Object> recipe = (LinkedTreeMap<String, Object>) hit.get("recipe");

					String uri, label, recipeUrl, imageUrl;
					List<String> ingredients = new ArrayList<String>();
					double calories, yield, totalTime;
					// TODO: totalNutrients, healthLabels, source(author)

					uri = (String) recipe.get("uri");
					label = (String) recipe.get("label");
					recipeUrl = (String) recipe.get("url");
					imageUrl = (String) recipe.get("image");
					yield = (double) recipe.get("yield");
					calories = (double) recipe.get("calories");
					totalTime = (double) recipe.get("totalTime");
					calories = Math.round(calories);

					ingredients = (ArrayList<String>) recipe.get("ingredientLines");

					/*
					 * System.out.println("\nnew entry:\n" + " uri: " + uri + "\nlabel: " + label + "\nurl: " + recipeUrl + "\ncalories: " + calories + "\ningredients" + ingredients);
					 */

					ingredientsAsString.append("[\n");

					for (String i : ingredients) {
						ingredientsAsString.append("\t\t\"").append(i).append("\",\n");
					}
					ingredientsAsString.delete(ingredientsAsString.length() - 2, ingredientsAsString.length() - 1);

					ingredientsAsString.append("\t]");

					// System.out.println("\njson-ld:\n");

					recipiesAsString.append("{\n" + "\t\"@context\": \"http://schema.org\",\n"
							+ "\t\"@type\": \"Recipe\",\n" + "\t\"author\": \"John Smith\", \n" + "\t\"name\": \""
							+ label + "\",\n" + "\t\"identifier\": \"" + uri + "\",\n" + "\t\"url\": \"" + recipeUrl
							+ "\",\n" + "\t\"image\": \"" + imageUrl + "\",\n" + "\t\"recipeYield\": \"" + yield
							+ "\",\n" + "\t\"totalTime\": \"" + totalTime + "\",\n"
							+ "\t\"nutrition\": {\n\t\t\"@type\": \"NutritionInformation\",\n\t\t\"calories\" : \""
							+ calories + " calories\"\n\t},\n" + "\t\"recipeIngredient\": "
							+ ingredientsAsString.toString() + " \n" + "},\n");

				}

				// recipiesAsString.delete(recipiesAsString.length() - 2, recipiesAsString.length() - 1);
				// recipiesAsString.append("]");

				conn.disconnect();
				System.out.println("New chunk added!");
				return recipiesAsString.toString();

			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("failed to add!");
		return null;

	}
}
