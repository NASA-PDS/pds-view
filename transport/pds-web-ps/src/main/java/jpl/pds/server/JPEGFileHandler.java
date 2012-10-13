// Copyright 2002 California Institute of Technology.  ALL RIGHTS RESERVED.
// U.S. Government Sponsorship acknowledged.
//

package jpl.pds.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import jpl.eda.product.ProductException;
import jpl.eda.xmlquery.Header;
import jpl.eda.xmlquery.LargeResult;
import jpl.eda.xmlquery.Result;
import jpl.eda.xmlquery.XMLQuery;
import jpl.pds.util.VAXReader;

/**
 * Handle product queries to return JPEG PDS products. If jpegName is
 * PDS_JPEG_SIZE then return the size of file that PDS_JPEG would
 * download.
 *
 * @author J. Crichton.
 */
public class JPEGFileHandler extends ChunkedFileHandler {
	public XMLQuery queryForFile(XMLQuery query, File[] fileArray, String jpegName) throws ProductException {
		boolean returnSize = jpegName.equals("PDS_JPEG_SIZE");

		// Only one file will be passed
		File file = fileArray[0];
		if (!file.isFile() || !file.canRead()) {
			System.err.println("JPEGFileHandler can't find " + file);
			return query;
		}

		// Figure out what MIME type the client can digest.
		boolean compatible = false;
		List mimeAccept = new ArrayList(query.getMimeAccept());
		if (returnSize) {
			// Return total size of files found
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
		} else {
			// Return JPEG file
			if (mimeAccept.isEmpty())
				mimeAccept.add("image/jpeg");
			for (Iterator i = mimeAccept.iterator(); i.hasNext();) {
				String desiredType = (String) i.next();
				if ("image/jpeg".equals(desiredType) || "image/*".equals(desiredType)
					|| "*/*".equals(desiredType)) {
					compatible = true;
					break;
				}
			}
		}
		if (!compatible) {
			System.err.println("JPEGFileHandler can't satisfy request for non-compatible MIME types");
			return query;
		}

		// Verify that file is a PDS image file
		boolean foundLabel = false;
		try {
			VAXReader r = new VAXReader(file);
			String line, tline;
			int lineCount = 0;
			while ((line = r.readLine()) != null) {
				tline = line.trim();
				if (tline.startsWith("^IMAGE"))  {
					foundLabel = true;
					break;
				}
				if ("END".equals(tline)) break;
				lineCount++;
				if (!foundLabel && lineCount > 100) break;
			}
			r.close();
			Result result = null;
			if (!foundLabel) {
				throw new ProductException("File header does not contain IMAGE specification");
			}
		} catch (IOException io) {
			throw new ProductException(io.getMessage());	
		}

		// Convert file to JPEG and return result
		try {
			File tempFile = File.createTempFile("oodt", ".jpeg");
			tempFile.deleteOnExit();

			String cmdLine = PDS2JPEG + " -if " + file.getAbsolutePath();
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(cmdLine);
			process.getOutputStream().close();
			jpl.eda.util.Utility.redirect(process.getErrorStream(), System.err);


			BufferedInputStream reader = new BufferedInputStream(process.getInputStream());
			BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(tempFile));
			byte [] readBuffer = new byte[16384];
			int cnt;
			while ((cnt = reader.read(readBuffer,0,readBuffer.length)) > 0)
				writer.write(readBuffer,0,cnt);
			reader.close();
			writer.close();

			int rc = process.waitFor();
			if (rc != 0) {
				System.err.println("Conversion of IMAGE file to JPEG format failed");
				System.err.println(PDS2JPEG + " returned status " + rc);
				tempFile.delete();
				return query;
			}

			if (returnSize) {
				// Return total size of files found
				long jpegSize = tempFile.length();
				StringBuffer b = new StringBuffer("");
				b.append("<?xml version=\"1.0\"?>").append(NL);
				b.append("<!DOCTYPE dirresult PUBLIC \"-//JPL/DTD OODT dirresult 1.0//EN\" \"http://starbrite.jpl.nasa.gov:80/dtd/dirresult.dtd\">").append(NL);
				b.append("<dirResult>").append(NL);
				b.append("<dirEntry>").append(NL);
				b.append("<fileSize>");
				b.append(jpegSize);
				b.append("</fileSize>").append(NL);
				b.append("</dirEntry>").append(NL);
				b.append("</dirResult>").append(NL);
				String id = "Size" + String.valueOf(productID++);
				Result result = new Result(id, "text/xml", "", "", Collections.EMPTY_LIST, b.toString());
				query.getResults().add(result);

			} else {
				// Return JPEG file
				String id = "jpeg" + String.valueOf(productID++);
				String productFilename = jpegName + ".jpeg";
				addProduct(id, tempFile, /*temporary*/true);
				Result result = new LargeResult(id, "image/jpeg", "JPL.PDS.JPEG", productFilename,
					Collections.singletonList(new Header(/*name*/"File", /*type*/"binary", /*unit*/null)),
					tempFile.length());
				query.getResults().add(result);
			}
			tempFilename = tempFile.getPath();	// used for debugging
		} catch (InterruptedException ex) {
			throw new IllegalStateException("Unexpected InterruptedException: " + ex.getMessage());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return query;
	}

	/** What PDS considers a newline. */
	private static final String NL = "\r\n";

	/** Next product ID to generate. */
	private int productID = 0;      

	/** Let main grab the tempfile */
	private String tempFilename = null;      

	/**
	 * Command-line driver.
	 *
	 * @param argv Command-line arguments.
	 */
	public static void main(String[] argv) {
		if (argv.length != 1) {
			System.err.println("Usage: filename");
			System.exit(1);
		}
		XMLQuery query = new XMLQuery("ONLINE_FILE_SPECIFICATION_NAME="+argv[0], /*id*/"cli-1",
			/*title*/"Command-line Query",
			/*desc*/"This query came from the command-line and is directed to the JPEGFileHandler",
			/*ddId*/null, /*resultModeId*/null, /*propType*/null, /*propLevels*/null,
			XMLQuery.DEFAULT_MAX_RESULTS, null);
		JPEGFileHandler handler = new JPEGFileHandler();
		File productDir =new File(System.getProperty(FileQueryHandler.PRODUCT_DIR_PROPERTY, "/")); 
		File [] fileArray = new File[1];
		fileArray[0] = new File(productDir, argv[0]);
		try {
			handler.queryForFile(query, fileArray, "pds-image");
		} catch (ProductException e) {
			System.err.println("Product exception\n");
			e.printStackTrace();
		}
		System.out.println(query.getXMLDocString());

		// Grab temp file for debbuging before it is deleted
		byte [] readBuffer = new byte[16384];
		int cnt;
		try {
			FileInputStream src = new FileInputStream(handler.tempFilename);
			FileOutputStream dst = new FileOutputStream(JPEGFILE);
			while ((cnt = src.read(readBuffer,0,readBuffer.length)) > 0)
				dst.write(readBuffer,0,cnt);
			src.close();
			dst.close();
		} catch (IOException ignore) {}

		System.exit(0);
	}

	/** Where the PDS2JPEG utility is. */
	private static final String PDS2JPEG = System.getProperty("jpl.pds.server.JPEGFileHandler.pds2jpeg",
		"/work/pds/src/native/pds2jpeg/win/pds2jpeg.exe");
	/** Location to copy zip file for debugging */
	private static final String JPEGFILE = System.getProperty("jpl.pds.server.JPEGFileHandler.jpegfile",
		"C:/temp/out.jpeg");
}
