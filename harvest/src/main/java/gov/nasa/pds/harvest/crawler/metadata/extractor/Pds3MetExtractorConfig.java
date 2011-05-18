// Copyright 2006-2011, by the California Institute of Technology.
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

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import gov.nasa.jpl.oodt.cas.metadata.MetExtractorConfig;
import gov.nasa.pds.harvest.policy.Association;
import gov.nasa.pds.harvest.policy.Pds3ProductMetadata;

public class Pds3MetExtractorConfig implements MetExtractorConfig {
  private List<Association> associations;
  private String lidPrefix;
  private List<String> ancillaryMetadata;
  /**
   * Default contstructor.
   *
   * @param candidates A class that contains what product types
   * to extract and which metadata fields to get from those
   * product types.
   *
   * @throws ParserConfigurationException If there was an error
   * setting up the default XML Extractor.
   */
  public Pds3MetExtractorConfig(Pds3ProductMetadata metadata)
  throws ParserConfigurationException {
    associations = metadata.getAssociations().getAssociation();
    lidPrefix = metadata.getLidPrefix();
    ancillaryMetadata = metadata.getAncillaryMetadata().getElementName();
  }

  public List<Association> getAssociations() {
    return associations;
  }

  public String getLidPrefix() {
    return lidPrefix;
  }

  public List<String> getAncillaryMetadata() {
    return ancillaryMetadata;
  }
}
