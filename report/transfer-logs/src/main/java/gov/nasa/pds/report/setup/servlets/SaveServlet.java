/** 
 * Copyright (c) 2011, California Institute of Technology.
 * ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
 *
 * $Id$ 
 * 
 */

package gov.nasa.pds.report.setup.servlets;

import gov.nasa.pds.report.setup.model.LogSet;
import gov.nasa.pds.report.setup.model.Profile;
import gov.nasa.pds.report.setup.properties.EnvProperties;
import gov.nasa.pds.report.setup.runnable.Copy;
import gov.nasa.pds.report.setup.util.DBUtil;
import gov.nasa.pds.report.setup.util.SFTPUtil;
import gov.nasa.pds.report.setup.util.SawmillUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
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
	private static final long serialVersionUID = 1L;
	
	private Logger _log = Logger.getLogger(this.getClass().getName());
       
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String error = "";
		//if (Integer.parseInt(request.getParameter("new-log-set")) > 0) {  //TODO find better method
			Profile profile = new Profile();
			profile.setProfile(request.getParameterMap());
			
			String realPath = getServletContext().getRealPath("/WEB-INF/classes");
			
			boolean isNew = false;
			try {
				DBUtil db = new DBUtil(realPath);
				
				if (request.getParameter("profile").equals("new")) {
					isNew=true;
					db.createNew(profile);
				} else
					db.update(profile);
				
				EnvProperties env = new EnvProperties(realPath);
				String logBasePath = env.getLogDest()+'/'+profile.getNode()+'/'+profile.getName()+'/';
				
				SawmillUtil cfg = new SawmillUtil(env.getProfileCfgHome(), logBasePath, realPath, profile);
				cfg.buildCfg();
				
				Copy copyLogs = new Copy(logBasePath, env, profile, isNew);
				copyLogs.start();
			} catch (SQLException e) {
				error = "SQL Error: "+e.getMessage();
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				error= "Environment Error.  Notify system administrator.";
				e.printStackTrace();
			} catch (IOException e) {
				error= "Config Error: "+e.getMessage();
				e.printStackTrace();
			} 
			
			JsonObject root = new JsonObject();
			root.add("error", new JsonPrimitive(error));
			
			Gson gson = new Gson();
			gson.toJson(root, response.getWriter());
		//}
	}

}
