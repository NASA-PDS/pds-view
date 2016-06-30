package gov.nasa.arc.pds.lace.server.schema;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements an object that holds the default schema file configuration.
 */
public class SchemaDefaults {

	private static final String SCHEMA_ROOT = "schema";

	private List<String> schemaPaths = new ArrayList<String>();
	
	/**
	 * Removes any schema defaults that have been added.
	 */
	public void clearDefaults() {
		schemaPaths.clear();
	}
	
	/**
	 * Adds another schema file to the default configuration.
	 * 
	 * @param path the path to the new schema file, relative to the schema root
	 */
	public void addSchemaFile(String path) {
		schemaPaths.add(path);
	}
	
	/**
	 * Gets the set of configured schema files.
	 * 
	 * @return an array of files
	 */
	public File[] getSchemaFiles() {
		List<File> result = new ArrayList<File>();
		
		for (URI uri : getSchemaURIs()) {
			result.add(new File(uri));
		}
		
		return result.toArray(new File[result.size()]);
	}
	
	/**
	 * Gets the set of configured schema files, as URIs.
	 * 
	 * @return an array of URIs
	 */
	public URI[] getSchemaURIs() {
		List<URI> result = new ArrayList<URI>();
		for (String path : schemaPaths) {
			result.add(getSchemaURI(path));
		}
		
		return result.toArray(new URI[result.size()]);
	}
	
	private URI getSchemaURI(String schemaPath) {
		String resourcePath = "/" + SCHEMA_ROOT + "/" + schemaPath;
		URL resourceURL = getClass().getResource(resourcePath);
		URI uri = null;
		
		try {
			uri = resourceURL.toURI();
		} catch (URISyntaxException e) {
			// ignore - cannot happen, since Java is generating the URL and URI
		} catch (NullPointerException e) {
			System.err.println("Cannot find resource in classpath: " + resourcePath);
		}
		
		return uri;
	}
	
}
