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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class RDFMisc {

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

	public static void initSearchTerms(Collection<String> searchTerms) throws UnsupportedEncodingException {

		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("src/Cuisines.txt")), "UTF-8"));

			String output = "";
			while ((output = br.readLine()) != null) {
				searchTerms.add(URLEncoder.encode(output, "UTF-8"));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
