// Copyright 1999-2005 California Institute of Technology. ALL RIGHTS
// RESERVED. U.S. Government Sponsorship acknowledged.
//
package jpl.pds.parser;

import java.text.*;
import java.io.*;
import java.util.*;

/**
 * Read product label file into a StringBuffer. If any STRUCTURE pointer files
 * are encountered then read pointer file into the StringBuffer. Look for included
 * file in the current directrory and its parent directory. If not found then look
 * in parent directory and LABEL subdirectory. If not found keep moving up the
 * directory and looking in LABEL subdirectory.
 */
public class ReadLabelFile {
    /**
     * Create ReadLabelFile and save the StringBuffer for reading the file into.
     *
     * @param mergedFile StringBuffer to hold the merged files.
     * @param runLog RunLog object used for writing messages to cmd window and output log.
     */
    public ReadLabelFile(final StringBuffer mergedFile, final RunLog runLog) {
        this.mergedFile = mergedFile;
        this.runLog = runLog;
    }

    /**
     * Read the file into the StringBuffer and merge any STRUCTURE pointer files
     * found.
     *
     * @param filename Filename to read.
     * @throws IOException if read error or file not found.
     */
    public final void read(final String filename) throws IOException {
        File labelFile = new File(filename);
        BufferedReader reader = null;

        String pathFilename = null;
        try {
            // Add label file to list
            if (!labelFile.exists()) {
                throw new IOException("Label File " + labelFile.getPath() + " does not exist");
            }
            if (!labelFile.canRead()) {
                throw new IOException("Can not read Label File " + labelFile.getPath());
            }

            pathFilename = labelFile.getAbsolutePath();
            labelFile = new File(pathFilename);
            String mainDirName =  labelFile.getParent();

            // Search labelFile for referenced files
            String line = null;
            MessageFormat mf = null;
            reader = new BufferedReader(new FileReader(labelFile));
            int lineCnt = 0;
            int lineno = 0;
            while ((line = reader.readLine()) != null) {
                int mergedFileLength = mergedFile.length();
                Object[] objs = null;
                mergedFile.append(line + NL);
                line = deleteSpace(line);
                if (line.equals("END")) {
                    // omit END on included files
                    if (includeDepth > 0)
                        mergedFile.setLength(mergedFileLength); // remove reference from output
                    break;
                }
                if (!validLabelFile && lineCnt++>100) {         // quiting after 100 lines
                    mergedFile.setLength(mergedFileLength);     // remove reference from output
                    break;
                }
                //System.err.print(++lineno + ". " + line.trim() + NL);
                if (line.startsWith("PDS_VERSION_ID") || line.startsWith("RECORD_TYPE")) {
                    validLabelFile = true;
                    continue;
                }
                if (line.startsWith("^")) {
                    // Needs a "=" to be file refernce
                    if (line.indexOf('=') < 0) continue;        // ignore this line

                    // ^var = number (no file name)
                    if (objs == null) {
                        mf = new MessageFormat("^{0}={1,number}");
                        objs = mf.parse(line, new ParsePosition(0));
                        if (objs != null) continue;     // no filename
                    }

                    // ^var = ("filename",number)
                    if (objs == null) {
                        mf = new MessageFormat("^{0}=(\"{1}\",{2})");
                        objs = mf.parse(line, new ParsePosition(0));
                    }

                    // ^var = ('filename',number)
                    if (objs == null) {
                        mf = new MessageFormat("^{0}=(''{1}'',{2})");
                        objs = mf.parse(line, new ParsePosition(0));
                    }

                    // ^var = "filename"
                    if (objs == null) {
                        mf = new MessageFormat("^{0}=\"{1}\"");
                        objs = mf.parse(line, new ParsePosition(0));
                    }

                    // ^var = 'filename'
                    if (objs == null) {
                        mf = new MessageFormat("^{0}=''{1}''");
                        objs = mf.parse(line, new ParsePosition(0));
                    }
                } else continue;

                if (objs == null) {     // None of the formats match
                    if (!validLabelFile) return;
                    continue;
                }

                // find referenced file
                String refFilename = (String)objs[1];
                String altDirName = (String)objs[0];
                String ucAltDirName = altDirName.toUpperCase();
                //if (ucAltDirName.endsWith("STRUCTURE") || ucAltDirName.endsWith("DESCRIPTION") || ucAltDirName.endsWith("DESC"))
                if (ucAltDirName.endsWith("STRUCTURE")) {
                    altDirName = "LABEL";
                } else {
                    continue;                   // only want STRUCTURE, DESCRIPTION, or DESC
                }
                pathFilename = findPath(refFilename,mainDirName,altDirName);

                // If file exists then include file here
                if (pathFilename != null) {
                    runLog.append("<includeFile>" + pathFilename + "</includeFile>", 4);
                    mergedFile.setLength(mergedFileLength);     // remove reference from output
                    includeDepth++;
                    this.read(pathFilename);
                } else {
                    throw new FileNotFoundException("Could not find Pointer File " + refFilename);
                }
            }
            if (validLabelFile) return;
            throw new IOException("ReadLabelFile err: no PDS Version or Record Type found");
        } finally {
            includeDepth--;
            try {
                if (reader != null) reader.close();
            } catch (IOException  e) {}
        }
    }

