//	Copyright 2009-2012, by the California Institute of Technology.
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
//	$Id: TestConstants.java 12301 2013-10-23 18:24:16Z jpadams $
//

package gov.nasa.pds.report.manager.constants;

import org.junit.Ignore;

/**
 * 
 * @author jpadams
 * @version $Revision: 12301 $
 * 
 */
@Ignore
public final class TestConstants {
	/** Relative Test Data Directory Path for testing locally **/
	public static final String TEST_DIR_RELATIVE = "./src/test/test-data/";
	
	/** Relative Test Dump directory path to hold test output **/
	public static final String TEST_DUMP_RELATIVE = "./target/test/";

	/** Relative Config Directory Path for testing locally **/
	public static final String CONFIG_DIR_RELATIVE = "./src/main/resources/conf/";
	
	/** Relative Report Manager Home Directory Path for testing locally **/
	public static final String REPORT_MGR_HOME = "./src/main/resources/";
	
	public static final String STAGING_AREA_TESTS_FILE = "stagingAreaTests.xml";
	
	public static final String SITES_TEST_FILE_PATH = TEST_DUMP_RELATIVE + "etc/conf/RemoteSpecs.xml";
	
	public static final String PROPERTIES_TEST_FILE_PATH = TEST_DUMP_RELATIVE + "etc/push_pull_framework.properties";

}
