package gov.nasa.pds.tools.validate.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.Command;

/**
 * Implements an object that allows callers to find out what
 * validators exist.
 */
public class ValidationRuleManager {

	private final Catalog catalog;

	/**
	 * Creates a new instance with a given catalog.
	 *
	 * @param catalog the rule catalog
	 */
	public ValidationRuleManager(Catalog catalog) {
		this.catalog = catalog;
	}

	/**
	 * Gets the list of available validators. The list is sorted
	 * by the validator caption string.
	 *
	 * @return a list of validators
	 */
	public List<ValidationRule> getValidators() {
		@SuppressWarnings("unchecked")
		Iterator<String> names = catalog.getNames();

		List<ValidationRule> rules = new ArrayList<ValidationRule>();

		while (names.hasNext()) {
			String name = names.next();
			Command command = catalog.getCommand(name);
			if (command instanceof ValidationRule) {
				rules.add((ValidationRule) command);
			}
		}

		Collections.sort(rules, new Comparator<ValidationRule>() {
			@Override
			public int compare(ValidationRule r, ValidationRule s) {
				return r.getCaption().compareTo(s.getCaption());
			}

		});

		return rules;
	}

	/**
	 * Finds an applicable rule for the given location.
	 *
	 * @param location the location to validate
	 * @return an applicable rule, or null if no such rule found
	 */
	public ValidationRule findApplicableRule(String location) {
		for (ValidationRule rule : getValidators()) {
			if (rule.isApplicable(location)) {
				return rule;
			}
		}

		return null;
	}

	/**
	 * Finds a validation rule by name.
	 *
	 * @param name the rule name
	 * @return the validation rule, or null if no such rule found
	 */
	public ValidationRule findRuleByName(String name) {
        Command cmd = catalog.getCommand(name);
        if (cmd instanceof ValidationRule) {
            return (ValidationRule) cmd;
        }

        return null;
	}

	/**
	 * Finds a rule given its caption.
	 *
	 * @param caption the rule caption
	 * @return the rule with that caption, or null if no such rule
	 */
	public ValidationRule findRuleByCaption(String caption) {
		for (ValidationRule rule : getValidators()) {
			if (rule.getCaption().equals(caption)) {
				return rule;
			}
		}

		return null;
	}

}
