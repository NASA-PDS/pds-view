package gov.nasa.pds.report.update.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

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
		this.dbProps.load(new FileInputStream(path + '/' + ENV_FILE));

	}

	public EnvProperties() throws IOException {
		this.dbProps = new Properties();
		this.dbProps.load(new FileInputStream(ENV_FILE));

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
		// EnvProperties envProps = new
		// EnvProperties("/Users/jpadams/dev/workspace/2010-workspace/report/profile-setup/src/main/resources/conf");
		EnvProperties envProps = new EnvProperties();
		System.out.println("logDest = " + envProps.getSawmillLogHome());
		System.out.println("saw home = " + envProps.getSawmillHome());
		System.out.println("prof home = " + envProps.getSawmillProfileHome());
		// Connection conn = connect.getConnection();
		// conn.close();
	}

}
