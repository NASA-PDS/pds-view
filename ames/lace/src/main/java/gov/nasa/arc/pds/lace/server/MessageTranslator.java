package gov.nasa.arc.pds.lace.server;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Defines a class for translating DOM and Schematron error messages
 * into more readable forms.
 */
@Singleton
public class MessageTranslator {
	
	// The default translation removes the prefix before the first colon.
	private static final Translation DEFAULT_TRANSLATION =
			new Translation("^[A-Za-z0-9._-]+: (.*)", Action.TRANSLATE, "{1}", null, null);
	
	private List<Translation> translations = new ArrayList<Translation>();

	@Inject
	public MessageTranslator() {
		Properties props = new Properties();
		try {
			props.load(getClass().getResourceAsStream("MessageTranslator.properties"));
		} catch (IOException e) {
			System.err.println("Could not load properties translations: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		for (Object keyObj : props.keySet()) {
			String key = (String) keyObj;
			if (!key.endsWith(".FORMAT") && !key.endsWith(".OMIT")) {
				String pattern = props.getProperty(key);
				
				// Replace () and [] with \(\) and \[\].
				pattern = pattern.replaceAll("([\\[\\]\\(\\)])", "\\\\$1");
				
				// Replace doubled single-quotes with a placeholder value.
				pattern = pattern.replaceAll("''", "!!!SINGLE_QUOTE!!!");
				
				// Replace {N} with (.*).
				pattern = pattern.replaceAll("\\{[0-9]+\\}", "(.*)");
				
				// Replace remaining {} with \{\}.
				pattern = pattern.replaceAll("([\\{\\}])", "\\\\$1");
				
				// Remove all remaining single quotes.
				pattern = pattern.replaceAll("'", "");
				
				// Now replace the doubled-single-quotes with a single quote.
				pattern = pattern.replaceAll("!!!SINGLE_QUOTE!!!", "'");
				
				if (props.containsKey(key + ".FORMAT")) {
					Translation translation = new Translation(
							pattern,
							Action.TRANSLATE,
							props.getProperty(key + ".FORMAT"),
							props.getProperty(key + ".ATTRIBUTE"),
							props.getProperty(key + ".VALUE")
					);
					translations.add(translation);
				} else if (props.containsKey(key + ".OMIT")) {
					Translation translation = new Translation(
							pattern,
							Action.OMIT,
							null,
							null,
							null
					);
					translations.add(translation);
				}
			}
		}
	}
	
	/**
	 * Translates a message to another form, if a matching translation
	 * is found. Otherwise returns the message unchanged.
	 * 
	 * @param message an error message to be translated
	 * @return a translated message, or the same message if no suitable translation exists
	 */
	public Translation translate(String message) { 	
		for (Translation translation : translations) {
			if (translation.matches(message)) {
				return translation;
			}
		}
		
		// No translation matched. Use the default translation, if it matches.
		if (DEFAULT_TRANSLATION.matches(message)) {
			return DEFAULT_TRANSLATION;
		}
		
		return null;
	}
	
	private enum Action {
		TRANSLATE,
		OMIT;
	}
	
	/**
	 * Defines a translation action for a message.
	 */
	public static class Translation {
		
		private Pattern pattern;
		private Action action;
		private MessageFormat resultFormat;
		private MessageFormat attributeNameFormat;
		private MessageFormat valueFormat;
		
		private Translation(String patStr, Action action, String formatStr, String attrStr, String valueStr) {
			this.pattern = Pattern.compile(patStr);
			this.action = action;
			if (formatStr != null) {
				this.resultFormat = new MessageFormat(formatStr);
			}
			if (attrStr != null) {
				this.attributeNameFormat = new MessageFormat(attrStr);
			}
			if (valueStr != null) {
				this.valueFormat = new MessageFormat(valueStr);
			}
		}
		
		/**
		 * Gets the translation action.
		 */
		public Action getAction() {
			return action;
		}
		
		public boolean shouldOmit() {
			return action == Action.OMIT;
		}
		
		private boolean matches(String message) {
			return pattern.matcher(message).matches();
		}
		
		public String translate(String message) {
			return applyFormat(resultFormat, message);
		}
		
		public String getAttributeName(String message) {
			String result = applyFormat(attributeNameFormat, message);
			
			// Remove the "@" at the beginning of the attribute name.
			return (result==null ? null : result.replace("@", ""));
		}

		public String getValue(String message) {
			return applyFormat(valueFormat, message);
		}
		
		private String applyFormat(MessageFormat format, String message) {
			if (format == null) {
				return null;
			}
			
			Matcher matcher = pattern.matcher(message);
			matcher.matches();
			Object[] groups = new Object[matcher.groupCount() + 1];
			for (int i=0; i < groups.length; ++i) {
				groups[i] = matcher.group(i);
			}
			
			// Remove the "@" at the beginning of the attribute name.
			return format.format(groups);
		}
		
	}
	
}
