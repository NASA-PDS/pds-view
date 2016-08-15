<%@ taglib prefix="s" uri="/struts-tags" %>


	<form action="<s:url action="SaveRowOrder" />"  method="post"> 
		<input type="hidden" name="procId" value="<s:property value="procId" />" />

<table cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td nowrap="nowrap" class="defaultWrapper" style="overflow: auto; vertical-align: top; border-right-width: 0px; border-bottom-width:0px;">
			<div class="title">
				<s:text name="orderRows.title" />
			</div>
			<div class="basicBox"  style="padding-right: 50px;">
				<ul>
					<li><s:text name="orderRows.text.filter" /></li>
					<li><s:text name="orderRows.text.multipleSort" /></li>
					
				</ul>
			</div>
		</td>
		<td  class="defaultWrapper" style="vertical-align: top;  border-bottom-width:0px; width: 100%; ">
			<div class="title"><s:text name="manageTabularData.title.sourceFiles" /></div>
			<div class="basicBox" style="padding-top:22px;">
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
			<div class="defaultWrapper"  style="border-top-width: 0px; border-bottom-width: 0px;">
				<div class="title" ><s:text name="orderRows.title.buildCriteria" /></div>	
				<table cellpadding="8" cellspacing="0" width="100%">		
					<tr class="alt">
						<td nowrap="nowrap">
							<div class="formLabel"><s:text name="orderRows.label.selectColumn" /></div>
							<div class="contents">
								<select name="selectedColumn">
									<s:iterator value="columnOptions">
										<option value="<s:property value="value" />"<s:if test="selected"> selected="selected"</s:if>><s:property value="label" /></option>
									</s:iterator>
								</select>
							</div>			
						</td> 		
						<td nowrap="nowrap">
							<div class="formLabel"><s:text name="orderRows.label.selectSortOrder" /></div> 
							<div class="contents">
								<select name="order" >
									<s:iterator value="orderOptions">
										<option value="<s:property value="value"/>"><s:property value="label" /></option> 
									</s:iterator>		
								</select>
							</div>
						</td>
						<td nowrap="nowrap" >
							<input type="submit" name="addOrder" value="<s:text name="orderRows.button.addOrder" />">
						</td>
						<td style="width:100%"></td>
					</tr>
				</table>
				
			<div class="listBox" style="border-top:1px solid #BBBBBB;" >
				<div class="secondaryTitle" ><s:text name="orderRows.title.yourCriteria" /></div>	

				<s:if test="tabularDataContainer.sorts.size > 0">				
					<table  cellpadding="0" cellspacing="0" width="100%">	
					
						<tr>
							<th><s:text name="orderRows.label.columnName" /></th>
							<th><s:text name="orderRows.label.order" /></th>
							<th><s:text name="orderRows.label.delete" /></th>
							<th style="width: 100%" ></th>
						</tr>
						<s:iterator value="tabularDataContainer.sorts" status="status">
							<tr>
								<td nowrap="nowrap">
									<s:property value="column.getName()"/>	
								</td>
								<td nowrap="nowrap">
									<s:property value="condition" />
								</td>
								<td>
								<a href="<s:url action="SaveRowOrder" >
									<s:param name="columnToRemove" value="%{column.getName()}" />
									<s:param name="conditionToRemove" value="%{condition}" />
									<s:param name="procId" value="%{procId}"/>
								</s:url>">
								<img src="<s:url value="/web/images/icons/delete.gif" />" width="12" height="12" />
									</a>
								</td>
								<td style="width: 100%" ></td>
								
							</tr>
						</s:iterator>
					</table>	
				</s:if>
			
			
				<s:if test="tabularDataContainer.sorts.size == 0 ">
					<div class="basicBox">
						<div class="content"><s:text name="orderRows.text.noOrderEntered"/></div>
					</div>
				</s:if>
				</div>	
				<div class="listBox" style="padding:10px; border-top:1px solid #BBBBBB;">
					<s:if test="tabularDataContainer.conditions.size > 1">		
						<span class="buttonWrapper"><input type="submit" name="save" value="<s:text name="manageTabularData.button.save" />" ></span>			
					</s:if>
					<span class="buttonWrapper"><input type="submit" name="next" value="<s:text name="manageTabularData.button.continue" />" ></span>			
					<span class="content"><s:property value="tabularDataContainer.rowsReturned"/> <s:text name="manageTabularData.text.of" /> <s:property value="tabularDataContainer.totalRows"/>  <s:text name="manageTabularData.text.rowsReturned" /></span>			
				</div>  
			</div>
		</td>
	</tr>	
	
	<tr>	
		<td colspan="2"  >
			<div class="listBox" style="border-top-width:0px;">		
	 			<div class="title" ><s:text name="manageTabularData.title.preview" /></div>
	 			<div class="basicBox">
					<div class="contents"><s:property value="startRow"/> to <s:property value="endRow"/> <s:text name="manageTabularData.text.of" /> <s:property value="tabularDataContainer.rowsReturned"/> rows
						<span style="margin:0 10px;">Jump To:
					<s:if test="jumpToValues.size > 1">				
							<select name="startRow"  onchange=" this.form.submit();">
								<s:iterator value="jumpToValues">
									<option value="<s:property value="value" />" <s:if test="selected"> selected="selected"</s:if>> <s:property value="label" /> </option>
								</s:iterator>								
							</select>
					</s:if>
					<s:else>
							<select name="startRow"  onchange=" this.form.submit();" disabled="disabled">
								<s:iterator value="jumpToValues">
									<option value="<s:property value="value" />" <s:if test="selected"> selected="selected"</s:if>> <s:property value="label" /> </option>
								</s:iterator>								
							</select>
						<input type="hidden" name="startRow" value="<s:property value="startRow" />" />
					</s:else>
						 </span>
					
					<s:if test="perPageValues.size > 1">				
						<span style="margin:0 10px;">Show: 
							<select name="numRowsPerPage"  onchange=" this.form.submit();">
								<s:iterator value="perPageValues">
									<option value="<s:property value="value" />" <s:if test="selected"> selected="selected"</s:if>> <s:property value="label" /> </option>
								</s:iterator>								
							</select>
						 </span>
					</s:if>				
					<s:else>
						<span style="margin:0 10px;">Show: 
							<select name="numRowsPerPage"  onchange=" this.form.submit();" disabled="disabled">
								<s:iterator value="perPageValues">
									<option value="<s:property value="value" />" <s:if test="selected"> selected="selected"</s:if>> <s:property value="label" /> </option>
								</s:iterator>								
							</select>
						 </span>
						<input type="hidden" name="numRowsPerPage" value="<s:property value="numRowsPerPage" />" />
					</s:else>
					</div>	 				
	 			</div>					
				<table cellpadding="0" cellspacing="0" width="100%" style="border-top:1px solid #E4E4E4">
					<s:if test="slice.includeHeaders">
						<tr>
							<td><i>row</i></td>
							<s:iterator value="previewTable">
								<th class="tooltip" tooltip="<s:property value="value"/>"><s:property value="key"/></th>
							</s:iterator>
							<th style="width: 100%"></th>
							
						</tr>
					</s:if>						
					<s:iterator value="previewRows" status="status" >
						<tr<s:if test="#status.even"> class="even"</s:if>>
							<td class="tableCell"><i><s:property value="%{#status.count+startRow-1}" /><i></td>
							<s:iterator id="item">
								<td class="tableCell" nowrap="nowrap" <s:if test="isNumber(#item)"> class="numeric" </s:if>	><s:property /></td>
							</s:iterator>
							<td style="width: 100%"></td>
						</tr>
					</s:iterator>
				</table>		
				<s:if test="previewRows.size() == 0">
					<div class="basicBox">
						<div class="content">
							<s:text name="selectRows.error.noResultsReturned" />
						</div>
					</div>
				</s:if>
			</div>					
		</td>
	</tr>
</table>
</form>
	