package gov.nasa.pds.web.ui.actions;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Base action for json type results. All json results should come through an
 * action that extends this.
 * 
 * Results of this type have appropriate headers set
 * 
 * @see gov.nasa.pds.web.ui.interceptors.InitializeInterceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
 * 
 * @author jagander
 */
public abstract class BaseJSONAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	/**
	 * JSON output string for display in browser or return result in ajax
	 * request
	 */
	protected String json = ""; //$NON-NLS-1$

	/**
	 * Top level container for all return results
	 */
	protected JSONObject jsonContainer = new JSONObject();

	/**
	 * Flag for whether a custom submission handler has been set or a default
	 * ajax handler should be used. This is not currently made use of but it is
	 * a common practice as ajax requests become more common in an application.
	 */
	protected boolean hasSubmitHandler = false;

	/**
	 * Set a submission handler for returned results. This becomes common when
	 * using an advanced javascript architecture that has built in error
	 * handling, which you sometimes need to override.
	 */
	public void setSubmitHandler(final String hasHandler) {
		try {
			this.hasSubmitHandler = Boolean.parseBoolean(hasHandler);
		} catch (final Exception e) {
			// TODO: determine if this should be logged or some error should
			// appear in the UI somehow
		}
	}

	/**
	 * Primary execution method, wraps the action specific execution
	 * 
	 * @return result string
	 */
	@Override
	public String execute() {

		try {

			// do the action execution
			executeInner();
		} catch (Exception e) {

			// if an uncaught exception, add it for later retrieval and display
			addError(e);
		}

		// render results to json string
		renderJsonString();

		return JSON;
	}

	/**
	 * Primary execution method that implementations of this class must use.
	 * 
	 * @see #execute()
	 */
	protected abstract void executeInner() throws Exception;

	/**
	 * Add an exception for display. Currently there is no implementation for
	 * display of errors with/through javascript. Future implementations should
	 * have a fading dialog box that displays the errors.
	 * 
	 * @param e
	 *            exception to add
	 */
	public void addError(Exception e) {
		// stub
	}

	/**
	 * Render output construct to appropriate javascript string
	 */
	@SuppressWarnings("nls")
	protected void renderJsonString() {
		try {
			// indicate this is valid json, lets class know this isn't a bad
			// return
			this.jsonContainer.put("validJSON", true);

			// if the page handles the result or there are errors, send
			// everything
			// down
			if (this.hasSubmitHandler || hasErrors() || hasWarnings()) {
				JSONArray errorContainer = new JSONArray();
				JSONArray warningContainer = new JSONArray();
				JSONArray noticeContainer = new JSONArray();

				// push errors into JSONObject
				populateJSONArrayFromList(getErrorMessages(), errorContainer);
				// push non-blocking errors into JSONObject
				populateJSONArrayFromList(getWarningMessages(),
						warningContainer);
				// push notices into JSONObject
				populateJSONArrayFromList(getNoticeMessages(), noticeContainer);
				// put errors and notices in return object if non-empty
				if (hasErrors()) {
					this.jsonContainer.put("errors", errorContainer);
				}
				if (hasWarnings()) {
					this.jsonContainer.put("warnings", warningContainer);
				}
				if (hasNotices()) {
					this.jsonContainer.put("notices", noticeContainer);
				}
			}

			// if no errors and no handler, tell the popup to refresh
			if (!this.hasSubmitHandler && !hasErrors()) {
				this.jsonContainer.put("doRefresh", true);
			}
		} catch (final Exception e) {

			// noop
		}

		// render the result to string
		this.json = this.jsonContainer.toString();
	}

	/**
	 * Push a list into a json array
	 */
	// TODO: update everything to use MessageContainer
	protected void populateJSONArrayFromList(final List<String> list,
			final JSONArray array) throws JSONException {
		/*
		 * for (final MessageContainer item : list) { final JSONObject
		 * jsonObject = new JSONObject(); try { jsonObject.put("id",
		 * item.getId()); jsonObject.put("message", item.getMessage()); } catch
		 * (JSONException e) { // why would this happen exactly?
		 * e.printStackTrace(); } array.put(jsonObject); }
		 */
		for (final String item : list) {
			final JSONObject jsonObject = new JSONObject();

			jsonObject.put("id", item); //$NON-NLS-1$

			array.put(jsonObject);
		}
	}

	/**
	 * Get the json output string
	 * 
	 * @return the json output string
	 */
	public String getJson() {
		return this.json;
	}
}
