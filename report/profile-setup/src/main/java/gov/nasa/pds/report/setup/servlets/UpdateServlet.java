package gov.nasa.pds.report.setup.servlets;

import gov.nasa.pds.report.update.db.DBUtil;
import gov.nasa.pds.report.update.model.Profile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Servlet implementation class UpdateServlet
 */
public class UpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateServlet() {
        super();
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		String error = "";
		int removeId;
		int empty = 0;
		String id;
		JsonObject root = new JsonObject();
		JsonObject jObj;
		JsonArray jArray;
		try {
			DBUtil util = new DBUtil(getServletContext().getRealPath("/WEB-INF/classes"));
			removeId = Integer.parseInt(request.getParameter("removed-log-set"));
			if (removeId != 0) {
				util.removeLogInfo(removeId);
			} else {
				id = request.getParameter("profile");
				if (id.equals("new")) {
					jArray = new JsonArray();
					for (Profile prof : util.findAllProfiles()) {
						jObj =  new JsonObject();
						
						jObj.add("id", new JsonPrimitive(prof.getProfileId()));
						jObj.add("name", new JsonPrimitive(prof.getName()));
	
						jArray.add(jObj);
					}
					root.add("profiles", jArray);
				} else {
					root = util.findByProfileId(Integer.parseInt(id)).toJson();
				}
			}
		} catch (SQLException e) {
			error = "Notify System Administrator: SQLException: "+e.getMessage();
		} catch (NullPointerException e) {
			//error = "Notify System Administrator: NullPointerException: "+e.getMessage();
			// Ignore error: Means no profiles were found.
			empty = 1;
			
		}
		root.add("error", new JsonPrimitive(error));
		root.add("empty", new JsonPrimitive(empty));
		
		this.log.info(root.toString());
		Gson gson = new Gson();
		gson.toJson(root, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
