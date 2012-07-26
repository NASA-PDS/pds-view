// Copyright 2003 California Institute of Technology.  ALL RIGHTS RESERVED.
// U.S. Government Sponsorship acknowledged.
//
// $Id$

package jpl.pds;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Filter based on software virtual host aliases.
 *
 * This is a J2EE servlet container filter that examines the software virtual host in
 * every request to the container.  For those for which a path mapping is specified, the
 * filter gets the request dispatcher for that path and forwards the request to it.
 * Otherwise, it continues to let the filter chain process the request.
 *
 * <p>This enables you to direct specific software virtual host names to specific servlets
 * or resources.  For example, suppose you had <code>http://x/proc.jsp</code> and you
 * wanted to be able to access <code>proc.jsp</code> from the URL
 * <code>http://proc/</code>.  Just define the mapping from the software virtual host
 * <code>proc</code> to the path <code>/proc.jsp</code>.
 *
 * <p>To define the mappings, use this filter's <code>init-params</code> in the deployment
 * descriptor.  Define the key <code>mappings</code> as a comma-separated list of software
 * virtual host names.  Then, define a key for each software virtual host you put in the
 * <code>mappings</code> parameter.  The value for each should be the path of the resource
 * to which the filter should direct the request.
 *
 * <p>The astute (or anal retentive) reader will note that this setup prevents you from
 * having a software virtual host named <code>mappings</code>.  Too bad.
 *
 * @author Kelly
 * @version $Revision$
 */
public class AliasFilter implements Filter {
	/**
	 * Set up the mappings.
	 *
	 * @param config a {@link FilterConfig} value.
	 */
	public void init(FilterConfig config) {
		String m = config.getInitParameter("mappings");
		if (m == null || m.length() == 0) {
			System.err.println("AliasFilter WARNING: No mappings defined");
			return;
		}
		for (Iterator i = jpl.eda.util.Utility.parseCommaList(m); i.hasNext();) {
			String vhost = (String) i.next();
			String path = config.getInitParameter(vhost);
			if (path == null) {
				System.err.println("AliasFilter WARNING: No URL path defined for vhost " + vhost);
				continue;
			}
			mappings.put(vhost, path);
		}
	}

	/**
	 * Filter the request.
	 *
	 * If the requested server is in our table of software virtual hosts, forward the
	 * request to the path specified during initialization.  Otherwise, let the filter
	 * chain continue.
	 *
	 * @param req Request.
	 * @param res Response.
	 * @param chain Additional filters.
	 * @throws IOException if an error occurs.
	 * @throws ServletException if an error occurs.
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		String vhost = req.getServerName();
		String path = (String) mappings.get(vhost);
		if (path != null) {
			RequestDispatcher rd = req.getRequestDispatcher(path);
			rd.forward(req, res);
		} else
			chain.doFilter(req, res);
	}

	/**
	 * Do nothing, as nothing's required.
	 */
	public void destroy() {}

	/** Mappings of {@link String} software virtual host to {@link String} path. */
	private Map mappings = new HashMap();
}
