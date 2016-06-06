<html>
  <head>
    <title>Search Servlet Results Page</title>
    <%@ page language="java" import = "gov.nasa.pds.search.servlet.*, org.apache.solr.common.*"%>
  </head>
  <body>
    <h1>Search Servlet Results Page</h1>

<%
    String solrServerUrl = application.getInitParameter("solrServerUrl");
    String query = request.getParameter("q");
    if ((query == null) || (query == "")) {
%>
    <p>Please specify a query.</p>
<%
    } else {
      SolrSearch solrSearch = new SolrSearch(solrServerUrl);
      SolrDocumentList docList = solrSearch.search(query, 0, 50);
      long totalCount = docList.getNumFound();
      int returnedCount = 0;
      StringBuffer sb = new StringBuffer("http://localhost:8080/transport-registry/prod?");
      for (SolrDocument doc : docList) {
        returnedCount++;
        sb.append("identifier=" + (String) doc.getFirstValue("identifier") + "&");
      }
      sb.delete(sb.length()-1, sb.length());
      String queryString = sb.toString();
%>
    <p>Displaying <%=returnedCount%> of <%=totalCount%> results. Return to the <a href="index.html">query</a> page to refine the constraints, <a href="<%=queryString%>">download</a> all of the products listed below or use the corresponding <i>Download</i> buttons to download specific products.</p>
    <table>
      <tr>
        <th align="left">File Name</th>
        <th>Access</th>
      </tr>
<%
      for (SolrDocument doc : docList) {
        String identifier = (String) doc.getFirstValue("identifier");
        String fileName = (String) doc.getFirstValue("file_name");
%>
      <tr>
        <td align="left"><%=fileName%></td>
        <td><a href="http://localhost:8080/transport-registry/prod?identifier=<%=identifier%>"><input type="button" value="Download"/></a></td>
      </tr>
<%
      }
%>
    </table>
<%
    }
%>
  </body>
</html>
