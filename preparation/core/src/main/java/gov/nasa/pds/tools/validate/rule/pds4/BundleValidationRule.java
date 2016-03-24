package gov.nasa.pds.tools.validate.rule.pds4;

import gov.nasa.pds.tools.validate.rule.AbstractValidationChain;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements a validation chain that validates PDS4 bundles. It is applicable
 * if there is a bundle label in the root directory.
 */
public class BundleValidationRule extends AbstractValidationChain {

	private static final Pattern BUNDLE_LABEL_PATTERN = Pattern.compile("bundle(_.*)\\.xml", Pattern.CASE_INSENSITIVE);

	@Override
	public boolean isApplicable(String location) {
		File f = new File(location);

		if (!f.isDirectory()) {
			return false;
		}

		// Check for bundle(_.*)?\.xml file.
		for (String childName : f.list()) {
			Matcher matcher = BUNDLE_LABEL_PATTERN.matcher(childName);
			if (matcher.matches()) {
				return true;
			}
		}

		return false;
	}

}
