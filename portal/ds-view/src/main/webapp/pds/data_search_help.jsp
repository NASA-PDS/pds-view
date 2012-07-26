<%
String pdshome = "";
pdshome = application.getInitParameter("pdshome.url");
%>
<html>
<head>
<title>Planetary Data System: Data Services: Data Search Help</title>
<meta  name="keywords"  content="Planetary Data System">
<meta  name="description" content="This website serves as a mechanism for subscribing to data, software and documentation from the PDS.">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="<%=pdshome%>css/pds_style.css" rel="stylesheet" type="text/css">
</head>

<body bgcolor="#000000">

<table align="center" bgcolor="#ffffff" border="0" cellspacing="0" cellpadding="4">
	<tr>
		<td>
         <table border="0" cellpadding="0" cellspacing="0">
         	<tr>
               <td class="pageTitle">Data Search Help</td>
               <td align="right"><a href="javascript:window.close()"><img src="images/btn_close_window.gif" alt="Close Window" border="0" width="117" height="24"></a></td>
            </tr>
         	<tr>
               <td colspan="2"><img src="images/gray.gif" width="504" height="1" alt="Gray Horizontal Divider" border="0"></td>
            </tr>
         </table>
         <br>
         <table width="504" border="0" cellpadding="0" cellspacing="0">
         	<tr>
               <td valign="top" width="304">
               <span class="pageTitleSub">Topic</span>
               <br>
               <ol>
                  <b>
                  <li><a href="#SelectingParameters">Selecting Parameters</a></li><br>
                  <li><a href="#FilteringParameters">Filtering Parameters</a></li><br>
                  <li><a href="#KeyboardShortcuts">Keyboard Shortcuts</a></li><br>
                  </b>
               </ol>
               </td>
               <td width="200">
                  <a href="#DataSearchHelp"><img src="images/search_help_thm.jpg" width="200" height="172" border="0" alt="Data Search Help Thumbnail"></a>
               </td>
            </tr>
         </table>

         <img src="images/gray.gif" width="504" height="1" alt="Gray Horizontal Divider" border="0">
         <br><br>

         <table width="504" border="0" cellspacing="2" cellpadding="8">
            <tr>
               <td bgcolor="#EBEBD6"><a name="SelectingParameters"></a>
               <b>1. Selecting Parameters</b>
               <br><br>
               Parameters are searchable items such as Mission, Target, Instrument etc. These  parameters provide a starting point to search the PDS Catalog for data sets and products.
               For a description of the parameter, select the parameter name.
               <br><br>
               One may select one or more parameters to begin a search. See Keyboard Shortcuts below for help in selecting parameters
               </td>
            </tr>
            <tr>
               <td bgcolor="#F4F4E9"><a name="FilteringParameters"></a>
               <b>2. Filtering Parameters</b>
               <br><br>
               Clicking on one or more parameters then hitting the Filter icon narrows the other parameters on the screen to only those that are related to the selected parameters. For example, selecting Mission Galileo and filtering will show only those instruments, targets, etc that relate to the Galileo mission.
               <br><br>
               Note that selecting one of the following parameters immediately run Filter: Target Name, Target Type Instrument Type.
               <br><br>
               For the other parameters, the user may select one or more parameters before clicking FILTER to run the filter process.
               </td>
            </tr>
            <tr>
               <td bgcolor="#F9F9F4"><a name="KeyboardShortcuts"></a>
               <b>3. Keyboard Shortcuts</b>
               <br><br>
               <table bgcolor="#FFFFFF" border="0" cellspacing="1" cellpadding="4">
                  <tr bgcolor="#E2E2E2">
                     <td height="30"><b>Action</b></td>
                     <td><b>Windows/Unix/Linux Shortcut</b></td>
                     <td><b>Mac Shortcut</b></td>
                  </tr>
                  <tr>
                     <td>To select a parameter</td>
                     <td><i>CLICK</i> on the parameter</td>
                     <td><i>CLICK</i> on the parameter</td>
                  </tr>
                  <tr bgcolor="#E7EEF9">
                     <td>To <b>de</b>-select a parameter</td>
                     <td><i>CTRL-CLICK</i> on the parameter</td>
                     <td><i>APPLE-CLICK</i> on the parameter</td>
                  </tr>
                  <tr>
                     <td>To highlight several parameters</td>
                     <td><i>SHIFT-CLICK</i> each parameter or <i>CLICK</i> and <i>DRAG</i> over several parameters</td>
                     <td><i>SHIFT-CLICK</i> each parameter or <i>CLICK</i> and <i>DRAG</i> over several parameters</td>
                  </tr>
                  <tr bgcolor="#E7EEF9">
                     <td>To highlight separate parameters</td>
                     <td><i>CTRL-CLICK</i> each parameter</td>
                     <td><i>APPLE-CLICK</i> each parameter</td>
                  </tr>
                  <tr>
                     <td>To find a parameter</td>
                     <td><i>CLICK</i> in the list, then type the first few letters of the parameter</td>
                     <td><i>CLICK</i> in the list, then type the first few letters of the parameter</td>
                  </tr>
               </table>
               </td>
            </tr>
         </table>

         <img src="images/gray.gif" width="504" height="1" alt="Gray Horizontal Divider" border="0">
         <br><br>

         <a name="DataSearchHelp"></a>
         <img src="images/search_help.jpg" width="504" height="551" border="0" alt="Data Search Help">
			<br>
			<div align="center"><a href="javascript:window.close()"><img src="images/btn_close_window.gif" alt="Close Window" border="0" width="117" height="24"></a></div>
		</td>
	</tr>
</table>
</body>
</html>

