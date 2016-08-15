<%@ taglib prefix="s" uri="/struts-tags"%>
  
<script type="text/javascript" >
/* <![CDATA[ */


function checkAll(checkname, exby) {
	  for (i = 0; i < checkname.length; i++){
	  checkname[i].checked = exby.checked? true:false;
	  }
	}


function sortSubmit(sortColumn){
	//put passed in value into hidden field
	document.getElementById('sortColumn').value = sortColumn;
	//submit form
	document.selectColumns.submit();
}
/* ]]> */
</script>


<form action="<s:url action="SaveSelectedColumns" />" name="selectColumns" method="post">
	<input type="hidden" name="procId" value="<s:property value="procId" />" />
	<input type="hidden" name="savedSort" value="<s:property value="sort" />" />
	<input type="hidden" name="savedChangedColumns" id="savedChangedColumns" value="<s:property value="changedColumns" />">
	<input type="hidden" name="sortColumn" id="sortColumn" value="">
				
				
<table cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td nowrap="nowrap" class="defaultWrapper" style="vertical-align: top; border-right-width: 0px; border-bottom-width:0px;">
			<div class="title"><s:text name="selectColumns.title" /></div>
			<div class="basicBox"  style="padding-right: 50px; ">
				<ul>
					<li><s:text name="selectColumns.text.useCheckboxes" /></li>
					<li><s:text name="selectColumns.text.sortArrows" /></li> 
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
		<td colspan="2" class="defaultWrapper" style="vertical-align: top; border-top-width: 0px; ">
			<div class="listBox" style="border-top-width:0px;" >
				<div class="title" ><s:text name="selectColumns.title.availableColumns" /></div>	
				<div class="scrollingBox">
					<table cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<th class="problem" ><span style="vertical-align:bottom;"><input type="checkbox" name="all" 
								onClick="checkAll(document.selectColumns.checkedColumns,this)" 
							   <s:if test="inErrorState == false & tabularDataContainer.selectedColumns.size > (tabularDataContainer.columns.size/2) ">checked="checked" </s:if>></span>				
								<s:text name="selectColumns.label.all" />
							</th>
							<th>
								<a href="#" onClick="sortSubmit('name')">
								 
								<s:text name="selectColumns.label.columnName" /></a>	
								<s:if test="sort.equalsIgnoreCase('nameAsc')">
									<img src="<s:url value="/web/images/icons/down-triangle.png" />" width="12" height="12" border="0" alt="Sort Name Ascending" >
								</s:if>
								<s:if test="sort.equalsIgnoreCase('nameDesc')">
									<img src="<s:url value="/web/images/icons/up-triangle.png" />" width="12" height="12" border="0" alt="Sort Name Descending" >
								</s:if>
							</th>
							<th>
								<a href="#" onClick="sortSubmit('dataType')">
								<s:text name="selectColumns.label.dataType" /></a>
								<s:if test="sort.equalsIgnoreCase('datatypeAsc')">
									<img src="<s:url value="/web/images/icons/down-triangle.png" />" width="12" height="12" border="0" alt="Sort DataType Ascending" >
								</s:if>
								<s:if test="sort.equalsIgnoreCase('datatypeDesc')">
									<img src="<s:url value="/web/images/icons/up-triangle.png" />" width="12" height="12" border="0" alt="Sort DataType Descending" >
								</s:if>
							
							</th>
							<th><s:text name="selectColumns.label.description" /></th>
						</tr>			
						<s:iterator value="columnCheckboxes" status="status">
							<tr<s:if test="#status.odd"> class="alt"</s:if>>
								<td style="width: 0px; text-align: center;">
									<input type="checkbox" 
											name="checkedColumns" 
											onclick="logChecked(<s:property value="value"/>);"
											value="<s:property value="value"/>" 
											id="<s:property value="value"/>"
											<s:if test="isChecked() == true"> checked="checked"</s:if> /> 			
								</td>
								<td nowrap="nowrap" valign="top"><s:property value="label" /></td>
								<td valign="top"><s:property value="datatype" /></td>
								<td valign="top"><s:property value="description" /></td>
							</tr>
						</s:iterator>
					</table>
				</div>
				<div style="margin:10px;" >
					<span class="buttonWrapper"><input type="submit" name="save" value="<s:text name="manageTabularData.button.save" />" ></span>
					<span class="buttonWrapper"><input type="submit" name="cancel" value="<s:text name="manageTabularData.button.cancel" />" ></span>
				</div>
			</div>
		</td>
	</tr>
</table>
</form>

	