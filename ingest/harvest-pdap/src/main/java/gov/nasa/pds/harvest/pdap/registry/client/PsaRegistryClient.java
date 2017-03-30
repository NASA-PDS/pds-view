// Copyright 2006-2017, by the California Institute of Technology.
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

import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ManualPathResolver;
import gov.nasa.pds.tools.label.parser.DefaultLabelParser;
import gov.nasa.pds.tools.util.MessageUtils;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.oodt.cas.metadata.Metadata;

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

  private SimpleDateFormat dateFormat;

  /**
   * Constructor.
   *
   * @param baseUrl The base url of the registry.
   */
  public PsaRegistryClient(String baseUrl) {
    this.baseUrl = baseUrl;
    dateFormat = new SimpleDateFormat("yyyyMMdd");
  }

  public List<StarTable> getDataSets() throws PdapRegistryClientException {
    return getDataSets(null);
  }

  /**
   * Gets datasets starting from a given date.
   *
   * @return List of datasets in VOTable format.
   */
  @Override
  public List<StarTable> getDataSets(Date startDate)
      throws PdapRegistryClientException {
    try {
      String urlString = baseUrl + "/pdap/metadata?RETURN_TYPE=VOTABLE";
      String query = "";
      if (startDate != null) {
        query += "&DATASET_RELEASE_DATE>='" + dateFormat.format(startDate) + "'";
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
      URL url = new URL(baseUrl + "/pdap/metadata/DATA_SET_ID=" + datasetId
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
      URL url = new URL(baseUrl + "/pdap/metadata?DATA_SET_ID=" + datasetId
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
  public Label getCatalogFile(String missionName, String datasetId, 
      String filename)
  throws PdapRegistryClientException {
    try {
      String encodedMissionName = missionName.replaceAll("\\s", "s");
      String encodedDatasetId = datasetId.replaceAll("/", "-");
      URL url = new URL(baseUrl + "/pdap/fileaccess?ID=/repo/" + encodedMissionName + "/"
          + encodedDatasetId + "/CATALOG/" + filename);
      Label label = null;
      try {
        label = parseLabel(url);
      } catch (Exception e) {
        //Some datasets replace the '/' with a '_' in the service endpoint
        encodedDatasetId = datasetId.replaceAll("/", "_");
        url = new URL(baseUrl + "/pdap/fileaccess?ID=/repo/" + encodedMissionName + "/"
            + encodedDatasetId + "/CATALOG/" + filename);
        label = parseLabel(url);
      }
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
  public Label getVoldescFile(String missionName, String datasetId) 
      throws PdapRegistryClientException {
    try {
      String encodedMissionName = missionName.replaceAll("\\s", "s");
      String encodedDatasetId = datasetId.replaceAll("/", "-");
      URL url = new URL(baseUrl + "/pdap/fileaccess?ID=/repo/" + encodedMissionName + "/"
          + encodedDatasetId + "/VOLDESC.CAT");
      Label label = null;
      try {
        label = parseLabel(url);
      } catch (Exception e) {
        //Some datasets replace the '/' with a '_' in the service endpoint.
        encodedDatasetId = datasetId.replaceAll("/", "_");
        url = new URL(baseUrl + "/pdap/fileaccess?ID=/repo/" + encodedMissionName + "/"
            + encodedDatasetId + "/VOLDESC.CAT");
        label = parseLabel(url);
      }
      return label;
    } catch (Exception e) {
      throw new PdapRegistryClientException(e.getMessage());
    }

  }
  
  private Label parseLabel(URL url) throws Exception {
    ManualPathResolver resolver = new ManualPathResolver();
    resolver.setBaseURI(ManualPathResolver.getBaseURI(url.toURI()));
    DefaultLabelParser parser = new DefaultLabelParser(false, true, true, resolver);
    try {
      Label label = parser.parseLabel(url);
      return label;
    } catch (LabelParserException lpe) {
      String message = "";
      try {
        message = MessageUtils.getProblemMessage(lpe);
      } catch (RuntimeException re) {
        // For now the MessageUtils class seems to be throwing this exception
        // when it can't find a key. In these cases the actual message seems to
        // be the key itself.
        message = lpe.getKey();
      }
      throw new Exception ("Error occurred while trying to parse label '"
          + url.toString() + "': " + message);
    } catch (IOException io) {
      throw new Exception ("Error occurred while trying to parse label '"
          + url.toString() + "': " + io.getMessage());
    }
  }

  public static void main(String[] args) {
    PsaRegistryClient client = new PsaRegistryClient("http://psa.esa.int");
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
