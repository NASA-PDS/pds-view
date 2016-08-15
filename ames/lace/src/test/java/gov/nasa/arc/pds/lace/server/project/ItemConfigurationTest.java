package gov.nasa.arc.pds.lace.server.project;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.pds.lace.shared.project.ProjectItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ItemConfigurationTest {

	private static final SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	static {
		ISO_8601_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	private File tempDir;
	private File configDir;

	@BeforeMethod
	public void init() {
		String tempDirName = getClass().getSimpleName() + "-temp";
		tempDir = new File(new File(System.getProperty("java.io.tmpdir")), tempDirName);
		if (tempDir.exists()) {
			recursivelyDelete(tempDir);
		}
		tempDir.mkdir();

		configDir = new File(tempDir, "item");
	}

	@AfterMethod
	public void cleanup() {
		recursivelyDelete(tempDir);
	}

	private void recursivelyDelete(final File f) {
		if (f.isDirectory()) {
			for (File child : f.listFiles()) {
				recursivelyDelete(child);
			}
		}

		f.delete();
	}

	@Test
	public void testGettersSetters() throws FileNotFoundException, IOException {
		ItemConfiguration config = new ItemConfiguration(configDir, true);

		config.setItemType(ProjectItem.Type.FOLDER);
		config.setItemName("one");
		assertEquals(config.getItemType(), ProjectItem.Type.FOLDER);
		assertEquals(config.getItemName(), "one");

		config.setItemType(ProjectItem.Type.LABEL);
		assertEquals(config.getItemType(), ProjectItem.Type.LABEL);

		config.setItemName("two");
		assertEquals(config.getItemName(), "two");
	}

	@Test
	public void testSaveConfiguration() throws FileNotFoundException, IOException {
		ItemConfiguration config = new ItemConfiguration(configDir, true);
		config.setItemType(ProjectItem.Type.FOLDER);
		config.setItemName("one");
		config.saveConfiguration();

		assertTrue(configDir.exists());
		File configFile = new File(configDir, "item.properties");
		assertTrue(configFile.exists());
	}

	@Test
	public void testLoadConfiguration() throws FileNotFoundException, IOException {
		ItemConfiguration config = new ItemConfiguration(configDir, true);
		config.setItemType(ProjectItem.Type.FOLDER);
		config.setItemName("one");
		config.saveConfiguration();

		config = new ItemConfiguration(configDir);
		assertEquals(config.getItemType(), ProjectItem.Type.FOLDER);
		assertEquals(config.getItemName(), "one");

		config.setItemName("two");
		config.saveConfiguration();

		config = new ItemConfiguration(configDir);
		assertEquals(config.getItemName(), "two");
	}

	@Test
	public void testLastUpdatedNewConfiguration() throws FileNotFoundException, IOException, ParseException {
		ItemConfiguration config = new ItemConfiguration(configDir, true);
		long now = System.currentTimeMillis();

		Date date = config.getLastUpdated();
		assertTrue(Math.abs(date.getTime() - now) < 1000);

		Date epoch = ISO_8601_FORMAT.parse("1970-01-01T00:00:00-0000");
		config.setLastUpdated(epoch);
		assertEquals(config.getLastUpdated(), epoch);
	}

	@Test
	public void testLastUpdatedNoValue() throws FileNotFoundException, IOException {
		File configFile = new File(configDir, "item.properties");
		Properties props = new Properties();
		configDir.mkdir();
		props.store(new FileOutputStream(configFile), "Unit tests");

		ItemConfiguration config = new ItemConfiguration(configDir);
		Date date = config.getLastUpdated();
		assertEquals(date.getTime(), configFile.lastModified());
	}

	@Test
	public void testLastUpdatedWithValue() throws FileNotFoundException, IOException, ParseException {
		ItemConfiguration config = new ItemConfiguration(configDir, true);
		Date epoch = ISO_8601_FORMAT.parse("1970-01-01T00:00:00-0000");
		config.setLastUpdated(epoch);
		config.saveConfiguration();

		config = new ItemConfiguration(configDir);
		assertEquals(config.getLastUpdated(), epoch);
	}

	@Test
	public void testAttributes() throws FileNotFoundException, IOException, ParseException {
		ItemConfiguration config = new ItemConfiguration(configDir, true);
		assertNull(config.getItemAttribute("x"));

		config.setItemAttribute("x", "value");
		config.saveConfiguration();

		config = new ItemConfiguration(configDir);
		assertEquals(config.getItemAttribute("x"), "value");

		config.removeItemAttribute("x");
		config.saveConfiguration();

		config = new ItemConfiguration(configDir);
		assertNull(config.getItemAttribute("x"));
	}

}
