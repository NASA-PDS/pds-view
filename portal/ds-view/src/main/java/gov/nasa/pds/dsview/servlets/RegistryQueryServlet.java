// This software was developed by the Object Oriented Data Technology task of the Science
// Data Engineering group of the Engineering and Space Science Directorate of the Jet
// Propulsion Laboratory of the National Aeronautics and Space Administration, an
// independent agency of the United States Government.
// 
// This software is copyrighted (c) 2000 by the California Institute of Technology.  All
// rights reserved.
// 
// Redistribution and use in source and binary forms, with or without modification, is not
// permitted under any circumstance without prior written permission from the California
// Institute of Technology.
//
// THIS SOFTWARE IS PROVIDED BY THE AUTHORS AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR
// IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
// THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
// EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

package gov.nasa.pds.dsview.servlets;

import java.util.*;
import java.text.*;
import java.io.*;
import java.net.URLEncoder;
import javax.servlet.*;
import javax.servlet.http.*;
//import jpl.pds.beans.*;

import gov.nasa.pds.dsview.registry.GetSearchParams;

public class RegistryQueryServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res) 
			throws ServletException, IOException {
		doIt(req, res);
	}
	public void doPost(HttpServletRequest req, HttpServletResponse res) 
			throws ServletException, IOException {
		doIt(req, res);
	}

	public void doIt(HttpServletRequest req, HttpServletResponse res) 
			throws ServletException, IOException 
	{
		
		String errorMessage = validateUserInputs(req, res);
		if (! errorMessage.equals(""))
		{
			// Take the user back to the search specification page
			// because not all parameters are validated.
			req.setAttribute("message",errorMessage);
			getServletConfig().getServletContext().getRequestDispatcher
				("/pds/index.jsp").forward(req,res);
			return;
		}
		
		System.out.println("*****************in the doIt method of RegistryQueryServlet class...");
		//System.out.println("req = " + req.toString());
		String queryString = constructKeywordQuery(req);
		System.out.println("queryString = " + queryString);

		/*
		res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Testing RegistryQueryServlet!</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Testing RegistryQueyrServlet!</h1>");
        out.println("parameter names = " + req.getParameterMap().toString() + "<br>");
        //out.println("parameter values = " + req.getParameterValues().toString() + "<br>");
        //out.println("registryUrl = " + req.getParameterValues("registryUrl")[0]);
        out.println("</body>");
        out.println("</html>");
        */
        req.getSession().setAttribute("queryString", queryString);
        getServletConfig().getServletContext().getRequestDispatcher
		("/pds/results.jsp").forward(req,res);

	}

	String validateUserInputs(HttpServletRequest req, HttpServletResponse res)
                        throws ServletException, IOException
	{
		// assume the user knows what he's doing for now.
		return "";
	}

	static String[] singleValueCriteria = {"targname", "targtype", "dataobjtype", "dsname",
		"dsid", "strttime", "stoptime", "insttype", "insthosttype",
		"archivestat", "nodename", "defullname", "msnstopdate",
		// the following params are not on the DataSetView but we
		// add them here so the QueryServlet can serve queries from clients other than
		// the DataSetView
		"insthostid", "instid", "nodeid", "nssdcdsid",
		"onlineid", "reslink", "resname", "volumeid", "volumename"};

	static String[] multiValueCriteria = {"msnname", "instname", "insthostname"};

	/*
	** Construct the HTML style query string with PDS keywords
	*/
	String constructKeywordQuery (HttpServletRequest req)
	{
		StringBuffer query = new StringBuffer("");
		for (int i=0; i<singleValueCriteria.length; i++) {
			query.append(getKeywordValue(req, singleValueCriteria[i], query.length()));
		}
		for (int i=0; i<multiValueCriteria.length; i++) {
			query.append(getKeywordValue(req, multiValueCriteria[i], query.length()));
		}
		return query.toString();
	}

	
	String getConnector (StringBuffer query) {
		if (query.length() < 1) {
			return "";
		} else {
			return " AND ";
		}
	}
