package gov.nasa.pds.search;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.XmlUpdateRequestHandler;

/** Implements a command-line tools to perform database updates. */
public class XMLUpdate {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		System.exit((new XMLUpdate()).doUpdate(args));
	}
	
	protected int doUpdate(String[] args) throws FileNotFoundException {
		if (args.length != 1) {
			System.err.println("usage: " + this.getClass().getName() + " xmlFile");
			return 1;
		} else {
			return doUpdate(new FileReader(args[0]));
		}
	}

	protected int doUpdate(FileReader reader) {
		SolrCore core = SolrCore.getSolrCore();
		SolrServer server = new EmbeddedSolrServer( core );
		try {
			return 0;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 1;
		}
	}

}
