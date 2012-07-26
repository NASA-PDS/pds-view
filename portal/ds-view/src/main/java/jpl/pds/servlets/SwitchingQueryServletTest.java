// Copyright 2003 California Institute of Technology.  ALL RIGHTS RESERVED.
// U.S. Government Sponsorship acknowledged.
//
// $Id$

package jpl.pds.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import junit.framework.TestCase;

/**
 * Unit test for {@link SwitchingQueryServlet}.
 *
 * @author Kelly
 * @version $Revision$
 */
public abstract class SwitchingQueryServletTest extends TestCase implements ServletConfig, HttpServletRequest, RequestDispatcher,
	ServletContext {

	/**
	 * Creates a new <code>SwitchingQueryServletTest</code> instance.
	 *
	 * @param caseName Test case name.
	 */
	public SwitchingQueryServletTest(String caseName) {
		super(caseName);
	}

	/**
	 * Test the servlet.  It should eventually call our request dispatcher with the
	 * correct path and have removed the resclass parameter.
	 *
	 * @throws ServletException if an error occurs.
	 * @throws IOException if an error occurs.
	 */
	public void testServlet() throws ServletException, IOException {
		SwitchingQueryServlet servlet = new SwitchingQueryServlet();
		servlet.init(this);
		servlet.doGet(this, null);
		servlet.doPost(this, null);
	}

	/**
	 * Masquerading as the servlet config, return the only init parameter.  This
	 * paramter maps the testing resclass <code>tab</code> to the resource path
	 * <code>/slot</code>.
	 *
	 * @param name a {@link String} parameter name.
	 * @return a {@link String} value, which if <var>name</var> is <code>tab</code>, is <code>/slot</code>.
	 */
	public String getInitParameter(String name) {
		return "tab".equals(name)? "/slot" : null;
	}

	/**
	 * Masquerading as the servlet config, return the name of the only init parameter.
	 *
	 * @return an {@link Enumeration} with just one string, <code>tab</code>.
	 */
	public Enumeration getInitParameterNames() {
		return Collections.enumeration(Collections.singleton("tab"));
	}

	/**
	 * Masquerading as the servlet request, get the string value of named request parameter.
	 *
	 * The request parameters include <code>resclass</code>, set to the test name
	 * <code>tab</code>, and another test parameter called <code>param</code>, with
	 * value <code>value</code>.
	 *
	 * @param name a {@link String} value.
	 * @return a {@link String} value.
	 */
	public String getParameter(String name) {
		if ("resclass".equals(name))
			return "tab";
		else if ("param".equals(name))
			return "value";
		else
			return null;
	}

	/**
	 * Masquerading as the servlet request, return a map of the request parameters.
	 *
	 * This map defines the same values as for {@link #getParameter(String)}.
	 *
	 * @return a {@link Map} value.
	 */
	public Map getParameterMap() {
		Map m = new HashMap();
		m.put("resclass", new String[]{ "tab" });
		m.put("param", new String[]{ "value" });
		return Collections.unmodifiableMap(m);
	}

	/**
	 * Masquerading as the servlet request, get a request dispatcher for a given path.
	 *
	 * We expect the path to be <code>/slot</code>, which is the resource to which the
	 * <code>resclass</code> of <code>tab</code> maps.  Any other path results in an
	 * {@link IllegalArgumentException}, which causes this test to fail.
	 *
	 * @param path a {@link String} value.
	 * @return a {@link RequestDispatcher} value.
	 */
	public RequestDispatcher getRequestDispatcher(String path) {
		if ("/slot".equals(path))
			return this;
		throw new IllegalArgumentException("Bad path " + path + " from test case");
	}

	/**
	 * Masquerading as the request dispatcher, forward the request.
	 *
	 * Of course, this doesn't really forward the request, but when we get here, we
	 * make sure that the <code>resclass</code> parameter got removed, and that the
	 * other parameter (named <code>param</code>) is still there and as its same
	 * value.
	 *
	 * @param req a {@link ServletRequest} value.
	 * @param res a {@link ServletResponse} value.
	 */
	public void forward(ServletRequest req, ServletResponse res) {
		assertNull(req.getParameter("resclass"));
		assertEquals("value", req.getParameter("param"));
	}

	public BufferedReader getReader() { return null; }
	public Cookie[] getCookies() { return null; }
	public Enumeration getAttributeNames() { return null; }
	public Enumeration getHeaderNames() { return null; }
	public Enumeration getHeaders(String name) { return null; }
	public Enumeration getLocales() { return null; }
	public Enumeration getParameterNames() { return null; }
	public Enumeration getServletNames() { return null; }
	public Enumeration getServlets() { return null; }
	public HttpSession getSession() { return null; }
	public HttpSession getSession(boolean create) { return null; }
	public InputStream getResourceAsStream(String path) { return null; }
	public Locale getLocale() { return null; }
	public Object getAttribute(String name) { return null; }
	public Principal getUserPrincipal() { return null; }
	public RequestDispatcher getNamedDispatcher(String name) { return null; }
	public Servlet getServlet(String name) { return null; }
	public ServletContext getContext(String path) { return null; }
	public ServletContext getServletContext() { return this; }
	public ServletInputStream getInputStream() { return null; }
	public Set getResourcePaths(String path) { return null; }
	public String getAuthType() { return null; }
	public String getCharacterEncoding() { return null; }
	public String getContentType() { return null; }
	public String getContextPath() { return null; }
	public String getHeader(String name) { return null; }
	public String getMethod() { return null; }
	public String getMimeType(String file) { return null; }
	public String getPathInfo() { return null; }
	public String getPathTranslated() { return null; }
	public String getProtocol() { return null; }
	public String getQueryString() { return null; }
	public String getRealPath(String path) { return null; }
	public String getRemoteAddr() { return null; }
	public String getRemoteHost() { return null; }
	public String getRemoteUser() { return null; }
	public String getRequestURI() { return null; }
	public String getRequestedSessionId() { return null; }
	public String getScheme() { return null; }
	public String getServerInfo() { return null; }
	public String getServerName() { return null; }
	public String getServletContextName() { return null; }
	public String getServletName() { return null; }
	public String getServletPath() { return null; }
	public StringBuffer getRequestURL() { return null; }
	public String[] getParameterValues(String name) { return null; }
	public URL getResource(String path) { return null; }
	public boolean isRequestedSessionIdFromCookie() { return false; }
	public boolean isRequestedSessionIdFromURL() { return false; }
	public boolean isRequestedSessionIdFromUrl() { return false; }
	public boolean isRequestedSessionIdValid() { return false; }
	public boolean isSecure() { return false; }
	public boolean isUserInRole(String role) { return false; }
	public int getContentLength() { return 0; }
	public int getIntHeader(String name) { return 0; }
	public int getMajorVersion() { return 0; }
	public int getMinorVersion() { return 3; }
	public int getServerPort() { return 0; }
	public long getDateHeader(String name) { return 0; }
	public void include(ServletRequest req, ServletResponse res) {}
	public void log(Exception ex, String msg) {}
	public void log(String msg) {}
	public void log(String msg, Throwable t) {}
	public void removeAttribute(String name) {}
	public void setAttribute(String name, Object obj) {}
	public void setCharacterEncoding(String env) {}
}
