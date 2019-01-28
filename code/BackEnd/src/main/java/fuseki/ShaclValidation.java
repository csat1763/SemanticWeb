package fuseki;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
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

public class ShaclValidation {
	private static Logger logger = LoggerFactory.getLogger(ShaclValidation.class);
	
	// Why This Failure marker
	private static final Marker WTF_MARKER = MarkerFactory.getMarker("WTF");

	public static void main(String[] args) {
		try {
			Path path = Paths.get(".").toAbsolutePath().normalize();
			
			//TODO: do for all recipes
			String data = "file:" + path.toFile().getAbsolutePath() + "/src/main/resources/shacl/###TODO###FromEdamam.jsonld";
			String shape = "file:" + path.toFile().getAbsolutePath() + "/src/main/resources/shacl/shacl.ttl";
			
			Model dataModel = JenaUtil.createDefaultModel();
			dataModel.read(data);
			
			Model shapeModel = JenaUtil.createDefaultModel();
			shapeModel.read(shape);
			
			Resource reportResource = ValidationUtil.validateModel(dataModel, shapeModel, true);
			boolean conforms = reportResource.getProperty(SH.conforms).getBoolean();
			logger.trace("Conforms = " + conforms);
			
			if (!conforms) {
				String report = path.toFile().getAbsolutePath() + "/src/main/resources/shacl/report.ttl";
				File reportFile = new File(report);
				
				reportFile.createNewFile();
				OutputStream reportOutputStream = new FileOutputStream(reportFile);
				RDFDataMgr.write(reportOutputStream, reportResource.getModel(), RDFFormat.TURTLE);
				System.out.println("ERROR - Created file report.ttl in path: " + report);
			} else {
				System.out.println("No Shape violations detected!");
			}
		} catch (Throwable t) {
			logger.error(WTF_MARKER, t.getMessage(), t);
		}
	}
}
