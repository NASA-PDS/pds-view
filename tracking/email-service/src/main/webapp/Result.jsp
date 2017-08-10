<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
     "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Result</title>
</head>
<body>
    <table>
      <tr>
        <td><img src="images/pds4_logo.png" alt="PDS Logo" /></td>
        <td><img src="images/spacer.gif" alt="Spacer" width="50"/></td>
        <td><h1>Email Service Result</h1></td>
      </tr>
    </table>

    <hr/>
    
	<center>
		<h3><%=request.getAttribute("Message")%></h3>
	</center>
</body>
</html>