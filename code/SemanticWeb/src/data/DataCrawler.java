package data;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

public class DataCrawler {

	private static Collection<String> searchTerms = new ArrayList<String>();

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		// getRawData();
		translateRawDataToRDF();
		// translateJsonLdToN3();
		// translateTxtToJson();
		// crawl();

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

			PrintWriter tempWriter = new PrintWriter(recipesFromEdamam);
			tempWriter.print(rep.toString());
			tempWriter.flush();
			tempWriter.close();

			// Close connection instance
			conn.disconnect();
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(s + " done.");
		}
	}

	@SuppressWarnings("unchecked")
	public static void translateRawDataToRDF() throws IOException {

		String searchTerm = "";
		String filename = "./TripleStore/Triple/src/main/resources/data/recipes/" + searchTerm + "FromEdamam.jsonld";

		initSearchTerms();

		for (String s : searchTerms) {

			searchTerm = s;
			filename = "../TripleStore/Triple/src/main/resources/data/recipes/" + searchTerm + "FromEdamam.jsonld";

			File rawData = new File("rawRecipes/" + searchTerm + ".json");
			InputStream rawDataStream = new FileInputStream(rawData);

			BufferedReader br = new BufferedReader(new InputStreamReader(rawDataStream, "UTF-8"));

			StringBuilder rep = new StringBuilder();
			String output = "";
			while ((output = br.readLine()) != null) {
				rep.append(output);
			}
			br.close();

			// Transform output to json
			LinkedTreeMap<String, Object> jsonResult = new Gson().fromJson(rep.toString(), LinkedTreeMap.class);

			List<LinkedTreeMap<String, Object>> hits = (ArrayList<LinkedTreeMap<String, Object>>) jsonResult
					.get("hits");

			if (null != hits && !hits.isEmpty()) {

				StringBuilder recipiesAsString = new StringBuilder();
				recipiesAsString.append("[\n");

				for (LinkedTreeMap<String, Object> hit : hits) {

					StringBuilder ingredientsAsString = new StringBuilder();

					// Publishing Date (YYYY-MM-DD) begin
					LinkedTreeMap<String, Object> recipe = (LinkedTreeMap<String, Object>) hit.get("recipe");

					String urlRecipe, label, recipeUrl, imageUrl, recipeId;
					List<String> ingredients = new ArrayList<String>();
					List<String> tags = new ArrayList<String>();
					double calories, yield, totalTime;
					// TODO: totalNutrients

					recipeId = (String) recipe.get("uri");
					recipeId = recipeId.replaceAll("\"", "").replaceAll("\n", "").replaceAll("\r\n", "")
							.replaceAll("\t", "").replaceAll(",", "").replaceAll("\\\\", "").replaceAll(" ", "");
					urlRecipe = (String) recipe.get("url");
					urlRecipe = urlRecipe.replaceAll("\"", "").replaceAll("\n", " ").replaceAll("\r\n", " ")
							.replaceAll("\t", " ").replaceAll(",", " ").replaceAll("\\\\", "");
					label = (String) recipe.get("label");
					label = label.replaceAll("\"", "").replaceAll("\n", " ").replaceAll("\r\n", " ")
							.replaceAll("\t", " ").replaceAll(",", " ").replaceAll("\\\\", "");
					recipeUrl = (String) recipe.get("url");
					recipeUrl = recipeUrl.replaceAll("\"", "").replaceAll("\n", " ").replaceAll("\r\n", " ")
							.replaceAll("\t", " ").replaceAll(",", " ").replaceAll("\\\\", "");
					imageUrl = (String) recipe.get("image");
					imageUrl = imageUrl.replaceAll("\"", "").replaceAll("\n", " ").replaceAll("\r\n", " ")
							.replaceAll("\t", " ").replaceAll(",", " ").replaceAll("\\\\", "");
					yield = (double) recipe.get("yield");
					calories = (double) recipe.get("calories");
					totalTime = (double) recipe.get("totalTime");
					calories = Math.round(calories);

					ingredients = (ArrayList<String>) recipe.get("ingredientLines");
					if (recipe.get("healthLabels") != null) {
						tags.addAll((ArrayList<String>) recipe.get("healthLabels"));
					}
					if (recipe.get("dietLabels") != null) {
						tags.addAll((ArrayList<String>) recipe.get("dietLabels"));
					}

					ingredientsAsString.append("[\n");
					String pattern = "^\\*? ?([0-9\\.\\/½¼¾]* ?[0-9\\.\\/½¼¾]+)[ \\-]?([a-zA-Z\\.\\(\\)]+)? +([a-zA-Z0-9 \\+\\-\\,\\/\\.\\(\\)®%\\'éèîñ&]+)$";
					Pattern r = Pattern.compile(pattern);
					String amount = "";
					String unit = "";
					String ingredient = "";
					for (String i : ingredients) {

						i = i.replaceAll("\"", "").replaceAll("\n", " ").replaceAll("\r\n", " ").replaceAll("\t", " ")
								.replaceAll(",", " ").replaceAll("\\\\", "");
						/*
						 * Matcher m = r.matcher(i);
						 * 
						 * if (m.find()) {
						 * 
						 * amount = m.group(1).replaceAll("½",
						 * " 1/2").replaceAll("¼", " 1/4") .replaceAll("¾",
						 * " 3/4").replaceAll("  ", " ").replaceAll("^ +", "");
						 * unit = m.group(2); if (unit == null) { unit = "pcs";
						 * } ingredient = m.group(3);
						 * 
						 * }
						 */
						ingredientsAsString
								.append("\t\t{\r\n \t\t\t\"@type\" : \"IngredientAddition\",\r\n"
										+ "\t\t\t\"ingredientName\" : {\r\n" + "\t\t\t\t\"@type\" : \"Ingredient\",\r\n"
										+ "\t\t\t\t\"name\" : \"" + ingredient + "\",\r\n"
										+ "\t\t\t\t\"ingridientFullName\" : \"" + i + "\"\r\n" + " \t\t\t},")
								.append("\r\n \t\t\t\"ingridientAmount\" : {\r\n"
										+ "\t\t\t\t\"@type\" : \"QuantitativeValue\",\r\n" + "\t\t\t\t\"unitText\" : \""
										+ unit + "\",\r\n" + "\t\t\t\t\"value\" : \"" + amount + "\"\r\n" + "\t\t\t}");

						if (ingredient != null)
							ingredientsAsString.append("\t\t\t,\r\n").append(potentialActionGen(ingredient))
									.append("\t\t},\n");
						else {
							ingredientsAsString.append("\r\n\t\t},\n");
						}
					}
					ingredientsAsString.delete(ingredientsAsString.length() - 2, ingredientsAsString.length() - 1);

					ingredientsAsString.append("\t]");

					recipiesAsString.append("{\n" + "\t\"@context\": \"http://schema.org\",\n" +

							"\t\"@id\": \"" + recipeId + "\",\n" +

							"\t\"@type\": \"Recipe\",\n" +

							"\t\"sameAs\": \"" + recipeUrl + "\",\n" +

							"\t\"creator\": {" + "\n" + "\t\t\"@type\": \"Person\",\n"
							+ "\t\t\"name\": \"not given\"\n\t},\n" +

							"\t\"nutrition\": {\n" + "\t\t\"@type\": \"NutritionInformation\",\n"
							+ "\t\t\"calories\" : \"" + calories + " calories\"\n\t},\n" +

							"\t\"keywords\": [ " + generateKeywordString(tags) + " ],\n" +

							"\t\"name\": \"" + label + "\",\n" +

							"\t\"recipeYield\": {" + "\n" + "\t\t\"@type\": \"QuantitativeValue\",\n"
							+ "\t\t\"value\" : \"" + yield + "\"\n\t}, \n" +

							"\t\"image\": {\n" + "\t\t\"@type\": \"ImageObject\",\n" + "\t\t\"contentUrl\" : \""
							+ imageUrl + "\", \n" + "\t\t\"caption\" : \"" + label + "\"\n\t}, \n" +

							"\t\"cookTime\": \"" + "-" + "\",\n" +

							"\t\"prepTime\": \"" + "-" + "\",\n" +

							"\t\"totalTime\": \"" + convertDoubleToISODuration(totalTime) + "\",\n" +

							"\t\"recipeInstructions\": {\n" + "\t\t\"@type\": \"CreativeWork\",\n" + "\t\t\"url\" : \""
							+ urlRecipe + "\"\n\t}, \n" +

							"\t\"recipeIngredient\": " + ingredientsAsString.toString() + " \n" + "},\n");

				}

				recipiesAsString.delete(recipiesAsString.length() - 2, recipiesAsString.length() - 1);
				recipiesAsString.append("]");

				File recipesFromEdamam = new File(filename);

				PrintWriter tempWriter = new PrintWriter(recipesFromEdamam);
				tempWriter.print(recipiesAsString.toString());
				tempWriter.flush();
				tempWriter.close();

			}

			System.out.println("Edamam search term: " + s + " done.");
		}
	}

	public static void translateJsonLdToN3() throws IOException {
		URL url = new URL("http://rdf-translator.appspot.com/convert/json-ld/nt/content");

		File file = new File("recipesFromEdamam.jsonld");
		InputStream fileInputStream = new FileInputStream(file);

		byte[] fileContent = new byte[(int) file.length()];
		fileInputStream.read(fileContent);
		fileInputStream.close();

		String str = new String(fileContent, "UTF-8");

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
				String recipeId = label.replaceAll(" ", "");

				recipiesAsString.append("{\n" + "\t\"@context\": \"http://schema.org\",\n" +

						"\t\"@id\": \"http://example.com/ns#" + recipeId + "\",\n" +

						"\t\"@type\": \"Recipe\",\n" +

						"\t\"sameAs\": \"" + url + "\",\n" +

						"\t\"creator\": {" + "\n" + "\t\t\"@type\": \"Person\",\n"
						+ "\t\t\"name\": \"not given\"\n\t},\n" +

						"\t\"nutrition\": {\n" + "\t\t\"@type\": \"NutritionInformation\",\n" + "\t\t\"calories\" : \""
						+ "not given" + "\"\n\t},\n" +

						"\t\"name\": \"" + label + "\",\n" +

						"\t\"recipeYield\": {" + "\n" + "\t\t\"@type\": \"QuantitativeValue\",\n" + "\t\t\"value\" : \""
						+ yield + "\"\n\t}, \n" +

						"\t\"image\": {\n" + "\t\t\"@type\": \"ImageObject\",\n" + "\t\t\"contentUrl\" : \"" + imageUrl
						+ "\", \n" + "\t\t\"caption\" : \"" + label + "\"\n\t}, \n" +

						"\t\"cookTime\": \"" + cookTime + "\",\n" +

						"\t\"prepTime\": \"" + prepTime + "\",\n" +

						"\t\"totalTime\": \"" + addTwoIsoTimes(cookTime, prepTime) + "\",\n" +

						"\t\"recipeInstructions\": {\n" + "\t\t\"@type\": \"CreativeWork\",\n" + "\t\t\"url\" : \""
						+ url + "\"\n\t}, \n" +

						"\t\"recipeIngredient\": " + ingredientsAsString.toString() + " \n" + "},\n");

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

	public static String convertDoubleToISODuration(double d) {
		StringBuilder sb = new StringBuilder();

		int hours = 0;
		int minutes = 0;

		if (d >= 60) {
			hours = (int) d / 60;
			minutes = (int) d % 60;
			sb.append("PT" + hours + "H" + minutes + "M");
		} else {
			sb.append("PT" + (int) d + "M");
		}
		return sb.toString();
	}

	public static String addTwoIsoTimes(String cookTime, String prepTime) {
		String cookTimeStr = cookTime.replaceAll("[^\\d.]", "");
		String prepTimeStr = prepTime.replaceAll("[^\\d.]", "");

		long cookTimeInt = 0;
		long prepTimeInt = 0;
		int totalTime = 0;

		if (!cookTimeStr.equals("")) {
			java.time.Duration durCook = java.time.Duration.parse(cookTime);
			cookTimeInt = durCook.get(java.time.temporal.ChronoUnit.SECONDS);
		}

		if (!prepTimeStr.equals("")) {
			java.time.Duration durPrep = java.time.Duration.parse(prepTime);
			prepTimeInt = durPrep.get(java.time.temporal.ChronoUnit.SECONDS);
		}

		totalTime = (int) ((cookTimeInt + prepTimeInt) / 60);
		return convertDoubleToISODuration(totalTime);
	}

	public static String potentialActionGen(String ingredient) {
		if (ingredient == null)
			return null;
		String ingrStr = ingredient.replaceAll(" ", "+");
		return "\t\t\t\"potentialAction\" : {\r\n" + "\t\t\t\t\"@type\": \"SearchAction\",\r\n"
				+ "\t\t\t\t\"target\": \"https://www.freshdirect.com/srch.jsp?searchParams=" + "" + "\"\r\n"
				+ "\t\t\t}\r\n";
		/*
		 * return "\t\t\t\"potentialAction\" : {\r\n" +
		 * "\t\t\t\t\"@type\": \"SearchAction\",\r\n" +
		 * "\t\t\t\t\"target\": \"https://www.freshdirect.com/srch.jsp?searchParams="
		 * + ingrStr + "\"\r\n" + "\t\t\t}\r\n";
		 */
	}

	public static String generateKeywordString(List<String> tags) {

		StringBuilder sb = new StringBuilder();

		for (String tag : tags) {
			sb.append("\"").append(tag).append("\"").append(", ");
		}

		if (tags.size() > 0) {
			sb.deleteCharAt(sb.length() - 1);
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}

	public static void initSearchTerms() {
		searchTerms.add("alcohol");
		searchTerms.add("american");
		searchTerms.add("asian");
		searchTerms.add("beef");
		searchTerms.add("beer");
		searchTerms.add("burger");
		searchTerms.add("cake");
		searchTerms.add("cheese");
		searchTerms.add("chicken");
		searchTerms.add("chocolate");
		searchTerms.add("fish");
		searchTerms.add("fruit");
		searchTerms.add("german");
		searchTerms.add("greek");
		searchTerms.add("hot");
		searchTerms.add("ice");
		searchTerms.add("italian");
		searchTerms.add("korean");
		searchTerms.add("mexican");
		searchTerms.add("noodle");
		searchTerms.add("pizza");
		searchTerms.add("pork");
		searchTerms.add("potato");
		searchTerms.add("rice");
		searchTerms.add("salad");
		searchTerms.add("soup");
		searchTerms.add("sour");
		searchTerms.add("sushi");
		searchTerms.add("swedish");
		searchTerms.add("sweet");
		searchTerms.add("african");
		searchTerms.add("british");
		searchTerms.add("caribbean");
		searchTerms.add("chinese");
		searchTerms.add("french");
		searchTerms.add("indian");
		searchTerms.add("irish");
		searchTerms.add("japanese");
		searchTerms.add("nordic");
		searchTerms.add("pakistani");
		searchTerms.add("portuguese");
		searchTerms.add("spanish");
		searchTerms.add("thai");
		searchTerms.add("turkish");
		searchTerms.add("russian");

	}

}
