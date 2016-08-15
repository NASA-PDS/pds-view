package gov.nasa.arc.pds.lace.server;

/**
 * Defines attribute names for session storage values.
 */
public interface SessionConstants {

	/** The callback URL for OAuth authentication. */
	public static final String OAUTH_CALLBACK = "lace.oauth-callback";

	/** The currently logged in user ID. Only set if the user is approved to use the application. */
	public static final String USER_ID_PROPERTY = "lace.user";

	/** The ID under which the user logged in. Always set when the user logs in. */
	public static final String USER_LOGIN_ID_PROPERTY = "lace.login-id";

	/** An indication of which authentication provider was used to log in.
	 * Should be one of the values in {@link AuthenticationProvider}.
	 */
	public static final String AUTHENTICATION_PROVIDER = "lace.authentication-provider";

	/** The URI requested when authentication is required. */
	public static final String REQUESTED_URI = "lace.requested-uri";

	/** The OAuth access token returned from the authentication provider. */
	public static final String ACCESS_TOKEN = "lace.access-token";

	/** The location of the user's current project. */
	public static final String CURRENT_PROJECT_LOCATION_PROPERTY = "lace.project.location";

	/** A list of saved notification messages, if any. */
	public static final String SAVED_MESSAGES = "lace.saved.messages";

	/** A list of saved error messages, if any. */
	public static final String SAVED_ERRORS = "lace.saved.errors";

	// Attributes to store non-persistently.

	/** A provider object for OAuth authentication services. */
	public static final String OAUTH_PROVIDER = "lace.oauth-provider";

	/** The information about the currently logged-in user. */
	public static final String USER_INFO_PROPERTY = "lace.user-info";

	/** The current schema manager. */
	public static final String CURRENT_SCHEMA_MANAGER_PROPERTY = "lace.schema.manager";

}
