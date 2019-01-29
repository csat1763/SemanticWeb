package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

public class DataTranslator {

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		translateRawDataToRDF();
	}

	@SuppressWarnings("unchecked")
	public static void translateRawDataToRDF() throws IOException {

		String searchTerm = "";
		String filename = "./TripleStore/Triple/src/main/resources/data/recipes/" + searchTerm + "FromEdamam.jsonld";
		int hitss = 0;

		for (String s : listFilesForFolder(new File("rawRecipes"))) {

			File rawData = new File(s);
			InputStream rawDataStream = new FileInputStream(rawData);

			searchTerm = rawData.getName().replaceAll(".json", "");

			filename = "../TripleStore/Triple/src/main/resources/data/recipes/" + searchTerm + "FromEdamam.jsonld";

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
			hitss += hits.size();

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
						 * amount = m.group(1).replaceAll("½", " 1/2").replaceAll("¼", " 1/4") .replaceAll("¾", " 3/4").replaceAll("  ", " ").replaceAll("^ +", ""); unit = m.group(2); if (unit == null) { unit = "pcs"; }
						 * ingredient = m.group(3);
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

					recipiesAsString.append("{\n"
							+ "\t\"@context\": [{ \"@vocab\": \"http://schema.org/\" },{\"fo\": \"http://purl.org/ontology/fo/\"}],\n"

							+ "\t\"fo:cuisine\" : \"" + URLDecoder.decode(searchTerm, "UTF-8") + "\",\n" +

							"\t\"@id\": \"" + recipeId + "\",\n" +

							"\t\"@type\": \"Recipe\",\n" +

							"\t\"sameAs\": \"" + recipeUrl + "\",\n" +

							"\t\"creator\": {" + "\n" + "\t\t\"@type\": \"Person\",\n"
							+ "\t\t\"name\": \"not given\"\n\t},\n" +

							"\t\"nutrition\": {\n" + "\t\t\"@type\": \"NutritionInformation\",\n"
							+ "\t\t\"calories\" : \"" + calories + " calories\"\n\t},\n" +

							"\t\"keywords\": [ " + RDFMisc.generateKeywordString(tags) + " ],\n" +

							"\t\"name\": \"" + label + "\",\n" +

							"\t\"recipeYield\": {" + "\n" + "\t\t\"@type\": \"QuantitativeValue\",\n"
							+ "\t\t\"value\" : \"" + yield + "\"\n\t}, \n" +

							"\t\"image\": {\n" + "\t\t\"@type\": \"ImageObject\",\n" + "\t\t\"contentUrl\" : \""
							+ imageUrl + "\", \n" + "\t\t\"caption\" : \"" + label + "\"\n\t}, \n" +

							"\t\"cookTime\": \"" + "-" + "\",\n" +

							"\t\"prepTime\": \"" + "-" + "\",\n" +

							"\t\"totalTime\": \"" + RDFMisc.convertDoubleToISODuration(totalTime) + "\",\n" +

							"\t\"recipeInstructions\": {\n" + "\t\t\"@type\": \"CreativeWork\",\n" + "\t\t\"url\" : \""
							+ urlRecipe + "\"\n\t}, \n" +

							"\t\"recipeIngredient\": " + ingredientsAsString.toString() + " \n" + "},\n");

				}

				recipiesAsString.delete(recipiesAsString.length() - 2, recipiesAsString.length() - 1);
				recipiesAsString.append("]");

				File recipesFromEdamam = new File(filename);

				try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(recipesFromEdamam),
						StandardCharsets.UTF_8)) {
					writer.write(recipiesAsString.toString());
					writer.flush();
					writer.close();

				}

			}

			System.out.println("Edamam search term: " + s + " done.");
		}
		System.out.println("Recipecount: " + hitss);

	}

	public static String potentialActionGen(String ingredient) {
		if (ingredient == null)
			return null;
		// String ingrStr = ingredient.replaceAll(" ", "+");
		return "\t\t\t\"potentialAction\" : {\r\n" + "\t\t\t\t\"@type\": \"SearchAction\",\r\n"
				+ "\t\t\t\t\"target\": \"https://www.freshdirect.com/srch.jsp?searchParams=" + "" + "\"\r\n"
				+ "\t\t\t}\r\n";
		/*
		 * return "\t\t\t\"potentialAction\" : {\r\n" + "\t\t\t\t\"@type\": \"SearchAction\",\r\n" + "\t\t\t\t\"target\": \"https://www.freshdirect.com/srch.jsp?searchParams=" + ingrStr + "\"\r\n" +
		 * "\t\t\t}\r\n";
		 */
	}

	public static ArrayList<String> listFilesForFolder(File folder) {
		ArrayList<String> files = new ArrayList<String>();
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				files.add(fileEntry.getAbsolutePath().replace("\\", "/"));
			}

		}

		return files;
	}

}
