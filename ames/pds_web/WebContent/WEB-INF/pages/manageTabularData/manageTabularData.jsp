<%@ taglib prefix="s" uri="/struts-tags"%>
<form action="<s:url action="SaveDownloadFormat" />"> 
	<input type="hidden" name="procId" value="<s:property value="procId" />" />
	
<table cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td class="defaultWrapper" style="vertical-align: top; border-bottom-width: 0px; border-right-width: 0px;" >
			<table cellpadding="0" cellspacing="0" >
				<tr>
					<td nowrap="nowrap" style="vertical-align: top;">
						<div class="title" ><s:text name="manageTabularData.title.columns" /></div>	
						<div class="basicBox" >			
							<div style="padding:10px 10px 20px 0px;">					
								<span class="buttonWrapper"><input type="submit" id="selectColumns" name="selectColumns" value="<s:text name="manageTabularData.button.selectColumns" />" ></span>			
								<span class="buttonWrapper"><input type="submit" id="orderColumns" name="orderColumns" value="<s:text name="manageTabularData.button.orderColumns" />" ></span>			
							</div>
							<div class="label"><s:property value="tabularDataContainer.selectedColumnCount"/> <s:text name="manageTabularData.text.of" /> <s:property value="tabularDataContainer.columns.size"/> <s:text name="manageTabularData.text.columnsSelected" /></div>	
						</div>	
					</td>
					<td nowrap="nowrap" class="defaultWrapper" style="vertical-align: top; border-right-width: 0px; border-bottom-width:0px; border-top-width:0px;">
						<div class="title" ><s:text name="manageTabularData.title.rows" /></div>	
						<div class="basicBox" >			
							<div style="padding:10px 10px 20px 0px;">	
								<span class="buttonWrapper"><input type="submit" id="selectRows" name="selectRows" value="<s:text name="manageTabularData.button.selectRows" />" ></span>			
								<span class="buttonWrapper"><input type="submit" id="orderRows" name="orderRows" value="<s:text name="manageTabularData.button.orderRows" />" ></span>			
							</div>  
							<div class="label"><s:property value="tabularDataContainer.rowsReturned"/> <s:text name="manageTabularData.text.of" /> <s:property value="tabularDataContainer.totalRows"/>  <s:text name="manageTabularData.text.rowsReturned" /></div>			
							</div>
							
							<s:if test="tabularDataContainer.conditions.size > 0 || tabularDataContainer.sorts.size > 0">				
								<div class="listBox" >
									<div class="title">Conditions</div>
									<div class="basicBox">
										<s:text name="selectRows.text.match" /> 
										<b>
										<s:if test='tabularDataContainer.queryMode == "OR"'><s:text name="selectRows.label.any" /></s:if>		
										<s:if test='tabularDataContainer.queryMode == "AND"'><s:text name="selectRows.label.all" /></s:if>
										
										</b>
				 						<s:text name="selectRows.text.ofTheConditions"  /> 								
									</div>
									<table  cellpadding="0" cellspacing="0" width="100%" style="border-top:1px solid #DDDDDD;">	
								
										<s:if test="tabularDataContainer.conditions.size > 0">				
									
											<tr>
												<th><s:text name="selectRows.label.columnName" /></th>
												<th><s:text name="selectRows.label.condition" /></th>
												<th><s:text name="selectRows.label.value" /></th>
											</tr>
											<s:iterator value="conditionsDisplay" status="status">
												<tr>
													<td nowrap="nowrap">
														<s:property value="column.getName()"/>	
													</td>
													<td nowrap="nowrap">
														<s:property value="condition" />
													</td>
													<td nowrap="nowrap">
														<s:property value="value" />
													</td>								
												</tr>
											</s:iterator>
										</s:if>
						 				<s:if test="tabularDataContainer.sorts.size > 0">				
											<tr>
												<th><s:text name="orderRows.label.columnName" /></th>
												<th><s:text name="orderRows.label.order" /></th>
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
													<td style="width: 100%" ></td>
													
												</tr>
											</s:iterator>
										</s:if>			
									</table>	
								</div>
							</s:if>
					</td>
					<td nowrap="nowrap" class="defaultWrapper" style="vertical-align: top; border-right-width: 0px; border-bottom-width:0px; border-top-width:0px;">
						<div class="title" ><s:text name="manageTabularData.title.output" /></div>	
						<div class="basicBox" >			
							<span class="inlineLabel"><s:text name="enterDownloadFormat.label.fileType" /></span>	
							<span class="contents">
								<select name="fileType" id="fileTypeOptions">
									<s:iterator value="fileTypeOptions">
										<option value="<s:property value="value" />" <s:if test="selected"> selected="selected"</s:if>> <s:property value="label" /> </option>
									</s:iterator>								
								</select>
							</span>
							<div style="margin-top:5px;">
							<span class="checkbox">
								<input  type="checkbox" name="includeHeaders" id="includeHeaders" value="true" 
								<s:if test="slice.includeHeaders">checked="checked"</s:if> onclick=" this.form.submit();" > </span>
								<span class="inlineLabel" style="vertical-align:top;line-height:1.5em;margin-left:5px;" >
									<label for="includeHeaders"><s:text name="manageTabularData.label.includeHeaderRow"/> </label>
								</span>
							</div>
	
							<div style="margin-top:10px; margin-left:75px">
								<span class="buttonWrapper"><input type="submit" id="download" name="download" value="<s:text name="manageTabularData.button.download" />" ></span>
							</div>
						</div>
					</td>
					<td  class="defaultWrapper" style="vertical-align: top;  border-bottom-width:0px; border-top-width:0px; border-right-width:0px;">
						<div class="title" ><s:text name="manageTabularData.title.sourceFiles" /></div>
						<div class="basicBox">
							<table>
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
								</tr>
								
								<s:if test="tabularDataContainer.labelTotalColumnCount != tabularDataContainer.tableTotalColumnCount">
                                <tr>
                                    <th nowrap="nowrap" colspan = "30">
                                        <div class="warningMessage" style="margin-right:20px;"><s:text name="manageTabularData.warning.message" />
                                        <s:property value="tabularDataContainer.tableTotalColumnCount"/>
                                        <s:text name="manageTabuladData.text.of"/><s:property value="tabularDataContainer.labelTotalColumnCount"/>
                                        <s:text name="manageTabularData.text.columnsProcessed" />
                                        </div>
                                    </th>
                                </tr>    
                                </s:if>
	
							</table>		
						</div>
						<div class="resultsMessageBox" id="messageBox">
    						<div class="title"><s:text name="displayProgress.title" /></div>
    							<div class="basicBox">
        						<!--  <div class="contentsStandalone"><s:text name="loadProgress.label.status" /></div> -->
        						<div class="contentsStandalone" id="statusContainer" style="display: none;">
        							<span class ="statusMessage" id="statusMessage"></span>
        							<div style="padding-top:10px;"></div>
        							<input id="cancelButton" type="button" value="<s:text name="generic.button.cancel" />" onclick="cancel()" />
        						</div>
    						</div>
						</div>
					</td>
				</tr>
			</table>
		</td>
		<td align="left" width="100%" nowrap="nowrap" class="defaultWrapper" style="vertical-align: top;  border-left-width: 0px; border-bottom-width:0px; ">
			<div class="title" style="border-left-width:0px;">&nbsp;</div>	
		</td>
	</tr>
