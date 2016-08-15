package gov.nasa.arc.pds.lace.shared.project;

import java.io.Serializable;
import java.util.Date;

/**
 * Holds the information about an item in the project area for a single
 * user, either a label or a folder.
 */
public class ProjectItem implements Serializable, Comparable<ProjectItem> {

	private static final long serialVersionUID = 1L;

	/**
	 * Defines the types of items within the project area for a user.
	 */
	public static enum Type {
		/** The item is a folder. */
		FOLDER,
		
		/** The item is a label. */
		LABEL;
	}

	private Type type;
	private String name;
	private Date lastUpdated;
	private String location;
	
	/**
	 * Gets the type of a project item.
	 * 
	 * @return the item type
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * Sets the type of a projec item.
	 * 
	 * @param newType the new type
	 */
	public void setType(Type newType) {
		type = newType;
	}
	
	/**
	 * Gets the name of a project item.
	 * 
	 * @return the item
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of a project item.
	 * 
	 * @param newName the new name
	 */
	public void setName(String newName) {
		name = newName;
	}
	
	/**
	 * Gets the location of a project item. The location has no meaning to
	 * the client, but is used by the server to find the persistent form
	 * of the project item.
	 * @return
	 */
	public String getLocation() {
		return location;
	}
	
	/**
	 * Gets the last updated date for the project item.
	 * 
	 * @return the last updated date
	 */
	public Date getLastUpdated() {
		return lastUpdated;
	}
	
	/**
	 * Sets the last updated date for the project item.
	 * 
	 * @param d the last updated date
	 */
	public void setLastUpated(Date d) {
		lastUpdated = d;
	}
	
	/**
	 * Sets the location of the project item.
	 * 
	 * @param newLocation the new location
	 */
	public void setLocation(String newLocation) {
		location = newLocation;
	}

	@Override
	public int compareTo(ProjectItem other) {
		return name.compareToIgnoreCase(other.name);
	}
	
}
