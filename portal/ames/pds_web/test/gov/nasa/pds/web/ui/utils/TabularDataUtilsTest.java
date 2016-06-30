package gov.nasa.pds.web.ui.utils;

import gov.nasa.pds.web.BaseTestCase;

import java.net.MalformedURLException;
import java.net.URL;

public class TabularDataUtilsTest extends BaseTestCase {

	public void testFindStructureFile() {
		URL testUrl;
		try {
			testUrl = new URL(
					"http://pds-atmospheres.nmsu.edu/PDS/data/messmas_2001/DATA/MASCS20040827/UVVS/FUV/2200/UVVSHDRC.FMT"); //$NON-NLS-1$
			assertTrue(TabularDataUtils.findStructureFile(testUrl) != null);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
