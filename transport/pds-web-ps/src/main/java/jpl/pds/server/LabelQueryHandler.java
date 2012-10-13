// Copyright 2002 California Institute of Technology.  ALL RIGHTS RESERVED.
// U.S. Government Sponsorship acknowledged.
//
// $Id$

package jpl.pds.server;

import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.io.File;
import java.io.IOException;
import jpl.eda.product.ProductException;
import jpl.eda.xmlquery.XMLQuery;
import jpl.eda.xmlquery.QueryElement;
import jpl.eda.xmlquery.Result;
import jpl.pds.util.PDS2FName;
import jpl.pds.util.VAXReader;

/**
 * Querier for PDS labels.
 *
 * @author PDS-D Java Class
 * @version $Revision$
 */
public class LabelQueryHandler implements FileQuerier {
	public XMLQuery queryForFile(XMLQuery q, File[] fileArray, String optArg) throws ProductException {
		// Only one file will be passed
		if (fileArray[0] == null) return q;
		
		// Check if the user wants a compatible MIME type
		List mimes = q.getMimeAccept();
		if (mimes.contains("*/*") || mimes.contains("text/*") || mimes.contains("text/plain")
			|| mimes.isEmpty()) {
			StringBuffer productListing = new StringBuffer();
			String labelFile = fileArray[0].getName();
			String filenames = null;
			filenames = PDS2FName.getNames(fileArray[0], filenames, PDS2FName.labelHandler, productListing, false);
			if (filenames == null) {
				System.err.println("PDS2FName returned null file list");
				throw new ProductException("Not a PDS label file");
			}

			Result result = new Result("1", "text/plain", "", "", Collections.EMPTY_LIST, productListing.toString());
			q.getResults().add(result);
		}
		return q;	
	}

	public byte[] retrieveChunk(String id, long offset, int length) {
		return null;
	}

	public void close(String id) {}
	
	/** What PDS considers a newline. */
	private static final String NL = "\r\n";
}
