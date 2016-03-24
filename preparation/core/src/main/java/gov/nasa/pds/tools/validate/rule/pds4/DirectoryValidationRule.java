package gov.nasa.pds.tools.validate.rule.pds4;

import gov.nasa.pds.tools.validate.rule.AbstractValidationChain;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements a rule chain for validating PDS4 directories, but not
 * necessarily bundles or collections.
 */
public class DirectoryValidationRule extends AbstractValidationChain {

	private static final Pattern LABEL_PATTERN = Pattern.compile(".*\\.xml", Pattern.CASE_INSENSITIVE);

	@Override
	public boolean isApplicable(String location) {
		File f = new File(location);

		if (!f.isDirectory()) {
			return false;
		}

		// Check for PDS4 labels.
		for (String childName : f.list()) {
			Matcher matcher = LABEL_PATTERN.matcher(childName);
			if (matcher.matches()) {
				return true;
			}
		}

		return false;
	}

}
