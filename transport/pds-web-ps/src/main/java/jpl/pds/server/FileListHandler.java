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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import jpl.eda.xmlquery.Result;
import jpl.eda.xmlquery.XMLQuery;
import jpl.pds.server.FileQueryHandler;
import jpl.pds.util.PDS2FName;

/**
 * Handle product queries to return directory listing of products
 * in XML format.
 *
 * @author J. Crichtons.
 */
public class FileListHandler extends ChunkedFileHandler {
	public XMLQuery queryForFile(XMLQuery query, File[] fileArray, String returnType) {
		// If the returnType is FILELIST, return info only for files in fileArray
		// else return info for files and referenced files
		int j;
		for (j=0; j<fileArray.length; j++) {
			if (!fileArray[j].isFile() || !fileArray[j].canRead()) {
				System.err.println("FileListHandler can't read file " + fileArray[j]);
				return query;
			}
		
		}

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
			System.err.println("FileListHandler can't satisfy request for non-compatible MIME types");
			return query;
		}

		StringBuffer b = new StringBuffer("");
		File productDir = new File(System.getProperty(FileQueryHandler.PRODUCT_DIR_PROPERTY, "/"));
		productDirName = productDir.getAbsolutePath(); 
		lenProductDirName = productDirName.length()
			+ (productDirName.endsWith(File.separator) ? 0:1);

		b.append("<?xml version=\"1.0\"?>").append(NL);
		b.append("<!DOCTYPE dirresult PUBLIC \"-//JPL/DTD OODT dirresult 1.0//EN\" \"http://starbrite.jpl.nasa.gov:80/dtd/dirresult.dtd\">").append(NL);
		b.append("<dirResult>").append(NL);
		String filenames = null;
		for (j=0; j<fileArray.length; j++) {
			if (returnType.equals("PDS_FILELIST") || returnType.equals("PDS_FILELISTZIP")) {
				// new file names get appended to filenames
				filenames = PDS2FName.getNames(fileArray[j],filenames);
			} else if (returnType.equals("FILELIST") || returnType.equals("FILELISTZIP")) {
				if (filenames == null) filenames = fileArray[j].getAbsolutePath();
				else filenames += " " + fileArray[j].getAbsolutePath();
			}
		}
		if (returnType.endsWith("ZIP")) b.append(zipSizeFiles(filenames));
		if (returnType.endsWith("FILELIST")) b.append(listFiles(filenames));
		b.append("</dirResult>").append(NL);
		String id = "FileList" + String.valueOf(productID++);
		Result result = new Result(id, "text/xml", "", "", Collections.EMPTY_LIST, b.toString());
		query.getResults().add(result);

