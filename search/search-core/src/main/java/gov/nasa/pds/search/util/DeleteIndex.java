package gov.nasa.pds.search.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.SolrCore;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.sun.org.apache.bcel.internal.classfile.Field;

public class DeleteIndex {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SolrServerException 
	 * @throws SAXException 
	 */
	public static void main(String[] args) throws SolrServerException, IOException, SAXException {
		SolrCore core = SolrCore.getSolrCore();
		SolrServer server = new EmbeddedSolrServer(core);
		
		server.deleteByQuery("*:*");
		
		server.commit();
		core.close();
	}

}
