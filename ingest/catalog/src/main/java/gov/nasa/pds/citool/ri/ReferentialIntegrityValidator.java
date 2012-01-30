// Copyright 2009, by the California Institute of Technology.
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

package gov.nasa.pds.citool.ri;

import gov.nasa.pds.citool.report.ReferentialIntegrityReport;
import gov.nasa.pds.citool.status.Status;
import gov.nasa.pds.tools.label.Label;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that checks a set of PDS catalog files for referential integrity.
 *
 * @author mcayanan
 *
 */
public class ReferentialIntegrityValidator {
    List<RIChecker> riCheckers;
    private ReferentialIntegrityReport report;

    /**
     * Default constructor.
     *
     * @param report An object representation of a referential integrity
     * report to log the errors.
     */
    public ReferentialIntegrityValidator(ReferentialIntegrityReport report) {
        this.report = report;
        riCheckers = new ArrayList<RIChecker>();
    }

    public boolean isValid(List<Label> catalogs, URL allrefs) {
        boolean isValid = true;
        RIFileSorter sorter = new RIFileSorter(catalogs);
        report.printHeader();
        for (RIChecker checker : riCheckers) {
            sorter.sort(checker.getType());
            report.setIntegrityCheckHeader(checker.getType().getName().replaceAll("_", " "));
            if (checker instanceof ReferenceRIChecker) {
                checker.setSupportFile(allrefs);
            }
            if (sorter.getChildren().isEmpty()) {
                report.recordSkip(sorter.getParentFiles(), new Exception(
                        "No children to perform referential integrity checking."));
            } else {
                checker.performCheck(sorter.getParents(), sorter.getChildren());
                Status status = report.record(sorter.getParentFiles(), checker.getProblems());
                if (Status.FAIL.getName().equals(status)) {
                    isValid = false;
                }
            }
        }
        return isValid;
    }

    /**
     * Adds an appropriate checker to the validator.
     *
     * @param checkers A list of referential integrity checks to perform.
     */
    public void addRICheckers(List<RIChecker> checkers) {
        riCheckers.addAll(checkers);
    }

    public void addRIChecker(RIType type) {
        if (RIType.DATA_SET.equals(type)) {
            riCheckers.add(new DataSetRIChecker());
        } else if (RIType.INSTRUMENT.equals(type)) {
            riCheckers.add(new InstrumentRIChecker());
        } else if (RIType.INSTRUMENT_HOST.equals(type)) {
            riCheckers.add(new InstrumentHostRIChecker());
        } else if (RIType.MISSION.equals(type)) {
            riCheckers.add(new MissionRIChecker());
        } else if (RIType.PERSONNEL.equals(type)) {
            riCheckers.add(new PersonnelRIChecker());
        } else if (RIType.REFERENCE.equals(type)) {
            riCheckers.add(new ReferenceRIChecker());
        } else if (RIType.TARGET.equals(type)) {
            riCheckers.add(new TargetRIChecker());
        } else if (RIType.VOLUME.equals(type)) {
            riCheckers.add(new VolumeRIChecker());
        }
    }
}