		return query;
	}

	// return the filename and size
	private StringBuffer listFiles(String filenames) {
		StringBuffer b = new StringBuffer("");
		for (StringTokenizer tokens = new StringTokenizer(filenames); tokens.hasMoreTokens();) {
			String filename = tokens.nextToken();
			File file = new File(filename);
			if (filename.startsWith(productDirName)) 	// strip productDirName
				filename = filename.substring(lenProductDirName);
			b.append("  <dirEntry>").append(NL);
			b.append("    <OFSN>");
			b.append(filename);
			b.append("</OFSN>").append(NL);
			b.append("    <fileSize>");
			b.append(file.length());
			b.append("</fileSize>").append(NL);
			b.append("  </dirEntry>").append(NL);
		}
		return b;
	}

	// zip filenames and return the zip size of filenames
	private StringBuffer zipSizeFiles(String filenames) {
		StringBuffer b = new StringBuffer("");
		ZipOutputStream out = null;
		BufferedInputStream source = null;
		File tempFile = null;
		try {
			tempFile = File.createTempFile("oodt", ".zip");
			tempFile.deleteOnExit();
			out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(tempFile)));

			byte[] buf = new byte[512];
			int numRead;
			for (StringTokenizer tokens = new StringTokenizer(filenames); tokens.hasMoreTokens();) {
				String filenameToAdd = tokens.nextToken();
				File fileToAdd = new File(filenameToAdd);
				if (filenameToAdd.startsWith(productDirName)) 	// strip productDirName
					filenameToAdd = filenameToAdd.substring(lenProductDirName);
				ZipEntry entry = new ZipEntry(filenameToAdd);
				entry.setMethod(ZipEntry.DEFLATED);
				out.putNextEntry(entry);

				source = new BufferedInputStream(new FileInputStream(fileToAdd));
				while ((numRead = source.read(buf)) != -1) {
					out.write(buf, 0, numRead);
				}
					
				out.closeEntry();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (out != null) try {
				out.close();
			} catch (IOException ignore) {}
			if (source != null) try {
				source.close();
			} catch (IOException ignore) {}
		}
		b.append("  <dirEntry>").append(NL);
		b.append("    <OFSN>");
		b.append("product.zip");
		b.append("</OFSN>").append(NL);
		b.append("    <fileSize>");
		b.append(tempFile.length());
		b.append("</fileSize>").append(NL);
		b.append("  </dirEntry>").append(NL);
		return b;
	}

	/** Next product ID to generate. */
	private int productID = 0;      

	/** Start of directory path to remove */
	String productDirName = null;
	int lenProductDirName = 0;

	/** Let main grab the tempfile */
	private String tempFilename = null;      

	/**
	 * Command-line driver.
	 *
	 * @param argv Command-line arguments.
	 */
	public static void main(String[] argv) {
		if (argv.length < 1) {
			System.err.println("Usage: [filename...]");
			System.exit(1);
		}
		File productDir = new File(System.getProperty(FileQueryHandler.PRODUCT_DIR_PROPERTY, "/"));
		File [] fileArray = new File[argv.length];
		for (int i=0; i<argv.length; i++) {
			fileArray[i] = new File(productDir,argv[i]);
		}

		XMLQuery query = null;
		FileListHandler handler = null;

		System.out.println("-------------------    Query FILELIST ---------------------");
		query = new XMLQuery("ONLINE_FILE_SPECIFICATION_NAME="+argv[0], /*id*/"cli-1",
			/*title*/"Command-line Query",
			/*desc*/"This query came from the command-line and is directed to the FileListHandler",
			/*ddId*/null, /*resultModeId*/null, /*propType*/null, /*propLevels*/null,
			XMLQuery.DEFAULT_MAX_RESULTS, null);
		handler = new FileListHandler();
		handler.queryForFile(query, fileArray, "FILELIST");
		System.out.println(query.getXMLDocString().replaceAll("&lt;","<").replaceAll("&lt;",">").replaceAll("&quot;","\""));

		System.out.println("-------------------    Query PDS_FILELIST ---------------------");
		query = new XMLQuery("ONLINE_FILE_SPECIFICATION_NAME="+argv[0], /*id*/"cli-1",
			/*title*/"Command-line Query",
			/*desc*/"This query came from the command-line and is directed to the FileListHandler",
			/*ddId*/null, /*resultModeId*/null, /*propType*/null, /*propLevels*/null,
			XMLQuery.DEFAULT_MAX_RESULTS, null);
		handler = new FileListHandler();
		handler.queryForFile(query, fileArray, "PDS_FILELIST");
		System.out.println(query.getXMLDocString().replaceAll("&lt;","<").replaceAll("&lt;",">").replaceAll("&quot;","\""));

		System.out.println("-------------------    Query FILELISTZIP ---------------------");
		query = new XMLQuery("ONLINE_FILE_SPECIFICATION_NAME="+argv[0], /*id*/"cli-1",
			/*title*/"Command-line Query",
			/*desc*/"This query came from the command-line and is directed to the FileListHandler",
			/*ddId*/null, /*resultModeId*/null, /*propType*/null, /*propLevels*/null,
			XMLQuery.DEFAULT_MAX_RESULTS, null);
		handler = new FileListHandler();
		handler.queryForFile(query, fileArray, "FILELISTZIP");
		System.out.println(query.getXMLDocString().replaceAll("&lt;","<").replaceAll("&lt;",">").replaceAll("&quot;","\""));

		System.out.println("-------------------    Query PDS_FILELISTZIP ---------------------");
		query = new XMLQuery("ONLINE_FILE_SPECIFICATION_NAME="+argv[0], /*id*/"cli-1",
			/*title*/"Command-line Query",
			/*desc*/"This query came from the command-line and is directed to the FileListHandler",
			/*ddId*/null, /*resultModeId*/null, /*propType*/null, /*propLevels*/null,
			XMLQuery.DEFAULT_MAX_RESULTS, null);
		handler = new FileListHandler();
		handler.queryForFile(query, fileArray, "PDS_FILELISTZIP");
		System.out.println(query.getXMLDocString().replaceAll("&lt;","<").replaceAll("&lt;",">").replaceAll("&quot;","\""));
		System.exit(0);
	}

	/** What PDS considers a newline. */
	private static final String NL = "\r\n";
}
