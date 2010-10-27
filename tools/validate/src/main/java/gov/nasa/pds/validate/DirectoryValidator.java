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
package gov.nasa.pds.validate;

import gov.nasa.pds.validate.crawler.DirectoryCrawler;
import gov.nasa.pds.validate.report.Report;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

public class DirectoryValidator extends Validator {
    private boolean recurse;
    private List<String> fileFilters;

    public DirectoryValidator(Report report)
    throws SAXException {
        super(report);
        recurse = true;
        fileFilters = new ArrayList<String>();
    }

    public void setRecurse(boolean value) {
        recurse = value;
    }

    public void setFileFilters(List<String> filters) {
        fileFilters = filters;
    }

    public void validate(File directory) throws SAXException, IOException {
        DirectoryCrawler crawler = new DirectoryCrawler();
        List<File> targets = crawler.crawl(directory, recurse, fileFilters);
        for(File target : targets) {
            if(target.isDirectory()) {
                validate(target);
            } else {
                FileValidator fv = new FileValidator(report);
                fv.validate(target);
            }
        }
    }
}
