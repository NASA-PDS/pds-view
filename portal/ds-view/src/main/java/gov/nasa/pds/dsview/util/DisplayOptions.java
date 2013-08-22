// Copyright 2000-2013, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$

package gov.nasa.pds.dsview.util;

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
    //  defaultVal[0] = "Planet";
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
