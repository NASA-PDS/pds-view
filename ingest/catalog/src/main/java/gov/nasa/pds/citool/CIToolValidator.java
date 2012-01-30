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

import gov.nasa.pds.citool.report.NewStandardValuesReport;
import gov.nasa.pds.citool.report.ReferentialIntegrityReport;
import gov.nasa.pds.citool.report.Report;
import gov.nasa.pds.citool.ri.NewValidValueFinder;
import gov.nasa.pds.citool.ri.RIType;
import gov.nasa.pds.citool.ri.ReferentialIntegrityValidator;
import gov.nasa.pds.citool.target.Target;
import gov.nasa.pds.citool.util.Utility;
import gov.nasa.pds.citool.validate.ReferenceFinderValidator;
import gov.nasa.pds.citool.validate.ReferenceValidator;
import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.constants.Constants.ProblemType;
import gov.nasa.pds.tools.dict.Dictionary;
import gov.nasa.pds.tools.dict.parser.DictionaryParser;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.IncludePointer;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ManualPathResolver;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.PointerStatement;
import gov.nasa.pds.tools.label.Statement;
import gov.nasa.pds.tools.label.parser.DefaultLabelParser;
import gov.nasa.pds.tools.label.validate.Validator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CIToolValidator {
    private ManualPathResolver resolver;
    private DefaultLabelParser parser;
    private Report report;
    private URL allrefs;

    public CIToolValidator(Report report) {
        this(report, new ArrayList<URL>());
    }

    public CIToolValidator(Report report, List<URL> includePaths) {
        this.report = report;
        this.allrefs = null;
        resolver = new ManualPathResolver();
        if (!includePaths.isEmpty()) {
            resolver.setIncludePaths(includePaths);
        }
        parser = new DefaultLabelParser(true, true, true, resolver);
    }

    public Dictionary parseDictionary(List<String> dictionaries,
            boolean aliasing)
    throws URISyntaxException, CIToolValidatorException {
        Dictionary dictionary = null;
        boolean dictionaryPassed = true;
        URL url = null;
        Iterator<String> i = dictionaries.iterator();
        try {
            url = Utility.toURL(i.next());
            dictionary = DictionaryParser.parse(url, aliasing, true);
            report.addConfiguration("   Dictionary version     "
                    + dictionary.getVersion());
            if (dictionary.getProblems().size() != 0) {
                dictionaryPassed = false;
                report.record(dictionary.getDictionaryURI(),
                        dictionary.getProblems());
            }
            while (i.hasNext()) {
                url = Utility.toURL(i.next());
                Dictionary mergedDictionary = DictionaryParser.parse(url,
                        aliasing, true);
                dictionary.merge(mergedDictionary);
                report.addConfiguration("   Dictionary version     "
                        + mergedDictionary.getVersion());
                if (mergedDictionary.getProblems().size() != 0) {
                    dictionaryPassed = false;
                    report.record(mergedDictionary.getDictionaryURI(),
                            mergedDictionary.getProblems());
                }
            }
        } catch (LabelParserException le) {
            List<LabelParserException> problems =
                new ArrayList<LabelParserException>();
            problems.add(le);
            report.record(url.toURI(), problems);
            dictionaryPassed = false;
        } catch (IOException io) {
            report.recordSkip(url.toURI(), io);
            dictionaryPassed = false;
        }
        if (!dictionaryPassed) {
            throw new CIToolValidatorException("Dictionary did not pass");
        }
        return dictionary;
    }

    public void setAllrefs(URL url) {
        this.allrefs = url;
    }

    public Report doReferentialIntegrity(List<Label> catalogs) {
        Set<RIType> typesFound = new HashSet<RIType>();
        for(Label catalog : catalogs) {
            if(!catalog.getObjects(
                    RIType.REFERENCE.getName().toUpperCase()).isEmpty()) {
                typesFound.add(RIType.REFERENCE);
            } else if(!catalog.getObjects(
                    RIType.PERSONNEL.getName().toUpperCase()).isEmpty()) {
                typesFound.add(RIType.PERSONNEL);
            } else if(!catalog.getObjects(
                    RIType.TARGET.getName().toUpperCase()).isEmpty()) {
                typesFound.add(RIType.TARGET);
            } else if(!catalog.getObjects(
                    RIType.INSTRUMENT_HOST.getName().toUpperCase())
                    .isEmpty()) {
                typesFound.add(RIType.INSTRUMENT_HOST);
            } else if(!catalog.getObjects(
                    RIType.INSTRUMENT.getName().toUpperCase()).isEmpty()) {
                typesFound.add(RIType.INSTRUMENT);
            } else if(!catalog.getObjects(
                    RIType.MISSION.getName().toUpperCase()).isEmpty()) {
                typesFound.add(RIType.MISSION);
            } else if(!catalog.getObjects(
                    RIType.DATA_SET.getName().toUpperCase()).isEmpty()) {
                typesFound.add(RIType.DATA_SET);
            }
        }
        ReferentialIntegrityReport riReport = new ReferentialIntegrityReport();
        riReport.setOutput(report.getOutput());
        ReferentialIntegrityValidator riv = new ReferentialIntegrityValidator(
                riReport);
        for (RIType typeFound : typesFound) {
            riv.addRIChecker(typeFound);
        }
        riv.isValid(catalogs, allrefs);
        return riReport;
    }

    /**
     * Finds new standard values in the given set of label files.
     *
     * @param catalogs A list of catalog files.
     * @param dictionary A PDS data dictionary
     */
    public NewStandardValuesReport findNewStandardValues(List<Label> catalogs,
            Dictionary dictionary) {
        List<AttributeStatement> newValues =
            new ArrayList<AttributeStatement>();
        NewStandardValuesReport svReport = new NewStandardValuesReport();
        svReport.setOutput(report.getOutput());
        for (Label catalog : catalogs) {
            List<Statement> statements = (List<Statement>)
            catalog.getStatements();
            for (Statement s : statements) {
                for (AttributeStatement newValue :
                    NewValidValueFinder.getNewValues(s, dictionary)) {
                    if (!newValues.contains(newValue)) {
                        newValues.add(newValue);
                    }
                }
            }
        }
        svReport.printHeader();
        if (!newValues.isEmpty()) {
            for (AttributeStatement newValue : newValues) {
                String strippedValue = Utility.stripOnlyWhitespaceAndNewLine(
                        newValue.getValue().toString());
                svReport.printStandardValue(newValue.getIdentifier().getId(),
                        strippedValue);
            }
        }
        return svReport;
    }

    public void validate(Target target, List<String> dictionaries,
            boolean recurse, boolean aliasing)
    throws CIToolValidatorException {
        try {
            List<Label> catalogs = new ArrayList<Label>();
            Dictionary dictionary = parseDictionary(dictionaries, aliasing);
            if (target.isDirectory()) {
                List<URL> urls = target.traverse(recurse);
                for (Iterator<URL> i = urls.iterator(); i.hasNext();) {
                    Label catalog = validate(i.next(), dictionary);
                    if (catalog != null) {
                        catalogs.add(catalog);
                    }
                }
            } else {
                Label catalog = validate(target.toURL(), dictionary);
                if (catalog != null) {
                    catalogs.add(catalog);
                }
            }
            Report riReport = doReferentialIntegrity(catalogs);
            NewStandardValuesReport svReport =
                findNewStandardValues(catalogs, dictionary);
            riReport.printFooter();
            svReport.printFooter();
        } catch (CIToolValidatorException ve) {
            //Don't do anything. This means that dictionary validation
            //failed.
        } catch (Exception e) {
            e.printStackTrace();
            throw new CIToolValidatorException(e.getMessage());
        }
    }

    public Label validate(URL url, Dictionary dictionary)
    throws URISyntaxException {
        Label catalog = null;
        try {
            resolver.setBaseURI(ManualPathResolver.getBaseURI(url.toURI()));
            catalog = parser.parseLabel(url);
        } catch (LabelParserException lpe) {
            //Product tools library records files that have a missing
            //PDS_VERSION_ID as an error. However, we want VTool to record
            //this as a warning, so we need to instantiate a new
            //LabelParserException.
            if ("parser.error.missingVersion".equals(lpe.getKey())) {
                report.recordSkip(url.toURI(), new LabelParserException(
                        lpe.getSourceFile(),
                        null,
                        null,
                        lpe.getKey(), ProblemType.INVALID_LABEL_WARNING,
                        lpe.getArguments()));
            } else {
                report.recordSkip(url.toURI(), lpe);
            }
        } catch (IOException ie) {
            report.recordSkip(url.toURI(), ie);
        } catch (Exception e) {
            report.recordSkip(url.toURI(), e);
        }
        if (catalog != null) {
            Validator validator = new Validator();
            validator.setDuplicateIdCheck(true);
            validator.setDictionaryCheck(true);
            validator.addValidator(new ReferenceFinderValidator());
            if (!catalog.getObjects(
                    RIType.REFERENCE.getName().toUpperCase()).isEmpty()) {
                validator.addValidator(new ReferenceValidator());
            }
            validator.validate(catalog, dictionary);
            checkFileRefs(catalog);
            report.record(url.toURI(), catalog.getProblems());
        }
        return catalog;
    }

    /**
     * Check for the existence of referenced files specified in a label
     *
     * @param label
     */
    private void checkFileRefs(Label label) {
        List<PointerStatement> externalPointers = new ArrayList<PointerStatement>();
        List<PointerStatement> localPointers = new ArrayList<PointerStatement>();

        for (PointerStatement pointer : getPointersRecursively(label)) {
            //Do not check include pointers. Its already being
            //handled at the parser level.
            if (!(pointer instanceof IncludePointer)) {
                if (!label.equals(pointer.getLabel())) {
                    externalPointers.add(pointer);
                } else {
                    localPointers.add(pointer);
                }
            }
            for (PointerStatement lp : localPointers) {
                try {
                    resolver.resolveURIs(lp);
                } catch (IOException i) {

                }
            }
            //Pointers found in a fragment file are handled differently.
            Set<LabelParserException> externalProblems =
                                    new LinkedHashSet<LabelParserException>();
            for(PointerStatement ep : externalPointers) {
            //Missing referenced files are captured in the label
            //associated with the external pointer
                try {
                    resolver.resolveURIs(ep);
                    externalProblems.addAll(ep.getLabel().getProblems());
                } catch (IOException i) {

                }
            }

            //Add these external problems back into the parent label
            for(LabelParserException externalProblem : externalProblems) {
                //Check for duplication of messages
                if(!label.getProblems().contains(externalProblem)) {
                    label.addProblem(externalProblem.getSourceURI(), externalProblem);
                }
            }
        }
    }

    private List<PointerStatement> getPointersRecursively(Label label) {
        List<PointerStatement> result = new ArrayList<PointerStatement>();

        //TODO: See if we can put this functionality into the Label class
        result.addAll(label.getPointers());
        for(ObjectStatement object : label.getObjects()) {
            result.addAll(getPointersRecursively(object));
        }
        Collections.sort(result);
        return result;
    }

    private List<PointerStatement> getPointersRecursively(ObjectStatement object) {
        List<PointerStatement> result = new ArrayList<PointerStatement>();

        result.addAll(object.getPointers());
        for(ObjectStatement nestedObject : object.getObjects()) {
            result.addAll(getPointersRecursively(nestedObject));
        }
        return result;
    }
}
