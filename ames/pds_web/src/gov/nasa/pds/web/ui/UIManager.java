package gov.nasa.pds.web.ui;

import gov.nasa.arc.pds.tools.util.LocaleUtils;

import java.util.Locale;

public class UIManager {

	/**
	 * Flag for whether dev mode is on
	 */
	private static Boolean devMode;

	/**
	 * An instance of locale utilities to do locale related activities such as
	 * getting locale specific text from properties files
	 */
	private LocaleUtils localeUtils;

	/**
	 * Instance of locale to use for locale specific activities
	 */
	private final Locale locale;

	/**
	 * Instantiate UI manager with a given locale
	 * 
	 * @param locale
	 *            the locale to use for all locale specific work
	 */
	public UIManager(Locale locale) {
		this.locale = locale;
	}

	/**
	 * Get a message from from a message bundle using a given key and arguments
	 * 
	 * @param key
	 *            message key
	 * @param arguments
	 *            array of arguments to use in message construction. Strings,
	 *            numbers and dates are the primary object types.
	 */
	public String getTxt(final String key, final Object... arguments) {
		return getLocaleUtils().getText(key, arguments);
	}

	/**
	 * Get the instance of locale utilities. Public so that it may be passed
	 * down into object construction where UIManager is not needed.
	 */
	public LocaleUtils getLocaleUtils() {
		if (this.localeUtils == null) {
			this.localeUtils = new LocaleUtils(this.locale, UIManager
					.isDevMode());
		}
		return this.localeUtils;
	}

	/**
	 * Is struts devMode set to true? Use this for debug messaging and increased
	 * strictness.
	 * 
	 * This is currently ALWAYS true. Support for setting in the build process
	 * should be added in at a later date.
	 * 
	 * @return is dev mode enabled?
	 * @see struts.properties (struts.devMode)
	 */
	public static Boolean isDevMode() {
		if (UIManager.devMode == null) {
			// TODO: get this from the properties file
			UIManager.devMode = Boolean.TRUE;
		}

		return devMode;
	}

}
