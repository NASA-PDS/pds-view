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

package jpl.pds.util;

import javax.servlet.http.*;
import java.util.*;

public class DisplayOptions 
{
	public static String getMultiValues(HttpServletRequest request, String keyword,
				String param, String selected) {
		StringBuffer options = new StringBuffer();
		String formParam = "";
		if (request.getParameterValues(param) != null) {
			formParam = param;
		} else if (request.getParameterValues(keyword) != null) {
			formParam = keyword;
		}
		if (! formParam.equals("")) {
			String[] list = request.getParameterValues(formParam);
			for (int i=0; i<list.length; i++) {
				options.append("<option ").append(selected).append(" value=\"").append(list[i].trim()).
					append("\">").append(list[i].trim());
			}
			if (options.length() > 0) {
				options.append("</option>");
			}
		}
		return options.toString();
	}

	public static String displayValList(HttpServletRequest request, String keyword,
			String param, String[][] valList) {

		// Note: the valList is an array of (display value, database value)
		StringBuffer options = new StringBuffer();
		String formParam = "";
		if (request.getParameterValues(param) != null) {
			formParam = param;
		} else if (request.getParameterValues(keyword) != null) {
			formParam = keyword;
		}
		String[] defaultVal = new String[1];
		defaultVal[0] = "All";
		String[] selectedVal = (request.getParameterValues(formParam) != null ?
					request.getParameterValues(formParam) : defaultVal);
		for (int i=0; i<valList.length; i++) {
			options.append("<option ");
			if (isItemSelected(valList[i][1],selectedVal)) {
				options.append("selected ");
			}
			options.append("value=\"").append(valList[i][1].trim()).append("\">").append(valList[i][0].trim()).append("\n");
		}
		if (options.length() > 0) {
			options.append("</option>");
		}
		return options.toString();
	}

	public static String displayValList(HttpServletRequest request, String keyword,
			String param, String[]valList) {

		StringBuffer options = new StringBuffer();
		String formParam = "";
		if (request.getParameterValues(param) != null) {
			formParam = param;
		} else if (request.getParameterValues(keyword) != null) {
			formParam = keyword;
		}
		String[] defaultVal = new String[1];
		//if (param.equals("targtype")) {
		//	defaultVal[0] = "Planet";
		//} else {
			defaultVal[0] = "All";
		//}
		String[] selectedVal = (request.getParameterValues(formParam) != null ?
					request.getParameterValues(formParam) : defaultVal);
		for (int i=0; i<valList.length; i++) {
			options.append("<option ");
			if (isItemSelected(valList[i],selectedVal)) {
				options.append("selected ");
			}
			options.append("value=\"").append(valList[i].trim()).append("\">").append(valList[i].trim()).append("\n");
		}
		if (options.length() > 0) {
			options.append("</option>");
		}
		return options.toString();
	}

	public static String displayInput (HttpServletRequest request, String keyword, 
			String param, String defaultVal) {
		String formParam = "";
		if (request.getParameterValues(param) != null) {
			formParam = param;
		} else if (request.getParameterValues(keyword) != null) {
			formParam = keyword;
		}
		return  (request.getParameterValues(formParam) != null ?
					request.getParameterValues(formParam)[0] : defaultVal);
	}

	public static String CapFirstLetter(String instring) {
		StringTokenizer st =
			new StringTokenizer(instring.toLowerCase(),
					" .,/-():[]{}\"'", true);
		StringBuffer retval = new StringBuffer();

		try {
			while(st.hasMoreElements()) {
				String temp = st.nextToken();
				retval.append( Character.toUpperCase(temp.charAt(0)));
				if (temp.length()>1)
					retval.append( temp.substring(1));
			}
		} catch(NoSuchElementException e) {
			retval.setLength(0);
		}
		return retval.toString();

	}


	static boolean isItemSelected (String value, String[] selectedList) {
		for (int i=0; i<selectedList.length; i++) {
			if (selectedList[i].equalsIgnoreCase(value)) {
				return true;
			}
		}
		return false;
		
	}
}
