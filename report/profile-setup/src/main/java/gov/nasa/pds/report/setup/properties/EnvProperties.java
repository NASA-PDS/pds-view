package gov.nasa.pds.report.setup.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class EnvProperties {

	private static final String ENV_FILE = "environment.properties";
	private Logger log = Logger.getLogger(this.getClass().getName());
	private Properties dbProps;

	/**
	 * 
	 * @param path
	 * @throws IOException
	 */
	public EnvProperties(final String path) throws IOException {
		this.dbProps = new Properties();
		this.dbProps.load(new FileInputStream(path+'/'+ENV_FILE));

	}

	/**
	 * 
	 * @return
	 */
	public final String getSawmillLogHome() {
		return this.dbProps.getProperty("sawmill.log.home");
	}

	/**
	 * 
	 * @return
	 */
	public final String getSawmillProfileHome() {
		return this.dbProps.getProperty("sawmill.profile.home");
	}

	/**
	 * 
	 * @return
	 */
	public final String getSawmillHome() {
		return this.dbProps.getProperty("sawmill.home");
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		EnvProperties envProps = new EnvProperties("/Users/jpadams/dev/workspace/2010-workspace/report/profile-setup/src/main/resources");
		System.out.println("logDest = " + envProps.getSawmillLogHome());
		System.out.println("saw home = " + envProps.getSawmillHome());
		System.out.println("prof home = " + envProps.getSawmillProfileHome());
		//Connection conn = connect.getConnection();
		//conn.close();
	}

}
