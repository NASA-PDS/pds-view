// Copyright 2002 California Institute of Technology.  ALL RIGHTS RESERVED.
// U.S. Government Sponsorship acknowledged.
//
// $Id$

package jpl.pds.server;

import java.io.File;
import java.util.List;
import jpl.eda.xmlquery.Result;
import jpl.eda.xmlquery.LargeResult;
import jpl.eda.xmlquery.XMLQuery;
import jpl.eda.product.Retriever;
import jpl.eda.product.ProductException;
import junit.framework.TestCase;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.BufferedInputStream;

import java.util.Iterator;

/**
 * Unit tests for {@link RawFileHandler}.
 *
 * @author Kelly.
 */
public class RawFileHandlerTest extends FileHandlerTestCase {
	/**
	 * Creates a new <code>RawFileHandlerTest</code> instance.
	 *
	 * @param name a <code>String</code> value.
	 */
	public RawFileHandlerTest(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
		String oldBlockSize = System.getProperty("jpl.eda.xmlquery.blockSize");
		String oldMinBlockSize = System.getProperty("jpl.eda.query.ChunkedProductInputStream.minBlockSize");
		String oldMaxBlockSize = System.getProperty("jpl.eda.query.ChunkedProductInputStream.maxBlockSize");
		System.setProperty("jpl.eda.xmlquery.blockSize", "16");
		System.setProperty("jpl.eda.query.ChunkedProductInputStream.minBlockSize", "1");
		System.setProperty("jpl.eda.query.ChunkedProductInputStream.maxBlockSize", "16");
	}

	public void tearDown() throws Exception {
		if (oldBlockSize == null)
			System.getProperties().remove("jpl.eda.xmlquery.blockSize");
		else
			System.setProperty("jpl.eda.xmlquery.blockSize", oldBlockSize);
		if (oldMinBlockSize == null)
			System.getProperties().remove("jpl.eda.query.ChunkedProductInputStream.minBlockSize");
		else
			System.setProperty("jpl.eda.query.ChunkedProductInputStream.minBlockSize", oldMinBlockSize);
		if (oldMaxBlockSize == null)
			System.getProperties().remove("jpl.eda.query.ChunkedProductInputStream.maxBlockSize");
		else
			System.setProperty("jpl.eda.query.ChunkedProductInputStream.maxBlockSize", oldMaxBlockSize);
	}

	private String oldBlockSize;
	private String oldMinBlockSize;
	private String oldMaxBlockSize;

	/**
	 * Test the raw file handler.
	 */
	public void testHandler() {
		File [] fileArray = new File[1];
		RawFileHandler handler = new RawFileHandler();

		XMLQuery q1 = createQuery("nonexistent");
		fileArray[0] = new File("--nonexistent--");
		XMLQuery response = handler.queryForFile(q1, fileArray, "RAW");
		assertTrue(response.getResults().isEmpty());

		for (int i = 0; i < TEST_FILES.length; ++i) {
			XMLQuery q = createQuery(TEST_FILES[i]);
			fileArray[0] = testFiles[i];
			response = handler.queryForFile(q, fileArray, "RAW");
			List results = response.getResults();
			assertEquals(1, results.size());
			Result result = (Result) response.getResults().get(0);
			assertEquals(TEST_FILES[i], result.getResourceID());
			assertEquals("application/octet-stream", result.getMimeType());
		}

		XMLQuery q2 = createQuery(TEST_FILES[0]);
		q2.getMimeAccept().clear();
		q2.getMimeAccept().add("text/plain");
		fileArray[0] = testFiles[0];
		response = handler.queryForFile(q2, fileArray, "RAW");
		List results = response.getResults();
		assertEquals(1, results.size());
		Result result = (Result) response.getResults().get(0);
		assertEquals("text/plain", result.getMimeType());
	}

	/**
	 * This test exposes an OutOfMemoryError in CVS version 1.2 of
	 * ChunkedFileHandler.java.  All those expiration task objects that get created,
	 * canceled, and newly created causes an array in java.util.Timer to grow and grow
	 * and grow.  Version 1.3 fixes the problem.  You can enable this test case
	 * (change "false" to "true") and verify.  It's "false" now because it takes
	 * awhile to run.
	 *
	 * @throws IOException if an error occurs.
	 */
	public void testTimer() throws IOException {
		if (false) try {
			final RawFileHandler handler = new RawFileHandler();
			final File[] fileArray = { testFiles[0] };
			XMLQuery query = createQuery(TEST_FILES[0]);
			XMLQuery response = handler.queryForFile(query, fileArray, "RAW");
			List results = response.getResults();
			assertEquals(1, results.size());
			response.setRetriever(new Retriever() {
				public byte[] retrieveChunk(String id, long off, int len)
					throws ProductException {
					try {
						return handler.retrieveChunk(id, off, len);
					} catch (IOException ex) {
						throw new ProductException(ex);
					}

				}
				public void close(String id) throws ProductException {
					try {
						handler.close(id);
					} catch (IOException ex) {
						throw new ProductException(ex);
					}
				}
			});
			Result result = (Result) results.get(0);
			results.clear();
			assertTrue(result instanceof LargeResult);
			InputStream in = result.getInputStream();
			while (in.read() != -1);
			in.close();
		} catch (OutOfMemoryError ex) {
			fail("Ran out of memory when using a small block size");
		}
	}
}
