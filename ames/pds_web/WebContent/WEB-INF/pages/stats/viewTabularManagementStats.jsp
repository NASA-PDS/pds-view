<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="spacedWrapper">
	<div class="title">General Statistics</div>
	<div class="basicBox">
		<div class="label">Total Labels</div>
		<div class="contents"><s:property value="totalLabels" /></div>
		<div class="label">Total Tabular Data Objects</div>
		<div class="contents"><s:property value="totalTables" /></div>
		<div class="label">Total Downloads</div>
		<div class="contents"><s:property value="totalDownloads" /></div>
		<div class="label">Average Downloads per Tabular Data Object</div>
		<div class="contents"><s:property value="avgDownloadsPerTable" /></div>
		<div class="label">Average Conditional Filters per Download</div>
		<div class="contents"><s:property value="avgFilters" /></div>
		<div class="label">Average Labels per User Session</div>
		<div class="contents"><s:property value="avgLabelsPerVisit" /></div>
	</div>
</div>

<div class="listBox">
	<div class="title"><s:property value="statsByLabel.title" /></div>
	<table cellpadding="0" cellspacing="0" width="100%">
		<tr>
			<s:iterator value="statsByLabel.header">
				<th><s:property /></th>
			</s:iterator>
		</tr>
		<s:iterator value="statsByLabel.rows" status="status">
			<tr <s:if test="#status.odd"> class="alt"</s:if>>
				<s:iterator status="innerStatus">
					<%-- <s:if test="#innerStatus.index == 0">
						<td><a target="_blank" href="<s:url action="ViewDownloadDetails" />?tabId=<s:property />">details</a></td> 
					</s:if>
					<s:else>--%>
						<td nowrap="nowrap"><s:property /></td>
<%-- 					</s:else> --%>
				</s:iterator>
			</tr>
		</s:iterator>
	</table>
	<div class="title"><s:property value="statsByUrl.title" /></div>
	<table cellpadding="0" cellspacing="0" width="100%">
		<tr>
			<s:iterator value="statsByUrl.header">
				<th><s:property /></th>
			</s:iterator>
		</tr>
		<s:iterator value="statsByUrl.rows" status="status">
			<tr <s:if test="#status.odd"> class="alt"</s:if>>
				<s:iterator status="innerStatus">
					<s:if test="#innerStatus.index == 0">
						<td><a target="_blank" href="<s:url action="ViewSliceDetails" />?tabId=<s:property />">details</a></td>
					</s:if>
					<s:else>
						<td><s:property /></td>
					</s:else>
				</s:iterator>
			</tr>
		</s:iterator>
	</table>
</div>
