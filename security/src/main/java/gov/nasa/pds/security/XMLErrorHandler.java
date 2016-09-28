// Copyright 2009-2016, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government sponsorship acknowledged.
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
// $Id: $
package gov.nasa.pds.security;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLErrorHandler implements ErrorHandler {

	public void error(SAXParseException exception) throws SAXException {
		String message = "";
		if(exception.getLineNumber() != -1)
			message += "line " + exception.getLineNumber() + ": ";
		if(exception.getColumnNumber() != -1)
			message += "column " + exception.getColumnNumber() +": ";
		message += exception.getMessage();
		throw new SAXException(message);
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		String message = "";
		if(exception.getLineNumber() != -1)
			message = "line " + exception.getLineNumber() + ": ";
		if(exception.getColumnNumber() != -1)
			message += "column " + exception.getColumnNumber() +": ";
		message += exception.getMessage();
		throw new SAXException(message);
	}

	public void warning(SAXParseException exception) throws SAXException {
		String message = "";
		if(exception.getLineNumber() != -1)
			message = "line " + exception.getLineNumber() + ": ";
		if(exception.getColumnNumber() != -1)
			message += "column " + exception.getColumnNumber() +": ";
		message += exception.getMessage();
		throw new SAXException(message);
	}

}
