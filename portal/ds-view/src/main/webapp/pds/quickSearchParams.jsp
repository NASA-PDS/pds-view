        <tr>
      	   <td valign="top" colspan="2">
             <a href="javascript:popup('glossary.html#msnname')">
             <font face="verdana" size="2" color="#000033"><b><u>Missions:</u></b></font><font color="#000033"> (pick one or many and Filter)</font>&nbsp;&nbsp;</a></td>
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
                  <font face="verdana" size="2" color="#000033"><b><u>Target Name:</u></b></font><font color="#000033"> (pick one  to Filter)</font></a>
             </td>
	     <td valign="TOP" bgcolor="#EFEFEF">
                  <a href="javascript:popup('glossary.html#targtype')">
                  <font face="verdana" size="2" color="#000033"><b><u>Target Type:</u></b></font><font color="#000033"> (pick one to Filter)</font> </a><br>
             </td>
        </tr>
	<tr valign="TOP">
            <td valign="TOP" bgcolor="#EFEFEF"> <select id="targname" name="targname" onchange="changeSearchSpec()"> <%=opts[TARGNAME]%>
              </select> <BR> 
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
              <font face="verdana" size="2" color="#000033"><b><u>Instruments:</u></b></font><font color="#000033"> (pick one or many and Filter)</font> </a>
            </td>

	    <td valign="TOP">
               <a href="javascript:popup('glossary.html#insttype')">
               <font face="verdana" size="2" color="#000033"><b><u>Instrument Type:</u></b></font><font color="#000033"> (pick one to Filter)</font> </a>
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
