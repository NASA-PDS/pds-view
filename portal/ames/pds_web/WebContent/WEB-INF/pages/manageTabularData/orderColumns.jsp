<%@ taglib prefix="s" uri="/struts-tags" %>

<form action="<s:url action="SaveColumnOrder" />" method="post">
	<input type="hidden" name="procId" value="<s:property value="procId" />" />
	<input type="hidden" name="columnsString" value="<s:property value="columnsString" />" /> 
<table cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td nowrap="nowrap" class="defaultWrapper" style=" overflow: auto; vertical-align: top; border-right-width:0px; border-bottom-width:0px;">
			<div class="title">
				<s:text name="orderColumns.title" />
			</div>
			<div class="basicBox"  style="padding-right: 50px;">
				<ul>
					<li><s:text name="orderColumns.text.useArrows" /></li>
					<li><s:text name="selectColumns.text.scrollToSeeAll" /></li>
				</ul>
			</div>
		</td>
		<td  class="defaultWrapper" style="vertical-align: top;  border-bottom-width:0px; width: 100%; ">
			<div class="title"><s:text name="manageTabularData.title.sourceFiles" /></div>
			<div class="basicBox" style="padding-top:25px;">
				<table cellpadding="0" cellspacing="0" width="100%">
					<tr>
						<td nowrap="nowrap"  valign="top" align="left" style="padding-right:10px;">
							<div class="formLabel"><s:text name="manageTabularData.label.label" /></div>
								<div class="sourcefiletooltip" tooltip="<s:property value="tabularDataContainer.tableInformation"/>">
									<a  href="<s:property value="slice.labelURLString" />" target="_blank">
									<s:property value="slice.labelFileName" /></a>
								</div>
						</td>
						<td  class="defaultWrapper" style="vertical-align: top;  padding-right:10px; border-bottom-width:0px; border-top-width:0px; border-right-width:0px;">
						</td>
						<td nowrap="nowrap" align="left">
							<div class="formLabel"><s:text name="manageTabularData.label.tabular" /></div>
							<div class="sourcefiletooltip" tooltip="<s:property value="tabularDataContainer.fileInformation"/>">
							<a href="<s:property value="tabularDataContainer.tabFileUrl" />" target="_blank">
								<s:property value="tabularDataContainer.tabFileName" /></a></div>
						</td>
						<td style="width:100%"></td>
					</tr>
				</table>		
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="2">		
			<div class="listBox" style="border-top-width:0px;">
				<div class="title" ><s:text name="selectColumns.title.availableColumns" /></div>	
				<s:if test="columnDisplay.size() != 1">
				
				<div class="scrollingBox"  >
					<table cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<th><s:text name="orderColumns.label.up" /></th>
							<th><s:text name="orderColumns.label.down" /></th>
							<th><s:text name="orderColumns.label.columnName" /></th>
						</tr>
						<s:iterator value="columnDisplay" status="iterStatus" id="item"> 
							<tr <s:if test="#iterStatus.odd"> class="alt" </s:if> >
								<td align="center" >
									<s:if test="#iterStatus.first != true">
										<a href="<s:url action="MoveColumn" >
										    	<s:param name="column" value="%{item}" />
										    	<s:param name="direction" value="%{'up'}" /> 
										    	<s:param name="procId" value="%{procId}"/>		
										    	<s:param name="columnsString" value="%{columnsString}"/>  	
									    	</s:url>">
								    	<img src="<s:url value="/web/images/icons/up_16.png" />" width="16" height="16" /></a>
								   	</s:if> 
								   	<s:else>&nbsp;</s:else>
								</td>
							   	<td align="center">
									<s:if test="#iterStatus.last != true">
								   		<a href="<s:url action="MoveColumn" >
										    	<s:param name="column" value="%{item}" />
										    	<s:param name="direction" value="%{'down'}" /> 
										    	<s:param name="procId" value="%{procId}"/>
										    	<s:param name="columnsString" value="%{columnsString}"/> 					    	
									    	</s:url>">
								    	<img src="<s:url value="/web/images/icons/down_16.png" />" width="16" height="16" /></a>
								   </s:if>
								   <s:else>&nbsp;</s:else>
								</td>
								<td style="width: 100%"><s:property /></td>	
							</tr>
						</s:iterator>
					</table>
				</div>		
				<div style="margin:10px;" >
					<span class="buttonWrapper"><input type="submit" name="save" value="<s:text name="manageTabularData.button.save" />" ></span>
					<span class="buttonWrapper"><input type="submit" name="cancel" value="<s:text name="manageTabularData.button.cancel" />" ></span>
				</div>  
				</s:if>
				<s:else>
					<div class="basicBox">
						<div class="content"> <s:text name="orderColumns.text.noColumnsSelected" /></div>
						<div style="margin:10px;" >
							<span class="buttonWrapper"><input type="submit" name="cancel" value="<s:text name="orderColumns.button.back" />" ></span>
						</div>
				</s:else>
			</div>	
		</td>
	</tr>	
</table>
</form>

					
	