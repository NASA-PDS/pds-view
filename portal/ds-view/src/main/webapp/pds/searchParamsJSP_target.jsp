<%--
  This file is included by /pds/index.jsp and /pds/advanced.jsp
  It contains code to generate queries from search params
  and builds new search params from result of queries.

  This code probably rightly belongs in a servlet, but I'm putting
  it here in a jsp to eleminate a step.  Hopefully, putting this code
  in an include file isolates it somewhat from the display code.
--%>

<%@ page import="javax.naming.*,javax.sql.*"%>

<%!

  // simple critertia
  final int MSNNAME  = 0;
  final int TARGNAME = 1;
  final int TARGTYPE = 2;
  final int INSTNAME = 3;
  final int INSTTYPE = 4;
  // advanced criteria
  final int DATAOBJTYPE  = 5;
  final int DSID         = 6;
  final int DSNAME       = 7;
  final int INSTHOSTNAME = 8;
  final int INSTHOSTTYPE = 9;

  final int TARGNAMES = 10;

// capitalize first letter in sentence, lower case all others

  static String dontDisplay = "|UNKNOWN|UNK|NA|N/A|";

  int c, j = 0; // generic counter

static String CapFirstLetter(String instring)
{
  StringTokenizer st =
    new StringTokenizer(instring.toLowerCase(),
      " .,/-():[]{}\"'", true);
  StringBuffer retval = new StringBuffer();

  try
  {
      while(st.hasMoreElements())
      {
        String temp = st.nextToken();
        retval.append( Character.toUpperCase(temp.charAt(0)));
        if (temp.length()>1)
          retval.append( temp.substring(1));
      }
  }
  catch(NoSuchElementException e)
  {
    retval.setLength(0);
  }
  return retval.toString();
}


static String[] singleValueCriteria =
  {"ti.targtype", "ii.insttype",
   // advanced params follow
   "di.dataobjtype", "di.dsid", "di.dsname", "hi.insthosttype"};

static String[] multiValueCriteria =
  {"dm.msnname", "ti.targname", "ii.instname",
   // advanced params follow
   "hi.insthostname"};

static String queryStr = 
" FROM dsinfo di, dshost dh, hostinfo hi, instinfo ii," +
"     dstarg dt, dsmsn dm, dsnode dn," +
"     targetinfo ti, resds rd, resinfo ri, dsnssdc dsns" +
" WHERE upper(ri.resclass) LIKE '%BROWSERP%'" +
" and dh.dsid = di.dsid" +
" and dh.insthostid = hi.insthostid" +
" and dh.insthostid = ii.insthostid" +
" and dh.instid = ii.instid" +
" and dt.dsid = di.dsid" +
" and dt.targname = ti.targname" +
" and dn.dscolldsid = di.dsid" +
" and dsns.dscolldsid = di.dsid" +
" and dm.dsid = di.dsid" +
" and di.dsid = rd.dsid" +
" and rd.resourceid = ri.resourceid" ;

String getMultiValues(HttpServletRequest req, String param, int len) {
  String paramname;
  int i;
  if ((i=param.indexOf("."))>=0) paramname=param.substring(i+1);
  else paramname = param;

  StringBuffer query = new StringBuffer("");
  if (req.getParameterValues(paramname) != null &&
      ! req.getParameterValues(paramname)[0].equalsIgnoreCase("ALL")) {
    String[] list = req.getParameterValues(paramname);
    query.append(" AND (");
    for (i=0; i<list.length; i++) {
      if (i>0) {
        query.append(" OR ");
      }
      query.append("upper("+param + ") = '" + list[i].toUpperCase() + "'");
    }
    query.append(") ");
  }
  return query.toString();
}

boolean defaultSingleValue( HttpServletRequest req, String param) {
  return req.getParameterValues(param) == null ||
    req.getParameterValues(param)[0].equals("") ||
    req.getParameterValues(param)[0].equalsIgnoreCase("ALL") ||
    req.getParameterValues(param)[0].trim().equalsIgnoreCase("YYYY-MM-DD");
}

String getSingleValue(HttpServletRequest req, String param, int len) {
  
  String paramname;
  int i;
  if ((i=param.indexOf("."))>=0) paramname=param.substring(i+1);
  else paramname = param;
  // hard code in for targname=all for now
/*  if (paramname.equals("targname") &&
     req.getParameterValues(paramname)[0].equalsIgnoreCase("ALL") &&
     req.getParameterValues("targnamechoices")!=null)
    return( " AND upper(ti.targname) in "+req.getParameterValues("targnamechoices")[0]+" ");
  else
  */
  if (!defaultSingleValue( req, paramname)) {
    return(" AND "+ "upper("+param +") = '" + (req.getParameterValues(paramname)[0]).toUpperCase() + "'");
  }
  else
    return "";
}

