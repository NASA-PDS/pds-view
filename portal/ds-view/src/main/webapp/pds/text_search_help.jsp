<%
String pdshome = "";
pdshome = application.getInitParameter("pdshome.url");
%>
<html>
<head>

<title>Planetary Data System: Data Services: Text Search Help</title>
<meta  name="keywords"  content="Planetary Data System">
<meta  name="description" content="This website serves as a mechanism for subscribing to data, software and documentation from the PDS.">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="<%=pdshome%>css/pds_style.css" rel="stylesheet" type="text/css">
</head>

<body>

<table bgcolor="#FFFFFF" border="0" cellspacing="0" cellpadding="2" width="100%">

<tr> <td>
         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <td class="pageTitle">Text Search Help</td>
               <td align="right">
                <a href="javascript:window.close()">
                <img src="images/btn_close_window.gif" alt="Close Window" 
                    border="0" width="117" height="24"></a>   </td>
            </tr>
            <tr>
               <td colspan="2"><img src="images/gray.gif" width="504" height="1" alt="Gray Horizontal Divider" border="0"></td>
            </tr>
         </table>
</td></tr>
<br>




      <tr><td bgcolor="#525D76">
          <font size="+1" color="#ffffff"><strong>Overview</strong></font>
      </td></tr>
      <tr><td>
        <blockquote>
         <p>This search engine provides free text search for the PDS Central Catalog and Engineering Node web site. For this prototype we have used an open source Apache project named <a href="http://lucene.apache.org/java/docs/">Lucene</a>. Currently, this is a prototype effort so there may be bugs that we would like to <a href="mailto:Paul.Ramirez@jpl.nasa.gov">hear</a> about. If you happen to run across an anomalous result, please let us know the query you submitted and the page for which the result is found. There is an effort to clean up the catalog and this type of information will be of help.
         </p>
        </blockquote>
      </td></tr>
      <tr><td><br/></td></tr>
</table>

<table bgcolor="#FFFFFF" border="0" cellspacing="0" cellpadding="2" width="100%">
      <tr><td bgcolor="#525D76">
        <font color="#ffffff" size="+1">
          <strong>Terms</strong>
        </font>
      </td></tr>
      <tr><td>
        <blockquote>
          <p>A query is broken up into terms and operators. There are two types of terms: Single Terms and Phrases.</p>
          <p>A Single Term is a single word such as "planets" or "spacecraft".</p>
          <p>A Phrase is a group of words surrounded by double quotes such as "Space Science Reviews".</p>
          <p>Multiple terms can be combined together with Boolean operators to form a more complex query (see below).</p>
        </blockquote>

        </p>
      </td></tr>
      <tr><td><br/></td></tr>
</table>
                                                
