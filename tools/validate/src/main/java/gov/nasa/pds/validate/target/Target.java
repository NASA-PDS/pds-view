package gov.nasa.pds.validate.target;

import gov.nasa.pds.validate.util.XMLExtractor;

import java.io.File;


public class Target implements TargetType {
    private final static String PRODUCT_TYPE_XPATH =
        "//*[ends-with(name(),'Identification_Area')]/product_class";

    private String file;
    private String type;

    public Target(File file) {
        this(file.toString());
    }

    public Target(String file) {
        this.file = file;
        if(new File(file).isDirectory()) {
            type = DIRECTORY;
        } else {
            try {
                XMLExtractor extractor = new XMLExtractor(file);
                String type = extractor.getValueFromDoc(PRODUCT_TYPE_XPATH);
                if(BUNDLE.equals(type)) {
                    type = BUNDLE;
                } else if(COLLECTION.equals(type)) {
                    type = COLLECTION;
                } else {
                    type = FILE;
                }
            } catch (Exception e) {
                type = FILE;
            }
        }
    }

    public String getFile() {
        return file;
    }

    public String getType() {
        return type;
    }
}