/*

advanced query:

SELECT dm.msnname, ti.targname, ti.targtype, ii.instname, ii.insttype,
       hi.insthostname, hi.insthosttype, di.dataobjtype, di.dsid, di.dsname
FROM dsinfo di, dshost dh, hostinfo hi, instinfo ii,
     dstarg dt, dsmsn dm, dsnode dn,
     targetinfo ti, resds rd, resinfo ri, dsnssdc dsns
WHERE upper(ri.resclass) LIKE '%BROWSERP%'
 and dh.dsid = di.dsid
 and dh.insthostid = hi.insthostid
 and dh.insthostid = ii.insthostid
 and dh.instid = ii.instid
 and dt.dsid = di.dsid
 and dt.targname = ti.targname
 and dn.dscolldsid = di.dsid
 and dsns.dscolldsid = di.dsid
 and dm.dsid = di.dsid
 and di.dsid = rd.dsid
 and rd.resourceid = ri.resourceid

*/

String constructQuery(HttpServletRequest req, boolean advanced)
    throws ServletException, IOException
{

  StringBuffer query = new StringBuffer("");

  int L = 6;
  for (int i=0; i<L; i++) {
    query.append(getSingleValue(req, singleValueCriteria[i], query.length()));
  }

  L = 4;
  for (int i=0; i<L; i++) {
    query.append(getMultiValues(req, multiValueCriteria[i], query.length()));
  }

  if (query.length()>0)
    return
      "SELECT distinct dm.msnname, ti.targname, ti.targtype, "+
			"ii.instname, ii.insttype " +
      		", di.dataobjtype, di.dsid, di.dsname, "+
                    "hi.insthostname, hi.insthosttype " +
      queryStr + // FROM clause and join clause
      query.toString();
       
  else return "";
}

static String[] idxToName =
  { "msnname", "targname", "targtype", "instname", "insttype",
    "dataobjtype", "dsid", "dsname", "insthostname", "insthosttype",
    "archivestat", "nodename" };

// return " selected" if param=value is in req, "" otherwise
// used to pre-select options in a SELECT box that the user previously selected
String isSelected( HttpServletRequest req, String param, String value)
{
  if (req.getParameterValues(param)!=null)
    for( int i=0; i<req.getParameterValues(param).length; i++)
      if (req.getParameterValues(param)[i].equalsIgnoreCase(value))
        return " selected";
  return "";
}