<table bgcolor="#FFFFFF" border="0" cellspacing="0" cellpadding="2" width="100%">
      <tr><td bgcolor="#525D76">
        <font color="#ffffff" size="+1">
          <strong>Fields</strong>

        </font>
      </td></tr>
      <tr><td>
        <blockquote>
           <p>Lucene supports fielded data. When performing a search you can either specify a field, or use the default field. The field names and default field is implementation specific.</p>
           <p>You can search any field by typing the field name followed by a colon ":" and then the term you are looking for. </p>
           <p>As an example, the index contains two fields, Title and contents and contents is the default field. If you wish to find an entry entitled "Mars Observer" which contains the contents "Canaveral", you can enter:                                               
    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

      <td bgcolor="#ffffff"><pre>Title:&quot;Mars Observer&quot; AND contents:Canaveral</pre></td>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

    </tr>
    </table>
    </div>
                                                <p>or</p>
                                                    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#ffffff"><pre>title:&quot;Mars Orbiter&quot; AND Canaveral</pre></td>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

    </tr>
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    </table>
    </div>
         <p>Since contents is the default field, the field indicator is not required.</p>
         <p>Note: The field is only valid for the term that it directly precedes, so the query</p>
         <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>

    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#ffffff"><pre>Title:Mars Orbiter</pre></td>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    </table>
    </div>
       <p>Will only find "Mars" in the Title field. It will find "Orbiter" in the default field (in this case the contents field). </p>

       <p>Note: The field names are case sensitive and below is a listing of available fields arranged by content type</p>

     <table cellspacing="1" cellpadding="5" border="1" width="100%">
         <tr>
          <th>All</th>
          <th>Dataset</th>
          <th>Mission</th>
          <th>Instrument</th>
         </tr>
         <tr>
           <td>
             Identifier<br/>
             Title<br/>
             Description<br/>
             resClass<br/>
           </td>
           <td>
             instrument_name<br/>
             stop_time<br/>
             resource_id<br/>
             archive_status<br/>
             data_set_release_date<br/>
             data_set_id<br/>
             data_set_name<br/>
             target_name<br/>
             instrument_host_id<br/>
             data_object_type<br/>
             target_type<br/>
             instrument_id<br/>
             instrument_type<br/>
             full_name<br/>
           </td>
           <td>
             mission_start_date<br/>
             mission_stop_date<br/>
             mission_name<br/>
           </td>
           <td>
             instrument_host_id<br/>
             instrument_id<br/>
             instrument_type<br/>
           </td>
          </tr>
     </table>
     <table cellspacing="1" cellpadding="5" border="1" width="100%">
        <tr>
          <th>Instrument Host</th>
          <th>Target</th>
          <th>Volume</th>
          <th>Resource</th>
          <th>Web</th>
         </tr>
         <tr>
           <td>
             instrument_host_name<br/>
             instrument_host_id<br/>
           </td>
           <td>
             primary_body_name<br/>
             target_name<br/>
             target_type<br/>
           </td>
           <td>
             &nbsp;
           </td>
           <td>
             resource_id<br/>
             resource_name<br/>
           </td>
           <td>
             &nbsp;
           </td>
     </table>

                            </blockquote>
        </p>
      </td></tr>

      <tr><td><br/></td></tr>
</table>
                                                
