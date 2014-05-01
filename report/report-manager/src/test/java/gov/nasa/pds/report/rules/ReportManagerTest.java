// Copyright 2013, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.report.rules;

import gov.nasa.pds.report.logging.ToolsLevel;
import gov.nasa.pds.report.logging.formatter.ReportManagerFormatter;
import gov.nasa.pds.report.logging.handler.PDSStreamHandler;

import java.util.logging.Logger;

import org.junit.Ignore;

/**
 * 
 * @author jpadams
 * @version $Revision$
 *
 */
@Ignore
public class ReportManagerTest extends PDSTest{

	/** Logger for test classes **/
	protected static Logger log = Logger.getLogger(PDSTest.class
			.getName());

	static {
		log.addHandler(new PDSStreamHandler(System.out,
				ToolsLevel.DEBUG, new ReportManagerFormatter()));
	}
	
}
