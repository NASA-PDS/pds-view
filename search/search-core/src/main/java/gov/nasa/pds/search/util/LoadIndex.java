package gov.nasa.pds.search.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.SolrCore;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class LoadIndex {

	/**
	 * @param args
	 * @throws IOException
	 * @throws SolrServerException
	 * @throws SAXException
	 */
	public static void main(String[] args) throws SolrServerException,
			IOException, SAXException {
		if (args.length != 1) {
			System.err.println("usage: LoadIndex xmlFile");
			System.exit(1);
		}

		SolrCore core = SolrCore.getSolrCore();
		SolrServer server = new EmbeddedSolrServer(core);

		final Digester digester = new Digester();
		digester.setValidating(false);

		digester.addRule("add", new Rule() {
			@Override
			public void begin(String namespace, String name,
					Attributes attributes) throws Exception {
				digester.push(new ArrayList<SolrInputDocument>());
			}
		});
		digester.addObjectCreate("add/doc", SolrInputDocument.class.getName());
		digester.addSetProperties("add/doc", "boost", "documentBoost");
		digester.addRule("add/doc/field", new Rule() {
			@Override
			public void begin(String namespace, String name,
					Attributes attributes) throws Exception {
				digester.push(attributes.getValue("name"));
				if (attributes.getValue("boost") == null) {
					digester.push("1");
				} else {
					digester.push(attributes.getValue("boost"));
				}
			}

			@Override
			public void body(String text) throws Exception {
				float boost = Float.parseFloat((String) digester.pop());
				String name = (String) digester.pop();
				((SolrInputDocument) digester.peek()).addField(name, text,
						boost);
			}
		});
		digester.addSetNext("add/doc", "add", SolrInputDocument.class.getName());

		File input = new File(args[0]);
		Collection<SolrInputDocument> docs = (Collection<SolrInputDocument>) digester
				.parse(input);

		server.add(docs);
		server.commit();
		core.close();
	}

}
