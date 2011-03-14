/** 
 * Copyright (c) 2011, California Institute of Technology.
 * ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
 *
 * $Id$ 
 * 
 */
package gov.nasa.pds.report.setup.servlets;

import gov.nasa.pds.report.setup.model.Profile;
import gov.nasa.pds.report.setup.util.SFTPUtil;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class SetupServlet
 */
public class SetupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SetupServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		Profile profile = new Profile();
		profile.setProfile(request.getParameterMap());
		
		SFTPUtil util = new SFTPUtil(profile.getLogSetList());
		
		Gson gson = new Gson();
		gson.toJson(util.checkConnections(), response.getWriter());	

	}

}
