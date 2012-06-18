// Copyright 2006-2012, by the California Institute of Technology.
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

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.RowSequence;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StarTableFactory;
import uk.ac.starlink.table.StoragePolicy;
import uk.ac.starlink.table.TableSequence;
import uk.ac.starlink.util.DataSource;
import uk.ac.starlink.votable.VOTableBuilder;

public class PsaRegistryClient implements PdapRegistryClient {
  private String baseUrl;

  public PsaRegistryClient(String baseUrl) {
    this.baseUrl = baseUrl + "/aio";
  }

  @Override
  public List<StarTable> getAllDataSets() throws PdapRegistryClientException {
    try {
      URL url = new URL(baseUrl + "/jsp/metadata.jsp?RETURN_TYPE=VOTABLE");
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

  @Override
  public Label getCatalogFile(String datasetId) throws PdapRegistryClientException {
    try {
      URL url = new URL(baseUrl + "/jsp/product.jsp?dataSetID=" + datasetId
          + "&productID=&path=CATALOG/&fileName=DATASET.CAT&protocol=HTTP");
      ManualPathResolver resolver = new ManualPathResolver();
      resolver.setBaseURI(ManualPathResolver.getBaseURI(url.toURI()));
      DefaultLabelParser parser = new DefaultLabelParser(true, true, true, resolver);
      Label label = parser.parseLabel(url);
      return label;
    } catch (Exception e) {
      throw new PdapRegistryClientException(e.getMessage());
    }
  }

  public static void main(String[] args) {
    PsaRegistryClient client = new PsaRegistryClient("http://psa.esac.esa.int:8000");
    try {
      List<StarTable> tables = client.getAllDataSets();
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
