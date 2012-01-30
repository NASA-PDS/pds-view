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

package gov.nasa.pds.citool.diff;

import gov.nasa.pds.tools.label.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.incava.util.diff.Diff;
import org.incava.util.diff.Difference;

public class ValueDiff {
    public static List<DiffRecord> diff(Value source, Value target) {
        return diff(source.toString(), target.toString(), -1, -1);
    }

    public static List<DiffRecord> diff(Value source, Value target,
            int sourceLine, int targetLine) {
        return diff(source.toString(), target.toString(), sourceLine,
                targetLine);
    }

    public static List<DiffRecord> diff(String source, String target) {
        return diff(source, target, -1, -1);
    }

    public static List<DiffRecord> diff(String source, String target,
            int sourceLine, int targetLine) {
        List<DiffRecord> results = new ArrayList<DiffRecord>();

        List<String> sLines = readLines(new StringReader(source));
        List<String> tLines = readLines(new StringReader(target));
        List<Difference> diffs = (new Diff<String>(sLines, tLines)).diff();
        for (Difference diff : diffs) {
            int delStart = diff.getDeletedStart();
            int delEnd = diff.getDeletedEnd();
            int addStart = diff.getAddedStart();
            int addEnd = diff.getAddedEnd();
            String from = "";
            String to = "";
            String type = "";
            String info = "";

            if (sourceLine != -1 && targetLine != -1) {
                from = toString(delStart, delEnd, sourceLine);
                to = toString(addStart, addEnd, targetLine);
                type = delEnd != Difference.NONE && addEnd != Difference.NONE
                ? "c" : (delEnd == Difference.NONE ? "a" : "d");
                info = from + type + to;
            }
            List<String> sMsgs = new ArrayList<String>();
            List<String> tMsgs = new ArrayList<String>();
            if (delEnd != Difference.NONE) {
                sMsgs = getLines(delStart, delEnd, "<", sLines.toArray(
                        new String[]{}));
                sMsgs.add("----");
            }
            if (addEnd != Difference.NONE) {
                tMsgs = getLines(addStart, addEnd, ">",
                        (String[]) tLines.toArray(new String[]{}));
            }
            results.add(new DiffRecord(info, sMsgs, tMsgs));
        }
        return results;
    }

    private static List<String> getLines(int start, int end, String ind,
            String[] lines) {
        List<String> msgs = new ArrayList<String>();
        for (int i = start; i <= end; ++i)
            msgs.add(ind + " " + lines[i]);
        return msgs;
    }

    private static String toString(int start, int end, int base)
    {
        StringBuffer buf = new StringBuffer();

        // match the line numbering from diff(1):
        buf.append(start+base);

        if (end != Difference.NONE && start != end) {
            buf.append(",").append(end+base);
        }
        return buf.toString();
    }

    /**
     * Read lines, stripping out excess whitespace characters.
     *
     * @param reader
     *
     * @return A list of strings read from the Reader.
     */
    private static List<String> readLines(Reader reader) {
        List<String> result = new ArrayList<String>();
        BufferedReader br = new BufferedReader(reader);

        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                String filteredLine = line.replaceAll("\\s+", " ").trim();
                result.add(filteredLine);
            }
        } catch (IOException io) {
            //Don't do anything
        }
        return result;
    }
}
