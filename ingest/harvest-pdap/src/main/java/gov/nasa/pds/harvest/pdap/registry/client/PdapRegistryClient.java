// Copyright 2006-2015, by the California Institute of Technology.
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
package gov.nasa.pds.harvest.pdap.registry.client;

import gov.nasa.pds.tools.label.Label;

import java.net.URL;
import java.util.List;

import org.joda.time.DateTime;

import uk.ac.starlink.table.StarTable;

/**
 * Interface class to the PDAP-Compliant Registries.
 *
 * @author mcayanan
 *
 */
public interface PdapRegistryClient {

  /** Gets all the datasets. */
  public List<StarTable> getDataSets() throws PdapRegistryClientException;

  /** Gets the datasets within a given datetime range. */
  public List<StarTable> getDataSets(DateTime startDate, DateTime stopDate) throws PdapRegistryClientException;

  /** Gets a single dataset. */
  public StarTable getDataSet(String datasetId) throws PdapRegistryClientException;

  /** Gets the resource link associated with the given dataset. */
  public URL getResourceLink(String datasetId) throws PdapRegistryClientException;

  /** Gets the catalog file associated with the given dataset. */
  public Label getCatalogFile(String datasetId, String filename) throws PdapRegistryClientException;

  /** Gets the VOLDESC.CAT file associated with the given dataset. */
  public Label getVoldescFile(String datasetId) throws PdapRegistryClientException;
}
