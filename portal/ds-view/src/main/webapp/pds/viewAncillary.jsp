<%@ page language="java" 
import="java.io.*,java.lang.*,java.net.URLEncoder"
import="java.net.HttpURLConnection, javax.servlet.GenericServlet"
import="java.net.URL,java.util.*,pds.util.*,java.util.List"
import="java.util.Iterator,javax.naming.*,javax.sql.*, java.sql.*" 
session="true" contentType="text/html" %>

<%!
 // returns a string either before or behind the specified pattern
 // based on the directiion specification at the call
 // 0 is to return the portion before the pattern
 // 1 is to return the portion behind the pattern
 String splitString (String str, String pattern, int direction) {
   String[] result = str.split(pattern,2);
   String ret=null;

   if (direction==0) ret=result[0].toString();
   if (direction==1) ret=result[1].toString();
   return ret;
}
%>


<%!
// provides a matching mime type based on file name extension 

   String getMimeType (String fn, boolean is_webgrid) { 
      String mimeType=null; 
      String tempstr=null;

      fn = fn.toLowerCase(); 
      if (is_webgrid)  tempstr="mime";
      else             tempstr="mimeType"; 

      if (fn.endsWith(".lbl") ) {
            mimeType = "PDS_LABEL";
      }
      else if (fn.endsWith(".txt") || fn.endsWith(".cat") ||
                 fn.endsWith(".fmt") || fn.endsWith(".asc") ||
                 fn.endsWith(".tab") || fn.endsWith(".tsc") ||
                 fn.endsWith(".ti") || fn.endsWith(".tls") ||
                fn.endsWith(".tpc") || fn.endsWith(".tf")) {

              mimeType = "RAW&"+tempstr+"=text/plain";
      }
      else if (fn.endsWith(".jpg") ) {
           mimeType = "RAW&"+tempstr+"=image/jpeg";
      }
      else if (fn.endsWith(".gif") ) {
           mimeType = "RAW&"+tempstr+"=image/gif";
      }
      else if (fn.endsWith(".htm") || fn.endsWith(".html")) {
           mimeType = "RAW&"+tempstr+"=text/html";
      //}else if (fn.endsWith(".pdf")) {
          //mimeType = "RAW&"+tempstr+"=application/pdf";
      //}else if (fn.endsWith(".zip")){
          //mimeType = "RAW&"+tempstr+"=application/zip";
      //}else if (fn.endsWith(".doc")){
          //mimeType = "RAW&"+tempstr+"=application/msword";
      }
      else{
           mimeType = "RAW";
      } 

      return mimeType; 
    } 
%>

<%
String server = getServletConfig().getServletContext().getInitParameter("prodserver.url");
String datasetID = request.getParameter("dsid");      // lowercase dsid
String directory = request.getParameter("volume");
String node = request.getParameter("nodename");
String temp_datasetID = request.getParameter("localdsid"); 
String psclass = request.getParameter("psclass");
String ancilfile = request.getParameter("ancillary");

String nodepath = "/data/";
ArrayList paths = new ArrayList();
ArrayList pathsSub = new ArrayList();
Vector printList = new Vector();

Connection conn = null;
Statement statement = null;
ResultSet rs = null;
String query = null;

String resclass = null;
String prod_reslink = null;
String leadProdLink = null;
boolean is_webgrid = true;

%>

<html><body><table width="760">

	<tr bgcolor="#efefef">
    	<td colspan=2><b>Software, Documentation and Other Ancillary Information</b></td>
  </tr>
  <tr bgcolor="#E7EEF9">
  	<td colspan=2>
		<table>
			<tr>
				<td>
					<ul>
<%

try {

   //find out the beginning portion of the URL to contact the product server
   if (psclass.equals("primary")) {
       resclass="system.productServer.primary";
   }
   else if (psclass.equals("backup")) {
       resclass="system.productServer.backup";
   }
   else {
       throw new Exception ("Invalid psclass parameter value -- "+psclass+" --");
   }

   javax.naming.Context init = new javax.naming.InitialContext();
   javax.naming.Context env = (Context)init.lookup("java:comp/env");
   DataSource ds = (DataSource) env.lookup("jdbc/pdsprofile");
   conn = ds.getConnection();
   statement = conn.createStatement();
   query = "select reslink from resinfo ri, dsinfo di, resds rd " +
           "where di.dsid = '"+ datasetID.toUpperCase() +"' and " +
           "ri.resclass='"+ resclass + "' and di.dsid=rd.dsid " +
              "and rd.resourceid=ri.resourceid"; 
   rs = statement.executeQuery(query);
   if (rs!=null) {
      while(rs.next()){
         prod_reslink = rs.getString(1);
      }
   }
   rs.close();
   statement.close();

   // if no matching resclass, send offline page
   if (prod_reslink==null) {
        response.sendRedirect("http://starbase.jpl.nasa.gov/archive/xxx-x-n-xxr-volume-offline-v1.0");
   }
}
catch (SQLException se) {
   System.err.println (se.getMessage());
   throw se;
}
catch (Exception e) {
   throw e;
}
finally {
	if (conn!=null) {
        conn.close();
   }   
}

