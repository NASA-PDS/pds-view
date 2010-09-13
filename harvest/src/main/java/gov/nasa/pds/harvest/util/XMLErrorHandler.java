package gov.nasa.pds.harvest.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLErrorHandler implements ErrorHandler {

    public void error(SAXParseException exception) throws SAXException {
        throw new SAXException(exception);
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        throw new SAXException(exception);
    }

    public void warning(SAXParseException exception) throws SAXException {
        throw new SAXException(exception);
    }

}
