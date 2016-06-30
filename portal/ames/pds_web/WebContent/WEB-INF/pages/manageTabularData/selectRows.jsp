 	<%@ taglib prefix="s" uri="/struts-tags" %>


	<form action="<s:url action="SaveSelectedRows" />"  method="post"> 
		<input type="hidden" name="procId" value="<s:property value="procId" />" />

<table cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td nowrap="nowrap" class="defaultWrapper" style="overflow: auto; vertical-align: top; border-right-width: 0px; border-bottom-width:0px;">
			<div class="title">
				<s:text name="selectRows.title" />
			</div>
			<div class="basicBox"  style="padding-right: 50px;">
				<ul>
					<li><s:text name="selectRows.text.filter" /></li>
					<li><s:text name="selectRows.text.conditions" /></li>
					<li><s:text name="selectRows.text.columnNotIncluded" /></li>
					<li><s:text name="selectRows.text.noCriteria" /></li>
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
			<div class="defaultWrapper" style="border-top-width: 0px; border-bottom-width: 0px;">
				<div class="title" ><s:text name="selectRows.title.createFilter" /></div>	
				<table cellpadding="8" cellspacing="0" width="100%">
					<tr>
						<td nowrap="nowrap">
							<div class="formLabel"><s:text name="selectRows.label.selectColumn" /></div> 
							<div class="contents">
								<select name="selectedColumn" onchange="this.form.submit()">
									<s:iterator value="columnOptions">
										<option value="<s:property value="value" />"<s:if test="selected"> selected="selected"</s:if>><s:property value="label" /></option>
									</s:iterator>
								</select>
							</div>
						
						</td> 
						<td nowrap="nowrap">
							<div class="formLabel"><s:text name="selectRows.label.selectCondition" /></div> 
							<div class="contents">
								<select name="condition">
									<s:iterator value="conditionOptions">
										<option value="<s:property value="value"/>"<s:if test="selected"> selected="selected"</s:if>><s:property value="label" /></option> 
									</s:iterator>		
								</select>
							</div>
							
						</td>
						<td  nowrap="nowrap">
							<div class="formLabel"><s:text name="selectRows.label.enterValue" /></div> 
							<div class="contents">
								<input type="text" name="value"  size="35" 	value="<s:property value="value"/>">
							</div>
						</td>
						<td>
							<input type="submit" name="addCondition" value="<s:text name="selectRows.button.addFilter" />">
						</td>
						<td style="width:100%"></td>
					</tr>			
				</table>
				
				
			<div class="listBox" style="border-top:1px solid #BBBBBB; " >
				<div class="secondaryTitle" ><s:text name="selectRows.title.yourCriteria" /></div>	
				<div class="basicBox">
					<s:text name="selectRows.text.match" /> 
						<span style="margin:0 5px;"><input type="radio" name="queryMode" value="AND"  onchange=" this.form.submit();"
						<s:if test="tabularDataContainer.conditions.size < 2">disabled = "disabled" </s:if>
						<s:if test='tabularDataContainer.queryMode == "AND"'>checked="checked"</s:if>> <s:text name="selectRows.label.all" /></span>							
						<span style="margin:0 5px;"><input type="radio" name="queryMode" value="OR"  onchange=" this.form.submit();"
						<s:if test="tabularDataContainer.conditions.size < 2">disabled = "disabled" </s:if>
						<s:if test='tabularDataContainer.queryMode == "OR"'>checked="checked"</s:if>> any</span>
					<s:text name="selectRows.text.ofTheConditions"  /> 
				</div>
				<s:if test="tabularDataContainer.conditions.size &lt; 2">
					<input type="hidden" name="queryMode" value="<s:property value='queryMode' />">
				</s:if>
				<s:if test="tabularDataContainer.conditions.size > 0">				
					<table  cellpadding="0" cellspacing="0" width="100%" style="border-top:1px solid #DDDDDD; ">	
					
						<tr>
							<th><s:text name="selectRows.label.columnName" /></th>
							<th><s:text name="selectRows.label.condition" /></th>
							<th><s:text name="selectRows.label.value" /></th>
							<th><s:text name="selectRows.label.delete" /></th>
							<th style="width: 100%" ></th>
						</tr>
						<s:iterator value="conditionsDisplay" status="status">
							<tr>
								<td nowrap="nowrap">
									<s:property value="column.getName()"/>	
								</td>
								<td nowrap="nowrap">
									<s:property value="condition" />
								</td>
								<td >
									<s:property value="value" />
								</td>
								<td>
								<a href="<s:url action="SaveSelectedRows" >
									<s:param name="columnToRemove" value="%{column.getName()}" />
									<s:param name="conditionToRemove" value="%{condition}" />
									<s:param name="valueToRemove" value="%{value}" />
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
				
				<s:if test="tabularDataContainer.conditions.size == 0">
					<div class="basicBox">
						<div class="content"><s:text name="selectRows.text.noCriteriaEntered"/></div>
					</div>
				</s:if>
				</div>	
				<div class="listBox" style="padding:10px; border-top:1px solid #BBBBBB;">
					<span class="buttonWrapper"><input type="submit" name="next" value="<s:text name="manageTabularData.button.continue" />" ></span>			
					<span class="content"><s:property value="tabularDataContainer.rowsReturned"/> <s:text name="manageTabularData.text.of" /> <s:property value="tabularDataContainer.totalRows"/>  <s:text name="manageTabularData.text.rowsReturned" /></span>			
				</div>  
			</div>
		</td>
	</tr>	
	
	<tr>	
		<td colspan="2" >
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
	