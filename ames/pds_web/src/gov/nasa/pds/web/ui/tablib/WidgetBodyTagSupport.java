package gov.nasa.pds.web.ui.tablib;

import gov.nasa.pds.web.ui.actions.BaseAction;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts2.views.jsp.TagUtils;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;

public abstract class WidgetBodyTagSupport extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	protected String basePath;

	protected StringBuffer contentBuffer = new StringBuffer();

	protected ValueStack stack;

	private BaseAction action;

	protected String getBasePath() {
		if (this.basePath == null) {
			this.basePath = ((HttpServletRequest) this.pageContext.getRequest())
					.getContextPath();
		}

		return this.basePath;
	}

	protected ValueStack getValueStack() {
		if (this.stack == null) {
			this.stack = TagUtils.getStack(this.pageContext);
		}

		return this.stack;
	}

	/*
	 * protected Boolean findBooleanVal(String value) { return
	 * ObjectUtils.getBooleanValue(findStringVal(value)); }
	 * 
	 * protected Integer findIntVal(String value) { return
	 * ObjectUtils.getIntegerValue(findStringVal(value)); }
	 */

	protected String findStringVal(String value) {
		return (String) getValueStack().findValue(value, String.class);
	}

	protected Object findValue(String value) {
		return getValueStack().findValue(value);
	}

	protected BaseAction getAction() {
		if (this.action == null) {
			// TODO: determine if actionContext is needed elsewhere
			ActionContext actionContext = ActionContext.getContext();
			this.action = (BaseAction) actionContext.getActionInvocation()
					.getAction();
		}
		return this.action;
	}

	protected String getWebRootRealPath() {
		return this.pageContext.getServletContext().getRealPath("/"); //$NON-NLS-1$
	}

	protected void out(final String string) {
		this.contentBuffer.append(string);
	}

	protected void writeBodyContents() throws IOException {
		if (this.bodyContent != null) {
			String bodyContents = this.bodyContent.getString();
			if (bodyContents == null || bodyContents.length() == 0) {
				this.contentBuffer.append("&nbsp;"); //$NON-NLS-1$
			} else {
				this.contentBuffer.append(bodyContents);
			}
			// clear out attribute values
			this.bodyContent.clear();
		}
	}

	@SuppressWarnings("nls")
	protected void writeContents() throws JspException {
		try {
			this.pageContext.getOut().print(this.contentBuffer);
			// overwrite the string buffer for next use on page
			this.contentBuffer = new StringBuffer();
			// nullify the stack so values are fresh
			this.stack = null;
			// nullify the action so that it's not cached across pages
			// TODO: investigate a way to clear this on action close rather than
			// tag close since it can/should be reused on same page
			this.action = null;
		} catch (Exception e) {
			throw new JspException("Error rendering "
					+ this.getClass().getName() + ". " + e.getMessage());
		}
	}

	@Override
	public int doStartTag() throws JspException {
		// evaluate contents between tags, override if not necessary in child
		// tag
		return EVAL_BODY_BUFFERED;
	}

	// TODO: determine if better way to do this
	// TODO: if not, move this somewhere else
	public String getPath() {
		HttpServletRequest request = (HttpServletRequest) this.pageContext
				.getRequest();
		while (request instanceof HttpServletRequestWrapper) {
			request = (HttpServletRequest) ((HttpServletRequestWrapper) request)
					.getRequest();
		}
		String uri = request.getRequestURI();
		String queryString = request.getQueryString();
		if (queryString.length() > 0) {
			uri += "?" + queryString; //$NON-NLS-1$
		}
		return uri;
	}
}
