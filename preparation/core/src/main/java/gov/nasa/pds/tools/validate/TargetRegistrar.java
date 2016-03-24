package gov.nasa.pds.tools.validate;

import gov.nasa.pds.tools.validate.ValidationTarget;

import java.util.Collection;

/**
 * Defines an interface for keeping track of validation targets
 * and their attributes.
 *
 * <p>The registrar enables answering these questions:</p>
 * <ul>
 *   <li>Is every target either a label or have a reference from a label?</li>
 *   <li>Does every referenced identifier (e.g., PDS4 LID) have a definition
 *   in some target?</li>
 *   <li>Is any identifier defined twice?</li>
 * </ul>
 */
public interface TargetRegistrar {

	/**
	 * Gets the root location.
	 *
	 * @return the root location
	 */
	ValidationTarget getRoot();

    /**
     * Indicates that a target was discovered in the subtree
     * being validated.
     *
     * @param parentLocation the parent target location, or null, if this is the root target
     * @param type the target type
     * @param location the target location
     */
    void addTarget(String parentLocation, TargetType type, String location);

    /**
     * Gets the locations of children of a given target.
     *
     * @param parent the parent target
     * @return a collection of child targets
     */
    Collection<ValidationTarget> getChildTargets(ValidationTarget parent);

    /**
     * Tests whether a target exists for a location.
     *
     * @param targetLocation the target location
     * @return true, if a target has been added for that location, false otherwise
     */
    boolean hasTarget(String targetLocation);

    /**
     * Gets the number of targets encountered so far for a target type.
     *
     * @param type the target type
     * @return the target count for the given type
     */
    int getTargetCount(TargetType type);

    /**
     * Indicates whether a target is a label.
     *
     * @param location the target location
     * @param isLabel true, if the target is a label
     */
    void setTargetIsLabel(String location, boolean isLabel);

    /**
     * Gets the number of labels encountered so far.
     *
     * @return the label count
     */
    int getLabelCount();

    /**
     * Sets an identifier for uniquely identifying the target.
     *
     * @param location the target location
     * @param identifier the target identifier
     */
    void setTargetIdentifier(String location, String identifier);

    /**
     * Adds a reference from a label to a target.
     *
     * @param referenceLocation the label location
     * @param targetLocation the target location referred to in the label
     */
    void addTargetReference(String referenceLocation, String targetLocation);

    /**
     * Tests whether a target was referenced.
     *
     * @param location the target location
     * @return true, if the target was referenced
     */
    boolean isTargetReferenced(String location);

    /**
     * Adds a reference to a target identifier.
     *
     * @param referenceLocation the location of the reference
     * @param identifier the target identifier being referenced
     */
    void addIdentifierReference(String referenceLocation, String identifier);

    /**
     * Tests whether an identifier was referenced.
     *
     * @param identifier the identifier
     * @return true, if the identifier was referenced
     */
    boolean isIdentifierReferenced(String identifier);

    /**
     * Gets the location where an identifier was defined.
     *
     * @param identifier the identifier
     * @return the location where it was defined, or null if not defined
     */
    String getTargetForIdentifier(String identifier);

    /**
     * Gets a collection of target locations that are never referenced.
     *
     * @return a collection of unreferenced targets
     */
    Collection<String> getUnreferencedTargets();

    /**
     * Gets a collection of identifiers that are defined but never
     * referenced.
     *
     * @return a collection of unreferenced identifiers
     */
    Collection<String> getUnreferencedIdentifiers();

    /**
     * Gets a collection of identifiers that are referenced but not defined.
     *
     * @return a collection of dangling identifier references
     */
    Collection<IdentifierReference> getDanglingReferences();

}
