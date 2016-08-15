package gov.nasa.arc.pds.lace.server.actions.auth;

import gov.nasa.arc.pds.lace.server.BaseAction;
import gov.nasa.arc.pds.lace.server.SessionConstants;
import gov.nasa.arc.pds.lace.server.oauth.OAuthUserInfo;
import gov.nasa.arc.pds.lace.server.project.ProjectManager;
import gov.nasa.arc.pds.lace.server.project.ProjectManager.UserRegistrationState;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

/**
 * Implements an action that allows the user to register for using the
 * application or to view registration approval or denial.
 */
@SuppressWarnings("serial")
@Results({
	@Result(name=RegisterAction.MUST_LOG_IN, location="login", type="redirectAction"),
})
public class RegisterAction extends BaseAction {

	// Default scope so they can be used in "Results" annotations.
	static final String MUST_LOG_IN = "login";
	static final String MUST_REGISTER = SUCCESS;
	static final String WAITING_FOR_APPROVAL = "approval";
	static final String APPROVED = "approved";
	static final String DENIED = "denied";

	private Provider<ProjectManager> projectManagerProvider;
	private OAuthUserInfo userInfo;

	// Form parameters.
	private String issuer;
	private String subject;
	private String action;
	private String name;
	private String email;
	private String affiliation;
	private String reason;
	private String agree;

	/**
	 * Creates a new instance of the action with a provider for a project manager.
	 *
	 * @param projectManagerProvider the project manager provider
	 */
	@Inject
	public RegisterAction(Provider<ProjectManager> projectManagerProvider) {
		this.projectManagerProvider = projectManagerProvider;
	}

	@Override
	public String executeInner() throws Exception {
		ProjectManager manager = projectManagerProvider.get();
		String loginID = (String) getSession().getAttribute(SessionConstants.USER_LOGIN_ID_PROPERTY);
		if (loginID == null) {
			return MUST_LOG_IN;
		}

		userInfo = getSessionAttribute(SessionConstants.USER_INFO_PROPERTY, OAuthUserInfo.class);

		// User is logged in. Check whether registration is needed.
		UserRegistrationState state = manager.getUserRegistrationState(loginID);
		if (state == UserRegistrationState.APPROVED) {
			return APPROVED;
		} else if (state == UserRegistrationState.DENIED) {
			return DENIED;
		} else if ("cancel".equals(action)) {
			return MUST_LOG_IN;
		} else if ("register".equals(action)) {
			return register();
		} else {
			return MUST_REGISTER;
		}
	}

	private boolean checkRequiredField(String content, String fieldName, String caption) {
		if (content==null || content.trim().isEmpty()) {
			addFieldError(fieldName, caption + " is a required field.");
			return false;
		}

		return true;
	}

	public String register() {
		boolean formOK = true;

		// Check that parameters are provided.
		formOK &= checkRequiredField(name, "name", "Name");
		formOK &= checkRequiredField(email, "email", "Email");
		formOK &= checkRequiredField(affiliation, "affiliation", "Affiliation");
		formOK &= checkRequiredField(reason, "reason", "Reason");

		if (!"true".equals(agree)) {
			addFieldError("agree", "You must agree to the Terms and Conditions.");
			formOK = false;
		}

		if (!formOK) {
			return MUST_REGISTER;
		}

		ProjectManager projectManager = projectManagerProvider.get();

		String userID = email;

		try {
			projectManager.createUserIfNeccesary(userID);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<String, String> properties = new HashMap<String, String>();
		properties.put("issuer", getIssuer());
		properties.put("subject", getSubject());
		properties.put("name", getName());
		properties.put("email", getEmail());
		properties.put("affiliation", getAffiliation());
		properties.put("reason", getReason());

		try {
			projectManager.setUserProperties(userID, properties);
			projectManager.setUserRegistrationState(userID, UserRegistrationState.AWAITING_APPROVAL);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return WAITING_FOR_APPROVAL;
	}

	public OAuthUserInfo getUserInfo() {
		return userInfo;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getAgree() {
		return agree;
	}

	public void setAgree(String agree) {
		this.agree = agree;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}
