package gov.nasa.pds.report.setup.properties;

import gov.nasa.pds.report.setup.Globals;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;
import org.w3c.dom.Document;

public class DBProperties extends Globals {

	private static final String DB_CONFIG = "database.properties";
	private Properties dbProps;

	private Document doc;

	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public static void main(final String[] args) throws Exception {
		DBProperties connect = new DBProperties("/Users/jpadams/dev/workspace/2010-workspace/report/profile-setup/src/main/resources");
		System.out.println("driver = " + connect.getDriver());
		System.out.println("url = " + connect.getUrl());
		connect.testConnection();
	}

	public DBProperties(final String path) throws IOException {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(CRYPT_PASSWORD);
		this.dbProps = new EncryptableProperties(encryptor);
		this.dbProps.load(new FileInputStream(path+'/'+DB_CONFIG));

	}
	
	public final String getUrl() {
		return this.dbProps.getProperty("datasource.url");
	}
	
	public final String getDriver() {
		return this.dbProps.getProperty("datasource.driver");
	}

	public final String getUsername() {
		return this.dbProps.getProperty("datasource.username");
	}
	
	public final String getPassword() {
		return this.dbProps.getProperty("datasource.password");
	}
	
	/*public final Properties getProperties() {
		return this.dbProps;
	}*/

	public final Connection testConnection() {
		try {
			Class.forName(getDriver());
			Connection conn = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
			System.out.println("Is connected? " + conn.isValid(10));
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/*public DBProperties(final String path) throws FileNotFoundException {
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
	}*/
}
