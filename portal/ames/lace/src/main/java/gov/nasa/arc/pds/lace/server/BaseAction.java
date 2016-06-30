package gov.nasa.arc.pds.lace.server;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public abstract class BaseAction extends ActionSupport {

	private static final Logger LOG = LoggerFactory.getLogger(BaseAction.class);

	private Provider<HttpSession> sessionProvider;
	private Provider<HttpServletRequest> requestProvider;
	private Provider<HttpServletResponse> responseProvider;
	private SessionAttributesManager sessionAttributesManager;

	@Inject
	private void setSessionProvider(Provider<HttpSession> sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	@Inject
	private void setRequestProvider(Provider<HttpServletRequest> requestProvider) {
		this.requestProvider = requestProvider;
	}

	@Inject
	private void setResponseProvider(Provider<HttpServletResponse> responseProvider) {
		this.responseProvider = responseProvider;
	}

	@Inject
	private void setSessionAttributesManager(SessionAttributesManager manager) {
		this.sessionAttributesManager = manager;
	}

	@Override
	public final String execute() throws Exception {
		HttpSession session = getSession();

		// Restore messages and errors, if they were stored in the session.
		@SuppressWarnings("unchecked")
		Collection<String> messages = (Collection<String>) session.getAttribute(SessionConstants.SAVED_MESSAGES);
		if (messages != null) {
			setActionMessages(messages);
			session.removeAttribute(SessionConstants.SAVED_MESSAGES);
		}

		@SuppressWarnings("unchecked")
		Collection<String> errors = (Collection<String>) session.getAttribute(SessionConstants.SAVED_ERRORS);
		if (messages != null) {
			setActionMessages(errors);
			session.removeAttribute(SessionConstants.SAVED_ERRORS);
		}

		try {
			return executeInner();
		} catch (Exception ex) {
			LOG.error("Uncaught exception in BaseAction", ex);
			throw ex;
		} finally {
			if (hasActionMessages()) {
				session.setAttribute(SessionConstants.SAVED_MESSAGES, getActionMessages());
			}
			if (hasActionErrors()) {
				session.setAttribute(SessionConstants.SAVED_ERRORS, getActionMessages());
			}
		}
	}

	protected HttpSession getSession() {
		return sessionProvider.get();
	}

	protected HttpServletRequest getRequest() {
		return requestProvider.get();
	}

	protected HttpServletResponse getResponse() {
		return responseProvider.get();
	}

	protected <T> T getSessionAttribute(String key, Class<T> clazz) {
		return sessionAttributesManager.getAttribute(getSession().getId(), key, clazz);
	}

	protected void setSessionAttribute(String key, Object value) {
		sessionAttributesManager.setAttribute(getSession().getId(), key, value);
	}

	protected void removeSessionAttribute(String key) {
		sessionAttributesManager.removeAttribute(getSession().getId(), key);
	}

	/**
	 * Subclasses should override this method to perform the action.
	 *
	 * @return the view that should be used, or null if the action has completed the response
	 * @throws Exception if there is an error executing the action
	 */
	public abstract String executeInner() throws Exception;

}
