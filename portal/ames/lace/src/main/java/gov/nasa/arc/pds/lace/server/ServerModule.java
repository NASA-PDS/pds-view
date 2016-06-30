package gov.nasa.arc.pds.lace.server;

import gov.nasa.arc.pds.lace.server.schema.SchemaDefaults;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Implements a server-side Guice module for the web application.
 */
public class ServerModule extends AbstractModule {

	private static final Logger LOG = LoggerFactory.getLogger(ServerModule.class);

	private String dataRootPath;

	/**
	 * Creates a new server Guice module with a given physical
	 * path to the root directory in which to store data.
	 *
	 * @param dataRootPath the path to the data root directory
	 */
	public ServerModule(String dataRootPath) {
		this.dataRootPath = dataRootPath;
	}

	@Override
	protected void configure() {
		Properties props = new Properties();
		try {
			props.load(getClass().getResourceAsStream("/lace.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Names.bindProperties(binder(), props);

		IDGenerator idFactory = new IDGenerator();
		bind(IDGenerator.class).toInstance(idFactory);

		// Make all created files writable so they can be manipulated by scripts
		// not running as the Tomcat user.
		File dataRoot = new File(dataRootPath);

		ServerConfiguration config = new ServerConfiguration();
		config.setDataRoot(dataRoot);
		bind(ServerConfiguration.class).toInstance(config);

		createDirectory(config.getProjectRoot());
		createDirectory(config.getUploadRoot());

		makeFilesWritable(dataRoot);

		SchemaDefaults schemaDefaults = new SchemaDefaults();
		schemaDefaults.addSchemaFile("pds4/1301/PDS4_PDS_1301.xsd"); // PDS4 1.3.0.1 schema
		schemaDefaults.addSchemaFile("pds4/1301/PDS4_PDS_1301.sch"); // PDS4 1.3.0.1 Schematron
		schemaDefaults.addSchemaFile("spase/2.2.3/spase-2_2_3.xsd"); // SPASE 2.2.3
		schemaDefaults.addSchemaFile("pds4/0300a/PDS4_OPS_0300a.xsd"); // PDS4 0.3.0.0.a schema
		schemaDefaults.addSchemaFile("pds4/0300a/PDS4_OPS_0300a.sch"); // PDS4 0.3.0.0.a Schematron

		schemaDefaults.addSchemaFile("pds4/PDS4_DISP_1100.sch"); // Display 1.1.0.0
		schemaDefaults.addSchemaFile("pds4/PDS4_DISP_1100.xsd"); //
		schemaDefaults.addSchemaFile("pds4/PDS4_IMG_1100.sch"); // Imaging 1.1.0.0
		schemaDefaults.addSchemaFile("pds4/PDS4_IMG_1100.xsd"); //
		schemaDefaults.addSchemaFile("pds4/PDS4_SP_1100.sch"); // Spectra 1.1.0.0
		schemaDefaults.addSchemaFile("pds4/PDS4_SP_1100.xsd"); //
		schemaDefaults.addSchemaFile("pds4/pds4_rings_1100.sch"); // Rings 1.1.0.0
		schemaDefaults.addSchemaFile("pds4/pds4_rings_1100.xsd"); //

		bind(SchemaDefaults.class).toInstance(schemaDefaults);
	}

	private void createDirectory(File dir) {
		dir.mkdirs();
		dir.setWritable(true, false);
	}

	private void makeFilesWritable(File f) {
		LOG.debug("Making file writable: {}", f.getAbsolutePath());
		f.setWritable(true, false);

		if (f.isDirectory()) {
			for (File child : f.listFiles()) {
				if (child.getParentFile().equals(f)) {
					makeFilesWritable(child);
				}
			}
		}
	}

}
