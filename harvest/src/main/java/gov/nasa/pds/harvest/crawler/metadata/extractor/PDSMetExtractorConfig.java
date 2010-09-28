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

import java.util.Iterator;
import java.util.List;

import gov.nasa.jpl.oodt.cas.metadata.MetExtractorConfig;
import gov.nasa.pds.harvest.policy.Candidate;
import gov.nasa.pds.harvest.policy.Namespace;
import gov.nasa.pds.harvest.policy.ProductMetadata;
import gov.nasa.pds.harvest.util.PDSNamespaceContext;

/**
 * Configuration class for extracting metadata from
 * PDS4 data products
 *
 * @author mcayanan
 *
 */
public class PDSMetExtractorConfig implements MetExtractorConfig {
    private Candidate candidates;

    /**
     * Default contstructor
     *
     * @param candidates A class that contains what product types
     * to extract and which metadata fields to get from those
     * product types.
     */
    public PDSMetExtractorConfig(Candidate candidates) {
        this.candidates = candidates;
    }

    /**
     * Gets XPath expressions for an object type
     *
     * @param objectType The PDS object type.
     *
     * @return A list of XPath expressions based on the given object type.
     */
    public List<String> getMetXPaths(String objectType) {
        for(ProductMetadata p : this.candidates.getProductMetadata()) {
            if(p.getObjectType().equalsIgnoreCase(objectType)) {
                return p.getXPath();
            }
        }
        return null;
    }

    /**
     * Gets a NamespaceContext for use with resolving namespaces
     * in an XML document.
     *
     * @return a PDSNamespaceContext object
     */
    public PDSNamespaceContext getNamespaceContext() {
        String defaultNamespace = null;
        for(Iterator<Namespace> i = this.candidates.getNamespace().iterator();
            i.hasNext() && (defaultNamespace == null);) {
            Namespace n = i.next();
            if(n.isDefault()) {
                defaultNamespace = n.getUri();
            }
        }
        return new PDSNamespaceContext(
                this.candidates.getNamespace(), defaultNamespace);
    }

    /**
     * Determines whether an object type exists in the configuration class.
     *
     * @param objectType The object type to search.
     *
     * @return true if the supplied object type was found.
     */
    public boolean hasObjectType(String objectType) {
        for(ProductMetadata p : this.candidates.getProductMetadata()) {
            if(p.getObjectType().equalsIgnoreCase(objectType)) {
                return true;
            }
        }
        return false;
    }
}
