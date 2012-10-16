// Copyright 2002 California Institute of Technology.  ALL RIGHTS RESERVED.
// U.S. Government Sponsorship acknowledged.
//
// $Id$

package jpl.pds.server;

import java.util.List;
import java.io.File;
import jpl.eda.product.ProductException;
import jpl.eda.xmlquery.Result;
import jpl.eda.xmlquery.XMLQuery;
import junit.framework.TestCase;

/**
 * Unit tests for {@link LabelQueryHandler}.
 *
 * @author Kelly.
 */
public class LabelQueryHandlerTest extends FileHandlerTestCase {
	/**
	 * Creates a new <code>LabelQueryHandlerTest</code> instance.
	 *
	 * @param name a <code>String</code> value.
	 */
	public LabelQueryHandlerTest(String name) {
		super(name);
	}

	/**
	 * Test the label query handler.
	 *
	 * @throws ProductException if an error occurs.
	 */
	public void testHandler() throws ProductException {
		LabelQueryHandler handler = new LabelQueryHandler();

		XMLQuery q1 = createQuery(LABEL_FILE_NAME);
		File [] fileArray = new File[1];
		fileArray[0] = labelFile;
		XMLQuery response = handler.queryForFile(q1, fileArray, null);
		assertEquals(1, response.getResults().size());
		Result result = (Result) response.getResults().get(0);		
		assertEquals("text/plain", result.getMimeType());
	}
}

