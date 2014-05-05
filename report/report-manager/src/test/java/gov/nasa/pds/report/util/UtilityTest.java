//	Copyright 2013, by the California Institute of Technology.
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
package gov.nasa.pds.report.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.logs.pushpull.OODTPushPull;
import gov.nasa.pds.report.rules.ReportManagerTest;
import gov.nasa.pds.report.util.Utility;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * 
 * @author jpadams
 * @version $Revision$
 *
 */
@RunWith(JUnit4.class)
public class UtilityTest extends ReportManagerTest {

	@Rule
	public SingleTestRule test = new SingleTestRule("testGetValuesFromXML");
	
	@BeforeClass
	public static void oneTimeSetUp() throws IOException {
		try {
			FileUtils.forceMkdir(new File(Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE + "etc/conf/")));
		} catch (FileNotFoundException e) { 
			// Expected
		}
	}
	
	@Test
	public void testReplaceStringInFile() {
		try {
			File inputFile = new File(Utility.getAbsolutePath(TestConstants.REPORT_MGR_HOME + "etc/conf/RemoteSpecs.xml"));
			File outputFile = new File(Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE + "etc/conf/RemoteSpecs.xml"));
			
			Utility.replaceStringInFile("\\[CAS_PP_RESOURCES\\]", Utility.getAbsolutePath(TestConstants.TEST_DIR_RELATIVE +"etc"), inputFile, outputFile);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testGetValuesFromXML() {
		try {
			List<String> correctValueList = Arrays.asList("STAGING_AREA_0", "STAGING_AREA_1", "STAGING_AREA_2");
			
			File inputFile = new File(Utility.getAbsolutePath(TestConstants.TEST_DIR_RELATIVE + TestConstants.STAGING_AREA_TESTS_FILE));
			List<String> values = Utility.getValuesFromXML(inputFile, OODTPushPull.STAGING_TAG_NAME, OODTPushPull.STAGING_ATTRIBUTE_NAME);
			
			/**for (String value : values) {
				System.out.println(value);
			}**/
			
			assertEquals(correctValueList, values);		
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