<table bgcolor="#FFFFFF" border="0" cellspacing="0" cellpadding="2" width="100%">
      <tr><td bgcolor="#525D76">
        <font color="#ffffff" size="+1">
          <strong>Term Modifiers</strong>
        </font>
      </td></tr>

      <tr><td>
        <blockquote>
                                    <p>Lucene supports modifying query terms to provide a wide range of searching options.</p>
                                                    
  <table border="0" cellspacing="0" cellpadding="2" width="100%">
      <tr><td bgcolor="#828DA6">
        <font color="#ffffff" size="+1">
          <strong>Wildcard Searches</strong>
        </font>

      </td></tr>
      <tr><td>
        <blockquote>
                                    <p>Lucene supports single and multiple character wildcard searches.</p>
                                                <p>To perform a single character wildcard search use the "?" symbol.</p>
                                                <p>To perform a multiple character wildcard search use the "*" symbol.</p>
                                                <p>The single character wildcard search looks for terms that match that with the single character replaced. For example, to search for "text" or "test" you can use the search:</p>

                                                    <div align="left">
    
    <table cellspacing="4" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

      <td bgcolor="#ffffff"><pre>te?t</pre></td>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>

    </table>
    </div>
                                                <p>Multiple character wildcard searches looks for 0 or more characters. For example, to search for orbit, orbits or orbiter, you can use the search: </p>
                                                    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#ffffff"><pre>orbit*</pre></td>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>

      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    </table>
    </div>
                                                <p>You can also use the wildcard searches in the middle of a term.</p>
                                                    <div align="left">

    <table cellspacing="4" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#ffffff"><pre>or*t</pre></td>

      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    </table>
    </div>

                                                <p>Note: You cannot use a * or ? symbol as the first character of a search.</p>
                            </blockquote>
      </td></tr>
      <tr><td><br/></td></tr>
  </table>
                                                    
  <table border="0" cellspacing="0" cellpadding="2" width="100%">
      <tr><td bgcolor="#828DA6">
        <font color="#ffffff" size="+1">

          <strong>Fuzzy Searches</strong>
        </font>
      </td></tr>
      <tr><td>
        <blockquote>
                                    <p>Lucene supports fuzzy searches based on the Levenshtein Distance, or Edit Distance algorithm. To do a fuzzy search use the tilde, "~", symbol at the end of a Single word Term. For example to search for a term similar in spelling to "rover" use the fuzzy search: </p>
                                                    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">

    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#ffffff"><pre>rover~</pre></td>

      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    </table>
    </div>

                                                <p>This search will find terms like cover and rovers.</p>
                                                <p>Starting with Lucene 1.9 an additional (optional) parameter can specify the required similarity. The value is between 0 and 1, with a value closer to 1 only terms with a higher similarity will be matched. For example:</p>
                                                    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#ffffff"><pre>oddysey~0.7</pre></td>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    </table>
    </div>
                                                <p>The default that is used if the parameter is not given is 0.5.</p>
                            </blockquote>
      </td></tr>

      <tr><td><br/></td></tr>
  </table>
                                                    
  <table border="0" cellspacing="0" cellpadding="2" width="100%">
      <tr><td bgcolor="#828DA6">
        <font color="#ffffff" size="+1">
          <strong>Proximity Searches</strong>
        </font>
      </td></tr>

      <tr><td>
        <blockquote>
                                    <p>Lucene supports finding words are a within a specific distance away. To do a proximity search use the tilde, "~", symbol at the end of a Phrase. For example to search for a "experiment" and "record" within 10 words of each other in a document use the search: </p>
                                                    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#ffffff"><pre>&quot;experiment record&quot;~10</pre></td>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>

      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    </table>
    </div>
                            </blockquote>
      </td></tr>
      <tr><td><br/></td></tr>

  </table>
                                                    
  <table border="0" cellspacing="0" cellpadding="2" width="100%">
      <tr><td bgcolor="#828DA6">
        <font color="#ffffff" size="+1">
          <strong>Range Searches</strong>
        </font>
      </td></tr>
      <tr><td>

        <blockquote>
                                    <p>Range Queries allow one to match documents whose field(s) values
            are between the lower and upper bound specified by the Range Query.
            Range Queries can be inclusive or exclusive of the upper and lower bounds.
            Sorting is done lexicographically.</p>
                                                    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#ffffff"><pre>start_date:[1995 TO 2001]</pre></td>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    </table>
    </div>
                                                <p>This will find results whose start_date fields have values between 1995 and 2001, inclusive.
            Note that Range Queries are not reserved for date fields.  You could also use range queries with non-date fields:</p>
                                                    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">

    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#ffffff"><pre>Title:{Mars TO Mercury}</pre></td>

      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    </table>
    </div>

                                                <p>This will find all documents whose titles are between Mars and Mercury, but not including Mars and Mercury.</p>
                                                <p>Inclusive range queries are denoted by square brackets.  Exclusive range queries are denoted by
            curly brackets.</p>
                            </blockquote>
      </td></tr>
      <tr><td><br/></td></tr>
  </table>
                                                    
  <table border="0" cellspacing="0" cellpadding="2" width="100%">
      <tr><td bgcolor="#828DA6">

        <font color="#ffffff" size="+1">
          <strong>Boosting a Term</strong>
        </font>
      </td></tr>
      <tr><td>
        <blockquote>
                                    <p>Lucene provides the relevance level of matching documents based on the terms found. To boost a term use the caret, "^", symbol with a boost factor (a number) at the end of the term you are searching. The higher the boost factor, the more relevant the term will be.</p>
                                                <p>Boosting allows you to control the relevance of a document by boosting its term. For example, if you are searching for</p>

                                                    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

      <td bgcolor="#ffffff"><pre>mars orbiter</pre></td>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>

    </table>
    </div>
                                                <p>and you want the term "mars" to be more relevant boost it using the ^ symbol along with the boost factor next to the term.
        You would type:</p>
                                                    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#ffffff"><pre>mars^4 orbiter</pre></td>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>

      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    </table>
    </div>
                                                <p>This will make documents with the term mars appear more relevant. You can also boost Phrase Terms as in the example: </p>
                                                    <div align="left">

    <table cellspacing="4" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#ffffff"><pre>&quot;mars rover&quot;^4 &quot;Mars Pathfinder&quot;</pre></td>

      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    </table>
    </div>

                                                <p>By default, the boost factor is 1. Although the boost factor must be positive, it can be less than 1 (e.g. 0.2)</p>
                            </blockquote>
      </td></tr>
      <tr><td><br/></td></tr>
  </table>
                            </blockquote>
        </p>
      </td></tr>

      <tr><td><br/></td></tr>
