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
import gov.nasa.pds.tools.label.ManualPathResolver;
import gov.nasa.pds.tools.label.parser.DefaultLabelParser;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.oodt.cas.metadata.Metadata;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.RowSequence;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StarTableFactory;
import uk.ac.starlink.table.StoragePolicy;
import uk.ac.starlink.table.TableSequence;
import uk.ac.starlink.util.DataSource;
import uk.ac.starlink.votable.VOTableBuilder;

/**
 * Client to the PSA Registry.
 *
 * @author mcayanan
 *
 */
public class PsaRegistryClient implements PdapRegistryClient {
  private String baseUrl;

  private DateTimeFormatter dtFormat;

  /**
   * Constructor.
   *
   * @param baseUrl The base url of the registry.
   */
  public PsaRegistryClient(String baseUrl) {
    this.baseUrl = baseUrl + "/aio";
    dtFormat = DateTimeFormat.forPattern("yyyy-MM-dd'%20'HH:mm:ss.SSS");
  }

  public List<StarTable> getDataSets() throws PdapRegistryClientException {
    return getDataSets(null, null);
  }

  /**
   * Gets all the datasets.
   *
   * @return List of datasets in VOTable format.
   */
  @Override
  public List<StarTable> getDataSets(DateTime startDateTime,
      DateTime stopDateTime) throws PdapRegistryClientException {
    try {
      String urlString = baseUrl + "/jsp/metadata.jsp?RETURN_TYPE=VOTABLE";
      String query = "";
      if (startDateTime != null) {
        query += "&START_TIME>" + dtFormat.print(startDateTime);
      }
      if (stopDateTime != null) {
        query += "&STOP_TIME<" + dtFormat.print(stopDateTime);
      }
      if (!query.isEmpty()) {
        urlString = urlString + query;
      }
      URL url = new URL(urlString);
      VOTableBuilder votBuilder = new VOTableBuilder();
      DataSource datsrc = DataSource.makeDataSource(url);
      StoragePolicy policy = StoragePolicy.getDefaultPolicy();
      TableSequence tseq = votBuilder.makeStarTables( datsrc, policy );
      List<StarTable> tList = new ArrayList<StarTable>();
      for ( StarTable table; ( table = tseq.nextTable() ) != null; ) {
        tList.add( table );
      }
      return tList;
    } catch (Exception e) {
      throw new PdapRegistryClientException(e.getMessage());
    }
  }

  /**
   * Gets a single dataset in VOTable format.
   *
   * @param datasetId The identifier of the dataset to get.
   *
   * @return The dataset in VOTable format.
   *
   * @throws PdapRegistryClientException If an error occurred while connecting
   *  to the PSA.
   */
  @Override
  public StarTable getDataSet(String datasetId) throws PdapRegistryClientException {
    try {
      URL url = new URL(baseUrl + "/jsp/metadata.jsp/DATA_SET_ID=" + datasetId
          + "&RETURN_TYPE=VOTABLE");
      StarTableFactory factory = new StarTableFactory();
      StarTable table = factory.makeStarTable(DataSource.makeDataSource(url),
          "votable");
      return table;
    } catch (Exception e) {
      throw new PdapRegistryClientException(e.getMessage());
    }
  }

  /**
   * Gets the resource link associated with the given dataset identifier.
   *
   * @param datasetId The dataset identifier.
   *
   * @return A URL to the resource.
   *
   * @throws PdapRegistryClientException If an error occurred while getting
   *  the resource link.
   */
  @Override
  public URL getResourceLink(String datasetId) throws PdapRegistryClientException {
    try {
      URL url = new URL(baseUrl + "/jsp/metadata.jsp?DATA_SET_ID=" + datasetId
        + "&RETURN_TYPE=HTML");
      return url;
    } catch (Exception e) {
      throw new PdapRegistryClientException(e.getMessage());
    }
  }

  /**
   * Gets the catalog file from the PSA Registry.
   *
   * @param datasetId The dataset identifier.
   * @param filename The catalog filename.
   *
   * @return The catalog file label.
   *
   * @throws PdapRegistryClientException If an error occurred while getting
   * the catalog file.
   */
  @Override
  public Label getCatalogFile(String datasetId, String filename)
  throws PdapRegistryClientException {
    try {
      URL url = new URL(baseUrl + "/jsp/product.jsp?dataSetID=" + datasetId
          + "&productID=&path=CATALOG/&fileName=" + filename + "&protocol=HTTP");
      ManualPathResolver resolver = new ManualPathResolver();
      resolver.setBaseURI(ManualPathResolver.getBaseURI(url.toURI()));
      DefaultLabelParser parser = new DefaultLabelParser(false, true, true, resolver);
      Label label = parser.parseLabel(url);
      return label;
    } catch (Exception e) {
      throw new PdapRegistryClientException(e.getMessage());
    }

  }

  /**
   * Gets the VOLDESC.CAT file.
   *
   * @param datasetId The dataset identifier.
   *
   * @return The VOLDESC.CAT label.
   *
   * @throws PdapRegistryClientException If an error occurred while getting
   *  the VOLDESC.CAT file.
   *
   */
  public Label getVoldescFile(String datasetId) throws PdapRegistryClientException {
    try {
      URL url = new URL(baseUrl + "/jsp/product.jsp?dataSetID=" + datasetId
          + "&productID=&path=/&fileName=VOLDESC.CAT&protocol=HTTP");
      ManualPathResolver resolver = new ManualPathResolver();
      resolver.setBaseURI(ManualPathResolver.getBaseURI(url.toURI()));
      DefaultLabelParser parser = new DefaultLabelParser(false, true, true, resolver);
      Label label = parser.parseLabel(url);
      return label;
    } catch (Exception e) {
      throw new PdapRegistryClientException(e.getMessage());
    }

  }

  public static void main(String[] args) {
    PsaRegistryClient client = new PsaRegistryClient("http://psa.esac.esa.int:8000");
    try {
      List<StarTable> tables = client.getDataSets();
      for (StarTable st : tables) {
        int count = st.getColumnCount();
        ColumnInfo info = st.getColumnInfo(0);
        RowSequence rseq = st.getRowSequence();
        Metadata metadata = new Metadata();
        while (rseq.next()) {
          for (int i = 0; i < st.getColumnCount(); i++) {
            if (rseq.getCell(i) != null) {
              metadata.addMetadata(st.getColumnInfo(i).getUCD(),
                rseq.getCell(i).toString());
            }
          }
        }
        for (String key : metadata.getAllKeys()) {
          System.out.println("KEY = " + key + ", VALUE = " + metadata.getMetadata(key));
        }
      }
    } catch (PdapRegistryClientException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}
