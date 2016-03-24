package gov.nasa.pds.tools.validate;

import gov.nasa.pds.tools.validate.TargetType;

import java.io.File;

/**
 * Represents a location within a validation subtree that can
 * have errors reported against it.
 */
public class ValidationTarget implements Comparable<ValidationTarget> {

    private TargetType type;
    private String name;
    private String location;
    private boolean targetIsLabel;
    private String identifier;

    private int knownHashCode;

    /**
     * Creates a new instance.
     *
     * @param target the target file or directory
     */
    public ValidationTarget(File target) {
	type = target.isDirectory() ? TargetType.FOLDER : TargetType.FILE;
	location = target.getAbsolutePath();
	name = target.getName();
    }

    /**
     * Creates an instance with a given location.
     *
     * @param location the location
     */
    public ValidationTarget(String location, TargetType type) {
	this.location = location;
	this.type = type;

	int slashPos = location.lastIndexOf('/');
	if (slashPos < 0) {
	    slashPos = location.lastIndexOf('\\');
	}

	if (slashPos < 0) {
	    name = location;
	} else {
	    name = location.substring(slashPos + 1);
	}
    }

    /**
     * Gets the target type.
     *
     * @return the target type
     */
    public TargetType getType() {
	return type;
    }

    /**
     * Sets the target type.
     *
     * @param type the new target type
     */
    public void setType(TargetType type) {
	this.type = type;
    }

    /**
     * Gets the target location.
     *
     * @return the location
     */
    public String getLocation() {
	return location;
    }

    /**
     * Sets the target location.
     *
     * @param location the new location
     */
    public void setLocation(String location) {
	this.location = location;
    }

    /**
     * Gets the name of the target.
     *
     * @return the target name
     */
    public String getName() {
	return name;
    }

    /**
     * Sets the target name.
     *
     * @param newName the new target name
     */
    public void setName(String newName) {
	name = newName;
    }

    /**
     * Tests whether the target is a label.
     *
     * @return true, if the target is a label
     */
    public boolean isLabel() {
	return targetIsLabel;
    }

    /**
     * Sets whether the target is a label.
     *
     * @param flag true, if the target is a label
     */
    public void setLabel(boolean flag) {
	targetIsLabel = flag;
    }

    /**
     * Gets the identification string for this target.
     *
     * @return the identifier
     */
    public String getIdentifier() {
	return identifier;
    }

    /**
     * Sets the identification string for this target.
     *
     * @param identifier the identifier
     */
    public void setIdentifier(String identifier) {
	this.identifier = identifier;
    }

    @Override
    public int compareTo(ValidationTarget other) {
        return location.compareTo(other.getLocation());
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        if (knownHashCode == 0) {
            String combined = location + ":" + (type==null ? "" : type.toString());
            knownHashCode = combined.hashCode();
        }

        return knownHashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ValidationTarget)) {
            return false;
        }

        ValidationTarget other = (ValidationTarget) obj;
        return location.equals(other.location);
    }

}
