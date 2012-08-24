//Copyright (c) 2009, California Institute of Technology.
//ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
// $Id$ 
//

package gov.nasa.pds.search.core.indexer.solr;


import gov.nasa.pds.search.core.constants.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * This class creates a lucene index for text search.
 * 
 * @author pramirez
 * @version $Revision$
 * 
 */
public class SolrIndexer {
	
	public static void main(String[] args) throws IOException, Exception {
		String usage = "java " + SolrIndexer.class
				+ " <output_directory> <crawl_directory>";
		if (args.length > 2) {
			System.err.println("Usage: " + usage);
			System.exit(1);
		}

		Date start = new Date();
		//try {
			File outputFile = new File(new File(args[0]), "/solr_index.xml");
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			bw.write("<add>");
			
			System.out.println("Index Docs: " + args[1]);
			indexDocs(bw, new File(args[1]));
			bw.write("</add>");
			bw.close();
			Date end = new Date();

			System.out.print(end.getTime() - start.getTime());
			System.out.println(" total milliseconds");

		/*} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
		}*/
	}

	public static void indexDocs(BufferedWriter writer, File file)
			throws IOException, Exception {
		// do not try to index files that cannot be read
		if (file.canRead() && !file.getName().equals(Constants.LOG_FNAME)) {
			if (file.isDirectory()) {
				String[] files = file.list();
				// an IO error could occur
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]));
					}
				}
			} else {
				//String filename = file.getName().substring(0,
				//		file.getName().indexOf("."))
				//		+ ".xml";
				// System.out.println("creating " + filename);
				StringBuffer doc = DocumentParser.parse(file);

				if (doc != null) {
					writer.write(doc.toString());
				}
			}
		}
	}
}
