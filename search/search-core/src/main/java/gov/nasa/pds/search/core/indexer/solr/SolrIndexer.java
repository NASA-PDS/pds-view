//	Copyright 2009-2013, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id$
//

package gov.nasa.pds.search.core.indexer.solr;

import gov.nasa.pds.search.core.constants.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 * This class creates Lucene index for ingestion into Solr
 * 
 * @author pramirez
 * @author jpadams
 * 
 */
public class SolrIndexer {

	private static final int INDEX_DOC_THRESHOLD = 1000;

	private static int totalIndexDocCount;
	private static String searchServiceIndexHome;
	private static OutputStreamWriter out;
	
	/**
	 * Current set up is similar to a CLI
	 * 
	 * @param args
	 * @throws IOException
	 * @throws Exception
	 */
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
		removePreviousIndex();

		setSolrIndexWriter();
		System.out.println("Index Docs: " + args[1]);
		indexDocs(new File(args[1]));

		closeSolrIndexWriter();
		Date end = new Date();

		System.out.print(end.getTime() - start.getTime());
		System.out.println(" total milliseconds");
	}

	/**
	 * Recursive algorithm to traverse directories for files, parse them,
	 * and append them to the current Solr Index.
	 * 
	 * @param file
	 * @throws IOException
	 * @throws Exception
	 */
	public static void indexDocs(File file) throws IOException, Exception {
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
	 * Add the index document to the solr index and determine if it has met the
	 * document threshold for the current Solr Index file.
	 * 
	 * @param doc	String of the document object created in the indexDocs
	 *            	method
	 * @throws IOException
	 */
	public static void write(String doc) 
			throws IOException {
		out.write(doc);
		if ((++totalIndexDocCount % INDEX_DOC_THRESHOLD) == 0) {
			closeSolrIndexWriter();
			setSolrIndexWriter();
		}
	}

	/**
	 * Create a new Solr Index file
	 * 
	 * @throws IOException
	 */
	private static void setSolrIndexWriter() 
			throws IOException {
		out = new OutputStreamWriter(new FileOutputStream(
				searchServiceIndexHome + "/" + Constants.SOLR_INDEX_PREFIX
						+ (totalIndexDocCount / INDEX_DOC_THRESHOLD)), "UTF-8");
		out.write("<add>");
	}

	/**
	 * Close the Solr Index Writer
	 * 
	 * @throws IOException
	 */
	private static void closeSolrIndexWriter() 
			throws IOException {
		out.write("</add>");
		out.close();
	}

	/**
	 * Not Completed. May be necessary if we want to backup the old Solr Indexes
	 * However, currently backs up the registry-date directory, so we can just
	 * rerun the SolrIndexer to create the index again.
	 * 
	 * @throws IOException
	 */
	private static void removePreviousIndex() 
			throws IOException {
		List<File> files = new ArrayList<File>(FileUtils.listFiles(new File(searchServiceIndexHome), new WildcardFileFilter("solr_index.xml.*"), null));
		for (File file : files) {
			FileUtils.forceDelete(file);
		}
		/*File pastIndex = new File(searchServiceIndexHome, "solr_index.xml.2");
		System.out.println(pastIndex.getAbsolutePath());
		try {
			FileUtils.forceDelete(pastIndex);
		} catch (IOException e) { /* Thrown if backUpDir DNE. */ //}
	}
}
