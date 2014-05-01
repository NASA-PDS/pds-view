// Copyright 2006-2010, by the California Institute of Technology.
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
// $Id: ToolsLogFilter.java 9719 2011-10-27 21:01:11Z mcayanan $
package gov.nasa.pds.report.logging.filter;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

/**
 * Class to filter logging messages that are coming from the underlying
 * framework.
 *
 * @author mcayanan
 *
 */
public class ToolsLogFilter implements Filter {

    /**
     * Method that checks if a log record is loggable.
     *
     * @param record The LogRecord.
     *
     * @return true if the record can be logged by the handler.
     *
     */
    @Override
    public boolean isLoggable(LogRecord record) {
        return true;
    }
}
