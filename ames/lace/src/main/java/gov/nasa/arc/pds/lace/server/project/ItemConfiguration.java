package gov.nasa.arc.pds.lace.server.project;

import gov.nasa.arc.pds.lace.shared.project.ProjectItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;


/**
 * Holds the settings for a project item, either a folder or a label.
 */
public class ItemConfiguration {

	private static final SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	static {
		ISO_8601_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	private static final String CONFIGURATION_FILE = "item.properties";

	private static final String NAME_PROPERTY = "item.name";
	private static final String TYPE_PROPERTY = "item.type";
	private static final String LAST_UPDATED_PROPERTY = "item.last-updated";
	private static final String DIGEST_PROPERTY = "item.digest";

	private static final String ATTRIBUTE_PREFIX = "item.attr.";

	private static final String DEFAULT_NAME = "Untitled";

	private Properties properties;
	private File configurationFile;

	/**
	 * Loads an item configuration. Each item is represented by a
	 * directory. If the item is a folder, it has subdirectories
	 * which are themselves items. Otherwise it represents a
	 * single label. The type of an item and its name is stored in the
	 * properties file in its directory.
	 *
	 * @param dir the file for the item directory
	 * @throws FileNotFoundException if the configuration directory cannot be found
	 * @throws IOException if there is an error reading the configuration file
	 */
	public ItemConfiguration(File dir) throws FileNotFoundException, IOException {
		this(dir, false);
	}

	/**
	 * Creates a new configuration for the given directory. Optionally
	 * loads existing configuration information. This constructor has
	 * default scope since it should only be called by the implementation
	 * of the project manager for creating an empty configuration.
	 *
	 * @param dir the file for the item directory
	 * @param isNew true, if existing configuration information should be read
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	ItemConfiguration(File dir, boolean isNew) throws FileNotFoundException, IOException {
		if (!isNew && (!dir.isDirectory() || !dir.canRead())) {
			throw new IllegalArgumentException("Directory does not represent a project item: " + dir.getAbsolutePath());
		}

		configurationFile = new File(dir, CONFIGURATION_FILE);
		properties = new Properties();
		if (isNew) {
			setLastUpdated(new Date());
		} else {
			properties.load(new FileInputStream(configurationFile));
			if (!properties.containsKey(LAST_UPDATED_PROPERTY)) {
				setLastUpdated(new Date(configurationFile.lastModified()));
			}
		}
	}

	/**
	 * Saves the project configuration.
	 *
	 * @throws FileNotFoundException if the configuration file path cannot be found
	 * @throws IOException if there is an exception writing the configuration
	 */
	public void saveConfiguration() throws FileNotFoundException, IOException {
		String comment = "Properties saved at " + (new Date()).toString();
		configurationFile.getParentFile().mkdirs();
		configurationFile.getParentFile().setWritable(true, false);

		properties.store(new FileOutputStream(configurationFile), comment);
		configurationFile.setWritable(true, false);
	}

	/**
	 * Gets the type of a project item.
	 *
	 * @return the item type
	 */
	public ProjectItem.Type getItemType() {
		String typeStr = properties.getProperty(TYPE_PROPERTY, ProjectItem.Type.LABEL.toString());
		return ProjectItem.Type.valueOf(typeStr);
	}

	/**
	 * Sets the type of the project item. This method has
	 * default scope because it should only be called by
	 * the project manager implementation.
	 *
	 * @param newType the new item type
	 */
	void setItemType(ProjectItem.Type newType) {
		properties.setProperty(TYPE_PROPERTY, newType.toString());
	}

	/**
	 * Gets the name of a project item.
	 *
	 * @return the item name
	 */
	public String getItemName() {
		return properties.getProperty(NAME_PROPERTY, DEFAULT_NAME);
	}

	/**
	 * Sets the name of a project item.
	 *
	 * @param newName the new name
	 */
	public void setItemName(String newName) {
		properties.setProperty(NAME_PROPERTY, newName);
	}

	/**
	 * Gets the last updated date.
	 *
	 * @return the last updated date
	 */
	public Date getLastUpdated() {
		String dateStr = properties.getProperty(LAST_UPDATED_PROPERTY);
		if (dateStr == null) {
			return new Date();
		} else {
			try {
				return ISO_8601_FORMAT.parse(dateStr);
			} catch (ParseException e) {
				// Ignore, but return current date.
				return new Date();
			}
		}
	}

	/**
	 * Sets the last updated date.
	 *
	 * @param lastUpdated the last updated date
	 */
	public void setLastUpdated(Date lastUpdated) {
		String dateStr = ISO_8601_FORMAT.format(lastUpdated);
		properties.setProperty(LAST_UPDATED_PROPERTY, dateStr);
	}

	/**
	 * Gets the digest value of an item. The contents of the digest
	 * are defined by the user of the item configuration.
	 *
	 * @return the digest
	 */
	public String getDigest() {
		return properties.getProperty(DIGEST_PROPERTY);
	}

	/**
	 * Sets the digest value of an item. The contents of the digest
	 * are defined by the user of the item configuration.
	 *
	 * @param newDigest the new digest value
	 */
	public void setDigest(String newDigest) {
		properties.setProperty(DIGEST_PROPERTY, newDigest);
	}

	/**
	 * Gets an item attribute with a given key.
	 *
	 * @param key the attribute key
	 * @return the attribute value, or null if no such attribute
	 */
	public String getItemAttribute(String key) {
		return properties.getProperty(ATTRIBUTE_PREFIX + key);
	}

	/**
	 * Sets an item attribute.
	 *
	 * @param key the attribute key
	 * @param value the new value
	 */
	public void setItemAttribute(String key, String value) {
		properties.setProperty(ATTRIBUTE_PREFIX + key, value);
	}

	/**
	 * Removes an item attribute.
	 *
	 * @param key the attribute key
	 */
	public void removeItemAttribute(String key) {
		properties.remove(ATTRIBUTE_PREFIX + key);
	}

}
