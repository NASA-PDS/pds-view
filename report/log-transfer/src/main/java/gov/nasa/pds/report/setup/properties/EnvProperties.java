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

	private Document _doc;

	private Logger _log = Logger.getLogger(this.getClass().getName());

	public EnvProperties(String path) throws FileNotFoundException {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			File envFile = new File(path+'/'+ENV_FILE);
			this._doc = docBuilder.parse(envFile);

			// normalize text representation
			this._doc.getDocumentElement().normalize();
			this._log.finest("Root element of the doc is "
					+ _doc.getDocumentElement().getNodeName());

		} catch (SAXParseException err) {
			this._log.warning("** Parsing error" + ", line " + err.getLineNumber()
					+ ", uri " + err.getSystemId());
			this._log.warning(" " + err.getMessage());

		} catch (SAXException e) {
			// Exception x = e.getException ();
			// ((x == null) ? e : x).printStackTrace ();
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}
	
	public String getLogDest() {
		return this._doc.getElementsByTagName("log_dest").item(0).getTextContent();
	}

	public String getProfileCfgHome() {
		return this._doc.getElementsByTagName("profile_cfg_home").item(0).getTextContent();
	}

	public String getSawmillHome() {
		return this._doc.getElementsByTagName("sawmill_home").item(0).getTextContent();
	}
	
	public static void main(String[] args) throws Exception {
		EnvProperties envProps = new EnvProperties("/Users/jpadams/dev/workspace/2010-workspace/report/transfer-logs/src/main/resources");
		System.out.println("logDest = " + envProps.getLogDest());
		//Connection conn = connect.getConnection();
		//conn.close();
	}

}