String getSearchParams(
     HttpServletRequest req, String query,
     String[] opts, boolean advanced) {

  String r = "";
  int numcolumns = 10;
  int g=0;  // generic counter
  List alias = null;
  ArrayList text_list, dbname_list=null;

  List[] optLists = new ArrayList[numcolumns];
  
 //get database server and database from web.xml
 //String dbserver = getServletConfig().getServletContext().getInitParameter("dbserver");
 //String db = getServletConfig().getServletContext().getInitParameter("db");
 //String dblogin = getServletConfig().getServletContext().getInitParameter("dblogin");

  for (int i=0; i<optLists.length; i++) optLists[i] = new ArrayList();
 
  try
  {
    long time0 = System.currentTimeMillis();
    //create connection to database
	javax.naming.Context init = new javax.naming.InitialContext();
	javax.naming.Context env = (Context)init.lookup("java:comp/env");
	DataSource ds = (DataSource) env.lookup("jdbc/pdsprofile");
	Connection connection = ds.getConnection();
	Statement statement = connection.createStatement();
    /*Class.forName("com.sybase.jdbc2.jdbc.SybDriver");
    Connection connection = DriverManager.getConnection("jdbc:sybase::Tds:" + dbserver + db + "?user=" + dblogin + "&password=" + dblogin);
		//"jdbc:sybase::Tds:" + dbserver + db,
                //dblogin, dblogin);
    java.sql.Statement statement = connection.createStatement();*/
    System.err.println("====== context sensitive query : " + query);
    ResultSet rs = statement.executeQuery( query);
  
    time0 = System.currentTimeMillis();
    while (rs.next()) {
      for (int i=0; i<numcolumns; i++) {
        List l = optLists [i];
        String s = rs.getString(i+1);
        if (dontDisplay.indexOf("|"+s+"|")<0 && ! l.contains(s)) {
          l.add(s);
        }
      }
    }
    rs.close();
    statement.close();
    connection.close();

    // loop through all columns
    for (int i=0; i<optLists.length; i++) {
        List l = optLists [i];
        text_list = new ArrayList();
        dbname_list = new ArrayList();

        if (i==MSNNAME) {
           String [] msntext=req.getParameterValues("msntext");
           alias = new ArrayList();

           // create alias list
           // This list is to be checked against while building the
           // Missions list on non first page
           for (c=0; c<msntext.length; c++) {
             msntext[c].replaceAll(" ", "");
             if (msntext[c].length()>0) {
               alias.add((String)msntext[c]);
             }
           }

           // sort the alias list because that's what will be displayed
           Collections.sort(alias);
           // create a temp array containing pairs of names as
           // (displayed text/alias , db msnname)
           j=0;
           for (c=0; c<alias.size(); c++) {

              String str=(String)alias.get(c);
              // if alias is one of the variations for 2001 Mars Odyssey
              // put alias and "2001 MARS ODYSSEY" into temp array
              if (str.equals("Mars Odyssey")||
                       (str.equals("Odyssey"))) {
                  if (l.contains("2001 MARS ODYSSEY")) {
                     text_list.add(str);
                     dbname_list.add("2001 MARS ODYSSEY");
                  }
              }
              // if alias is one of the variations for Deep Space
              // Program Science Experiment
              // put alias and "DEEP SPACE PROGRAM SCIENCE EXPERIMENT"
              // into temp array
              else if (str.equals("Clementine 1")){
                  if (l.contains("DEEP SPACE PROGRAM SCIENCE EXPERIMENT")){
                     text_list.add(str);
                     dbname_list.add("DEEP SPACE PROGRAM SCIENCE EXPERIMENT");
                  }
              }
              // if alias is in the returned list from db,
              // add the same strings into the temp array
              else if (l.contains(str.toUpperCase())) {
                  text_list.add(str);
                  dbname_list.add(str.toUpperCase());
              }
           }

           for (c=0; c<text_list.size(); c++) {
              if (!opts[i].equals("")) opts[i] += "<br>";
              opts[i] += "<option "
                         +" value=\""+dbname_list.get(c)+"\""
                         +isSelected(req, idxToName[i],(String)dbname_list.get(c))+">"
                         +jpl.pds.util.DisplayOptions.CapFirstLetter((String)text_list.get(c))
;
           }
           opts[i] += "<br>";
        }

        else {

           Collections.sort(l);
           for (Iterator o = l.iterator(); o.hasNext();) {
                String s = (String)o.next();
                if (!opts[i].equals("")) opts[i] += "\t";
                opts[i] += "<option value=\""+s+"\""
                     +isSelected( req, idxToName[i], s)+">"
                     + ( (i==DSNAME || i==DSID) ?
                         s : jpl.pds.util.DisplayOptions.CapFirstLetter( s) );
                if (i==TARGNAME) {
                    if (!opts[TARGNAMES].equals("")) opts[TARGNAMES] += ", ";
                    opts[TARGNAMES] +=  "'"+s.toUpperCase()+"'";
                }
            }
        }

       alias.clear();
       text_list.clear();
       dbname_list.clear();

    } // for  - to go over all columns
   }
   catch (Exception e) {
    r = e.toString();
    e.printStackTrace();
   }

  return r;
}

%>

