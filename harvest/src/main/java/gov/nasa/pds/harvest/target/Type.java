package gov.nasa.pds.harvest.target;

/**
 * Class representing the different target types
 * in the Harvest Tool.
 *
 * @author mcayanan
 *
 */
public enum Type {
    /** Bundle file. */
    BUNDLE("Bundle"),

    /** Collection file. */
    COLLECTION("Collection"),

    /** PDS3 Directory file. */
    PDS3_DIRECTORY("Pds3Directory"),

    /** PDS4 Directory file. */
    PDS4_DIRECTORY("Pds4Directory");

    private final String name;

    /**
     * Constructor.
     *
     * @param name The name of the type.
     */
    private Type(final String name) {
        this.name = name;
    }

    /**
     * Get the name of the target type.
     *
     * @return The name.
     */
    public String getName() {
      return this.name;
    }
}
