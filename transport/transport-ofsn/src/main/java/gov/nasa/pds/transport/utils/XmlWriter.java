package gov.nasa.pds.transport.utils;

/**
 * Utility class to write XML documents returned by PDS handlers.
 * 
 * @author Luca Cinquini
 *
 */
public class XmlWriter {
	
	/** PDS newline */
	private static final String NL = "\r\n";


	public static String writeSizeDocument(long size) {
		
		StringBuffer b = new StringBuffer("");
		b.append("<?xml version=\"1.0\"?>").append(NL);
		b.append("<!DOCTYPE dirresult PUBLIC \"-//JPL/DTD OODT dirresult 1.0//EN\" \"http://starbrite.jpl.nasa.gov:80/dtd/dirresult.dtd\">").append(NL);
		b.append("<dirResult>").append(NL);
		b.append("<dirEntry>").append(NL);
		b.append("<fileSize>");
		b.append(size);
		b.append("</fileSize>").append(NL);
		b.append("</dirEntry>").append(NL);
		b.append("</dirResult>").append(NL);

		return b.toString();
		
	}
	
}