</table>
<table cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td>
		
	 		<div class="listBox" style="border-top-width:0px;">		
	 			<div class="title" ><s:text name="manageTabularData.title.preview" /></div>
	 			<div class="basicBox">
					<div class="contents"><s:property value="startRow"/> to <s:property value="endRow"/> <s:text name="manageTabularData.text.of" /> <s:property value="tabularDataContainer.rowsReturned"/> rows
						<span style="margin:0 10px;">Jump To:
					<s:if test="jumpToValues.size > 1">				
							<select id="jumpToDropDown" name="startRow"  onchange=" this.form.submit();">
								<s:iterator value="jumpToValues">
									<option value="<s:property value="value" />" <s:if test="selected"> selected="selected"</s:if>> <s:property value="label" /> </option>
								</s:iterator>								
							</select>
					</s:if>
					<s:else>
							<select id="jumpToDropDown" name="startRow"  onchange=" this.form.submit();" disabled="disabled">
								<s:iterator value="jumpToValues">
									<option value="<s:property value="value" />" <s:if test="selected"> selected="selected"</s:if>> <s:property value="label" /> </option>
								</s:iterator>								
							</select>
						<input type="hidden" name="startRow" value="<s:property value="startRow" />" />
					</s:else>
						 </span>
					
					<s:if test="perPageValues.size > 1">				
						<span style="margin:0 10px;">Show: 
							<select id="numRowsPerPageDropDown" name="numRowsPerPage"  onchange=" this.form.submit();">
								<s:iterator value="perPageValues">
									<option value="<s:property value="value" />" <s:if test="selected"> selected="selected"</s:if>> <s:property value="label" /> </option>
								</s:iterator>								
							</select>
						 </span>
					</s:if>				
					<s:else>
						<span style="margin:0 10px;">Show: 
							<select id="numRowsPerPageDropDown" name="numRowsPerPage"  onchange=" this.form.submit();" disabled="disabled">
								<s:iterator value="perPageValues">
									<option value="<s:property value="value" />" <s:if test="selected"> selected="selected"</s:if>> <s:property value="label" /> </option>
								</s:iterator>								
							</select>
						 </span>
						<input type="hidden" name="numRowsPerPage" value="<s:property value="numRowsPerPage" />" />
					</s:else>
					</div>	 
					<div class="label"><s:text name="manageTabularData.text.browseColumns.message"></s:text> <s:text name="manageTabularData.text.missingConstant"></s:text></div>				
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
							<td class="tableCell"><i><s:property value="%{#status.count+startRow-1}" /></i></td>
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
<script type="text/javascript">
/* <![CDATA[ */
    var statusMessage = returnObject('statusMessage');
    var statusContainer = returnObject('statusContainer');
    var messageBox = returnObject('messageBox');
    var fileTypeOptions = document.getElementById('fileTypeOptions');
    var includeHeaders = document.getElementById('includeHeaders');
    var selectColumns = document.getElementById('selectColumns');
    var orderColumns = document.getElementById('orderColumns');
    var download = document.getElementById('download');
    var selectRows = document.getElementById('selectRows');
    var orderRows = document.getElementById('orderRows');
    var cancelButton = document.getElementById('cancelButton');
    var jumpToDropDown = document.getElementById('jumpToDropDown');
    var numRowsPerPageDropDown = document.getElementById('numRowsPerPageDropDown');
    
    function updateStatusHandler(result) {
        if(result.validJSON) {
            if(result.done) {
            	location.reload();
            } else if(!result.cancelled) {
            	selectColumns.disabled = true;
            	orderColumns.disabled = true;
            	download.disabled = true;
            	selectRows.disabled = true;
            	orderRows.disabled = true;
            	includeHeaders.disabled = true;
            	fileTypeOptions.disabled = true;
            	jumpToDropDown.disabled = true;
            	numRowsPerPageDropDown.disabled = true;
            	updateStatus(result.status);
            	cancelButton.value = "Cancel";
            	
            } else {
            	
            	selectColumns.disabled = false;
            	orderColumns.disabled = false;
            	download.disabled = false;
            	selectRows.disabled = false;
            	orderRows.disabled = false;
            	includeHeaders.disabled = false;
            	fileTypeOptions.disabled = false;
            	jumpToDropDown.disabled = false;
            	numRowsPerPageDropDown.disabled = false;
            	statusMessage.className = "finishedStatusMessage";
            	updateStatus("Processing complete. You may select and view the rest of the data.");
                cancelButton.value = "Start over";
            }
        } else {
        	updateStatus("Unable to retrieve status with asynchronous request.");
        }
    }

    function cancelHandler(result) {
        if(result.validJSON) {
            if(result.cancelled) {
                redirectAction("TableExplorer");
            } else {
            	updateStatus("Unable to cancel process, please submit a bug.");
            }
        } else {
            updateStatus("Unable to get a valid result from cancel request.");
        }
    }

    var statusConnection = new asynchronousRequest();
    statusConnection.setURL("<s:url action="LoadStatus" />");
    statusConnection.setHandlerFunction(updateStatusHandler);
    statusConnection.setParamString("procId=<s:property value="procId" />");
    statusConnection.run();

    var timeoutHandler = null;

    
    function updateStatus(message) {
        setInnerHTML(statusMessage, message);
        if(statusContainer.style.display == 'none') {
            statusContainer.style.display = 'block';
        }
        timeoutHandler = window.setTimeout(statusConnection.run, 1000);
    }
    
    function cancel() {
        window.clearTimeout(timeoutHandler);
        var cancelConnection = new asynchronousRequest();
        cancelConnection.setURL("<s:url action="LoadStatus" />");
        cancelConnection.setHandlerFunction(cancelHandler);
        cancelConnection.setParamString("procId=<s:property value="procId" />&cancel=true");
        cancelConnection.run();
    }
    
    function clearStatus() {
        statusContainer.style.display = 'none';
    }

    function redirectAction(actionName) {
        // get full url, do a regex replace for anything /XXX.action?asdfas=asdf
        var location = document.location.href;
        var newLocation = location.replace(/^(.*)\/[a-zA-Z0-9]+\.action(.*)$/gi, "$1/" + actionName + ".action" + "$2");
        document.location.replace(newLocation);
    }
    
/* ]]> */
</script>