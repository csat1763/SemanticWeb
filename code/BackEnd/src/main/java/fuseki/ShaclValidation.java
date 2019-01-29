package fuseki;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.topbraid.shacl.validation.ValidationUtil;
import org.topbraid.shacl.vocabulary.SH;
import org.topbraid.spin.util.JenaUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

public class ShaclValidation {
	private static Logger logger = LoggerFactory.getLogger(ShaclValidation.class);

	// Why This Failure marker
	private static final Marker WTF_MARKER = MarkerFactory.getMarker("WTF");

	public static void main(String[] args) {

		try {
			Path path = Paths.get(".").toAbsolutePath().normalize();

			for (String data : FusekiConnection
					.listFilesForFolder(new File("../TripleStore/Triple/src/main/resources/data/recipes"))) {
				String cuisineName = getCuisineFromFile(data);

				String shape = "file:" + path.toFile().getAbsolutePath() + "/src/main/resources/shacl/shacl.ttl";

				Model dataModel = JenaUtil.createDefaultModel();
				dataModel.read(data);

				Model shapeModel = JenaUtil.createDefaultModel();
				shapeModel.read(shape);

				Resource reportResource = ValidationUtil.validateModel(dataModel, shapeModel, true);
				boolean conforms = reportResource.getProperty(SH.conforms).getBoolean();
				logger.trace("Conforms = " + conforms);

				if (!conforms) {
					String report = "../TripleStore/Triple/src/main/resources/shaclReports/" + cuisineName
							+ "FromEdamamReport.jsonld";
					File reportFile = new File(report);

					reportFile.createNewFile();
					OutputStream reportOutputStream = new FileOutputStream(reportFile);
					RDFDataMgr.write(reportOutputStream, reportResource.getModel(), RDFFormat.JSONLD);
					System.out.println("ERROR - Created file report.jsonld in path: " + report);
					removeCorruptEntriesFromFile(data, report, cuisineName);
				} else {
					System.out.println("No Shape violations detected!");
				}

			}
		} catch (Throwable t) {
			logger.error(WTF_MARKER, t.getMessage(), t);
		}

	}

	@SuppressWarnings({ "unchecked" })
	public static String getCuisineFromFile(String filePath) {
		try {
			File rawData = new File(filePath);
			InputStream rawDataStream;

			rawDataStream = new FileInputStream(rawData);

			BufferedReader br = new BufferedReader(new InputStreamReader(rawDataStream, "UTF-8"));
			StringBuilder rep = new StringBuilder();
			String output = "";
			while ((output = br.readLine()) != null) {
				rep.append(output);
			}
			br.close();

			ArrayList<LinkedTreeMap<String, Object>> jsonResult = new Gson().fromJson(rep.toString(), ArrayList.class);

			return URLEncoder.encode(jsonResult.get(0).get("fo:cuisine").toString(), "UTF-8");
		} catch (

		IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	public static void removeCorruptEntriesFromFile(String dataFileName, String reportFileName, String cuisine) {
		try {
			/* Read datafile first */
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(dataFileName)), "UTF-8"));
			StringBuilder rep = new StringBuilder();
			String output = "";
			while ((output = br.readLine()) != null) {
				rep.append(output);
			}
			br.close();

			HashMap<String, LinkedTreeMap<String, Object>> idToIdx = new HashMap<String, LinkedTreeMap<String, Object>>();

			/* Then reportfile */
			ArrayList<LinkedTreeMap<String, Object>> jsonResult = new Gson().fromJson(rep.toString(), ArrayList.class);
			for (LinkedTreeMap<String, Object> recipeEntry : jsonResult) {
				idToIdx.put((String) recipeEntry.get("@id"), recipeEntry);
			}

			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(reportFileName)), "UTF-8"));
			rep = new StringBuilder();
			output = "";
			while ((output = br.readLine()) != null) {
				rep.append(output);
			}
			br.close();

			ArrayList<LinkedTreeMap<String, Object>> reportEntries = (ArrayList<LinkedTreeMap<String, Object>>) new Gson()
					.fromJson(rep.toString(), LinkedTreeMap.class).get("@graph");

			for (LinkedTreeMap<String, Object> reportEntry : reportEntries) {
				if (reportEntry.get("@type").equals("http://www.w3.org/ns/shacl#ValidationResult")) {
					String id = (String) reportEntry.get("focusNode");
					jsonResult.remove(idToIdx.get(id));
				}
			}

			try (OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(new File("../TripleStore/Triple/src/main/resources/data/recipes/" + cuisine
							+ "FromEdamamSHACLvalid.jsonld")),
					StandardCharsets.UTF_8)) {
				writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(jsonResult));
				writer.flush();
				writer.close();

			}

		} catch (

		IOException e) {
			e.printStackTrace();
		}

	}

}
