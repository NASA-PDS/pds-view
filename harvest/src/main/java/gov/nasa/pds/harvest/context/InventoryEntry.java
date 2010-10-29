package gov.nasa.pds.harvest.context;

import java.io.File;

public class InventoryEntry {
    private File file;
    private String checksum;
    private String lidvid;

    public InventoryEntry(File file, String checksum, String lidvid) {
        this.file = file;
        this.checksum = checksum;
        this.lidvid = lidvid;
    }

    public File getFile() {
        return file;
    }

    public String getChecksum() {
        return checksum;
    }

    public String getLidvid() {
        return lidvid;
    }
}
