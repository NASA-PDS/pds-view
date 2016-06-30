package gov.nasa.pds.web.ui.constants;

import gov.nasa.pds.web.BaseTestCase;

@SuppressWarnings("nls")
public class DataSetConstantsTest extends BaseTestCase {

	public void testCUMINDEX_NAME_REGEX() {
		assertTrue("CUMINDEX.LBL".matches(DataSetConstants.CUMINDEX_NAME_REGEX));
		assertTrue("cumindex.lbl".matches(DataSetConstants.CUMINDEX_NAME_REGEX));
		assertTrue("1xZCMIDX.LBL".matches(DataSetConstants.CUMINDEX_NAME_REGEX));
		assertFalse("1xCMIDX.LBL".matches(DataSetConstants.CUMINDEX_NAME_REGEX));
	}
}
