package gov.nasa.pds.harvest.context;

import java.io.File;

public class InventoryEntry {
    private File file;
    private String checksum;

    public InventoryEntry(File file, String checksum) {
        this.file = file;
        this.checksum = checksum;
    }

    public File getFile() {
        return file;
    }

    public String getChecksum() {
        return checksum;
    }

}
