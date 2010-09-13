package gov.nasa.pds.harvest.context;

public class ReferenceEntry {
    private String logicalID;
    private String version;
    private String associationType;
    private String objectType;

    private boolean hasVersion;

    public ReferenceEntry() {
        logicalID = null;
        version = null;
        associationType = null;
        objectType = null;

        hasVersion = false;
    }

    public String getLogicalID() {
        return logicalID;
    }

    public void setLogicalID(String id) {
        logicalID = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String ver) {
        version = ver;
        hasVersion = true;
    }

    public boolean hasVersion() {
        return hasVersion;
    }

    public String getAssociationType() {
        return associationType;
    }

    public void setAssociationType(String type) {
        associationType = type;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String type) {
        objectType = type;
    }
}