</table>
                                                
<table bgcolor="#FFFFFF" border="0" cellspacing="0" cellpadding="2" width="100%">
      <tr><td bgcolor="#525D76">
        <font color="#ffffff" size="+1">
          <strong>Boolean operators</strong>
        </font>
      </td></tr>

      <tr><td>
        <blockquote>
                                    <p>Boolean operators allow terms to be combined through logic operators.
        Lucene supports AND, "+", OR, NOT and "-" as Boolean operators(Note: Boolean operators must be ALL CAPS).</p>
                                                    
  <table border="0" cellspacing="0" cellpadding="2" width="100%">
      <tr><td bgcolor="#828DA6">
        <font color="#ffffff" size="+1">
          <strong>OR</strong>
        </font>

      </td></tr>
      <tr><td>
        <blockquote>
                                    <p>The OR operator is the default conjunction operator. This means that if there is no Boolean operator between two terms, the OR operator is used.
        The OR operator links two terms and finds a matching document if either of the terms exist in a document. This is equivalent to a union using sets.
        The symbol || can be used in place of the word OR.</p>
                                                <p>To search for documents that contain either "Mars Pathfinder" or just "mars" use the query:</p>
                                                    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">
    <tr>

      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#ffffff"><pre>&quot;Mars Pathfinder&quot; mars</pre></td>

      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    </table>
    </div>

                                                <p>or</p>
                                                    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>

    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#ffffff"><pre>&quot;Mars Pathfinder&quot; OR mars</pre></td>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    </table>
    </div>
                            </blockquote>
      </td></tr>
      <tr><td><br/></td></tr>
  </table>

                                                    
  <table border="0" cellspacing="0" cellpadding="2" width="100%">
      <tr><td bgcolor="#828DA6">
        <font color="#ffffff" size="+1">
          <strong>AND</strong>
        </font>
      </td></tr>
      <tr><td>
        <blockquote>

                                    <p>The AND operator matches documents where both terms exist anywhere in the text of a single document.
        This is equivalent to an intersection using sets. The symbol &amp;&amp; can be used in place of the word AND.</p>
                                                <p>To search for documents that contain "Mars Pathfinder" and "mars rover" use the query: </p>
                                                    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#ffffff"><pre>&quot;Mars Pathfinder&quot; AND &quot;mars rover&quot;</pre></td>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>

    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    </table>
    </div>
                            </blockquote>
      </td></tr>

      <tr><td><br/></td></tr>
  </table>
                                                    
  <table border="0" cellspacing="0" cellpadding="2" width="100%">
      <tr><td bgcolor="#828DA6">
        <font color="#ffffff" size="+1">
          <strong>+</strong>
        </font>
      </td></tr>

      <tr><td>
        <blockquote>
                                    <p>The "+" or required operator requires that the term after the "+" symbol exist somewhere in a the field of a single document.</p>
                                                <p>To search for documents that must contain "mars" and may contain "rover" use the query:</p>
                                                    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#ffffff"><pre>+mars rover</pre></td>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>

    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    </table>
    </div>
                            </blockquote>
      </td></tr>

      <tr><td><br/></td></tr>
  </table>
                                                    
  <table border="0" cellspacing="0" cellpadding="2" width="100%">
      <tr><td bgcolor="#828DA6">
        <font color="#ffffff" size="+1">
          <strong>NOT</strong>
        </font>
      </td></tr>

      <tr><td>
        <blockquote>
                                    <p>The NOT operator excludes documents that contain the term after NOT.
        This is equivalent to a difference using sets. The symbol ! can be used in place of the word NOT.</p>
                                                <p>To search for documents that contain "Mars Pathfinder" but not "mars rover" use the query: </p>
                                                    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#ffffff"><pre>&quot;Mars Pathfinder&quot; NOT &quot;mars rover&quot;</pre></td>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

    </tr>
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    </table>
    </div>
                                                <p>Note: The NOT operator cannot be used with just one term. For example, the following search will return no results:</p>

                                                    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

      <td bgcolor="#ffffff"><pre>NOT &quot;mars rover&quot;</pre></td>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>

    </table>
    </div>
                            </blockquote>
      </td></tr>
      <tr><td><br/></td></tr>
  </table>
                                                    
  <table border="0" cellspacing="0" cellpadding="2" width="100%">
      <tr><td bgcolor="#828DA6">
        <font color="#ffffff" size="+1">

          <strong>-</strong>
        </font>
      </td></tr>
      <tr><td>
        <blockquote>
                                    <p>The "-" or prohibit operator excludes documents that contain the term after the "-" symbol.</p>
                                                <p>To search for documents that contain "Mars Pathfinder" but not "mars rover" use the query: </p>

                                                    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

      <td bgcolor="#ffffff"><pre>&quot;Mars Pathfinder&quot; -&quot;mars rover&quot;</pre></td>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

    </tr>
    </table>
    </div>
                            </blockquote>
      </td></tr>
      <tr><td><br/></td></tr>
  </table>
                            </blockquote>
        </p>

      </td></tr>
      <tr><td><br/></td></tr>
