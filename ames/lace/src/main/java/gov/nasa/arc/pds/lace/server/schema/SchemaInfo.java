package gov.nasa.arc.pds.lace.server.schema;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements an object holding information about the schema for
 * a single standard version, such as PDS4 1.1.0.0. Setter methods
 * for this class implement a small DSL for configuring a schema
 * standard.
 */
public class SchemaInfo {

	String namespaceURI;
	String description;
	List<String> schemaPaths = new ArrayList<String>();
	String schematronPath;
	
	/**
	 * Gets the namespace URI for this schema.
	 * 
	 * @return the namespace URI
	 */
	public String getNamespaceURI() {
		return namespaceURI;
	}
	
	/**
	 * Sets the namespace URI for this schema standard.
	 * 
	 * @param newURI the new namespace URI
	 * @return the schema info object
	 */
	public SchemaInfo namespaceURI(String newURI) {
		namespaceURI = newURI;
		return this;
	}

	/**
	 * Gets the description of this schema object.
	 * 
	 * @return the description string
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description of this schema object.
	 * 
	 * @param newDescription the new description
	 * @return the schema info object
	 */
	public SchemaInfo description(String newDescription) {
		description = newDescription;
		return this;
	}

	/**
	 * Gets the paths to XML schema files for this schema standard.
	 * 
	 * @return an array of file paths
	 */
	public String[] getSchemaPaths() {
		return schemaPaths.toArray(new String[schemaPaths.size()]);
	}
	
	/**
	 * Adds a path to an XML schema file.
	 * 
	 * @param path the schema file path
	 * @return the schema info object
	 */
	public SchemaInfo schemaPath(String path) {
		schemaPaths.add(path);
		return this;
	}

	/**
	 * Gets the path to a Schematron rules files for this schema standard.
	 * 
	 * @return a Schematron file path, or null if no Schematron file
	 */
	public String getSchematronPath() {
		return schematronPath;
	}
	
	/**
	 * Adds a path to a Schematron rules file for this schema standard.
	 * 
	 * @param path the Schematron rules file path
	 * @return the schema info object
	 */
	public SchemaInfo schematronPath(String path) {
		schematronPath = path;
		return this;
	}
	
}
