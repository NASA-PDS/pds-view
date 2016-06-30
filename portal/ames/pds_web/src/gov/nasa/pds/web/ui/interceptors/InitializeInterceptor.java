package gov.nasa.pds.web.ui.interceptors;

import gov.nasa.arc.pds.tools.util.StrUtils;
import gov.nasa.pds.web.ui.actions.BaseAction;
import gov.nasa.pds.web.ui.actions.BaseJSONAction;
import gov.nasa.pds.web.ui.actions.FileInterface;
import gov.nasa.pds.web.ui.actions.JavaScriptAction;

import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class InitializeInterceptor implements Interceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("nls")
	@Override
	public String intercept(ActionInvocation actionInvocation) throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();

		// get action
		BaseAction action = (BaseAction) actionInvocation.getAction();
		// make sure session is set up
		action.initSession();

		try {
			// get locale - tries session, then prefs, then default
			Locale locale = action.getUserLocale();
			// set locale in context so consumable by struts
			actionInvocation.getInvocationContext().setLocale(locale);

			// tell browser what the content type is and that it's in UTF-8
			if (action instanceof BaseJSONAction) {
				// see http://www.ietf.org/rfc/rfc4627.txt referenced from
				// json.org
				response.setContentType("application/json; charset=UTF-8");
			} else if (action instanceof JavaScriptAction) {
				response.setContentType("text/javascript");
			} else if (action instanceof FileInterface) {
				FileInterface fileAction = (FileInterface) action;
				response.setContentType(fileAction.getContentType());
				final String fileName = fileAction.getFileName();
				if (!StrUtils.nullOrEmpty(fileName)) {
					response.setHeader("Content-Disposition",
							"attachment; filename=\""
									+ fileAction.getFileName() + "\"");
				}
				response.setHeader("Content-Transfer-Encoding", "binary");
				response.setHeader("Accept-Ranges", "bytes");
				response.setHeader("Cache-control", "private");
				response.setHeader("Pragma", "private");
			} else {
				// NOTE: un-comment the below to verify well formatted xhtml
				// NOTE: do not commit as xhtml, fails in IE
				// response.setContentType("application/xhtml+xml; charset=UTF-8");
				response.setContentType("text/html; charset=UTF-8");
			}
			return actionInvocation.invoke();
		} catch (Exception e) {
			if (actionInvocation.getAction() instanceof BaseJSONAction) {
				BaseJSONAction jsonAction = (BaseJSONAction) action;
				jsonAction.addError(e);
				return jsonAction.execute();
			}
			action.setException(e);
			return Action.ERROR;
		}
	}
}