</table>
                                                
  <table bgcolor="#FFFFFF" border="0" cellspacing="0" cellpadding="2" width="100%">
      <tr><td bgcolor="#525D76">
        <font color="#ffffff" size="+1">
          <strong>Grouping</strong>
        </font>

      </td></tr>
      <tr><td>
        <blockquote>
                                    <p>Lucene supports using parentheses to group clauses to form sub queries. This can be very useful if you want to control the boolean logic for a query.</p>
                                                <p>To search for either "instrument" or "rover" and "spacecraft" use the query:</p>
                                                    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">
    <tr>

      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#ffffff"><pre>(instrument OR rover) AND spacecraft</pre></td>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

    </tr>
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    </table>
    </div>
                                                <p>This eliminates any confusion and makes sure you that spacecraft must exist and either term instrument or rover may exist.</p>

                            </blockquote>
        </p>
      </td></tr>
      <tr><td><br/></td></tr>
  </table>
                                                
  <table bgcolor="#FFFFFF" border="0" cellspacing="0" cellpadding="2" width="100%">
      <tr><td bgcolor="#525D76">
        <font color="#ffffff" size="+1">
          <strong>Field Grouping</strong>

        </font>
      </td></tr>
      <tr><td>
        <blockquote>
                                    <p>Lucene supports using parentheses to group multiple clauses to a single field.</p>
                                                <p>To search for a title that contains both the word "mars" and the phrase "data archive" use the query:</p>
                                                    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">

    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#ffffff"><pre>title:(+return +&quot;data archive&quot;)</pre></td>

      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    </table>
    </div>

                            </blockquote>
        </p>
      </td></tr>
      <tr><td><br/></td></tr>
  </table>
                                                
  <table bgcolor="#FFFFFF" border="0" cellspacing="0" cellpadding="2" width="100%">
      <tr><td bgcolor="#525D76">
        <font color="#ffffff" size="+1">
          <strong>Escaping Special Characters</strong>

        </font>
      </td></tr>
      <tr><td>
        <blockquote>
                                    <p>Lucene supports escaping special characters that are part of the query syntax. The current list special characters are</p>
                                                <p>+ - &amp;&amp; || ! ( ) { } [ ] ^ " ~ * ? : \</p>
                                                <p>To escape these character use the \ before the character. For example to search for (1+1):2 use the query:</p>

                                                    <div align="left">
    <table cellspacing="4" cellpadding="0" border="0">
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>

      <td bgcolor="#ffffff"><pre>\(1\+1\)\:2</pre></td>
      <td bgcolor="#023264" width="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>
    <tr>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
      <td bgcolor="#023264" width="1" height="1"><img src="/images/void.gif" width="1" height="1" vspace="0" hspace="0" border="0"/></td>
    </tr>


    </table>

</table>


<table bgcolor="#FFFFFF" border="0" cellspacing="0" cellpadding="2" width="100%">

  <tr> 
    <td>
         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <td colspan="2"><img src="images/gray.gif" width="504" height="1" 
                 alt="Gray Horizontal Divider" border="0"></td>
            </tr>
            <tr>
               <td align="center">
                <a href="javascript:window.close()">
                <img src="images/btn_close_window.gif" alt="Close Window"
                    border="0" width="117" height="24"></a>   </td>
            </tr>
         </table>
    </td>
  </tr>

</table>



</body>
</html>

