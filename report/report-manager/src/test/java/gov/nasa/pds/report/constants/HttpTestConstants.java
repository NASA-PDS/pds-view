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

package gov.nasa.pds.report.constants;

import org.junit.Ignore;

/**
 * This class makes constants available for testing pulling logs using the 
 * SFTP log puller implementation.
 * 
 * @author jpadams
 */
@Ignore
public final class HttpTestConstants{
	
	public static final String TEST_NODE = "rings_metrics";
	public static final String TEST_HOST = "http://pds-rings.seti.org/";
	public static final String TEST_USER = "anonymous";
	public static final String TEST_PASSWORD = "anonymous";
	public static final boolean TEST_ENCRYPTED = false;
	public static final String TEST_PATH =
			"met-mhNQwdRPkhJ2-rics/rings-pds-rings-apache2.2014-10-01.tar.gz";
	
}