/*
	String getMultiValues(HttpServletRequest req, String param, int len) {
		//jpl.pds.beans.SearchBean searchBean, boolean getPDSkeyword) {
		StringBuffer query = new StringBuffer("");
		//String keyword = (getPDSkeyword ? searchBean.getPDSKeyword(param) : param);
		String keyword;
		if (param.equals("msnname"))
	        keyword = "mission";
	    else if (param.equals("instname"))
	        keyword = "instrument";
	    else if (param.equals("insthostname"))
	    	keyword = "instrument_host_name";
	    else 
	    	keyword = param;
		
		if (req.getParameterValues(param) != null &&
		    ! req.getParameterValues(param)[0].equalsIgnoreCase("ALL")) {
			String[] list = req.getParameterValues(param);
			if (len > 0) { query.append(" AND "); }
			query.append(" (");
			for (int i=0; i<list.length; i++) {
				if (i>0) {
					query.append(" OR "); 
				}
				query.append(keyword + "=\"" + list[i] + "\"");
			}
			query.append(") ");
		}
		return query.toString();
	}

	String getSingleValue(HttpServletRequest req, String param, int len) {
		//jpl.pds.beans.SearchBean searchBean, boolean getPDSkeyword) {
		String connector = "";
		//String keyword = (getPDSkeyword ? searchBean.getPDSKeyword(param) : param);
		String keyword;
		if (param.equalsIgnoreCase("targname"))
			keyword = "target";
		else if (param.equalsIgnoreCase("targtype"))
			keyword = "target_type";
		else if (param.equalsIgnoreCase("insttype"))
			keyword = "instrument_type";
		else if (param.equalsIgnoreCase("dsid")) 
		    keyword = "data_set_id";
		else if (param.equalsIgnoreCase("dsname"))
		    keyword = "data_set_name";
		else if (param.equals("insthosttype"))
		    keyword = "instrument_host_type";
		else 
			keyword = param;
				
		if (req.getParameterValues(param) != null &&
                    ! req.getParameterValues(param)[0].equals("") &&
		    ! req.getParameterValues(param)[0].equalsIgnoreCase("ALL") &&
		    ! req.getParameterValues(param)[0].trim().equalsIgnoreCase("YYYY-MM-DD")) {

			if (len > 0) { connector = " AND "; }
                        return(connector + keyword +":\"" + req.getParameterValues(param)[0] + "\"");
		/*
		** since we're allowing querying of all datasets, no need
		** to treat "targname" differently than other params.
                } else if (param.equals("targname") &&
				req.getParameterValues("targname") != null &&
				 req.getParameterValues("targname")[0].equalsIgnoreCase("ALL") &&
				! getPDSkeyword) {
			if (len > 0) { connector = " AND "; }
			return (connector + "targname=Mars OR targname=Phobos OR targname=Deimos");
		***
		} else
			return "";
	}
*/
/*
	String getRangeValue(HttpServletRequest req, String param,
				String element, String operator, int len)
	{
		String connector = "";
		if (req.getParameterValues(param) != null &&
		    ! req.getParameterValues(param)[0].equals("")) {

			if (len > 0) { connector = " AND "; }
			return (connector + element + " " + operator + " \"" + 
				req.getParameterValues(param)[0]) + "\"";
		}
		else
			return "";
	}
*/
	String getKeywordValue (HttpServletRequest req, String param, 
			int len) 
	{
		StringBuffer query = new StringBuffer("");
		if (req.getParameterValues(param) != null &&
		    ! req.getParameterValues(param)[0].equalsIgnoreCase("ALL") &&
		    ! req.getParameterValues(param)[0].trim().equalsIgnoreCase("YYYY-MM-DD")) {
			//String keyword = searchBean.getPDSKeyword(param);
			String keyword;
			if (param.equalsIgnoreCase("targname"))
				keyword = "target";
			else if (param.equalsIgnoreCase("targtype"))
				keyword = "target_type";		
			else if (param.equals("msnname"))
		        keyword = "mission";
		    else if (param.equals("instname"))
		        keyword = "instrument";
		    else if (param.equals("insthostname"))
		    	keyword = "instrument_host_name";
		    else if (param.equalsIgnoreCase("insttype"))
				keyword = "instrument_type";
		    else if (param.equalsIgnoreCase("dsid")) 
			    keyword = "data_set_id";
			else if (param.equalsIgnoreCase("dsname"))
			    keyword = "data_set_name";
			else if (param.equals("insthosttype"))
			    keyword = "instrument_host_type";
			else 
				keyword = param;
			String[] list = req.getParameterValues(param);
			for (int i=0; i<list.length; i++) {
				if (list[i].equalsIgnoreCase("ALL"))
					continue;
				
				if (len > 0 || i > 0) {
					query.append(" AND ");
				}
				try {
					list[i]=list[i].trim();
					if (param.equals("dsid"))
						list[i] = list[i].replaceAll("/", "-");
					
					query.append(keyword+ ":" + URLEncoder.encode(list[i],"UTF-8"));
				} catch (java.io.UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return query.toString();
	}
}
