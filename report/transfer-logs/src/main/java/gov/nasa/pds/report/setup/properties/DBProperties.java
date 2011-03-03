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
	
	private Properties _dbProps;

	private Document _doc;

	private Logger _log = Logger.getLogger(this.getClass().getName());
	
	public static void main(String[] args) throws Exception {
		DBProperties connect = new DBProperties("/Users/jpadams/dev/workspace/2010-workspace/report/transfer-logs/src/main/resources");
		System.out.println("driver = " + connect.getDriver());
		System.out.println("driver = " + connect.getUrl());
		Connection conn = connect.getConnection();
		conn.close();
	}

	public DBProperties(String path) throws FileNotFoundException {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			File dbConfig = new File(path+'/'+DB_CONFIG);
			this._doc = docBuilder.parse(dbConfig);

			// normalize text representation
			this._doc.getDocumentElement().normalize();
			this._log.fine("Root element of the doc is "
					+ _doc.getDocumentElement().getNodeName());

			this._dbProps = new Properties();
			setProperties();
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

	public Connection getConnection() {
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

	public String getDriver() {
		return this._doc.getElementsByTagName("driver").item(0).getTextContent();
	}

	public Properties getProperties() {
		return this._dbProps;
	}

	public String getPropValue(String tagName) {
		return this._doc.getElementsByTagName(tagName).item(0).getTextContent();
	}

	public String getUrl() {
		return this._doc.getElementsByTagName("url").item(0).getTextContent();
	}

	public void setProperties() {
		String propName;

		// Gets all of the child nodes of the root.
		NodeList propList = this._doc.getDocumentElement().getChildNodes();
		for (int i = 0; i < propList.getLength(); i++) {
			Node propNode = propList.item(i);

			if (propNode.getNodeType() == Node.ELEMENT_NODE) {
				propName = propNode.getNodeName();
				if (!propName.equals("driver") || !propName.equals("url")) {
					this._dbProps.setProperty(propName, getPropValue(propName));
				}
			}
		}
	}
}
