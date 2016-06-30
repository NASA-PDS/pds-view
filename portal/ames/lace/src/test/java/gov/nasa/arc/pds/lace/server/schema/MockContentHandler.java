package gov.nasa.arc.pds.lace.server.schema;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class MockContentHandler extends DefaultHandler {
	
	private boolean documentStarted = false;
	
	public boolean isDocumentStarted() {
		return documentStarted;
	}
	
	@Override
	public void startDocument() throws SAXException {
		documentStarted = true;
	}
	
}