package gov.nasa.pds.tools.validate.rule.pds4;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nasa.pds.tools.validate.rule.AbstractValidationChain;

/**
 * Implements a validation chain for checking a single PDS4 label.
 */
public class LabelValidationChain extends AbstractValidationChain {

	private static final Pattern LABEL_PATTERN = Pattern.compile(".*\\.xml", Pattern.CASE_INSENSITIVE);

	@Override
	public boolean isApplicable(String location) {
		File f = new File(location);

		if (!f.isFile() || !f.canRead()) {
			return false;
		}

		Matcher matcher = LABEL_PATTERN.matcher(f.getName());
		return matcher.matches();
	}

}
