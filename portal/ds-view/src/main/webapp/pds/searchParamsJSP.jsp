<%--
  This file is included by /pds/index.jsp and /pds/advanced.jsp
  It contains code to generate queries from search params
  and builds new search params from result of queries.

  This code probably rightly belongs in a servlet, but I'm putting
  it here in a jsp to eleminate a step.  Hopefully, putting this code
  in an include file isolates it somewhat from the display code.
--%>

<%@ page import="javax.naming.*,javax.sql.*, gov.nasa.pds.dsview.registry.GetSearchParams" %>

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

  static String dontDisplay = "|UNKNOWN|UNK|NA|N/A|unk|unknown|null|na|n/a|";

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

// Escape an apostrophe by doubling it up.
// This is necessary when constructing a database query that 
// consists apostrophe in the statement to be parsed by JDBC
//
static String escape(String in) {
    if (in == null) return null;
    int len = in.length();
    StringBuffer b = new StringBuffer(len);
    for (int i = 0; i < len; ++i) {
        char c = in.charAt(i);
        if (c == '\'')
        b.append('\'');
        b.append(c);
    }
    return b.toString();
}


static String[] singleValueCriteria =
  {"ti.targname", "ti.targtype", "ii.insttype",
   // advanced params follow
   "di.dataobjtype", "di.dsid", "di.dsname", "hi.insthosttype"};

static String[] multiValueCriteria =
  {"dm.msnname", "ii.instname",
   // advanced params follow
   "hi.insthostname"};