try { 

   // examine whether the reslink is a webgrid or earlier interface
   // to product server. The URL syntax differs
   if (prod_reslink.indexOf("prod?object=")!=-1) is_webgrid=false;

   leadProdLink = splitString (prod_reslink, "\\+AND\\+RT%3DDIRLIST1", 0);
	DirList dirEntriesFile = DirParser.parse(leadProdLink + "/" + directory.toLowerCase() + "+AND+RETURN_TYPE+%3D+DIRLIST1");
			DirList FileList = dirEntriesFile.getDirEntriesByVolume(directory);
			for (Iterator i = FileList.iterator(); i.hasNext(); ){
				DirEntry entryFile = (DirEntry) i.next();
      			OFSN ofsnFile = entryFile.getOFSN();
				paths.add(ofsnFile.getPath());
			}


} catch (Exception e) { 
	// catch possible io errors from readLine()
	System.out.println("got a Product Server DirList error!");
	e.printStackTrace();
}
if (paths.size() <= 0){
	out.print("There are no ancillary files for this data set ID.");
}else{
	if (ancilfile == null || ancilfile.equals("")){
		for (int j = 0; j < paths.size(); j++){
			if (paths.get(j) != null && ((String)paths.get(j)).equalsIgnoreCase("software")){
				%>
				<li><A href="viewDataset.jsp?datasetid=<%=datasetID%>&nodename=<%=node%>&volume=<%=directory%>&localdsid=<%=temp_datasetID%>&psclass=<%=psclass%>&ancillary=software">Software</A>
				 - contains software for the archival data set and files within the data set.<P>
				<%
			}
			if (paths.get(j) != null && ((String)paths.get(j)).equalsIgnoreCase("document")){
				%>
				<li><A href="viewDataset.jsp?dsid=<%=datasetID%>&nodename=<%=node%>&volume=<%=directory%>&localdsid=<%=temp_datasetID%>&psclass=<%=psclass%>&ancillary=document">Document</A>
				 - contains documentation on the archival data set and files within the data set.<P>
				 <%
			}
			if (paths.get(j) != null && ((String)paths.get(j)).equalsIgnoreCase("calib")){
				%>
				<li><A href="viewDataset.jsp?dsid=<%=datasetID%>&nodename=<%=node%>&volume=<%=directory%>&localdsid=<%=temp_datasetID%>&psclass=<%=psclass%>&ancillary=calib">Calibration</A>
				 - contains leap second, planetary constants, clock conversion, and similar files.<P>
				<%
			}
				if (paths.get(j) != null && ((String)paths.get(j)).equalsIgnoreCase("catalog")){
				%>
				<li><A href="viewDataset.jsp?dsid=<%=datasetID%>&nodename=<%=node%>&volume=<%=directory%>&localdsid=<%=temp_datasetID%>&psclass=<%=psclass%>&ancillary=catalog">Catalog</A>
				 - contains catalog files for the data set.<P>
				<%
			}
		}
%>
			</ul>
<%
	}else {
		try { 
			DirList dirEntriesFile = DirParser.parse(leadProdLink + "/" + directory.toLowerCase() + "/" + ancilfile.toLowerCase() + "+AND+RETURN_TYPE+%3D+DIRFILELIST");
			DirList FileList = dirEntriesFile.getDirEntriesByVolume(directory);
			for (Iterator i = FileList.iterator(); i.hasNext(); ){
				DirEntry entryFile = (DirEntry) i.next();
      			OFSN ofsnFile = entryFile.getOFSN();
				pathsSub.add(ofsnFile.getPath());
			}
		} catch (Exception e) { 
           	// catch possible io errors from readLine()
           	System.out.println("got a Product Server DirList error!");
           	e.printStackTrace();
		}
		if (ancilfile.indexOf("software") != -1){
			for (int a = 0; a < pathsSub.size(); a++){
				if (((String)pathsSub.get(a)).equalsIgnoreCase("software/softinfo.txt")){%>
<div class="dsid"><b>Software</b><br>
<%
					URL u = new URL(leadProdLink + "/" + directory.toLowerCase() + "/software/softinfo.txt+AND+RETURN_TYPE+%3D+RAW");
					HttpURLConnection connection = (HttpURLConnection)u.openConnection();
					connection.connect();
					InputStream in = connection.getInputStream();
					String record = null;
					//FileReader fr=null;
   					BufferedReader br=null;
					try { 

	   					//fr = new FileReader(in);
						br = new BufferedReader(new InputStreamReader(in));
     					record = new String();
						while ((record = br.readLine()) != null) {
							StringTokenizer st = new StringTokenizer(record, "\n");
     						while (st.hasMoreTokens()) {
        						String recordline = st.nextToken();
								out.println(recordline);
%>
<br>
<%
							}
						}
       				 }catch (IOException e) { 
        				// catch possible io errors from readLine()
        				System.out.println("got an IOException error!");
        				e.printStackTrace();
    				}
				}
			}
		}
		if (ancilfile.indexOf("catalog") != -1){
			for (int a = 0; a < pathsSub.size(); a++){
				if (((String)pathsSub.get(a)).equalsIgnoreCase("catalog/catinfo.txt")){%>
<div class="dsid"><b>Catalog</b><br>
<%
			URL u = new URL(leadProdLink + "/" + directory.toLowerCase() + "/catalog/catinfo.txt+AND+RETURN_TYPE+%3D+RAW");
			HttpURLConnection connection = (HttpURLConnection)u.openConnection();
			connection.connect();
			InputStream in = connection.getInputStream();
			String record = null;
			//FileReader fr=null;
    		BufferedReader br=null;
       		try { 

	 			//fr = new FileReader(in);
	   			br = new BufferedReader(new InputStreamReader(in));
    			record = new String();
				while ((record = br.readLine()) != null) {
					StringTokenizer st = new StringTokenizer(record, "\n");
    				while (st.hasMoreTokens()) {
       					String recordline = st.nextToken();
						out.println(recordline);
%>
<br>
<%
					}
				}
    		 }catch (IOException e) { 
       			// catch possible io errors from readLine()
       			System.out.println("got an IOException error!");
       			e.printStackTrace();
    		}
			}
			}
		}
		if (ancilfile.indexOf("calib") != -1){
			for (int a = 0; a < pathsSub.size(); a++){
				if (((String)pathsSub.get(a)).equalsIgnoreCase("calib/calinfo.txt")){%>
<div class="dsid"><b>Calibration</b><br>
<%
			URL u = new URL(leadProdLink + "/" + directory.toLowerCase() + "/calib/calinfo.txt+AND+RETURN_TYPE+%3D+RAW");
			HttpURLConnection connection = (HttpURLConnection)u.openConnection();
			connection.connect();
			InputStream in = connection.getInputStream();
			String record = null;
			//FileReader fr=null;
    		BufferedReader br=null;
    		try { 
   				//fr = new FileReader(in);
   				br = new BufferedReader(new InputStreamReader(in));
   				record = new String();
				while ((record = br.readLine()) != null) {
					StringTokenizer st = new StringTokenizer(record, "\n");
   					while (st.hasMoreTokens()) {
       					String recordline = st.nextToken();
						out.println(recordline);
%>
<br>
<%
					}
				}
   			 }catch (IOException e) { 
       			// catch possible io errors from readLine()
       			System.out.println("got an IOException error!");
       			e.printStackTrace();
   			}
			}
			}
			
		}
		if (ancilfile.indexOf("document") != -1){
			for (int a = 0; a < pathsSub.size(); a++){
				if (((String)pathsSub.get(a)).equalsIgnoreCase("document/docinfo.txt")){%>
<div class="dsid"><b>Documentation</b><br> 
<%
			URL u = new URL(leadProdLink + "/" + directory.toLowerCase() + "/document/docinfo.txt+AND+RETURN_TYPE+%3D+RAW");
			HttpURLConnection connection = (HttpURLConnection)u.openConnection();
			connection.connect();
			InputStream in = connection.getInputStream();
			String record = null;
			//FileReader fr=null;
   			BufferedReader br=null;
   			try { 
  				//fr = new FileReader(in);
   				br = new BufferedReader(new InputStreamReader(in));
   				record = new String();
				while ((record = br.readLine()) != null) {
					StringTokenizer st = new StringTokenizer(record, "\n");
   					while (st.hasMoreTokens()) {
       					String recordline = st.nextToken();
						out.println(recordline);
%>
<br>
<%
					}
				}
   			 }catch (IOException e) { 
       			// catch possible io errors from readLine()
       			System.out.println("got an IOException error!");
       			e.printStackTrace();
   			}
			}
			}
		}
		out.println("<br><b>Files contained within the " + ancilfile + " directory:</b><br>");
		ArrayList crumbs = new ArrayList();
      String path_crumbs = null;
		for (int w = 0; w < pathsSub.size(); w++){
			 path_crumbs = (String)((String)pathsSub.get(w)).toLowerCase();

			 int temp1 = 0;
			 String tempString = null;
			 if (ancilfile.equals("software")){
				temp1 = 9;
			 } else if (ancilfile.equals("catalog")){
				temp1 = 8;
			 } else if (ancilfile.equals("document")){
				temp1 = 9;
			 } else if (ancilfile.equals("calib")){
				temp1 = 6;
			 }
			 tempString = ((String)pathsSub.get(w)).substring(temp1);

          String tempString2 = leadProdLink + '/'+ directory.toLowerCase()
                  +'/'+ path_crumbs + "+AND+RETURN_TYPE+%3D" 
                  + getMimeType(path_crumbs, is_webgrid);
					%>
          <a href="<%=tempString2%>"><%=tempString%></a>
         <br>
<%
		}
	}
}
			%></td>
			</tr>
			</table>
		</td>
	</tr>

</table></body></html>
