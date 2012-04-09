package gov.nasa.pds.search;

import java.io.IOException;

import javax.xml.transform.Transformer;

import org.apache.solr.request.SolrQueryRequest;

public class XSLTResponseWriter extends
		org.apache.solr.request.XSLTResponseWriter {

	public final static String SOLR_HOME_PARAMETER = "SOLR_HOME";

	@Override
	protected Transformer getTransformer(SolrQueryRequest arg0)
			throws IOException {
		Transformer transformer = super.getTransformer(arg0);

		// Set up standard transform parameters
		if (transformer != null
				&& transformer.getParameter(SOLR_HOME_PARAMETER) == null) {
			transformer.setParameter(SOLR_HOME_PARAMETER,
					System.getProperty("solr.pds.home") + "/pds");
		}
		return transformer;
	}

}
