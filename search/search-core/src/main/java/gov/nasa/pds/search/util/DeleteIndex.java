package gov.nasa.pds.search.util;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.SolrCore;
import org.xml.sax.SAXException;

public class DeleteIndex {

	/**
	 * @param args
	 * @throws IOException
	 * @throws SolrServerException
	 * @throws SAXException
	 */
	public static void main(String[] args) throws SolrServerException,
			IOException, SAXException {
		SolrCore core = SolrCore.getSolrCore();
		SolrServer server = new EmbeddedSolrServer(core);

		server.deleteByQuery("*:*");

		server.commit();
		core.close();
	}

}
