// Copyright 2006-2010, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
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
