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
package gov.nasa.pds.citool;

import gov.nasa.pds.citool.comparator.CatalogComparator;
import gov.nasa.pds.citool.report.CompareReport;
import gov.nasa.pds.citool.target.Target;
import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.constants.Constants.ProblemType;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ManualPathResolver;
import gov.nasa.pds.tools.label.parser.DefaultLabelParser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

public class CIToolComparator {
    private CompareReport report;

    public CIToolComparator(CompareReport report) {
        this.report = report;
    }

    /**
     * Compare two targets.
     *
     * @param source Object representation of the source
     * @param target Object representation of the target
     * @throws Exception
     */
    public void compare(Target source, Target target, boolean recurse)
    throws Exception {
        if (target.isDirectory()) {
            List<URL> sources = new ArrayList<URL>();
            List<URL> targets = new ArrayList<URL>();
            try {
                sources = source.traverse(recurse);
                targets = target.traverse(recurse);
            } catch (Exception e) {
                throw new Exception(e);
            }
            compare(sources, targets);
        } else {
            compare(source.toURL(), target.toURL());
        }
    }

    /**
     * Perform a comparison between 2 files.
     *
     * @param source URL of the source file.
     * @param target URL of the target file.
     * @throws LabelParserException
     * @throws IOException
     */
    public void compare(URL source, URL target) {
        Label sourceLabel = parse(source);
        Label targetLabel = parse(target);
        if (sourceLabel == null || targetLabel == null) {
            return;
        }
        if (sourceLabel.getProblems().isEmpty()
            && targetLabel.getProblems().isEmpty()) {
            CatalogComparator comparator = new CatalogComparator();
            targetLabel = comparator.checkEquality(sourceLabel, targetLabel);
            report.record(targetLabel.getLabelURI(),
                    targetLabel.getProblems());
        }
        else {
            if (!sourceLabel.getProblems().isEmpty()) {
                LabelParserException lp = new LabelParserException(
                        sourceLabel.getLabelURI(), null, null,
                        "compare.source.UnParseable",
                        ProblemType.INVALID_LABEL, sourceLabel.getLabelURI());
                report.recordSkip(sourceLabel.getLabelURI(), lp);
            }
            if (!targetLabel.getProblems().isEmpty()) {
                LabelParserException lp = new LabelParserException(
                        sourceLabel.getLabelURI(), null, null,
                        "compare.target.UnParseable",
                        ProblemType.INVALID_LABEL, targetLabel.getLabelURI());
                report.recordSkip(targetLabel.getLabelURI(), lp);
            }
        }
    }

    public void compare(List<URL> sources, List<URL> targets) {
        Map<URL, URL> matchingFiles = new LinkedHashMap<URL, URL>();
        for(URL target : targets) {
            String tName = FilenameUtils.getName(target.toString());
            for(URL source : sources) {
                String sName = FilenameUtils.getName(source.toString());
                if(sName.equals(tName)) {
                    matchingFiles.put(source, target);
                    break;
                }
            }
        }
        //Remove files that were found in the list in order to find out
        //which files are unmatched
        for(Map.Entry<URL, URL> entry : matchingFiles.entrySet()) {
            sources.remove(entry.getKey());
            targets.remove(entry.getValue());
        }
        for(URL source : sources) {
            URI uri = null;
            try {
                uri = source.toURI();
            } catch (URISyntaxException u) {
                //Don't do anything
            }
            LabelParserException lp = new LabelParserException(uri, null,
                    null, "compare.target.missingFile",
                    ProblemType.MISSING_RESOURCE, new Object[0]);
            report.recordSkip(uri, lp);
        }
        for(URL target : targets) {
            URI uri = null;
            try {
                uri = target.toURI();
            } catch (URISyntaxException u) {
                //Don't do anything
            }
            LabelParserException lp = new LabelParserException(uri, null,
                    null, "compare.source.missingFile",
                    ProblemType.MISSING_RESOURCE, new Object[0]);
            report.recordSkip(uri, lp);
        }

        for(Map.Entry<URL, URL> entry : matchingFiles.entrySet()) {
            compare(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Parse the given URL.
     *
     * @param url The URL to parse.
     * @return An object representation of the PDS label.
     * @throws LabelParserException
     * @throws IOException
     */
    public Label parse(URL url) {
        URI uri = null;
        try {
            uri = url.toURI();
        } catch (URISyntaxException u) {
            //Ignore
        }
        ManualPathResolver resolver = new ManualPathResolver();
        resolver.setBaseURI(ManualPathResolver.getBaseURI(uri));
        //Parser must have "parser.pointers" set to false
        DefaultLabelParser parser = new DefaultLabelParser(false, true, resolver);
        Label label = null;
        try {
            label = parser.parseLabel(url);
        } catch (LabelParserException lp) {
            //Product tools library records files that have a missing
            //PDS_VERSION_ID as an error. However, we want VTool to record
            //this as a warning, so we need to instantiate a new
            //LabelParserException.
            if("parser.error.missingVersion".equals(lp.getKey())) {
                report.recordSkip(uri, new LabelParserException(
                        lp.getSourceFile(), null, null,
                        lp.getKey(), ProblemType.INVALID_LABEL_WARNING,
                        lp.getArguments()));
            }
            else {
                report.recordSkip(uri, lp);
            }
        } catch (Exception e) {
            report.recordSkip(uri, e);
        }
        return label;
/*
        LabelParserFactory factory = LabelParserFactory.getInstance();
        LabelParser parser = factory.newLabelParser(LabelParser.CATALOG);
        parser.getProperties().setProperty("parser.pointers", "false");
        Label label = null;

        try {
            label = parser.parse(url);
        } catch (ParseException p) {
            label = new Label();
            label.setStatus(Label.SKIP);
            label.incrementWarnings();
        } catch (IOException io) {
            label = new Label();
            label.setStatus(Label.SKIP);
            label.incrementWarnings();
        }
        return label;
*/
    }
}
