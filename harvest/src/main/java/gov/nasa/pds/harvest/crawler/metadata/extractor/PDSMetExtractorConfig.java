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
package gov.nasa.pds.harvest.crawler.metadata.extractor;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.jpl.oodt.cas.metadata.MetExtractorConfig;
import gov.nasa.pds.harvest.policy.CandidateProduct;

public class PDSMetExtractorConfig implements MetExtractorConfig {
    private List<CandidateProduct> products;

    public PDSMetExtractorConfig(List<CandidateProduct> products) {
        this.products = new ArrayList<CandidateProduct>();
        this.products.addAll(products);
    }

    public List<String> getMetXPaths(String objectType) {
        for(CandidateProduct cp : this.products) {
            if(cp.getObjectType().equalsIgnoreCase(objectType)) {
                return cp.getMetadata().getXPath();
            }
        }
        return null;
    }

    public boolean hasObjectType(String objectType) {
        for(CandidateProduct cp : this.products) {
            if(cp.getObjectType().equalsIgnoreCase(objectType)) {
                return true;
            }
        }
        return false;
    }
}
