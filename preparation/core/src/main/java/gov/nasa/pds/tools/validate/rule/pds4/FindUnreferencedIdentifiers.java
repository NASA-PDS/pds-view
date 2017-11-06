// Copyright 2006-2017, by the California Institute of Technology.
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
package gov.nasa.pds.tools.validate.rule.pds4;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import gov.nasa.pds.tools.label.ExceptionType;
import gov.nasa.pds.tools.label.LabelException;
import gov.nasa.pds.tools.validate.Identifier;
import gov.nasa.pds.tools.validate.ListenerExceptionPropagator;
import gov.nasa.pds.tools.validate.rule.AbstractValidationRule;
import gov.nasa.pds.tools.validate.rule.ValidationTest;

/**
 * Implements a validation rule that checks that all identifiers are
 * referenced by some label.
 */
public class FindUnreferencedIdentifiers extends AbstractValidationRule {
  private static final Pattern COLLECTION_LABEL_PATTERN = Pattern.compile("collection(_.*)*\\.xml", Pattern.CASE_INSENSITIVE);
  
  @Override
  public boolean isApplicable(String location) {
    // This rule is applicable at the top level only.
    return getContext().isRootTarget();
  }

  /**
   * Iterate over unreferenced targets, reporting an error for each.
   */
  @ValidationTest
  public void findUnreferencedIdentifiers() {
    // Only run the test if we are the root target, to avoid duplicate errors.
    if (getContext().isRootTarget()) {
      for (Identifier id : getRegistrar().getIdentifierDefinitions().keySet()) {  
        String location = getRegistrar().getTargetForIdentifier(id);
        getListener().addLocation(location);
        boolean found = false;
        for (Identifier ri : getRegistrar().getReferencedIdentifiers()) {
          if (ri.equals(id)) {
            found = true;
            getListener().addProblem(new LabelException(ExceptionType.INFO,
                "Identifier '" + id.toString()
                + "' is a member of '"
                + getRegistrar().getIdentifierReferenceLocation(id) + "'",
                location,
                location,
                null,
                null));
            break;
          }
        }
        if (!found) {
          String memberType = "collection";
          Matcher matcher = COLLECTION_LABEL_PATTERN.matcher(
              FilenameUtils.getName(location));
          if (matcher.matches()) {
            memberType = "bundle";
          }
          // Don't print out messages if were validating using collection rules and 
          // the identifier in question is a collection
          if ( !("bundle".equals(memberType) && 
              getContext().getRule().getCaption().equals("PDS4 Collection")) ) {
            getListener().addProblem(new LabelException(ExceptionType.WARNING,
                "Identifier '" + id.toString() + "' is not a member of any "
                    + memberType + " within the given target", 
                location,
                location,
                null,
                null));
          }     
        }
        if (getListener() instanceof ListenerExceptionPropagator) {
          ListenerExceptionPropagator lp = 
              (ListenerExceptionPropagator) getListener();
          lp.record(getTarget().toString());
        }
      }
    }
  }

}
