        <tr>
                                <td valign="TOP" colspan="2">
                                        <IMG align=middle alt=Space border=0 height=2 hspace=0 
                                          src="/pds/images/space.jpg" width=760></td>
        </tr>

        <tr>
           <td valign="TOP">
              <a href="javascript:popup('glossary.html#archstat')">
              <FONT color=#000033 face=verdana size=2><B><u>Archive Status:</u></B></FONT>&nbsp;&nbsp;
              </a><BR></TD>

           <TD vAlign=top>
               <a href="javascript:popup('glossary.html#nodename')">
               <FONT color=#000033 face=verdana size=2><B><u>Curator:</u></B></FONT>&nbsp;&nbsp;
               </a><BR></TD>
       </TR>

       <TR vAlign=top>
              <td valign="TOP" >
                 <SELECT id=archivestat name=archivestat>
                   <%=jpl.pds.util.DisplayOptions.displayValList(request, searchBean.getPDSKeyword("archivestat"), "archivestat", paramBean.getArchiveStat())%>
	         </SELECT>
              </TD>
              <td valign="TOP" >
                  <SELECT id=nodename name=nodename>
                    <%=jpl.pds.util.DisplayOptions.displayValList(request, searchBean.getPDSKeyword("nodename"), "nodename", paramBean.getNodeName())%>
                  </SELECT>
              </TD>
       </TR>
        <tr>
                                <td valign="TOP" colspan="2">
                                        <IMG align=middle alt=Space border=0 height=2 hspace=0 
                                          src="/pds/images/space.jpg" width=760></td>
        </tr>
