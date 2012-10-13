// Copyright 2002 California Institute of Technology.  ALL RIGHTS RESERVED.
// U.S. Government Sponsorship acknowledged.

package jpl.pds.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import jpl.eda.xmlquery.Header;
import jpl.eda.xmlquery.LargeResult;
import jpl.eda.xmlquery.Result;
import jpl.eda.xmlquery.XMLQuery;

/**
 * Handle product queries for any kind of file. If optArg is
 * RAW_SIZE then return the size of file that RAW would
 * download.
 *
 * @author Kelly.
 */
public class RawFileHandler extends ChunkedFileHandler {
	public XMLQuery queryForFile(XMLQuery query, File[] fileArray, String optArg) {
		boolean returnSize = optArg.equals("RAW_SIZE");

		// Only one file will be passed
		File file = fileArray[0];
		if (!file.isFile() || !file.canRead()) {
			System.err.println("RawFileHandler can't find file " + file);
			return query;
		}

		if (returnSize) {
			// Return size of file found
			// Figure out what MIME type the client can digest.
			boolean compatible = false;
			List mimeAccept = new ArrayList(query.getMimeAccept());
			if (mimeAccept.isEmpty())
				mimeAccept.add("text/xml");
			for (Iterator i = mimeAccept.iterator(); i.hasNext();) {
				String desiredType = (String) i.next();
				if ("text/xml".equals(desiredType) || "text/*".equals(desiredType)
					|| "*/*".equals(desiredType)) {
					compatible = true;
					break;
				}
			}
			if (!compatible) {
				System.err.println("RawFileHandler can't satisfy request for non-compatible MIME types");
				return query;
			}

			long fileSize = file.length();
			StringBuffer b = new StringBuffer("");
			b.append("<?xml version=\"1.0\"?>").append(NL);
			b.append("<!DOCTYPE dirresult PUBLIC \"-//JPL/DTD OODT dirresult 1.0//EN\" \"http://starbrite.jpl.nasa.gov:80/dtd/dirresult.dtd\">").append(NL);
			b.append("<dirResult>").append(NL);
			b.append("<dirEntry>").append(NL);
			b.append("<fileSize>");
			b.append(fileSize);
			b.append("</fileSize>").append(NL);
			b.append("</dirEntry>").append(NL);
			b.append("</dirResult>").append(NL);
			String id = "Size" + String.valueOf(productID++);
			Result result = new Result(id, "text/xml", "", "", Collections.EMPTY_LIST, b.toString());
			query.getResults().add(result);
			return query;


		} else {
			// Return file found
			List headers = new ArrayList();
			headers.add(new Header(/*name*/"File", /*type*/"binary", /*unit*/null));
				
			// Set the returned MIME type to match what the user passed in.
			// By fiat, we choose the first one in the list, even if it's a
			// wildcard mimetype.  If there's none, then default it.
			List mimeTypes = query.getMimeAccept();
			String mimeType = "application/octet-stream";
			if (!mimeTypes.isEmpty()) {
				String userMimeType = (String) mimeTypes.get(0);
				if (!"*/*".equals(userMimeType))
					mimeType = userMimeType;
			}

			Result result = null;
			String resultID = "raw" + String.valueOf(productID++);
			long fileLength = file.length();
			addProduct(resultID, file, /*temporary*/false);

			// Always use LargeResult so that any mimeType can be
			// specified in the query. The code for returning a byte
			// array was deleted since it is incompatible with "text/*".
			result = new LargeResult(resultID, mimeType, "JPL.PDS.Raw",
				file.getName(), headers, fileLength);
			query.getResults().add(result);

			return query;
		}
	}

	/**
	 * Command-line driver.
	 *
	 * @param argv Command-line arguments.
	 */
	public static void main(String[] argv) throws Throwable {
		if (argv.length != 1) {
			System.err.println("Usage: filename");
			System.exit(1);
		}
		XMLQuery query = new XMLQuery("ONLINE_FILE_SPECIFICATION_NAME="+argv[0], /*id*/"cli-1",
			/*title*/"Command-line Query",
			/*desc*/"This query came from the command-line and is directed to the RawFileHandler",
			/*ddId*/null, /*resultModeId*/null, /*propType*/null, /*propLevels*/null,
			XMLQuery.DEFAULT_MAX_RESULTS, null);
		RawFileHandler handler = new RawFileHandler();
		File [] fileArray = new File[1];
		fileArray[0] = new File(argv[0]);
		handler.queryForFile(query, fileArray, null);
		System.out.println(query.getXMLDocString());

		Result r = (Result) query.getResults().get(0);
		if (r instanceof LargeResult) {
			LargeResult lr = (LargeResult) r;
			System.out.println("LARGE RESULT; retrieving by chunks:");
			byte[] buf;
			long numLeft = lr.getSize();
			long offset = 0L;
			while (numLeft > 0) {
				buf = handler.retrieveChunk(lr.getID(), offset, (int) Math.min(numLeft, 512L));
				numLeft -= buf.length;
				offset += buf.length;
				System.out.print(new String(buf));
			}
			handler.close(lr.getID());
		}
		System.out.println();
		System.exit(0);
	}

	/** What PDS considers a newline. */
	private static final String NL = "\r\n";

	/** Next product to generate. */ 
	private int productID;
}
