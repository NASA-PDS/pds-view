package gov.nasa.arc.pds.lace.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);

	private static final String AUTHENTICATION_PATH = "/auth/login";

	private static final String[] PUBLIC_PREFIXES = {
		"/favicon.ico",
		"/auth/",
		"/designer/",
		"/assets/",
		"/images/",
	};

	private String contextPath;

	@Override
	public void init(FilterConfig config) throws ServletException {
		LOG.debug("Authentication filter initialized");
		contextPath = config.getServletContext().getContextPath();
	}

	@Override
	public void destroy() {
		// Nothing to do.
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		boolean mustLogIn = true;
		HttpSession session = null;
		HttpServletRequest request = null;

		if (req instanceof HttpServletRequest) {
			request = (HttpServletRequest) req;
			session = request.getSession();
			mustLogIn = isLoginRequired(request, session);
		}

		if (!mustLogIn) {
			LOG.debug("Authentication not required: path={}", (request==null ? "unknown" : request.getServletPath()));
			chain.doFilter(req, resp);
		} else {
			LOG.debug("Authentication required: path={}", (request==null ? "unknown" : request.getServletPath()));

			if (session != null) {
				String path = request.getServletPath();
				if (request.getQueryString() != null) {
					path += "?" + request.getQueryString();
				}
				session.setAttribute(SessionConstants.REQUESTED_URI, path);
			}
			((HttpServletResponse) resp).sendRedirect(contextPath + AUTHENTICATION_PATH);
		}
	}

	private boolean isLoginRequired(HttpServletRequest request, HttpSession session) {
		String path = request.getServletPath();

		for (String prefix : PUBLIC_PREFIXES) {
			if (path.startsWith(prefix)) {
				return false;
			}
		}

		if (session.getAttribute(SessionConstants.USER_ID_PROPERTY) != null) {
			return false;
		}

		return true;
	}

}
