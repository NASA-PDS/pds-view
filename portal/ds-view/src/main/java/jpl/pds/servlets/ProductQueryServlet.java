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

package jpl.pds.servlets;

import java.util.*;
import java.text.*;
import java.io.*;
import java.net.URLEncoder;
import javax.servlet.*;
import javax.servlet.http.*;
import jpl.pds.beans.*;
import jpl.eda.xmlquery.XMLQuery;
import jpl.eda.profile.ProfileClient;
import jpl.eda.profile.ProfileException;

public class ProductQueryServlet extends HttpServlet {

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
		try {
                        ProductSearchBean searchBean =  new ProductSearchBean();
                        String query = constructQuery(req);
                        System.err.println(query);
                        //System.err.println(URLEncoder.encode(query,"UTF-8"));
                        searchBean.setProfileSearchText(query);
                        req.getSession().setAttribute("searchBean", searchBean);
                        getServletConfig().getServletContext().getRequestDispatcher
                                ("/pds/productResults.jsp").forward(req,res);
 
		}
		catch (Exception e)
		{
			e.printStackTrace();
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		return;

	}

	
	/*
	** Construct the DIS style query string
	*/
	String constructQuery(HttpServletRequest req)
			throws ServletException, IOException
	{

		StringBuffer query = new StringBuffer("");
		if (req.getParameterNames() == null) {
			return "";
		}
		Enumeration names = req.getParameterNames();
		while (names.hasMoreElements()) {
			query.append(getValue(req, (String) names.nextElement(), query.length()));
		}
		if (query.length() > 0) {
			query.append(" AND ");
		}
		query.append(" RETURN=DATA_SET_ID");

		return query.toString();
	}

	String getValue(HttpServletRequest req, String param, int len ) {
		String connector = "";
                if (req.getParameterValues(param) != null &&
                    ! req.getParameterValues(param)[0].equals("")) { 
			if (len > 0) { connector = " AND "; }
                        return(connector + param +"=\"" + req.getParameterValues(param)[0] + "\"");
		} else
			return "";
	}

}
