/** 
 * Copyright (c) 2011, California Institute of Technology.
 * ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
 *
 * $Id$ 
 * 
 */
package gov.nasa.pds.report.setup.servlets;

import gov.nasa.pds.report.setup.util.TransferUtil;
import gov.nasa.pds.report.update.model.LogSet;
import gov.nasa.pds.report.update.model.Profile;
import gov.nasa.pds.report.transfer.util.SFTPConnect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		Profile profile = new Profile();
		profile.setProfile(request.getParameterMap());
		
		JsonObject root = new JsonObject();
		List<LogSet> lsList = profile.getNewLogSets();
		if (lsList.size() != 0) {
			TransferUtil connect = new TransferUtil(lsList);
			root = connect.checkAllConnections();
		} else { 
			root.add("empty", new JsonPrimitive("0"));
		}
			
		
		Gson gson = new Gson();
		gson.toJson(root, response.getWriter());	

	}

}
