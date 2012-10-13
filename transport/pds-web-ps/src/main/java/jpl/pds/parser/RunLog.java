// Copyright 1999-2005 California Institute of Technology. ALL RIGHTS
// RESERVED. U.S. Government Sponsorship acknowledged.
//

package jpl.pds.parser;

import java.io.*;

/**
 * Captures the output to the command line window. Counts errors and warnings. Returns the
 * runlog as a string for returning in the HTTP stream.
 */
public class RunLog {
    /**
     * Return the runlog as a String.
     *
     * @return Runlog.
     */
    public final String toString() {
        return runLogBuffer.toString();
    }

    /**
     * Adds the message to the run log buffer but skips writing to the command line window.
     *
     * @param msg Message to append to the log buffer.
     * @param indent Number of spaces to prepend.
     */
    public final void append(final String msg, final int indent) {
        if (DEBUG) System.err.println(msg);
        runLogBuffer.append(spaces(indent) + msg + NL);
    }

    /**
     * Writes the message to the command line window and the log buffer and
     * increments the number of errors.
     *
     * @param msg Message to write.
     * @param indent Number of spaces to prepend.
     */
    public final void errorMessage(final String msg, int indent) {
        errorCount++;
        if (DEBUG) System.err.println(msg);
        runLogBuffer.append(spaces(indent) + "<errorMessage>" + msg + "</errorMessage>" + NL);
    }

    /**
     * Writes the message to the command line window and the log buffer and
     * increments the number of warnings.
     *
     * @param msg Message to write.
     * @param indent Number of spaces to prepend.
     */
    public final void warningMessage(final String msg, int indent) {
        warningCount++;
        if (DEBUG) System.err.println(msg);
        runLogBuffer.append(spaces(indent) + "<warningMessage>" + msg + "</warningMessage>" + NL);
    }

    /**
     * Checks if any errors or warnings were found.
     *
     * @return True if errorCount or warningCount > 0.
     */
    public final boolean foundErrorsWarnings() {
        return (errorCount + warningCount > 0);
    }

    /**
     * Pad spaces.
     *
     * @param indent Number of spaces.
     * @return Spaces.
     */
    private final String spaces(final int indent) {
        final String SPACES = "          ";
        if (indent == 0) return "";
        return SPACES.substring(0, indent-1);
    }

    /**
     * Gets the error count.
     *
     * @return Error count.
     */
    public final int getErrorCount() {
        return errorCount;
    }

    /**
     * Gets the warning count.
     *
     * @return Warning count.
     */
    public final int getWarningCount() {
        return warningCount;
    }

    /** StringBuffer for capturing runlog. */
    private StringBuffer runLogBuffer = new StringBuffer();

    /** Count number of errors. */
    private int errorCount = 0;

    /** Count number of errors. */
    private int warningCount = 0;

    /** Newline used by PDS. */
    private static final String NL = "\r\n";

    /** Write msg to stderr. */
    private static final boolean DEBUG = false;
}
