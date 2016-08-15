package gov.nasa.pds.web.ui.actions;

import gov.nasa.arc.pds.tools.util.FileUtils;
import gov.nasa.arc.pds.tools.util.StrUtils;
import gov.nasa.pds.web.BaseProcess;
import gov.nasa.pds.web.ui.UIManager;
import gov.nasa.pds.web.ui.constants.ApplicationConstants;
import gov.nasa.pds.web.ui.managers.LogManager;
import gov.nasa.pds.web.ui.utils.ApplicationProperties;
import gov.nasa.pds.web.ui.utils.ErrorCodeMapping;
import gov.nasa.pds.web.ui.utils.HTTPUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;

import com.opensymphony.xwork2.ActionSupport;

/**
 * This class acts as the basis for all actions. It should contain such
 * constants and methods that are applicable to all actions or JSP pages. Note
 * that to manage bloat in this class and provide access to common tasks outside
 * of struts, much of what otherwise might be placed here should instead be
 * located in a UIManager.
 * <p>
 * Note that few actions should directly reference this class. Instead they
 * should extend a sub-class that more closely fits the type of action you are
 * generating. Common types of actions are submit, view, filter, page, and json.
 * 
 * @author jagander
 */
public abstract class BaseAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

	/**
	 * Current session instance
	 */
	public transient HttpSession session;

	/**
	 * Last stored exception if any. Used to resurface in UI.
	 */
	private Exception exception;

	/**
	 * Instance of UI manager for convenience
	 */
	private UIManager uiManager;

	/**
	 * Location of data set directory on server, if it exists
	 */
	private File dataSetDirectory;

	/**
	 * Process id associated with the current action
	 */
	private String procId;

	/*
	 * Session keys
	 */

	/**
	 * Key for info level messages to be displayed in the UI
	 */
	public final static String ACTION_NOTICE = "actionNotice"; //$NON-NLS-1$

	/**
	 * Key for warning level messages to be displayed in the UI, this is the
	 * same as an error but is not blocking
	 */
	public final static String ACTION_WARNING = "actionWarning"; //$NON-NLS-1$

	/**
	 * Key for error level messages to be displayed in the UI
	 */
	public final static String ACTION_ERROR = "actionError"; //$NON-NLS-1$

	/**
	 * Key for invalid user submitted data - used to re-populate forms on error
	 */
	public final static String ACTION_USER_INPUT = "actionUserInput"; //$NON-NLS-1$

	/**
	 * Key for filters specific to a given view
	 */
	public final static String ACTION_FILTER = "actionFilter"; //$NON-NLS-1$

	/**
	 * Key for user id - id stored instead of object due to possible concurrency
	 * issues
	 */
	public final static String USER_ID_KEY = "userId"; //$NON-NLS-1$

	/**
	 * Key for getting process map from session
	 */
	public final static String PROCESS_KEY = "process"; //$NON-NLS-1$

	/*
	 * Additional Result Constants - used on in the return of the execute
	 * methods of Actions
	 * 
	 * @see com.opensymphony.xwork2.Action for other constants
	 */

	/**
	 * JSON return type. This is used on all json type results regardless of
	 * whether the action was successful or not.
	 */
	public static final String JSON = "json"; //$NON-NLS-1$

	/**
	 * Redirect return type. This is currently not used but is a stub for future
	 * use.
	 */
	public final static String REDIRECT = "redirect"; //$NON-NLS-1$

	/**
	 * An indication that the user has requested a protected resource
	 */
	public final static String JSON_LOGIN = "jsonLogin"; //$NON-NLS-1$

	/**
	 * Permissions violation - the authenticated user does not have access to
	 * the requested resource.
	 */
	public final static String ILLEGAL_ACCESS = "illegalAccess"; //$NON-NLS-1$

	/**
	 * GET params violation - accessing a resource without required info,
	 * usually a user directly accessing the resource. Typically used to
	 * redirect the user.
	 */
	public final static String MISSING_INFORMATION = "missingInformation"; //$NON-NLS-1$

	/*
	 * Miscellaneous constants
	 */

	/**
	 * Global identifier for "none" in select boxes
	 */
	public final static String NONE_OPTION = "-1"; //$NON-NLS-1$

	/**
	 * Global identifier for "all" in select boxes
	 */
	public final static String ALL_OPTION = "-2"; //$NON-NLS-1$

	/**
	 * Convenience int version of NONE_OPTION
	 */
	public final static int NONE_OPTION_INT = Integer.parseInt(NONE_OPTION);

	/**
	 * Convenience int version of ALL_OPTION
	 */
	public final static int ALL_OPTION_INT = Integer.parseInt(ALL_OPTION);

	/**
	 * Set session for this action
	 * 
	 * @param session
	 *            the current session
	 */
	public void setSession(final HttpSession session) {
		this.session = session;
	}

	/**
	 * Initialize session
	 */
	public void initSession() {
		if (this.session == null) {
			setSession(HTTPUtils.getSession());
		}
	}

	/**
	 * Get the current process id, assuming it is set
	 */
	public String getProcId() {
		return this.procId;
	}

	/**
	 * Get the current process, if it is set
	 */
	public BaseProcess getProcess() {
		// TODO accessors should be updated to throw a new exception
		// "InvalidProcess" exception or something.
		return HTTPUtils.getProcess(this.procId);
	}

	/**
	 * Set the process id
	 * 
	 * @param procId
	 *            the current process id
	 */
	public void setProcId(String procId) {
		this.procId = procId;
	}

	/*
	 * Messaging utilities. Used for displaying errors, and notices in the UI.
	 * 
	 * Note that this overrides message handling native to struts due to struts
	 * making messages be request scoped. Most messages will need to be
	 * displayed in the action following the action that generated the messages.
	 * e.g., a post is made to a processing action, the process completes
	 * successfully, a success message is generated, the user is redirected to a
	 * display page, and the success message is displayed on the resulting page.
	 * 
	 * These methods also extend the functionality of struts messaging to
	 * simplify externalization of strings and display reasonable messages for
	 * exceptions.
	 */

	@Override
	@Deprecated
	public void addActionError(String anErrorMessage) {
		throw new RuntimeException(
				"Not Implemented, use addError(final String key, final Object... arguments)"); //$NON-NLS-1$
	}

	@Override
	@Deprecated
	public void addActionMessage(String message) {
		throw new RuntimeException(
				"Not Implemented, use addNotice(final String key, final Object... arguments)"); //$NON-NLS-1$
	}

	@Override
	@Deprecated
	public boolean hasActionErrors() {
		throw new RuntimeException("Not Implemented, use hasErrors()"); //$NON-NLS-1$
	}

	@Override
	@Deprecated
	public boolean hasActionMessages() {
		throw new RuntimeException("Not Implemented, use hasErrors()"); //$NON-NLS-1$
	}

	/**
	 * Add an error message from the default properties file.
	 * <p>
	 * Note that the first element of the a. arguments array is inspected to see
	 * if it is itself an array. In this case the new array is used as the
	 * arguments list. This is a convenience for cases when you are assembling a
	 * long list of arguments or arguments are added conditionally in another
	 * section of code.
	 * 
	 * @param key
	 *            properties key used to look up message
	 * @param arguments
	 *            an array of arguments to be used in the message assembly
	 * 
	 * @see #addErrorSimple(String message)
	 * @see #getErrorMessage(String key, Object... arguments)
	 */
	public void addError(final String key, final Object... arguments) {
		addErrorSimple(getErrorMessage(key, arguments));
	}

	/**
	 * Add an error for a given exception and arguments.
	 * 
	 * This is not currently a supported method.
	 * 
	 * @param e
	 *            the exception to retrieve the error message from
	 * @param arguments
	 *            an array of arguments to be used in the message assembly
	 */
	public void addError(final Exception e, final Object... arguments) {
		// TODO: make all known errors implement a CodedException
		// TODO: get code from exception or use default "unknown" of -1 for
		// things that don't extend CodedException
		// TODO: create a message from exception - put something in StrUtils for
		// this that includes some of the stack trace
		throw new RuntimeException("not implemented"); //$NON-NLS-1$
	}

	/**
	 * Base error addition, just store the message in the session.
	 * 
	 * Private as all message additions should use properties files.
	 * 
	 * @param message
	 *            the error message to add
	 */
	private void addErrorSimple(final String message) {
		addMessage(message, ACTION_ERROR);
	}

	/**
	 * Get an error message for a given key and and arguments. This differs from
	 * other message retrieval in that it attempts to associate an error code
	 * with the error.
	 * 
	 * @param key
	 *            properties key used to look up message
	 * @param arguments
	 *            an array of arguments to be used in the message assembly
	 */
	protected String getErrorMessage(final String key,
			final Object... arguments) {
		String code = ""; //$NON-NLS-1$
		try {
			code = ErrorCodeMapping.get(key);
		} catch (Exception e) {
			// no key found, should be handled by tests but consider doing
			// something else here
		}
		// TODO: bind to the struts message lookup utils?
		final String message = getUIManager().getTxt(key, arguments);
		// TODO: determine if we want to conditionally display the code or just
		// format the message
		return code + ": " + message; //$NON-NLS-1$
	}

	/**
	 * Add notice message. This is an info level message.
	 * 
	 * @param key
	 *            properties key used to look up message
	 * @param arguments
	 *            an array of arguments to be used in the message assembly
	 */
	public void addNotice(final String key, final Object... arguments) {

		// get message
		final String message = getUIManager().getTxt(key, arguments);

		// add message to notice map
		addNoticeSimple(message);
	}

	/**
	 * Add notice message to session. Note that notice messages are displayed in
	 * a popup and must be javascript safe.
	 * 
	 * Private as all message additions should use properties files.
	 */
	private void addNoticeSimple(final String message) {
		addMessage(StrUtils.safeJS(message), ACTION_NOTICE);
	}

	/**
	 * Add a message to the session.
	 * 
	 * @param message
	 *            message to add to the session
	 * @param sessionKey
	 *            message type to use as a key in the session. Currently this is
	 *            limited to errors, warnings and notices.
	 */
	private void addMessage(final String message, final String sessionKey) {
		@SuppressWarnings("unchecked")
		Collection<String> messageMap = (Collection<String>) this.session
				.getAttribute(sessionKey);
		if (messageMap == null) {
			messageMap = new ArrayList<String>();
			this.session.setAttribute(sessionKey, messageMap);
		}
		messageMap.add(message);
	}

	/**
	 * Convenience method for checking if there are notices. Prepends "get" so
	 * that the method call in struts is not just "notices".
	 * 
	 * @return whether there are notices
	 */
	public boolean getHasNotices() {
		return hasNotices();
	}

	/**
	 * Check for whether notices are in the session
	 */
	public boolean hasNotices() {
		final Collection<?> notices = (Collection<?>) this.session
				.getAttribute(ACTION_NOTICE);
		return (notices != null) && ((notices.size() > 0));
	}

	/**
	 * Get all notice messages from the session. This clears the notices once
	 * retrieved.
	 */
	@SuppressWarnings("unchecked")
	public List<String> getNoticeMessages() {

		// separate list for notices so they may be returned AND cleared from
		// session
		final List<String> noticeList = new ArrayList<String>();

		// get notices from session
		final Collection<String> notices = (Collection<String>) this.session
				.getAttribute(ACTION_NOTICE);

		// add notices to return collection
		noticeList.addAll(notices);

		// clear notices
		notices.clear();

		return noticeList;
	}

	/**
	 * Add warning message
	 * 
	 * @param key
	 *            properties key used to look up message
	 * @param arguments
	 *            an array of arguments to be used in the message assembly
	 */
	public void addWarning(final String key, final Object... arguments) {

		// get message
		final String message = getUIManager().getTxt(key, arguments);

		// add to session
		addWarningSimple(message);
	}

	/**
	 * Base warning addition, just store the message in the session
	 * 
	 * Private as all message additions should use properties files.
	 */
	private void addWarningSimple(final String message) {
		addMessage(message, ACTION_WARNING);
	}

	/**
	 * Convenience method for checking if there are warnings. Prepends "get" so
	 * that the method call in struts is not just "warnings".
	 * 
	 * @return whether there are notices
	 */
	public boolean getHasWarnings() {
		return hasWarnings();
	}

	/**
	 * Check for whether warnings are in the session
	 */
	public boolean hasWarnings() {
		final Collection<?> warnings = (Collection<?>) this.session
				.getAttribute(ACTION_WARNING);
		return (warnings != null) && ((warnings.size() > 0));
	}

	/**
	 * Get warnings messages and clear from session once retrieved
	 * 
	 * @return warning messages
	 */
	@SuppressWarnings("unchecked")
	public List<String> getWarningMessages() {
		final List<String> warningList = new ArrayList<String>();
		final Collection<String> warnings = (Collection<String>) this.session
				.getAttribute(ACTION_WARNING);
		warningList.addAll(warnings);
		warnings.clear();
		return warningList;
	}

	/**
	 * Convenience method for checking if there are errors. Prepends "get" so
	 * that the method call in struts is not just "errors".
	 * 
	 * @return whether there are errors
	 */
	public boolean getHasErrors() {
		return hasErrors();
	}

	/**
	 * Check whether errors in the session
	 */
	@Override
	public boolean hasErrors() {
		final Collection<?> errors = (Collection<?>) this.session
				.getAttribute(ACTION_ERROR);
		return (errors != null) && ((errors.size() > 0));
	}

	/**
	 * Get error messages and clear from session once retrieved
	 * 
	 * @return error messages
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getErrorMessages() {
		final List<String> errorList = new ArrayList<String>();
		final Collection<String> errors = (Collection<String>) this.session
				.getAttribute(ACTION_ERROR);
		errorList.addAll(errors);
		errors.clear();
		return errorList;
	}

	/**
	 * Get error messages but do not clear from session, this is for test
	 * purposes only
	 * 
	 * @return error messages
	 */
	@SuppressWarnings("unchecked")
	public List<String> getErrorMessagesSimple() {
		final List<String> errorList = new ArrayList<String>();
		final Collection<String> errors = (Collection<String>) this.session
				.getAttribute(ACTION_ERROR);
		errorList.addAll(errors);

		return errorList;
	}

	/**
	 * Store an unhandled exception so that it may be displayed in the UI,
	 * printed to console, and logged. Any time this occurs, it means that there
	 * is a developmental error (bug) that should be fixed.
	 * 
	 * @param e
	 *            - the unhandled exception that was thrown
	 */
	public void setException(final Exception e) {

		// if in dev mode, print to console
		if (UIManager.isDevMode().equals(Boolean.TRUE)) {
			e.printStackTrace();
		}

		// set exception for display in ui
		this.exception = e;

		// log the exception so that it may be reviewed by developers on
		// production
		LogManager.logException(e);
	}

	/**
	 * Provide JSP access to the unhandled exception. This should only be called
	 * in the default error page. Any time this occurs, it means that there is a
	 * developmental error (bug) that should be fixed. The user is not expected
	 * to see this.
	 * 
	 * @return exception object
	 */
	public Exception getException() {
		return this.exception;
	}

	/**
	 * Get exception message (message only) for display
	 * 
	 * @return exception message
	 */
	public String getExceptionMessage() {

		// convert current exception, not for javascript, with 0 stack trace
		// lines
		return StrUtils.toString(getException(), false, 0);
	}

	/**
	 * Get exception message for display.
	 * 
	 * @return exception message
	 */
	public String getExceptionStackString() {

		// convert current exception for javascript with the default number of
		// stacktrace lines
		return StrUtils.toString(getException(), true, null);
	}

	/**
	 * Get UI manager instance
	 * 
	 * @return UI manager
	 */
	public UIManager getUIManager() {
		// TODO: get locale from user or browser
		if (this.uiManager == null) {
			this.uiManager = new UIManager(ApplicationConstants.DEFAULT_LOCALE);
		}
		return this.uiManager;
	}

	/**
	 * Get session value for a given class, context and key. Not currently used
	 * but is useful for class specific session values without having to worry
	 * about collisions.
	 * 
	 * @param className
	 *            classname context
	 * @param context
	 *            manually specified context
	 * @param key
	 *            property key
	 * 
	 * @return value associated with params if found
	 */
	public Object getSessionValue(final String className, final String context,
			final Object key) {
		Map<? extends Object, ? extends Object> contextValues = getSessionCollection(
				className, context);
		if (contextValues == null) {
			return null;
		}
		final Object value = contextValues.get(key);
		return value;

	}

	/**
	 * Simple accessor to the session
	 * 
	 * @return value associated with the key
	 */
	public Object getSessionValueSimple(final String key) {
		return this.session.getAttribute(key);
	}

	/**
	 * Get a collection value from the session for a given classname context and
	 * key
	 * 
	 * @param className
	 *            classname context
	 * @param key
	 *            collection key
	 * 
	 * @return collection associated with classname and key
	 */
	public Map<? extends Object, ? extends Object> getSessionCollection(
			final String className, final Object key) {
		Map<Object, Object> pageSessionValues = getSessionCollection(className);
		if (pageSessionValues == null) {
			return null;
		}
		@SuppressWarnings("unchecked")
		Map<Object, Object> contextValues = (Map<Object, Object>) pageSessionValues
				.get(key);
		if (contextValues == null) {
			return null;
		}
		return contextValues;
	}

	/**
	 * Get a collection value from the session for a given key.
	 * 
	 * @param key
	 *            collection key
	 * 
	 * @return collection associated with key
	 */
	public Map<Object, Object> getSessionCollection(final String key) {
		@SuppressWarnings("unchecked")
		Map<Object, Object> sessionCollection = (HashMap<Object, Object>) getSessionValueSimple(key);
		return sessionCollection;
	}

	// like setSessionValueSimple but adds to list if exists

	/**
	 * Add session value for current session
	 * 
	 * @param key
	 *            object key
	 * @param object
	 *            object to add to session, must be serializable
	 */
	public void addSessionValueSimple(final String key, final Object object) {
		addSessionValueSimple(this.session, key, object);
	}

	/**
	 * Add session value for a given session and key.
	 * 
	 * @param session
	 *            the session to add the object to
	 * @param key
	 *            object key
	 * @param object
	 *            object to add to the session
	 */
	@SuppressWarnings("unchecked")
	public static void addSessionValueSimple(final HttpSession session,
			final String key, final Object object) {
		Object value = session.getAttribute(key);
		if (value == null) {
			List<Object> list = new ArrayList<Object>();
			list.add(object);
			session.setAttribute(key, list);
		} else {
			if (value instanceof List) {
				((List<Object>) value).add(object);
			} else {
				// if was not a list, overwrite
				List<Object> list = new ArrayList<Object>();
				list.add(object);
				session.setAttribute(key, list);
			}
		}
	}

	/**
	 * Set a session value with a given classname context, manual context and
	 * key.
	 * 
	 * @param className
	 *            classname context
	 * @param context
	 *            manually set context
	 * @param key
	 *            object key
	 * @param value
	 *            object to add to session
	 */
	public void setSessionValue(final String className, final String context,
			final Object key, final Object value) {
		if (value != null) {
			// get the session values specific to that class
			@SuppressWarnings("unchecked")
			Map<? super String, Object> pageSessionValues = (HashMap<? super String, Object>) getSessionValueSimple(className);
			if (pageSessionValues == null) {
				pageSessionValues = new HashMap<Object, Object>();
				this.session.setAttribute(className, pageSessionValues);
			}

			// get the filter collection
			@SuppressWarnings("unchecked")
			Map<Object, Object> contextValues = (Map<Object, Object>) pageSessionValues
					.get(context);
			// create filter collection if didn't exist
			if (contextValues == null) {
				contextValues = new HashMap<Object, Object>();
				pageSessionValues.put(context, contextValues);
			}

			// if value is empty, remove it, else, add it
			if (value.toString().equals("")) { //$NON-NLS-1$
				contextValues.remove(key);
			} else {
				contextValues.put(key, value);
			}
		}
	}

	/**
	 * Get user locale. Currently this just returns the default locale as locale
	 * support is not implemented. This and other methods remain so that no
	 * extra structuring is necessary when locale detection or locale retrieval
	 * from user prefs and other property files are added.
	 */
	public Locale getUserLocale() {
		return ApplicationConstants.DEFAULT_LOCALE;
	}

	/**
	 * Has the user been authenticated? Currently a stub.
	 */
	public Boolean isAuthenticated() {
		// stub
		return true;
	}

	/**
	 * Get files from request.
	 */
	// TODO: move to UIManager
	// TODO: add basic error support
	public File[] getFiles(final String fileName) {
		final MultiPartRequestWrapper multipartRequest = ((MultiPartRequestWrapper) ServletActionContext
				.getRequest());
		return multipartRequest.getFiles(fileName);
	}

	/**
	 * Get data set directory. This is specific to the validation tool when
	 * volumes are on the local server. As this feature is for development or
	 * the rare case where a user installs the app, it has not been factored out
	 * appropriately.
	 */
	// TODO: move this somewhere else
	public File getDataSetDirectory() {
		if (this.dataSetDirectory == null) {
			final ServletContext servletContext = ServletActionContext
					.getServletContext();
			final String rootPath = servletContext.getRealPath("/"); //$NON-NLS-1$
			final File root = new File(rootPath);
			// log.trace("root exists = " + root.exists());
			final File webInf = new File(root, "WEB-INF"); //$NON-NLS-1$
			// log.trace("WEB-INF exists = " + webInf.exists());
			final File defaultDirectory = new File(webInf, "dataSets"); //$NON-NLS-1$
			defaultDirectory.mkdirs();

			File directory = null;

			final ApplicationProperties props = new ApplicationProperties();
			if (props.exists()) {
				final String foundPath = props.getDataSetRoot();
				String path = foundPath != null ? foundPath : defaultDirectory
						.toString();
				directory = new File(path);
				if (!FileUtils.exists(directory)) {
					addError("manageDataSets.error.invalidDataSetPath", path); //$NON-NLS-1$
				}
			} else {
				// log.trace("config not found");
			}

			if (directory == null) {
				// log.trace("using default data set directory "
				// + defaultDirectory.toString());
				directory = defaultDirectory;
			}
			this.dataSetDirectory = directory;
		} else {
			// log.trace("using data set directory "
			// + this.dataSetDirectory.toString());
		}
		return this.dataSetDirectory;
	}

	/**
	 * Override the data set directory, for test purposes only.
	 * 
	 * @see BaseAction#getDataSetDirectory()
	 */
	public void overrideDataSetDirectory(final File directory) {
		this.dataSetDirectory = directory;
	}

}
