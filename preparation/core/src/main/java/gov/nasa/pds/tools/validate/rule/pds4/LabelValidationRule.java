package gov.nasa.pds.tools.validate.rule.pds4;

import gov.nasa.pds.tools.label.LabelValidator;
import gov.nasa.pds.tools.label.MissingLabelSchemaException;
import gov.nasa.pds.tools.validate.ValidationResourceManager;
import gov.nasa.pds.tools.validate.rule.AbstractValidationRule;
import gov.nasa.pds.tools.validate.rule.GenericProblems;
import gov.nasa.pds.tools.validate.rule.ValidationTest;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Implements a validation chain that validates PDS4 bundles. It is applicable
 * if there is a bundle label in the root directory.
 */
public class LabelValidationRule extends AbstractValidationRule {

	private static final Pattern LABEL_PATTERN = Pattern.compile(".*\\.xml", Pattern.CASE_INSENSITIVE);

	private static final String XML_SUFFIX = ".xml";

	@Override
	public boolean isApplicable(String location) {
		File f = new File(location);

		if (!f.isFile() || !f.canRead()) {
			return false;
		}

		Matcher matcher = LABEL_PATTERN.matcher(f.getName());
		return matcher.matches();
	}

	/**
	 * Implements a rule that checks the label file extension.
	 */
	@ValidationTest
	public void checkLabelExtension() {
		if (!getTarget().getName().endsWith(XML_SUFFIX)) {
			reportError(PDS4Problems.INVALID_LABEL_EXTENSION, getTarget(), -1, -1);
		}
	}

	/**
	 * Parses the label and records any errors resulting from the parse,
	 * including schema and schematron errors.
	 */
	@ValidationTest
	public void validateLabel() {
		ExceptionProcessor processor = new ExceptionProcessor(getListener(), getTarget());

        LabelValidator validator = ValidationResourceManager.INSTANCE.getResource(LabelValidator.class);
		try {
			Document document = validator.parseAndValidate(processor, getTarget().toURI().toURL());
			getContext().put(PDS4Context.LABEL_DOCUMENT, document);
		} catch (SAXException | IOException | ParserConfigurationException
				| TransformerException | MissingLabelSchemaException e) {
			reportError(GenericProblems.UNCAUGHT_EXCEPTION, getTarget(), -1, -1, e.getMessage());
		}
	}

}
