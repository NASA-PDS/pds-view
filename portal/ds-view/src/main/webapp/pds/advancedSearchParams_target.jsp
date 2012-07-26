          <tr>
	    <td valign="TOP" colspan="2">
		<IMG align=middle alt=Space border=0 height=2 hspace=0 src="/pds/images/space.jpg" width=760></td>
          </tr>
          <TR>
             <TD bgColor=#efefef vAlign=top>
               <a href="javascript:popup('glossary.html#starttime')">
               <FONT color=#000033 face=verdana size=2><B>Start Date:</B></FONT>&nbsp;&nbsp;
               </a><BR>
             </TD>

             <TD bgColor=#efefef vAlign=top>
               <a href="javascript:popup('glossary.html#stoptime')">
               <FONT color=#000033 face=verdana size=2><B>Stop Date:</B></FONT>&nbsp;&nbsp;
               </a><BR>
             </TD>
          </TR>
          <TR vAlign=top>
             <TD bgColor=#efefef vAlign=top>
                <INPUT id="strttime" name="strttime" size=24 
                  value="<%=jpl.pds.util.DisplayOptions.displayInput(request, targetSearchBean.getPDSKeyword("strttime"), "strttime", "YYYY-MM-DD")%>"><BR></TD>
             <TD bgColor=#efefef vAlign=top>
                <INPUT id="stoptime" name="stoptime" size=24
                  value="<%=jpl.pds.util.DisplayOptions.displayInput(request, targetSearchBean.getPDSKeyword("stoptime"), "stoptime", "YYYY-MM-DD")%>"><BR></TD>
          </TR>
	  <tr>
             <td valign="TOP" colspan="2">
	        <IMG align=middle alt=Space border=0 height=2 hspace=0 src="/pds/images/space.jpg" width=760></td>
          </tr>
	  <TR vAlign=top>
             <TD bgColor=#efefef vAlign=top>
               <a href="javascript:popup('glossary.html#dataobjtype')">
                <FONT color=#000033 face=verdana size=2><B>Data Type:</B></FONT>&nbsp;&nbsp;
               </a><BR>
            </TD>

            <TD bgColor=#efefef vAlign=top>
               <a href="javascript:popup('glossary.html#dsid')">
                 <FONT color=#000033 face=verdana size=2><B>Data Set ID:</B></FONT>&nbsp;&nbsp;
               </a><BR>
            </TD>
          </TR>

          <TR vAlign=top>
            <TD bgColor=#efefef vAlign=top>
	       <select id="dataobjtype" name="dataobjtype"><%=opts[DATAOBJTYPE]%></select><br></td>
            <TD bgColor=#efefef vAlign=top>
               <SELECT id=dsid name=dsid><%=opts[DSID]%></SELECT><BR></TD>
          </TR>

          <tr>
            <td valign="TOP" colspan="2">
	       <IMG align=middle alt=Space border=0 height=2 hspace=0 src="/pds/images/space.jpg" width=760></td>
          </tr>
         
          <TR vAlign=top>
             <TD bgColor=#efefef colSpan=2 vAlign=top>
               <a href="javascript:popup('glossary.html#dsname')">
                 <FONT color=#000033 face=verdana size=2><B>Data Set Name:</B></FONT>&nbsp;&nbsp;
               </a><BR>
             </TD>
         </TR>
         <TR vAlign=top>
           <TD bgColor=#efefef colSpan=2 vAlign=top>
             <select id="dsname" name="dsname"><%=opts[DSNAME]%></SELECT><BR>
           </TD>
         </TR>

         <tr>
            <td valign="TOP" colspan="2">
	       <IMG align=middle alt=Space border=0 height=2 hspace=0 src="/pds/images/space.jpg" width=760></td>
         </tr>

         <TR>
            <TD bgColor=#efefef vAlign=top>
               <a href="javascript:popup('glossary.html#insthostname')">
               <FONT color=#000033 face=verdana size=2><B>Instrument Host:</B></font><font color=#000033> (pick one or many)</FONT>&nbsp;&nbsp;
               </a>
            </TD>

            <TD bgColor=#efefef vAlign=top>
               <a href="javascript:popup('glossary.html#insthosttype')">
                 <FONT color=#000033 face=verdana size=2><B>Instrument Host Type:</B></FONT>&nbsp;&nbsp; 
               </a><BR>
            </TD>
         </TR>
         <TR>
            <TD><SELECT multiple name=insthostname size=6> <%=opts[INSTHOSTNAME]%> </SELECT></TD>

            <TD bgColor=#efefef vAlign=top>
               <SELECT id=insthosttype name=insthosttype><%=opts[INSTHOSTTYPE]%></SELECT>
            </TD>
         </TR>
