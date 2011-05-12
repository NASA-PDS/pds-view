/** 
 * Copyright (c) 2011, California Institute of Technology.
 * ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
 *
 * $Id$ 
 * 
 */

package gov.nasa.pds.report.setup.servlets;

import gov.nasa.pds.report.setup.sawmill.SawmillController;
import gov.nasa.pds.report.update.db.DBUtil;
import gov.nasa.pds.report.update.model.LogPath;
import gov.nasa.pds.report.update.model.LogSet;
import gov.nasa.pds.report.update.model.Profile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
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
 * Servlet implementation class SaveServlet
 */
public class SaveServlet extends HttpServlet {
	/** Generated versionID **/
	private static final long serialVersionUID = 1L;
	
	protected String localPath;
	protected Profile profile;
	protected List<LogSet> newLogSets;
	protected LogPath logPath;
	protected boolean isNew;
	
	private Logger log = Logger.getLogger(this.getClass().getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		String error = "";
		this.isNew = false;

		this.profile = new Profile();
		this.profile.setProfile(request.getParameterMap());

		this.localPath = getServletContext().getRealPath("/WEB-INF/classes");

		this.logPath = new LogPath(this.profile.getNode(), this.profile.getName());

		this.newLogSets = this.profile.getNewLogSets();
		try {
			updateDB(request.getParameter("profile").equals("new"));

			SawmillController sawmill = new SawmillController(this.localPath, this.logPath, this.profile, this.isNew);
			sawmill.start();
		} catch (SQLException e) {
			error = "SQL Error: " + e.getMessage();
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			error = "Environment Error.  Notify system administrator.";
			e.printStackTrace();
		} catch (IOException e) {
			error = "Config Error: " + e.getMessage();
			e.printStackTrace();
		}

		JsonObject root = new JsonObject();
		root.add("error", new JsonPrimitive(error));

		Gson gson = new Gson();
		gson.toJson(root, response.getWriter());
	}
	
	protected void updateDB(boolean isNew) throws SQLException, FileNotFoundException {
		DBUtil db = new DBUtil(this.localPath);
		if (isNew) {
			this.isNew =  true;
			db.createNew(this.profile);
		} else {
			db.update(this.newLogSets, this.profile.getProfileId());
		}
	}
}
