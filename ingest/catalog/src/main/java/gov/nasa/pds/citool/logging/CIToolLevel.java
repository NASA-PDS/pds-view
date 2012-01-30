// Copyright 2009, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government sponsorship acknowledged.
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


package gov.nasa.pds.citool.logging;

import java.util.logging.Level;

import gov.nasa.pds.citool.logging.ToolsLevel;

public class CIToolLevel extends ToolsLevel {
	public static final Level DEBUG = new CIToolLevel("DEBUG", Level.FINEST.intValue() + 1);
	public static final Level INFO_NOTIFY = new CIToolLevel("INFO_NOTIFY", Level.INFO.intValue() + 1);
	public static final Level WARNING_NOTIFY = new CIToolLevel("WARNING_NOTIFY", Level.WARNING.intValue() + 1);
	public static final Level SEVERE_NOTIFY = new CIToolLevel("SEVERE_NOTIFY", Level.SEVERE.intValue() + 4);
	public static final Level DIFF = new CIToolLevel("DIFF", Level.SEVERE.intValue() + 5);

	protected CIToolLevel(String name, int value) {
		super(name, value);
	}
}
