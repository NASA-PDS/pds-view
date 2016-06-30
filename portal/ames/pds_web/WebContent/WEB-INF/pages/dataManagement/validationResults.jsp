<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="w" uri="/widget-tags"%>

<table cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td class="defaultWrapper" style="overflow: auto; vertical-align: top; border-right-width: 0px;">
			<div class="title"><s:text name="validationResults.title.volumeExplorer"><s:param value="volumeId" /></s:text></div>
	
			<div style="padding: 5px 8px 10px 0px;"><w:fileNodeListing node="nodes" procId="procId" /></div>
		</td>
		<td class="defaultWrapper" style="vertical-align: top;" width="100%">
			<table cellpadding="0" cellspacing="0" width="100%">
				<tr>
					<td valign="top">
						<div class="title"><s:text name="validationResults.title.elementDetails" /></div>
						<div class="basicBox">
							<div class="label"><s:text name="validationResults.label.name" /></div>
							<div class="contents">
								<s:property value="selectedNode.name" />
								<s:if test="rootNode"><s:text name="validationresults.text.validationDuration"><s:param value="duration" /></s:text></s:if>
							</div>
							
							<s:if test="rootNode">				
								<div class="label"><s:text name="validationResults.label.size" /></div>
								<div class="contents"><s:text name="validationResults.text.volumeSize"><s:param value="volumeFiles" /><s:param value="volumeFolders" /></s:text> (<s:property value="volumeSpace" />) <br />
								While the file navigator only displays files for whom errors, new values, or dictionary changes appear, the number of files and overall size is accurate for the entire volume.</div>
							</s:if>
							<s:elseif test="!selectedNode.directory">
								<div class="label"><s:text name="validationResults.label.size" /></div>
								<div class="contents"><s:property value="selectedNode.size" /></div>
							</s:elseif>
							
							<div class="label"><s:text name="validationResults.label.problems" /></div>
							<div class="contents"><s:text name="previewFile.text.numErrors"><s:param value="selectedNode.numErrors" /></s:text>, <s:text name="previewFile.text.numWarnings"><s:param value="selectedNode.numWarnings" /></s:text>, <s:text name="previewFile.text.numInfo"><s:param value="selectedNode.numInfo" /></s:text></div>
							
							<s:if test="!selectedNode.file.directory">
								<div class="contents">
									<s:if test="remote">
										<a href="#" onclick="validationApplet.previewFile('<s:property value="selectedNode.pathJS" />', '<s:property value="volumePathJS"  />', '<s:property value="procId" />')">
									</s:if>
									<s:else>
										<a href="<s:url action="PreviewFile" />?procId=<s:property value="procId"/>&amp;node=<s:property value="selectedNode.path" escape="false" />" target="_blank">
									</s:else>
									<s:text name="validationResults.link.previewFile" /></a>
								</div>
							</s:if>
						</div>
					</td>
					<td valign="top" style="border-left: 1px solid #546B9C; width: 50%">
						<div class="title"><s:text name="validationResults.title.options" /></div>
						<div class="basicBox">
						<style type="text/css">
							<s:if test="showGenerateReport()">#reportStatusDownload { display: none; }</s:if>
							#reportStatusMessage { display: none; }
						</style>
							<div class="labelStandalone">
								<s:if test="showGenerateReport()"><input id="generateReportButton" type="button" value="<s:text name="validationResults.button.generateReport" />" onclick="generateReport()" /></s:if>
								<div id="reportStatusMessage"></div>
								<div id="reportStatusDownload"><a href="<s:url action="DownloadReport" />?procId=<s:property value="procId"/>"><s:text name="validationResults.link.report" /></a></div>
							</div>
							<script type="text/javascript" language="javascript">
								/* <![CDATA[ */
									var reportStatusMessage = returnObject('reportStatusMessage');
									var reportStatusDownload = returnObject('reportStatusDownload');
									var generateReportButton = returnObject('generateReportButton');

									function updateReportStatusHandler(result) {
								        if(result.validJSON) {
								            if(result.done) {
								            	// clear timeout
								            	clearReportCheck();
								            	
								            	// hide the status message
												reportStatusMessage.style.display = 'none';
												
								            	// change display of link to block
												reportStatusDownload.style.display = 'block';
								            	
								            } else {
								            	updateReportStatus(result.status);
								            }
								        } else {
								        	updateStatus("Unable to retrieve status with asynchronous request.");
								        	// clear timeout
								        	clearReportCheck();
								        }
								    }

									var reportTimeoutHandler = null;
									function updateReportStatus(message) {
										// init display for processing state
										if(generateReportButton.style.display == '' || generateReportButton.style.display == 'block') {
											generateReportButton.style.display = 'none';
											reportStatusMessage.style.display = 'block';
										}
										setInnerHTML(reportStatusMessage, message);
										reportTimeoutHandler = window.setTimeout(statusConnection.run, 2000);
									}
									
									function clearReportCheck() {
										window.clearTimeout(reportTimeoutHandler);
									}

									var reportConnection = new asynchronousRequest();
									reportConnection.setURL("<s:url action="ValidationReport" />");
									reportConnection.setHandlerFunction(startReportStatus);
									reportConnection.setParamString("procId=<s:property value="procId" />");

									var statusConnection = new asynchronousRequest();
								    statusConnection.setURL("<s:url action="ValidationReportStatus" />");
								    statusConnection.setHandlerFunction(updateReportStatusHandler);
								    statusConnection.setParamString("procId=<s:property value="procId" />");

								    function startReportStatus() {
								    	statusConnection.run();
								    	updateReportStatus("Starting report generation");
								    }

								    function generateReport() {
								    	reportConnection.run();
								    }
								/* ]]> */
							</script>
							<div class="labelStandalone">
								<s:if test="remote">
									<script src="https://www.java.com/js/deployJava.js"></script>
									<script>
										var attributes = {
											code: 'gov.nasa.pds.web.applets.Validator',
											width: 400,
											height: 100,
											id: 'validationAppletNorm',
											mayscript: 'true',
											scriptable: 'true',
										};
										var parameters = {
											jnlp_href: 'web/applets/validator.jnlp',
											volumePath: '<s:property value="volumePathJS" />',
										};
										var version = '1.6';
										deployJava.runApplet(attributes,
												parameters, version);
									</script>

									<script type="text/javascript" language="javascript">
										var validationApplet = document
												.getElementById('validationAppletNorm');
									</script>
								</s:if>
								<s:else>
									<a href="<s:url action="ValidateVolume" />?volumePath=<s:property value="volumePath" />">
									<s:text name="validationResults.link.refresh" /></a>
								</s:else>
							</div>
						</div>
					</td>
				</tr>
			</table>
			
			<s:if test="rootNode">
				<div class="listBox">
					<div class="title"><span class="titleExpander"><img id="testResults_toggle" onclick="hideShow('testResults')" src="<s:url value="/web/images/icons/lightMinus.gif" />" /></span> <s:text name="validationResults.title.testResults" /></div>
					<table cellpadding="0" cellspacing="0" width="100%" id="testResults">
						<tbody>
							<tr class="<s:if test="labelsParseable">passed</s:if><s:else>failed</s:else>">
								<td><s:if test="labelsParseable"><img src="<s:url value="/web/images/icons/check.gif" />" /></s:if><s:else><img src="<s:url value="/web/images/icons/error.png" />" /></s:else></td>
								<td width="100%"><s:text name="validationResults.text.labelsParseable" /></td>
							</tr>
							<tr class="<s:if test="labelSyntaxValid">passed</s:if><s:else>failed</s:else>">
								<td><s:if test="labelSyntaxValid"><img src="<s:url value="/web/images/icons/check.gif" />" /></s:if><s:else><img src="<s:url value="/web/images/icons/error.png" />" /></s:else></td>
								<td><s:text name="validationResults.text.labelSyntax" /></td>
							</tr>
							<tr class="<s:if test="dictionaryValid">passed</s:if><s:else>failed</s:else>">
								<td><s:if test="dictionaryValid"><img src="<s:url value="/web/images/icons/check.gif" />" /></s:if><s:else><img src="<s:url value="/web/images/icons/error.png" />" /></s:else></td>
								<td><s:text name="validationResults.text.dictionaryValid" /></td>
							</tr>
							<tr class="<s:if test="valuesValid">passed</s:if><s:else>failed</s:else>">
								<td><s:if test="valuesValid"><img src="<s:url value="/web/images/icons/check.gif" />" /></s:if><s:else><img src="<s:url value="/web/images/icons/error.png" />" /></s:else></td>
								<td style="padding-left: 25px;"><s:text name="validationResults.text.validValues" /></td>
							</tr>
							<tr class="<s:if test="keysValid">passed</s:if><s:else>failed</s:else>">
								<td><s:if test="keysValid"><img src="<s:url value="/web/images/icons/check.gif" />" /></s:if><s:else><img src="<s:url value="/web/images/icons/error.png" />" /></s:else></td>
								<td style="padding-left: 25px;"><s:text name="validationResults.text.validKeys" /></td>
							</tr>
							<tr class="<s:if test="objectsValid">passed</s:if><s:else>failed</s:else>">
								<td><s:if test="objectsValid"><img src="<s:url value="/web/images/icons/check.gif" />" /></s:if><s:else><img src="<s:url value="/web/images/icons/error.png" />" /></s:else></td>
								<td style="padding-left: 25px;"><s:text name="validationResults.text.validObjects" /></td>
							</tr>
							<tr class="<s:if test="pointersValid">passed</s:if><s:else>failed</s:else>">
								<td><s:if test="pointersValid"><img src="<s:url value="/web/images/icons/check.gif" />" /></s:if><s:else><img src="<s:url value="/web/images/icons/error.png" />" /></s:else></td>
								<td><s:text name="validationResults.text.validPointers" /></td>
							</tr>
							<tr class="<s:if test="lineLengthsValid">passed</s:if><s:else>failed</s:else>">
								<td><s:if test="lineLengthsValid"><img src="<s:url value="/web/images/icons/check.gif" />" /></s:if><s:else><img src="<s:url value="/web/images/icons/error.png" />" /></s:else></td>
								<td><s:text name="validationResults.text.validLineLength" /></td>
							</tr>
							<tr class="<s:if test="lineEndingsValid">passed</s:if><s:else>failed</s:else>">
								<td><s:if test="lineEndingsValid"><img src="<s:url value="/web/images/icons/check.gif" />" /></s:if><s:else><img src="<s:url value="/web/images/icons/error.png" />" /></s:else></td>
								<td><s:text name="validationResults.text.validLineEndings" /></td>
							</tr>
							<tr class="<s:if test="indexValid">passed</s:if><s:else>failed</s:else>">
								<td><s:if test="indexValid"><img src="<s:url value="/web/images/icons/check.gif" />" /></s:if><s:else><img src="<s:url value="/web/images/icons/error.png" />" /></s:else></td>
								<td><s:text name="validationResults.text.validIndex" /></td>
							</tr>
							<tr class="<s:if test="productsCovered">passed</s:if><s:else>failed</s:else>">
								<td><s:if test="productsCovered"><img src="<s:url value="/web/images/icons/check.gif" />" /></s:if><s:else><img src="<s:url value="/web/images/icons/error.png" />" /></s:else></td>
								<td><s:text name="validationResults.text.validProductListing" /></td>
							</tr>
							<tr class="<s:if test="tablesValid">passed</s:if><s:else>failed</s:else>">
								<td><s:if test="tablesValid"><img src="<s:url value="/web/images/icons/check.gif" />" /></s:if><s:else><img src="<s:url value="/web/images/icons/error.png" />" /></s:else></td>
								<td><s:text name="validationResults.text.validTables" /></td>
							</tr>
							<tr class="<s:if test="requiredResourcesPresent">passed</s:if><s:else>failed</s:else>">
								<td><s:if test="requiredResourcesPresent"><img src="<s:url value="/web/images/icons/check.gif" />" /></s:if><s:else><img src="<s:url value="/web/images/icons/error.png" />" /></s:else></td>
								<td><s:text name="validationResults.text.requiredResources" /></td>
							</tr>
							<tr class="<s:if test="noEmptyResources">passed</s:if><s:else>failed</s:else>">
								<td><s:if test="noEmptyResources"><img src="<s:url value="/web/images/icons/check.gif" />" /></s:if><s:else><img src="<s:url value="/web/images/icons/error.png" />" /></s:else></td>
								<td><s:text name="validationResults.text.noEmptyResources" /></td>
							</tr>
						</tbody>
					</table>
				</div>
			</s:if>
			
			<s:if test="newValues.size() > 0">
				<div class="listBox">
					<div class="title"><span class="titleExpander"><img id="newValues_toggle" onclick="hideShow('newValues')" src="<s:url value="/web/images/icons/lightMinus.gif" />" /></span> <s:text name="validationResults.title.newValues" /></div>
					<table cellpadding="0" cellspacing="0" width="100%" id="newValues">
						<tr>
							<th><s:text name="validationResults.table.newValue.column.key" /></th>
							<th><s:text name="validationResults.table.newValue.column.value" /></th>
							<th><s:text name="validationResults.table.newValue.column.resource" /></th>
							<th><s:text name="validationResults.table.newValue.column.path" /></th>
							<th><s:text name="validationResults.table.newValue.column.lineNumber" /></th>
						</tr>
						<tbody>
							<s:iterator value="newValues" status="status">
								<tr class="<s:if test="#status.odd">alt</s:if><s:if test="#status.index + 1 == newValues.size()"> lastRow</s:if>">
									<td><s:property value="key" /></td>
									<td><s:property value="value" /></td>
									<td><s:property value="file.name" /></td>
									<td><s:property value="file.relativePath" /></td>
									<td class="numeric"><s:property value="lineNumber" /></td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</div>
			</s:if>
			
			<s:if test="dictionaryChanges.size() > 0">
				<div class="listBox">
					<div class="title"><span class="titleExpander"><img id="dictionaryChanges_toggle" onclick="hideShow('dictionaryChanges')" src="<s:url value="/web/images/icons/lightMinus.gif" />" /></span> <s:text name="validationResults.title.dictionaryChanges" /></div>
					<table cellpadding="0" cellspacing="0" width="100%" id="dictionaryChanges">
						<tr>
							<th><s:text name="validationResults.table.dictionaryChanges.column.key" /></th>
							<th><s:text name="validationResults.table.dictionaryChanges.column.message" /></th>
							<th><s:text name="validationResults.table.dictionaryChanges.column.lineNumber" /></th>
						</tr>
						<tbody>
							<s:iterator value="dictionaryChanges" status="status">
								<tr class="<s:if test="#status.odd">alt</s:if><s:if test="#status.index + 1 == newValues.size()"> lastRow</s:if>">
									<td><s:property value="id" /></td>
									<td>
										<s:if test="remote">
											<a href="#" onclick="validationApplet.previewFile('<s:property value="pathJS" />', '<s:property value="volumePathJS"  />', '<s:property value="procId" />', '<s:property value="scrollLineNumber" />')">
										</s:if>
										<s:else>
											<a href="<s:url action="PreviewFile" />?procId=<s:property value="procId"/>&amp;node=<s:property value="path" escape="false" />#line<s:property value="scrollLineNumber" />" target="_blank">
										</s:else>
											<s:property value="message" /></a>
									</td>
									<td class="numeric"><s:property value="lineNumber" /></td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</div>
			</s:if>
			
			<div class="listBox">
				<div class="title"><span class="titleExpander"><img id="problems_toggle" onclick="hideShow('problems')" src="<s:url value="/web/images/icons/lightMinus.gif" />" /></span> <s:text name="validationResults.title.problems" /></div>
				<table cellpadding="0" cellspacing="0" width="100%" id="problems">
					<tr>
						<th><s:text name="validationResults.table.problems.column.description" /></th>
						<th><s:text name="validationResults.table.problems.column.resource" /></th>
						<th><s:text name="validationResults.table.problems.column.path" /></th>
						<th><s:text name="validationResults.table.problems.column.lineNumber" /></th>
					</tr>
					<s:if test="problemGroups.size() == 0">
						<tr>
							<td colspan="4"><s:text name="validationResults.table.problems.text" /></td>
						</tr>
					</s:if>
					<s:iterator value="problemGroups" status="test">
						<%-- change the class of the tbody to refelct hide show --%>
						<tbody class="collapsible">
							<tr class="rowGroup" id="problemGroup<s:property value="hashCode()" />">
								<td colspan="4">
									<span class="rowExpander"><a href="<s:url action="UpdateValidateView" />?problemGroup=<s:property value="hashCode()" />&amp;state=<s:property value="!expanded" />&amp;procId=<s:property value="procId"/>"><img class="icon" src="<s:url value="/web/images/icons/" /><s:if test="expanded">minus.gif</s:if><s:else>plus.gif</s:else>" /></a></span>
									<img class="icon" src="<s:url value="/web/images/icons/" /><s:property value="severityIcon" />"/>
									<s:property value="description"/> (<s:property value="size" />)
								</td>
							</tr>
							<s:if test="expanded">
								<s:iterator value="clusters" status="status">
									<s:if test="problems.size() > 1">
										<tr class="rowGroup2" id="problemGroup<s:property value="hashCode()" />">
											<td class="firstElement">
												<span class="rowExpander"><a href="<s:url action="UpdateValidateView" />?problemGroup=<s:property value="hashCode()" />&amp;state=<s:property value="!expanded" />&amp;procId=<s:property value="procId"/>"><img class="icon" src="<s:url value="/web/images/icons/" /><s:if test="expanded">minus.gif</s:if><s:else>plus.gif</s:else>" /></a></span>
												<s:property value="description" /> <s:text name="generic.text.matches"><s:param value="problems.size()" /></s:text>
											</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
										</tr>
										<s:if test="expanded">
											<s:iterator value="problems" status="status2">
												<tr class="rowGroupMember2<s:if test="#status2.odd"> alt</s:if>">
													<td class="firstElement">
														<s:if test="lineNumber != null">
															<s:if test="remote">
																<a href="#" onclick="validationApplet.previewFile('<s:property value="pathJS" />', '<s:property value="volumePathJS"  />', '<s:property value="procId" />', '<s:property value="scrollLineNumber" />')">
															</s:if>
															<s:else>
																<a href="<s:url action="PreviewFile" />?procId=<s:property value="procId"/>&amp;node=<s:property value="path" escape="false" />#line<s:property value="scrollLineNumber" />" target="_blank">
															</s:else>
														</s:if>
														<s:property value="message" />
														<s:if test="lineNumber != null"></a></s:if>
													</td>
													<td><s:property value="resource" /></td>
													<td><s:property value="path" /></td>
													<td class="numeric"><s:property value="lineNumber" /></td> 
												</tr>
											</s:iterator>
										</s:if>
									</s:if>
									<s:else>
										<s:iterator value="problems">
											<tr<s:if test="#status.odd"> class="alt"</s:if>>
												<td class="firstElement">
													<s:if test="lineNumber != null">
														<s:if test="remote">
															<a href="#" onclick="validationApplet.previewFile('<s:property value="pathJS" />', '<s:property value="volumePathJS"  />', '<s:property value="procId" />', '<s:property value="scrollLineNumber" />')">
														</s:if>
														<s:else>
															<a href="<s:url action="PreviewFile" />?procId=<s:property value="procId"/>&amp;node=<s:property value="path" escape="false" />#line<s:property value="scrollLineNumber" />" target="_blank">
														</s:else>
													</s:if>
													<s:property value="message" />
													<s:if test="lineNumber != null"></a></s:if>
												</td>
												<td><s:property value="resource" /></td>
												<td><s:property value="path" /></td>
												<td class="numeric"><s:property value="lineNumber" /></td> 
											</tr>
										</s:iterator>
									</s:else>
								</s:iterator>
							</s:if>
							<s:if test="excessProblems > 0">
								<tr>
									<td class="firstElement" colspan="4"><s:text name="validation.text.excessErrors"><s:param value="excessProblems" /></s:text></td>
								</tr>
							</s:if>
						</tbody>
					</s:iterator>
				</table>
			</div>
		</td>
	</tr>
