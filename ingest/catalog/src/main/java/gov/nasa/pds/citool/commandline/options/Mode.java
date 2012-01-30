package gov.nasa.pds.citool.commandline.options;

public enum Mode {
    COMPARE("COMPARE"), INGEST("INGEST"), VALIDATE("VALIDATE");

    private final String name;

    private Mode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
