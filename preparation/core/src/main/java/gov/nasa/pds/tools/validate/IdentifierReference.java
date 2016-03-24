package gov.nasa.pds.tools.validate;

/**
 * Defines a reference from a location to an identifier.
 */
public class IdentifierReference implements Comparable<IdentifierReference> {

    private final String referenceLocation;
    private final String identifier;
    private int knownHashCode;

    /**
     * Creates a new instance with a given location for the target referring to
     * an identifier.
     *
     * @param referenceLocation the location of the target containing the reference
     * @param identifier the identifier referred to
     */
    public IdentifierReference(String referenceLocation, String identifier) {
        this.referenceLocation = referenceLocation;
        this.identifier = identifier;
    }

    /**
     * Gets the location of the target containing the reference.
     *
     * @return the target location containing the reference
     */
    public String getReferenceLocation() {
        return referenceLocation;
    }

    /**
     * Gets the identifier referenced.
     *
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public int compareTo(IdentifierReference other) {
        int result = referenceLocation.compareTo(other.referenceLocation);
        if (result != 0) {
            return result;
        } else {
            return identifier.compareTo(other.identifier);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IdentifierReference)) {
            return false;
        }

        IdentifierReference other = (IdentifierReference) obj;
        return identifier.equals(other.identifier) && referenceLocation.equals(other.referenceLocation);
    }

    @Override
    public int hashCode() {
        if (knownHashCode == 0) {
            String combined = identifier + ":" + referenceLocation;
            knownHashCode =combined.hashCode();
        }

        return knownHashCode;
    }

}