</table>
<script type="text/javascript" language="javascript">
/* <![CDATA[ */
	// get cookie id for page
	var pageID = cookieManager.getPageID();
	var cookieValue = cookieManager.get(pageID);
	var collapsedNodeNames = unescape(cookieValue).split(",");

	for(var i = 0, n = collapsedNodeNames.length; i < n; i++) {
		collapse(collapsedNodeNames[i]);
	}
	
	function collapse(id) {
		var cookieID = pageID + "_" + id;
		var toggleID = id + "_toggle";
		
		var targetObj = returnObject(id);
		if(targetObj != null) {
			targetObj.style.display = "none";
			switchToggle(id);
		}
	}
	
	function hideShow(targetName) {

		// get the object you want to hide or show
		var targetObj = returnObject(targetName);

		if(targetObj != null) {
			// hide or show it based on current state
			if(targetObj.style.display != 'none') {
				targetObj.style.display = 'none';
				saveState(targetName, false)
			} else {
				targetObj.style.display = 'block';
				saveState(targetName, true)
			}

			//switch toggle image
			switchToggle(targetName);
		}
	}

	function switchToggle(id) {
		var toggleID = id + "_toggle";
		var toggle = returnObject(toggleID);
		var srcString = toggle.src;
		var baseSrcIndex = srcString.lastIndexOf('/');
		var baseSrc = srcString.substring(0, baseSrcIndex + 1);

		// swap last plus or minus
		if(srcString.lastIndexOf('Minus') == -1) {
			toggle.src = baseSrc + "lightMinus.gif";
		} else {
			toggle.src = baseSrc + "lightPlus.gif";
		}
	}

	function saveState(id, state) {
		if(!state) {
			//add to collapse list
			if(!contains(collapsedNodeNames, id)) {
				collapsedNodeNames.push(id);
			}
		//will default to expanded
		} else {
			//try to remove from expanded list
			for (var i = 0; i < collapsedNodeNames.length; i++) {
				if(collapsedNodeNames[i] == id) {
					collapsedNodeNames.splice(i, 1);
				}
			}
		}
		storeNodeCookie();
	}

	function storeNodeCookie() {
		if(collapsedNodeNames.length > 0) {
			var nodeNamesString = collapsedNodeNames.join(',');
			cookieManager.set(pageID, nodeNamesString);
		} else {
			cookieManager.remove(pageID);
		}
	}
/* ]]> */
</script>