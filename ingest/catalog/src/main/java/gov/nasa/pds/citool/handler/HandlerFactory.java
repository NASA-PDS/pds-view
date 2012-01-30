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


package gov.nasa.pds.citool.handler;

import gov.nasa.pds.citool.handler.ToolsFileHandler;
import gov.nasa.pds.citool.handler.ToolsStreamHandler;
import gov.nasa.pds.citool.logging.ToolsLevel;
import gov.nasa.pds.citool.logging.CompareFormatter;
import gov.nasa.pds.citool.logging.IngestFormatter;
import gov.nasa.pds.citool.logging.CIToolLevel;
import gov.nasa.pds.citool.logging.ValidateFormatter;

import java.io.File;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;

public class HandlerFactory {
	private static HandlerFactory factory = null;
	public static final int COMPARE = 1;
	public static final int INGEST = 2;
	public static final int VALIDATE = 3;

	private HandlerFactory() {}

	public synchronized static HandlerFactory getInstance() {
		if(factory == null)
			factory = new HandlerFactory();

		return factory;
	}

	/**
	 * Get an instance of a Handler object for the human-readable report.
	 *
	 * @param mode Specify a logging mode (COMPARE=1, INGEST=2)
	 * @param reportFile Specify a file name or set to 'null' to write to standard out.
	 * @param severity Specify the severity level and above for the reporting. Must be set
	 * to 'Info', 'Warning', or 'Error'.
	 *
	 * @return A proper Handler object based on the input parameters.
	 *
	 * @throws SecurityException
	 * @throws IOException
	 * @throws UnknownHandlerConfigurationException
	 */
	public Handler newInstance(int mode, File reportFile, String severity) throws SecurityException, IOException,
	                                                                                   UnknownHandlerConfigurationException {
		Handler handler = null;
		Level level = null;

		if("INFO".equalsIgnoreCase(severity))
			level = ToolsLevel.INFO;
		else if("WARNING".equalsIgnoreCase(severity))
			level = ToolsLevel.WARNING;
		else if("SEVERE".equalsIgnoreCase(severity))
			level = ToolsLevel.SEVERE;
		else if("FINEST".equalsIgnoreCase(severity))
			level = CIToolLevel.DEBUG;
		else
			throw new UnknownHandlerConfigurationException("Unknown severity level: " + severity);

		if(reportFile != null) {
            if (mode==COMPARE)
                handler = new ToolsFileHandler(reportFile.toString(), level, new CompareFormatter());
            else if (mode == INGEST)
                handler = new ToolsFileHandler(reportFile.toString(), level, new IngestFormatter());
            else if (mode == VALIDATE)
            	handler = new ToolsFileHandler(reportFile.toString(), level, new ValidateFormatter());
        }
        else {
            if (mode==COMPARE)
                handler = new ToolsStreamHandler(System.out, level, new CompareFormatter());
            else if (mode == INGEST)
                handler = new ToolsStreamHandler(System.out, level, new IngestFormatter());
            else if (mode == VALIDATE)
            	handler = new ToolsStreamHandler(System.out, level, new ValidateFormatter());
        }
		return handler;
	}
}
