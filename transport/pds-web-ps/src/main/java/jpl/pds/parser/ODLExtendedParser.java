// Copyright 1999-2005 California Institute of Technology. ALL RIGHTS
// RESERVED. U.S. Government Sponsorship acknowledged.
//

package jpl.pds.parser;

import java.io.*;

import org.w3c.dom.Document;
import antlr.collections.AST;
import antlr.*;

/**
 * Initialize Antlr lex and parser. Parse data merged product label file
 * using Antlr lex and parser. Subclass DDParser so that error reporting
 * routines can be overridden in antlr.Parser.
 *
 * @author J. Crichton
 */
public class ODLExtendedParser extends ODLParser {

    /**
     * Create parser that uses the lexer.
     *
     * @param lexer Lexer that parses characters.
     * @param runLog RunLog object used for writing messages to cmd window and output log.
     */
    public ODLExtendedParser(final TokenStream lexer, final RunLog runLog) {
        super(lexer);
        this.runLog = runLog;
    }

    /**
     * Parser error-reporting function can be overridden in subclass.
     *
     * @param ex Antlr recognition exception object.
     */
    public final void reportError(final RecognitionException ex) {
        runLog.errorMessage("ODL Parser: " + ex, 4);
    }

    /**
     * Parser error-reporting function can be overridden in subclass.
     *
     * @param s Error message.
     */
    public final void reportError(final String s) {
        if (getFilename() == null) {
            runLog.errorMessage("DD Parser: " + "error: " + s, 4);
        } else {
            runLog.errorMessage("DD Parser: " + getFilename() + ": error: " + s, 4);
        }
    }

    /**
     * Parser warning-reporting function can be overridden in subclass.
     *
     * @param s Error message.
     */
    public final void reportWarning(final String s) {
        if (getFilename() == null) {
            runLog.warningMessage("ODL Parser: " + "warning: " + s, 4);
        } else {
            runLog.warningMessage("ODL Parser: " + getFilename() + ": warning: " + s, 4);
        }
    }

    /** Runlog for return results. */
    private RunLog runLog;

    /**
     * Initializes the Antlr lex and parser. Parses the input file.
     *
     * @param filename Name of the file included in display messages.
     * @param reader Reader for the filename.
     * @param rootName Root element name from xsd file
     * @param runLog RunLog object used for writing messages to cmd window and output log.
     * @return Document or null if the parser fails.
     */
    public static Document parseFile(final String filename, final Reader reader,
        final String rootName, final RunLog runLog) {

        int initialErrorCount = runLog.getErrorCount();
        int initialWarningCount = runLog.getWarningCount();

        ODLExtendedParser parser = null;
        ODLLexer lexer = null;
        Document document = null;

        runLog.append("<syntaxValidation>", 2);
        try {
            // Get lexer and parser
            try {
                lexer = new ODLLexer(reader);
                lexer.setFilename(filename);
                parser = new ODLExtendedParser((TokenStream)lexer, runLog);
                parser.setFilename(filename);
            } catch (Exception ex) {
                runLog.append("<errorMessage>Error creating parser: " + ex.getMessage() + "</errorMessage>", 4);
                return null;
            }

            // start parsing at the label rule
            try {
                parser.label();
                if (runLog.foundErrorsWarnings()) return null;
            } catch (Exception ex) {
                runLog.errorMessage("Error parsing label file: " + ex.getMessage(), 4);
                return null;
            }

            // Convert AST tree to DOM
            try {
                XMLDom xmlDom = new XMLDom();
                AST antlrparsetree = parser.getAST();
                String[] antlrtokennames = parser.getTokenNames();
                document = xmlDom.convert(antlrparsetree, antlrtokennames, rootName, runLog);
            } catch (Exception ex) {
                runLog.errorMessage("Error converting label file to DOM: " + ex.getMessage(), 4);
                return null;
            }
        } finally {
            runLog.append("<numberErrors>" + (runLog.getErrorCount()-initialErrorCount) + "</numberErrors>", 4);
            runLog.append("<numberWarnings>" + (runLog.getWarningCount()-initialWarningCount) + "</numberWarnings>", 4);
            runLog.append("</syntaxValidation>", 2);
            try { reader.close(); } catch (IOException io) {}
        }
        return document;
    }
}