    /**
     * Delete spaces on both sides of "=" character.
     *
     * @param line String that spaces will be deleted.
     * @return Line after spaces removed.
     */
    private String deleteSpace(final String line) {
        String out = "";
        char c;
        for (int i=0; i<line.length(); i++) {
        c = line.charAt(i);
        if (c == ' ' || c == '\t') continue;
            out += c;
        }
        return out;
    }

    /**
     * Search for the file in the main directory and the in the alternate directory where
     * the alternate directory can be in the main directory or in a directory up the tree.
     *
     * @param filename Filename to find.
     * @param mainDir Directory that will be searched.
     * @param altDir Directory that will be searched.
     * @return Absolute to file or null if not found.
     */
    private static String findPath(final String filename, final String mainDir, final String altDir) {
        // Try main dir first
        String fname = mainDir + File.separatorChar + filename;
        File f = new File(fname);
        // Try given name, lowercase name, and uppercase name
        if (f.canRead()) return f.getAbsolutePath();
        fname = mainDir + File.separatorChar + filename.toLowerCase();
        f = new File(fname);
        if (f.canRead()) return f.getAbsolutePath();
        fname = mainDir + File.separatorChar + filename.toUpperCase();
        f = new File(fname);
        if (f.canRead()) return f.getAbsolutePath();

        // If altDir specified, search for altDir and then file
        if (altDir == null || altDir.equals("")) return null;   // Search failed
        // start looking for altDir/filename in main Directory then look up directory tree
        File tree = new File(mainDir);
        while (tree != null) {
            String path = tree.getPath();
            fname = path + File.separatorChar + altDir + File.separatorChar + filename;
            f = new File(fname);
            if (f.canRead()) return fname;
            fname = path + File.separatorChar + altDir.toLowerCase()
                + File.separatorChar + filename.toLowerCase();
            f = new File(fname);
            if (f.canRead()) return fname;
            fname = path + File.separatorChar + altDir.toUpperCase()
                + File.separatorChar + filename.toUpperCase();
            f = new File(fname);
            if (f.canRead()) return fname;
            tree = tree.getParentFile();        // up one directory
        }
        return null;
    }

    /** Newline used by PDS. */
    private static final String NL = "\r\n";

    /** Buffer for merged file. */
    private StringBuffer mergedFile;

    /** Indicates if PDS_VERSION_ID or RECORD_TYPE found in file. */
    private boolean validLabelFile = false;

    /** Counts the recursive levels of include. */
    private int includeDepth = 0;

    /** Reference to RunLog for writing to the log buffer. */
    private RunLog runLog;
}
