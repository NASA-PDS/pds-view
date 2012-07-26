

        <tr>
      	   <td valign="top" colspan="2">
             <a href="javascript:popup('glossary.html#msnname')">
             <font face="verdana" size="2" color="#000033"><b>Missions:</b></font><font color="#000033"> (pick one or many and Filter)</font>&nbsp;&nbsp;</a></td>
        </tr>

        <tr>
           <td valign="top" colspan=2><nobr>
             <select name="msnname" onChange="selectionChanged=true;" size="6" multiple WIDTH="400" style="width=400px"> <%=opts[MSNNAME]%>
             </select>
             <a href="javascript:applyMultiSelect( document.postForm.msnname)"><img src="/pds/images/btn_filter.gif" border=0 alt="Filter"></a>&nbsp;Filter</nobr><BR>
           </td>
        </tr>
        <tr>
	     <td valign="TOP" colspan="2">
		<IMG align=middle alt=Space border=0 height=2 hspace=0 src="/pds/images/space.jpg" width=760></td>
        </tr>
	<tr valign="TOP">
	     <td valign="TOP">
                  <a href="javascript:popup('glossary.html#targname')">
                  <font face="verdana" size="2" color="#000033"><b>Target Name:</b></font><font color="#000033"> (Using the 
				  radio buttons, select your preferred method <br>for choosing a target name; either 
				  pick one from the list or type in a target and <br>click filter. NOTE:
				  FOR WILDCARD FUNCTIONALITY, USE * )</font></a>
             </td>
	     <td valign="TOP" bgcolor="#EFEFEF">
                  <a href="javascript:popup('glossary.html#targtype')">
                  <font face="verdana" size="2" color="#000033"><b>Target Type:</b></font><font color="#000033"> (pick one to Filter)</font> </a><br>
             </td>
        </tr>
	<tr valign="TOP">
            <td valign="TOP" bgcolor="#EFEFEF">
				<input type="radio" name="targselect" value="dropdown" checked>&nbsp;&nbsp;
				<!--<select id="targname" name="targname" onchange="changeSearchSpec()"><%//=opts[TARGNAME]%>-->
				<select id="targname" name="targname" onChange="selectionChanged=true;" size="6" multiple WIDTH="150" style="width=150px"><%=opts[TARGNAME]%>
              	</select> &nbsp;&nbsp;OR<br>
			  	<input type="radio" name="targselect" value="input">&nbsp;&nbsp;
				<input type="text" name="inputtargname">

			 <a href="javascript:setTargName(document.postForm.targname,document.postForm.inputtargname.value)"><img src="/pds/images/btn_filter.gif" border=0 alt="Filter"></a>&nbsp;Filter<BR>
            </td>
            <td valign="TOP" bgcolor="#EFEFEF"> <select id="targtype" name="targtype" onchange="changeSearchSpec()"> <%=opts[TARGTYPE]%> </select> <BR> 
            </td>
        </tr>
	<tr>
            <td valign="TOP" colspan="2">
	         <IMG align=middle alt=Space border=0 height=2 hspace=0 src="/pds/images/space.jpg" width=760>
            </td>
        </tr>
	<tr>
	    <td valign="TOP">
              <a href="javascript:popup('glossary.html#instname')">
              <font face="verdana" size="2" color="#000033"><b>Instruments:</b></font><font color="#000033"> (pick one or many and Filter)</font> </a>
            </td>

	    <td valign="TOP">
               <a href="javascript:popup('glossary.html#insttype')">
               <font face="verdana" size="2" color="#000033"><b>Instrument Type:</b></font><font color="#000033"> (pick one to Filter)</font> </a>
            </td>
         </tr>
         <tr>
            <td valign="TOP"><nobr>
               <select name="instname" onChange="selectionChanged=true;" size="6" multiple WIDTH="375" style="width="375"> <%=opts[INSTNAME]%> </select>

               <a href="javascript:applyMultiSelect( document.postForm.instname)"><img src="/pds/images/btn_filter.gif" border=0 alt="Filter"></a>&nbsp;Filter<BR>
            </td>
           <td valign="top"> <select id="insttype" name="insttype" onchange="changeSearchSpec()"> <%=opts[INSTTYPE]%> </select> <BR> 
           </td>
         </tr>
