<html>
<head>
  <!--style>
    body {
      font: 16px consolas;
    }
    div {
      font: 12px consolas;
    }
  </style-->
  <title>Transform Tools Service</title>
  <link rel="stylesheet" type="text/css" href="style.css" />
  <script src="js/d3.min.js"></script>
  <script src="js/jquery-2.1.4.min.js"></script>
  <script src="js/jquery.fileDownload.js"></script>
</head>

<body>
    <table>
      <tr>
        <td><img src="images/pds4_logo.png" alt="PDS Logo" /></td>
        <td><img src="images/spacer.gif" alt="Spacer" width="50"/></td>
        <td><h1>Transform Tools Service</h1></td>
      </tr>
    </table>

    <hr/>

   <p>The Transform Tools Service provides the transform capability
		for transforming PDS3 and PDS4 product labels and product data into
		other formats.</p>

    <ul>
      <li>Service Interface<br/><br/>
        The service only supports "multipart/form-data" HTTP POST requests.<br/>
        Use <b>'shift'</b> button to choose multiple files to be uploaded.<br/><br/>
      </li>
      <li>
        <form action="UploadDownloadFileServlet" method="post" enctype="multipart/form-data">
              Select File(s) to Transform:<input type="file" name="fileName" multiple><br /><br/>
              <input type="submit" value="Upload File(s)">
		</form>

        <span id="uploadedFiles"></span>
		
		<% String fileName = request.getParameter("fileName");
           String baseUrl = request.getParameter("baseUrl");
        %>
	    <button onclick="window.location.href='<%=baseUrl%>/transform.jsp?fileName=<%=fileName%>'">Continue</button>
		

          <!-- File:
          <input type="file" name="file" id="file" /> <br/>
          Path:
          <input type="text" name="path"/> <br/>
          <input type="button" name="submit_form" value="Upload" onclick="this.form.submit();"><br/>
          <input type="button" name="reset_form" value="Reset" onclick="this.form.reset();">
        </form-->
        
        
        <!--form action="https://localhost:8080/transport-upload/upload" method="post"
	    enctype="multipart/form-data">
		Select File to Transform:<input type="file" name="fileName" multiple>
		<input type="upload" value="Upload"-->
	
      </li>
    </ul>
  </body>
</html>