<%-- Save the request URI in the user's session so we know to take him back here from the results page --%>
<%


  request.getSession(true).setAttribute("requestURI", request.getRequestURI());
  //System.out.println("targname= " + request.getParameterValues("targname")[0] + " and input targname= " + request.getParameterValues("inputtargname")[0]);
  
  /*if (request.getParameterValues("inputtargname") != null &&
      ! request.getParameterValues("inputtargname")[0].equals("")) {
	  	request.setAttribute("targname", request.getParameterValues("inputtargname")[0]);
		System.out.println("targname= " + request.getParameterValues("targname")[0] + " and input targname= " + request.getParameterValues("inputtargname")[0]);
  }*/


  final String all_opt = "<option value=ALL selected>All\n";

  boolean power = request.getRequestURI().indexOf("targetSearch.jsp")>=0;
  boolean advanced = power || (request.getRequestURI().indexOf("advanced.jsp")>=0);

  boolean advWithParams = advanced && request.getParameterValues("dsid")!=null;

  String[] opts = new String[11];
  for (int i=0; i<opts.length; i++) opts[i]="";


  String q = ""; String r = "";

  // targname!=null means page called with params
  if (advWithParams) {
    q = constructQuery( request, true);
  }
  else
  if (request.getParameterValues("targname")!=null) {
    q = constructQuery( request, advanced);
  }

  // if index.jsp called with no parameters,
  // or if query is empty for some reason, then get all default values
  if (q.equals("")) {
    opts[MSNNAME]  = jpl.pds.util.DisplayOptions.displayValList(request, targetSearchBean.getPDSKeyword("msnname"), "msnname", targetParamBean.getMissionNamePair());
    opts[TARGNAME] = jpl.pds.util.DisplayOptions.displayValList(request, targetSearchBean.getPDSKeyword("targname"), "targname", targetParamBean.getTargetName());
    opts[TARGTYPE] = jpl.pds.util.DisplayOptions.displayValList(request, targetSearchBean.getPDSKeyword("targtype"), "targtype", targetParamBean.getTargetType());
    opts[INSTNAME] = jpl.pds.util.DisplayOptions.displayValList(request, targetSearchBean.getPDSKeyword("instname"), "instname", targetParamBean.getInstrumentName());
    opts[INSTTYPE] = jpl.pds.util.DisplayOptions.displayValList(request, targetSearchBean.getPDSKeyword("insttype"), "insttype", targetParamBean.getInstrumentType());
    opts[DATAOBJTYPE]  = jpl.pds.util.DisplayOptions.displayValList(request, targetSearchBean.getPDSKeyword("dataobjtype"), "dataobjtype", targetParamBean.getDataObjectType());
    opts[DSID]         = jpl.pds.util.DisplayOptions.displayValList(request, targetSearchBean.getPDSKeyword("dsid"), "dsid", targetParamBean.getDatasetId());
    opts[DSNAME]       = jpl.pds.util.DisplayOptions.displayValList(request, targetSearchBean.getPDSKeyword("dsname"), "dsname", targetParamBean.getDatasetName());
    opts[INSTHOSTNAME] = jpl.pds.util.DisplayOptions.displayValList(request, targetSearchBean.getPDSKeyword("insthostname"), "insthostname", targetParamBean.getInstrumentHostName());
    opts[INSTHOSTTYPE] = jpl.pds.util.DisplayOptions.displayValList(request, targetSearchBean.getPDSKeyword("insthosttype"), "insthosttype", targetParamBean.getInstrumentHostType());
    opts[TARGNAMES] = "";
    for (int i=1; i<targetParamBean.getTargetName().length; i++) {
      if (!opts[TARGNAMES].equals("")) opts[TARGNAMES] += ",";
      opts[TARGNAMES] += "'"+targetParamBean.getTargetName()[i].toUpperCase() +"'";
    }
  }
  // else user changed some search specs, we need to construct search parameters to match
  else
  {/*
    if (defaultSingleValue(request,"targname")) opts[TARGNAME] = all_opt;
    if (defaultSingleValue(request,"targtype")) opts[TARGTYPE] = all_opt;
    if (defaultSingleValue(request,"insttype")) opts[INSTTYPE] = all_opt;
    if (advanced) {
      if (defaultSingleValue(request,"dataobjtype")) opts[DATAOBJTYPE] = all_opt;
      if (defaultSingleValue(request,"dsid")) opts[DSID] = all_opt;
      if (defaultSingleValue(request,"dsname")) opts[DSNAME] = all_opt;
      if (defaultSingleValue(request,"insthosttype")) opts[INSTHOSTTYPE] = all_opt;
    }
    */
    r = getSearchParams( request, q, opts, advanced);

    // if a single value param has more than one options, then add "All" option;
    //if (opts[TARGNAME].indexOf("\t")>=0) opts[TARGNAME] = all_opt + opts[TARGNAME];
    if (opts[TARGTYPE].indexOf("\t")>=0) opts[TARGTYPE] = all_opt + opts[TARGTYPE];
    if (opts[INSTTYPE].indexOf("\t")>=0) opts[INSTTYPE] = all_opt + opts[INSTTYPE];
    if (opts[DATAOBJTYPE].indexOf("\t")>=0) opts[DATAOBJTYPE] = all_opt + opts[DATAOBJTYPE];
    if (opts[DSID].indexOf("\t")>=0) opts[DSID] = all_opt + opts[DSID];
    if (opts[DSNAME].indexOf("\t")>=0) opts[DSNAME] = all_opt + opts[DSNAME];
    if (opts[INSTHOSTTYPE].indexOf("\t")>=0) opts[INSTHOSTTYPE] = all_opt + opts[INSTHOSTTYPE];
  }
%>
