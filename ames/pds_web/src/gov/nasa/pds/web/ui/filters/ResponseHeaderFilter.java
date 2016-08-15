package gov.nasa.pds.web.ui.filters;

import gov.nasa.pds.web.ui.utils.DateUtils;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ResponseHeaderFilter implements Filter {

	private FilterConfig config;

	private String getModifiedDate(final String headerName) {
		final long offset = Long.parseLong(this.config
				.getInitParameter(headerName));

		final long timestamp = (new Date()).getTime();
		final Date expires = new Date(timestamp + (offset * 60000));
		final String GMTDate = DateUtils.toGMTString(expires);
		return GMTDate;
	}

	@Override
	public void destroy() {
		this.config = null;
	}

	@Override
	public void doFilter(final ServletRequest servletRequest,
			final ServletResponse servletResponse, final FilterChain chain)
			throws IOException, ServletException {
		final HttpServletResponse response = (HttpServletResponse) servletResponse;

		// set the provided HTTP response parameters
		for (final Enumeration<?> e = this.config.getInitParameterNames(); e
				.hasMoreElements();) {
			final String headerName = (String) e.nextElement();
			if (headerName.equals("Expires") //$NON-NLS-1$
					|| headerName.equals("Last-Modified")) { //$NON-NLS-1$
				response.addHeader(headerName, getModifiedDate(headerName));
			} else if (response.containsHeader(headerName)) {
				response.setHeader(headerName, this.config
						.getInitParameter(headerName));
			} else {
				response.addHeader(headerName, this.config
						.getInitParameter(headerName));
			}
		}
		// pass the request/response on
		chain.doFilter(servletRequest, response);
	}

	@Override
	public void init(final FilterConfig conf) {
		this.config = conf;

	}
}
