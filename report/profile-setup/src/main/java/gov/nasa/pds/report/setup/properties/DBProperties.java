package gov.nasa.pds.report.setup.properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DBProperties {

	private static final String DB_CONFIG = "db_config.xml";
	
	private Properties dbProps;

	private Document doc;

	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public static void main(final String[] args) throws Exception {
		DBProperties connect = new DBProperties("/Users/jpadams/dev/workspace/2010-workspace/report/transfer-logs/src/main/resources");
		System.out.println("driver = " + connect.getDriver());
		System.out.println("driver = " + connect.getUrl());
		Connection conn = connect.getConnection();
		conn.close();
	}

	public DBProperties(final String path) throws FileNotFoundException {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			File dbConfig = new File(path+'/'+DB_CONFIG);
			this.doc = docBuilder.parse(dbConfig);

			// normalize text representation
			this.doc.getDocumentElement().normalize();
			this.log.fine("Root element of the doc is "
					+ doc.getDocumentElement().getNodeName());

			this.dbProps = new Properties();
			setProperties();
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

	public final Connection getConnection() {
		try {
			Class.forName(getDriver());
			return DriverManager.getConnection(getUrl(), getProperties());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public final String getDriver() {
		return this.doc.getElementsByTagName("driver").item(0).getTextContent();
	}

	public final Properties getProperties() {
		return this.dbProps;
	}

	public final String getPropValue(final String tagName) {
		return this.doc.getElementsByTagName(tagName).item(0).getTextContent();
	}

	public final String getUrl() {
		return this.doc.getElementsByTagName("url").item(0).getTextContent();
	}

	public final void setProperties() {
		String propName;

		// Gets all of the child nodes of the root.
		NodeList propList = this.doc.getDocumentElement().getChildNodes();
		for (int i = 0; i < propList.getLength(); i++) {
			Node propNode = propList.item(i);

			if (propNode.getNodeType() == Node.ELEMENT_NODE) {
				propName = propNode.getNodeName();
				if (!propName.equals("driver") || !propName.equals("url")) {
					this.dbProps.setProperty(propName, getPropValue(propName));
				}
			}
		}
	}
}
