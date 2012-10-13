// Copyright 2002 California Institute of Technology.  ALL RIGHTS RESERVED.
// U.S. Government Sponsorship acknowledged.

package jpl.pds.server;

import java.io.IOException;
import jpl.eda.product.ProductException;
import jpl.eda.xmlquery.XMLQuery;
import java.io.File;

/**
 * A file querier queries for files.
 *
 * @author Kelly
 * @version $Revision$
 */
public interface FileQuerier {
	/**
	 * Query for a file.
	 *
	 * @param query a <code>XMLQuery</code> value.
	 * @param files array of Files to retrieve.
	 * @param optional arg (Zip name or Return_type).
	 * @return a <code>XMLQuery</code> value.
	 * @throws ProductException if an error occurs.
	 */
	XMLQuery queryForFile(XMLQuery query, File[] fileArray, String optArg) throws ProductException;

	/**
	 * Close a chunked file.
	 *
	 * @param id Product ID.
	 * @throws IOException if an error occurs.
	 */
	void close(String id) throws IOException;

	/**
	 * Retrieve a chunk.
	 *
	 * @param id Product ID.
	 * @param offset Where to retrieve a fragment.
	 * @param length How big a fragment to get.
	 * @return The fragment.
	 * @throws IOException if an error occurs.
	 */
	byte[] retrieveChunk(String id, long offset, int length) throws IOException;
}
