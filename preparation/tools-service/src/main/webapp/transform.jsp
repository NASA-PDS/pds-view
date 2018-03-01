<%
   String toolsServiceUrl = application.getInitParameter("tools.service.url");
   //out.println("toolsServiceUrl = " + toolsServiceUrl);
%>
<html>
<head>
  <title>Transform Tools Service</title>
  <link rel="stylesheet" type="text/css" href="style.css" />
  <script src="js/d3.min.js"></script>
  <script src="js/jquery-2.1.4.min.js"></script>
  <!--script src="js/jquery.fileDownload.js"></script-->
  <script src="js/transform.js"></script>
</head>

<body>
	<table>
		<tr>
			<td><img src="images/pds4_logo.png" alt="PDS Logo" /></td>
			<td><img src="images/spacer.gif" alt="Spacer" width="50" /></td>
			<td><h1>Transform Tools Service</h1></td>
		</tr>
	</table>
	<hr />
	<p>The Transform Tools Service provides the transform capability
		for transforming PDS3 and PDS4 product labels and product data into
		other formats.</p>

    <div>
	<table width="100%">
		<tr>
			<td>File to Transform: 
			<script type="text/javascript">	
			    //getBaseURL();
				document.write(findGetParameter("fileName"));
			</script>
			</td>
		</tr>
        
		<tr>
			<td>Format: 
				<select id="format" name="format-type">
				    <option>none</option>
					<option>csv</option>
					<option>html</option>
					<option>html-structure-only</option>
					<option>gif</option>
					<option>jp2</option>
					<option>jpg</option>
					<option>pds</option>
					<option>pds3-label</option>
					<option>pds4-label</option>
					<option>png</option>
					<option>pnm</option>
					<option>pvl</option>
					<option>tif</option>
			</select>
			</td>
		</tr>
		<!--tr><td>List Objects: <input type="checkbox" id="listObjs" value="false"></td></tr-->
     
		<tr>
			<td><button id="transformBtn" type="button">Transform</button></td>
		</tr>
		
		<tr>
			<td><a href="index.jsp"><button type="button" id="resetBtn">Reset</button></a></td>		    
		</tr>
   </table>
   <hr/>
   <input type='hidden' id="TSURL" value="<%=toolsServiceUrl%>" />
   <table>
		<tr><td><h2><b>Results</b></h2></td></tr>
		
		<tr>
			<td><span id="stdout"></span></td>
		</tr>
		<tr>
			<td><span id="download"></span></td>
		</tr>
	</table>
	</div>

	<script type="text/javascript">
	    console.log("toolsServiceUrl...... = " + $("#TSURL").val());
        $(document).ready(function() {
            $("#transformBtn").click(function() {
                transform($("#TSURL").val());
            });
        });
	</script>

</body>
</html>
