//Copyright (c) 2009, California Institute of Technology.
//ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
// $Id$ 
//

package gov.nasa.pds.search.core.indexer.solr;


import gov.nasa.pds.search.core.constants.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.io.FileUtils;

/**
 * This class creates a lucene index for text search.
 * 
 * @author pramirez
 * @version $Revision$
 * 
 */
public class SolrIndexer {
	
	private static final int INDEX_DOC_THRESHOLD = 2000;
	
	private static int totalIndexDocCount;
	private static String searchServiceIndexHome;
	private static OutputStreamWriter out;
	
	
	public static void main(String[] args) throws IOException, Exception {
		String usage = "java " + SolrIndexer.class
				+ " <output_directory> <crawl_directory>";
		if (args.length > 2) {
			System.err.println("Usage: " + usage);
			System.exit(1);
		}

		Date start = new Date();
		totalIndexDocCount = 0;

		searchServiceIndexHome = args[0];
		backUpIndex();
		
		setSolrIndexWriter();
			System.out.println("Index Docs: " + args[1]);
			indexDocs(new File(args[1]));
			
			closeSolrIndexWriter();
			Date end = new Date();

			System.out.print(end.getTime() - start.getTime());
			System.out.println(" total milliseconds");
	}

	public static void indexDocs(File file)
			throws IOException, Exception {
		// do not try to index files that cannot be read
		if (file.canRead() && !file.getName().equals(Constants.LOG_FNAME)) {
			if (file.isDirectory()) {
				String[] files = file.list();
				// an IO error could occur
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(new File(file, files[i]));
					}
				}
			} else {
				StringBuffer doc = DocumentParser.parse(file);

				if (doc != null) {
					write(doc.toString());
				}
			}
		}
	}
	
	/**
	 * Add the index document to the solr index and determine
	 * if it has met the document threshold for the current Solr Index
	 * file.
	 * 
	 * @param doc - String of the document object created in the indexDocs method
	 * @throws IOException
	 */
	public static void write(String doc) throws IOException {
		out.write(doc);
		if ((++totalIndexDocCount % INDEX_DOC_THRESHOLD) == 0) {
			closeSolrIndexWriter();
			setSolrIndexWriter();
		}
	}
	
	/**
	 * Create a new Solr Index file
	 * @throws IOException
	 */
	private static void setSolrIndexWriter() throws IOException {
		out = new OutputStreamWriter(new FileOutputStream(searchServiceIndexHome + "/solr_index.xml." + (totalIndexDocCount / INDEX_DOC_THRESHOLD)), "UTF-8");
		out.write("<add>");
	}
	
	/**
	 * Close the Solr Index Writer
	 * @throws IOException
	 */
	private static void closeSolrIndexWriter() throws IOException {
		out.write("</add>");
		out.close();
	}
	
	/**
	 * Not Completed.  May be necessary if we want to backup the old Solr Indexes
	 * However, currently backs up the registry-date directory, so we can just rerun the SolrIndexer
	 * to create the index again.
	 * 
	 * @throws IOException
	 */
	private static void backUpIndex() throws IOException {
		File backUpDir = new File(searchServiceIndexHome, "backup_solr_index");
		try {
			FileUtils.forceDelete(backUpDir);
		} catch (IOException e) { /* Thrown if backUpDir DNE */ }
	}
}