String getMultiValues(HttpServletRequest req, String param, int len) {
  String paramname;
  String formname="";
  int i;
  
  if ((i=param.indexOf("."))>=0) paramname=param.substring(i+1);
  else paramname = param;

System.out.println("in MultiValues   paramname = " + paramname);
  StringBuffer query = new StringBuffer("");
  if (req.getParameterValues(paramname) != null &&
      ! req.getParameterValues(paramname)[0].equalsIgnoreCase("ALL")) {
    String[] list = req.getParameterValues(paramname);
   
    //query.append(" AND ");
    for (i=0; i<list.length; i++) {
      String d = list[i].toUpperCase(); 
      if (paramname.equals("msnname")) {
         if (d.equalsIgnoreCase("MARS ODYSSEY") || 
             d.equalsIgnoreCase("ODYSSEY"))  {
             d="2001 MARS ODYSSEY";
         }
         else if (d.equalsIgnoreCase("CLEMENTINE 1")) {
             d="DEEP SPACE PROGRAM SCIENCE EXPERIMENT"; 
         }
      }
      
      if (i>0) {
        query.append(" OR ");
      }
      d = escape(d);
         
      if (paramname.equals("msnname"))
        formname="mission";
      else if (paramname.equals("instname"))
        formname="instrument";
      else if (paramname.equals("insthostname"))
        formname="instrument_host_name";
         
      query.append(formname + ":" + d);
    }
    
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
  StringBuffer query = new StringBuffer("");
  if ((i=param.indexOf("."))>=0) paramname=param.substring(i+1);
  else paramname = param;

  if (!defaultSingleValue( req, paramname)) {
    String pstring = req.getParameterValues(paramname)[0].toUpperCase();
    pstring = escape(pstring);
    
    System.out.println("paramname = " + paramname);
    
    if (paramname.equalsIgnoreCase("targname"))
      query.append(" AND " + "target:" + pstring);
    else if (paramname.equals("targtype"))
      query.append(" AND " + "target_type:" + pstring);
    else if (paramname.equals("insttype"))
      query.append(" AND " + "instrument_type:" + pstring);   
    else if (paramname.equals("dsid")) {
      //pstring = pstring.replaceAll("/", "-");
      query.append(" AND " + "data_set_id:" + pstring);
    }
    else if (paramname.equals("dsname"))
      query.append(" AND " + "data_set_name:" + pstring);
    else if (paramname.equals("insthosttype"))
      query.append(" AND " + "instrument_host_type:" + pstring);
  }
  
  return query.toString();
}

String constructQuery(HttpServletRequest req, boolean advanced)
    throws ServletException, IOException
{

  StringBuffer query = new StringBuffer("");

  int L = (advanced) ? 7 : 3;
  for (int i=0; i<L; i++) {
    query.append(getSingleValue(req, singleValueCriteria[i], query.length()));
  }  
  System.out.println("singleValue query = " + query.toString());

  if (query.toString().length()>0) 
     query.append(" AND ");
     
  L = (advanced) ? 3 : 2;
  for (int i=0; i<L; i++) {
    query.append(getMultiValues(req, multiValueCriteria[i], query.length()));
  }
  System.out.println("multiValue query = " + query.toString());
    
  if (query.length()>0)
    return query.toString();
       
  else return "";
}

static String[] idxToName =
  { "msnname", "targname", "targtype", "instname", "insttype",
    "dataobjtype", "dsid", "dsname", "insthostname", "insthosttype",
    "archivestat", "nodename" };

String isSelected( HttpServletRequest req, String param, String value)
{
  if (req.getParameterValues(param)!=null) {
    for( int i=0; i<req.getParameterValues(param).length; i++) {
      String pv=req.getParameterValues(param)[i];
      if (pv.equalsIgnoreCase(value))
        return " selected";
      else if (param.equals("msnname")) { 
         if (value.equals("2001 MARS ODYSSEY") && 
            (pv.equalsIgnoreCase("Odyssey") || pv.equalsIgnoreCase("Mars Odyssey"))) {
              return " selected";
         }
         else if (value.equals("DEEP SPACE PROGRAM SCIENCE EXPERIMENT") &&
            pv.equalsIgnoreCase("Clementine 1") ) {
              return " selected";
         }
      }
    }
  }
  return "";
}

String getSearchParams(
     HttpServletRequest req, String query,
     String[] opts, boolean advanced, String searchUrl) {

  String r = "";
  int numcolumns = (advanced) ? 10 : 5;
  int g=0;  // generic counter
  List alias = null;
  ArrayList text_list, dbname_list=null;
  List[] optLists = new ArrayList[numcolumns];

  // each parameter gets an ArrayList
  for (int i=0; i<optLists.length; i++) 
  	optLists[i] = new ArrayList();
  
  try
  {
    long time0 = System.currentTimeMillis();

    GetSearchParams paramBean = new GetSearchParams(searchUrl);
    
  	//paramBean.getParams(query);
  	/*
  	for (int i=0; i<numcolumns; i++) {
        List l = optLists [i];
        String s = rs.getString(i+1);
        if (dontDisplay.indexOf("|"+s+"|")<0 && ! l.contains(s)) {
          l.add(s);
        }
      }
    */
    /*
    String [] instname = paramBean.getInstrumentName();
    for (int i=0; i<instname.length; i++) {
               String s = instname[i];
               if (!opts[INSTNAME].equals("")) opts[INSTNAME] += "\t";
               opts[INSTNAME] += "<option value=\""+s+"\""
                     +isSelected( req, idxToName[3], s)+">"
                     + s;
     
     } //for
    //opts[INSTNAME] = paramBean.getInstrumentName();
    */
    
 /*   
    // loop through all columns
    for (int i=0; i<optLists.length; i++) {
        List l = optLists [i];

        text_list = new ArrayList();
        dbname_list = new ArrayList();
        String [] msntext=req.getParameterValues("msntext");
        String [] msnparams=req.getParameterValues("msnname");
      
        if (msntext!=null) { // form interface
           alias = new ArrayList();

           for (c=0; c<msntext.length; c++) {
				 msntext[c].replaceAll(" ", "");
				 if (msntext[c].length()>0) { 
               alias.add((String)msntext[c]); 
             }
           }
        }
        else if (msnparams!=null) { // URL call
            alias = new ArrayList();
            for (c=0; c<msnparams.length; c++) {
                alias.add(CapFirstLetter((String)msnparams[c]));
            }
        }

        // mission names has to be displayed whatever the user selects
        // which can be alias name or same as db msnname
        if (i==MSNNAME && (msnparams!=null || msntext!=null) ) {
           
           // displayed text/alias, db msnname

           // sort the alias list because that's what will be displayed
           Collections.sort(alias);

           // create a temp array containing pairs of names as
           // (displayed text/alias , db msnname)
           j=0;
           for (c=0; c<alias.size(); c++) {

              String str=(String)alias.get(c);

              // if alias is one of the variations for 2001 Mars Odyssey
              // put alias and "2001 MARS ODYSSEY" into temp array
              if (str.equalsIgnoreCase("Mars Odyssey")||
                       (str.equalsIgnoreCase("Odyssey"))) {
                  if (l.contains("2001 MARS ODYSSEY")) {
                     text_list.add(str);
                     dbname_list.add("2001 MARS ODYSSEY");
                  }
              }
              // if alias is one of the variations for Deep Space
              // Program Science Experiment 
              // put alias and "DEEP SPACE PROGRAM SCIENCE EXPERIMENT"
              // into temp array
              else if (str.equalsIgnoreCase("Clementine 1")){ 
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
                  if (!opts[TARGNAMES].equals("")) 
                        opts[TARGNAMES] += ", ";
                  opts[TARGNAMES] +=  "'"+s.toUpperCase()+"'";
               }
            } //for
            
        } //else

       if (alias!=null) alias.clear();

    } //for - to go over all columns
*/
   }
   catch (Exception e) {
      if (alias!=null) alias.clear();
      r = e.toString();
      e.printStackTrace();
   }

  return r;
}

%>

<%-- Save the request URI in the user's session so we know to take him back here from the results page --%>
<%

  request.getSession(true).setAttribute("requestURI", request.getRequestURI());

  final String all_opt = "<option value=ALL selected>All\n";

System.out.println("request.getRequestURI() = " + request.getRequestURI());

  boolean power = request.getRequestURI().indexOf("power.jsp")>=0;
  boolean advanced = power || (request.getRequestURI().indexOf("advanced.jsp")>=0);

  boolean advWithParams = advanced && request.getParameterValues("dsid")!=null;

  String[] opts = new String[11];
  for (int i=0; i<opts.length; i++) opts[i]="";


  String q = ""; String r = "";

  // targname!=null means page called with params
  if (advWithParams) {
    q = constructQuery( request, true);
    System.out.println("in searchParamsJSP.jsp....advWithParams....q = " + q);
  }
  else
	if (request.getParameterValues("targname")!=null) {
      q = constructQuery( request, advanced);
      System.out.println("in searchParamsJSP.jsp....else....q = " + q);
    }


  // populate list of attribute's value
  GetSearchParams paramBean = new GetSearchParams(searchUrl);
  paramBean.getParams();
  
  // if index.jsp called with no parameters,
  // or if query is empty for some reason, then get all default values
  if (q.equals("")) {
    opts[MSNNAME]  = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "MISSION_NAME", "msnname", paramBean.getMissionName());
    opts[TARGNAME] = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "TARGET_NAME", "targname", paramBean.getTargetName());
    opts[TARGTYPE] = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "TARGET_TYPE", "targtype", paramBean.getTargetType());
    opts[INSTNAME] = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "INSTRUMENT_NAME", "instname", paramBean.getInstrumentName());
    opts[INSTTYPE] = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "INSTRUMENT_TYPE", "insttype", paramBean.getInstrumentType());
    opts[DATAOBJTYPE]  = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "DATA_OBJECT_TYPE", "dataobjtype", paramBean.getDataObjectType());
    opts[DSID]         = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "DATA_SET_ID", "dsid", paramBean.getDatasetId());
    opts[DSNAME]       = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "DATA_SET_NAME", "dsname", paramBean.getDatasetName());
    opts[INSTHOSTNAME] = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "INSTRUMENT_HOST_NAME", "insthostname", paramBean.getInstrumentHostName());
    opts[INSTHOSTTYPE] = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "INSTRUMENT_HOST_TYPE", "insthosttype", paramBean.getInstrumentHostType());
    opts[TARGNAMES] = "";
    for (int i=1; i<paramBean.getTargetName().length; i++) {
      if (!opts[TARGNAMES].equals("")) opts[TARGNAMES] += ",";
      opts[TARGNAMES] += "'"+paramBean.getTargetName()[i].toUpperCase() +"'";
    }
  
  /*
    opts[MSNNAME]  = jpl.pds.util.DisplayOptions.displayValList(request, searchBean.getPDSKeyword("msnname"), "msnname", paramBean.getMissionNamePair());
    opts[TARGNAME] = jpl.pds.util.DisplayOptions.displayValList(request, searchBean.getPDSKeyword("targname"), "targname", paramBean.getTargetName());
    opts[TARGTYPE] = jpl.pds.util.DisplayOptions.displayValList(request, searchBean.getPDSKeyword("targtype"), "targtype", paramBean.getTargetType());
    opts[INSTNAME] = jpl.pds.util.DisplayOptions.displayValList(request, searchBean.getPDSKeyword("instname"), "instname", paramBean.getInstrumentName());
    opts[INSTTYPE] = jpl.pds.util.DisplayOptions.displayValList(request, searchBean.getPDSKeyword("insttype"), "insttype", paramBean.getInstrumentType());
    opts[DATAOBJTYPE]  = jpl.pds.util.DisplayOptions.displayValList(request, searchBean.getPDSKeyword("dataobjtype"), "dataobjtype", paramBean.getDataObjectType());
    opts[DSID]         = jpl.pds.util.DisplayOptions.displayValList(request, searchBean.getPDSKeyword("dsid"), "dsid", paramBean.getDatasetId());
    opts[DSNAME]       = jpl.pds.util.DisplayOptions.displayValList(request, searchBean.getPDSKeyword("dsname"), "dsname", paramBean.getDatasetName());
    opts[INSTHOSTNAME] = jpl.pds.util.DisplayOptions.displayValList(request, searchBean.getPDSKeyword("insthostname"), "insthostname", paramBean.getInstrumentHostName());
    opts[INSTHOSTTYPE] = jpl.pds.util.DisplayOptions.displayValList(request, searchBean.getPDSKeyword("insthosttype"), "insthosttype", paramBean.getInstrumentHostType());
    opts[TARGNAMES] = "";
    for (int i=1; i<paramBean.getTargetName().length; i++) {
      if (!opts[TARGNAMES].equals("")) opts[TARGNAMES] += ",";
      opts[TARGNAMES] += "'"+paramBean.getTargetName()[i].toUpperCase() +"'";
    }
    */
  }
  // else user changed some search specs, 
  // we need to construct search parameters to match
  else
  {
	/*
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
System.out.println("*****************************************");
System.out.println("before calling getSearchParams method...");
System.out.println("q = " + q);
    r = getSearchParams( request, q, opts, advanced, searchUrl);
    paramBean.initialize();
    paramBean.getParams(q);
    
    opts[MSNNAME]  = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "MISSION_NAME", "msnname", paramBean.getMissionName());
    opts[TARGNAME] = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "TARGET_NAME", "targname", paramBean.getTargetName());
    opts[TARGTYPE] = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "TARGET_TYPE", "targtype", paramBean.getTargetType());
    opts[INSTNAME] = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "INSTRUMENT_NAME", "instname", paramBean.getInstrumentName());
    opts[INSTTYPE] = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "INSTRUMENT_TYPE", "insttype", paramBean.getInstrumentType());
    opts[DATAOBJTYPE]  = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "DATA_OBJECT_TYPE", "dataobjtype", paramBean.getDataObjectType());
    opts[DSID]         = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "DATA_SET_ID", "dsid", paramBean.getDatasetId());
    opts[DSNAME]       = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "DATA_SET_NAME", "dsname", paramBean.getDatasetName());
    opts[INSTHOSTNAME] = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "INSTRUMENT_HOST_NAME", "insthostname", paramBean.getInstrumentHostName());
    opts[INSTHOSTTYPE] = gov.nasa.pds.dsview.util.DisplayOptions.displayValList(request, "INSTRUMENT_HOST_TYPE", "insthosttype", paramBean.getInstrumentHostType());
    opts[TARGNAMES] = "";
    for (int i=1; i<paramBean.getTargetName().length; i++) {
      if (!opts[TARGNAMES].equals("")) opts[TARGNAMES] += ",";
      opts[TARGNAMES] += "'"+paramBean.getTargetName()[i].toUpperCase() +"'";
    }
    
    
    
    /*
    // if a single value param has more than one options, then add "All" option;
    if (opts[TARGNAME].indexOf("\t")>=0) opts[TARGNAME] = all_opt + opts[TARGNAME];
    if (opts[TARGTYPE].indexOf("\t")>=0) opts[TARGTYPE] = all_opt + opts[TARGTYPE];
    if (opts[INSTTYPE].indexOf("\t")>=0) opts[INSTTYPE] = all_opt + opts[INSTTYPE];
    if (advanced) {
        if (opts[DATAOBJTYPE].indexOf("\t")>=0) opts[DATAOBJTYPE] = all_opt + opts[DATAOBJTYPE];
        if (opts[DSID].indexOf("\t")>=0) opts[DSID] = all_opt + opts[DSID];
        if (opts[DSNAME].indexOf("\t")>=0) opts[DSNAME] = all_opt + opts[DSNAME];
        if (opts[INSTHOSTTYPE].indexOf("\t")>=0) opts[INSTHOSTTYPE] = all_opt + opts[INSTHOSTTYPE];
    }
    */
  }
%>
