//	Copyright 2013, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id: SearchCoreLauncher.java 11350 2013-02-22 00:55:41Z jpadams $
//

package gov.nasa.pds.report.profile.manager.servlets;

import gov.nasa.pds.report.profile.manager.util.TransferUtil;
import gov.nasa.pds.report.update.model.LogSet;
import gov.nasa.pds.report.update.model.Profile;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Servlet implementation class SetupServlet
 * 
 * FIXME This software only works with the old rs-update API, not report-manager
 * 
 */
public class SetupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SetupServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected final void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		Profile profile = new Profile();
		profile.setProfile(request.getParameterMap());

		JsonObject root = new JsonObject();
		List<LogSet> lsList = profile.getNewLogSets();
		if (lsList.size() != 0) {
			TransferUtil connect = new TransferUtil();
			root = connect.checkAllConnections(lsList);
		} else {
			root.add("empty", new JsonPrimitive("0"));
		}

		Gson gson = new Gson();
		gson.toJson(root, response.getWriter());

	}

}
