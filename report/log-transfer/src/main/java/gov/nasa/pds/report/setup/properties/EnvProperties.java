package gov.nasa.pds.report.setup.properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class EnvProperties {

	private static final String ENV_FILE = "env_vars.xml";

	private Document doc;

	private Logger log = Logger.getLogger(this.getClass().getName());

	public EnvProperties(final String path) throws FileNotFoundException {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			File envFile = new File(path+'/'+ENV_FILE);
			this.doc = docBuilder.parse(envFile);

			// normalize text representation
			this.doc.getDocumentElement().normalize();
			this.log.finest("Root element of the doc is "
					+ doc.getDocumentElement().getNodeName());

		} catch (SAXParseException err) {
			this.log.warning("** Parsing error" + ", line " + err.getLineNumber()
					+ ", uri " + err.getSystemId());
			this.log.warning(" " + err.getMessage());

		} catch (SAXException e) {
			// Exception x = e.getException ();
			// ((x == null) ? e : x).printStackTrace ();
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}
	
	public final String getLogDest() {
		return this.doc.getElementsByTagName("log_dest").item(0).getTextContent();
	}

	public final String getSawmillProfileHome() {
		return this.doc.getElementsByTagName("sawmill_profile_home").item(0).getTextContent();
	}

	public final String getSawmillHome() {
		return this.doc.getElementsByTagName("sawmill_home").item(0).getTextContent();
	}
	
	public static void main(final String[] args) throws Exception {
		EnvProperties envProps = new EnvProperties("/Users/jpadams/dev/workspace/2010-workspace/report/transfer-logs/src/main/resources");
		System.out.println("logDest = " + envProps.getLogDest());
		//Connection conn = connect.getConnection();
		//conn.close();
	}

}
