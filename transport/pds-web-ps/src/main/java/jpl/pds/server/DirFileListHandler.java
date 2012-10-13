// Copyright 2002 California Institute of Technology.  ALL RIGHTS RESERVED.
// U.S. Government Sponsorship acknowledged.
//

package jpl.pds.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import jpl.eda.xmlquery.Result;
import jpl.eda.xmlquery.XMLQuery;
import jpl.pds.server.FileQueryHandler;

/**
 * Handle product queries to return directory listing of products
 * in XML format. If the return type is DIRFILELIST1 then only the
 * files in the starting directory are returned.
 *
 * @author J. Crichtons.
 */
public class DirFileListHandler extends ChunkedFileHandler {
	public XMLQuery queryForFile(XMLQuery query, File[] fileArray, String optArg) {
		boolean oneLevel = optArg.equals("DIRFILELIST1");
		int j;

		for (j=0; j<fileArray.length; j++) {
			if (!fileArray[j].isDirectory()) {
				System.err.println("DirFileListHandler can't access dir " + fileArray[j]);
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
			System.err.println("DirFileListHandler can't satisfy request for non-compatible MIME types");
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
		String filename = null;
		for (j=0; j<fileArray.length; j++) {
			b.append(listDirFiles(fileArray[j], oneLevel));
		}
		b.append("</dirResult>").append(NL);
		String id = "DirFileList" + String.valueOf(productID++);
		Result result = new Result(id, "text/xml", "", "", Collections.EMPTY_LIST, b.toString());
		query.getResults().add(result);

		return query;
	}

	private StringBuffer listDirFiles(File dirFile, boolean oneLevel) {
		StringBuffer b = new StringBuffer("");
		File[] fileList = dirFile.listFiles();
		for(int i=0; i<fileList.length; i++) {
			String filename = null;
			if (fileList[i].isDirectory()) continue;
			filename = fileList[i].getAbsolutePath();
			if (filename.startsWith(productDirName)) 	// strip productDirName
				filename = filename.substring(lenProductDirName);
			b.append("  <dirEntry>").append(NL);
			b.append("    <OFSN>");
			b.append(filename);
			b.append("</OFSN>").append(NL);
			b.append("    <fileSize>");
			b.append(fileList[i].length());
			b.append("</fileSize>").append(NL);
			b.append("  </dirEntry>").append(NL);
		}
		if (oneLevel) return b;		// don't traverse sub directories
		for(int i=0; i<fileList.length; i++) {
			if (!fileList[i].isDirectory()) continue;
			b.append(listDirFiles(fileList[i], oneLevel));
		}
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
			System.err.println("Usage: [dirname...]");
			System.exit(1);
		}
		XMLQuery query = new XMLQuery("ONLINE_FILE_SPECIFICATION_NAME="+argv[0], /*id*/"cli-1",
			/*title*/"Command-line Query",
			/*desc*/"This query came from the command-line and is directed to the DirFileListHandler",
			/*ddId*/null, /*resultModeId*/null, /*propType*/null, /*propLevels*/null,
			XMLQuery.DEFAULT_MAX_RESULTS, null);
		DirFileListHandler handler = new DirFileListHandler();
		File productDir = new File(System.getProperty(FileQueryHandler.PRODUCT_DIR_PROPERTY, "/"));
		File [] fileArray = new File[argv.length];
		for (int i=0; i<argv.length; i++) {
			fileArray[i] = new File(productDir,argv[i]);
		}
		handler.queryForFile(query, fileArray, null);
		System.out.println(query.getXMLDocString().replaceAll("&lt;","<").replaceAll("&lt;",">").replaceAll("&quot;","\""));
		System.exit(0);
	}

	/** What PDS considers a newline. */
	private static final String NL = "\r\n";
}
