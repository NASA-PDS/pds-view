<%@ taglib prefix="s" uri="/struts-tags" %>

<script type='text/javascript' src='http://www.google.com/jsapi'></script>
<script type='text/javascript'>
	google.load('visualization', '1', {'packages':['annotatedtimeline']});
	google.setOnLoadCallback(drawVisualization);

	function handleQueryResponse(response) {
		if (response.isError()) {
			alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
			return;
		}

		var data = response.getDataTable();
		var container = document.getElementById('chart_div');
		var chart = new google.visualization.AnnotatedTimeLine(container);
		var now = new Date();
		// 2592000000 = 30 days in milliseconds
		var startDate = new Date(now.getTime() - 2592000000);
		chart.draw(data, {displayAnnotations: true, zoomStartTime : startDate});
	};

	function drawVisualization() {
		var query = new google.visualization.Query('<s:url action="GetStatsData" />');
		//var query = new google.visualization.Query('<s:url value="/web/js/response2.js" />');
		query.setQuery('time=' + new Date());

		// Send the query with a callback function.
		query.send(handleQueryResponse);
		//console.log(query);
	};
</script>


<div class="spacedWrapper">
	<div class="title">Validation Over Time</div>
	<div class="basicBox">
		<div id='chart_div' style='width: 900px; height: 300px;'></div>
	</div>
</div>

<div class="spacedWrapper">
	<div class="title">General Statistics</div>
	<div class="basicBox">
		<div class="label">Total Validations</div>
		<div class="contents"><s:property value="totalValidations" /></div>
		
		<div class="label">Unique Volumes</div>
		<div class="contents"><s:property value="totalVolumes" /></div>
		
		<div class="label">Maximum Number of Files</div>
		<div class="contents"><s:property value="maxNumFiles" /> (<a target="_blank" href="<s:url action="ViewVolumeDetails" />?volume=<s:property value="maxNumFilesId" />"><s:property value="maxNumFilesId" /></a>)</div>
		
		<div class="label">Minimum Number of Files</div>
		<div class="contents"><s:property value="minNumFiles" /> (<a target="_blank" href="<s:url action="ViewVolumeDetails" />?volume=<s:property value="minNumFilesId" />"><s:property value="minNumFilesId" /></a>)</div>
		
		<div class="label">Average Number of Files</div>
		<div class="contents"><s:property value="avgNumFiles" /></div>
		
		<div class="label">Maximum Duration</div>
		<div class="contents"><s:property value="maxDuration" /> (<a target="_blank" href="<s:url action="ViewVolumeDetails" />?volume=<s:property value="maxDurationId" />"><s:property value="maxDurationId" /></a>)</div>
		
		<div class="label">Minimum Duration</div>
		<div class="contents"><s:property value="minDuration" /> (<a target="_blank" href="<s:url action="ViewVolumeDetails" />?volume=<s:property value="minDurationId" />"><s:property value="minDurationId" /></a>)</div>
		
		<div class="label">Average Duration</div>
		<div class="contents"><s:property value="avgDuration" /></div>
		
		<div class="label">Bounces (one validation only, no fix and follow up)</div>
		<div class="contents"><s:property value="bounces" /> (<s:property value="bouncePercent" />%)</div>
	</div>
</div>

<div class="listBox">
	<div class="title"><s:property value="errorImprovementStats.title" /></div>
	<table cellpadding="0" cellspacing="0" width="100%">
		<tr>
			<s:iterator value="errorImprovementStats.header">
				<th><s:property /></th>
			</s:iterator>
		</tr>
		<s:iterator value="errorImprovementStats.rows" status="status">
			<tr <s:if test="#status.odd"> class="alt"</s:if>>
				<s:iterator>
					<td><s:property /></td>
				</s:iterator>
			</tr>
		</s:iterator>
	</table>
</div>
<br />
<div class="listBox">
	<div class="title"><s:property value="errorStats.title" /></div>
	<table cellpadding="0" cellspacing="0" width="100%">
		<tr>
			<s:iterator value="errorStats.header">
				<th><s:property /></th>
			</s:iterator>
		</tr>
		<s:iterator value="errorStats.rows" status="status">
			<tr <s:if test="#status.odd"> class="alt"</s:if>>
				<s:iterator>
					<td><s:property /></td>
				</s:iterator>
			</tr>
		</s:iterator>
	</table>
</div>
<br />
<div class="listBox">
	<div class="title"><s:property value="generalVolStats.title" /></div>
	<table cellpadding="0" cellspacing="0" width="100%">
		<tr>
			<s:iterator value="generalVolStats.header">
				<th><s:property /></th>
			</s:iterator>
		</tr>
		<s:iterator value="generalVolStats.rows" status="status">
			<tr <s:if test="#status.odd"> class="alt"</s:if>>
				<s:iterator status="innerStatus">
					<s:if test="#innerStatus.index == 0">
						<td><a target="_blank" href="<s:url action="ViewVolumeDetails" />?volume=<s:property />"><s:property /></td>
					</s:if>
					<s:else>
						<td><s:property /></td>
					</s:else>
				</s:iterator>
			</tr>
		</s:iterator>
	</table>
</div>
<br />
<div class="listBox">
	<div class="title"><s:property value="improvement.title" /></div>
	<table cellpadding="0" cellspacing="0" width="100%">
		<tr>
			<s:iterator value="improvement.header">
				<th><s:property /></th>
			</s:iterator>
		</tr>
		<s:iterator value="improvement.rows" status="status">
			<tr <s:if test="#status.odd"> class="alt"</s:if>>
				<s:iterator status="innerStatus">
					<s:if test="#innerStatus.index == 0">
						<td><a target="_blank" href="<s:url action="ViewVolumeDetails" />?volume=<s:property />"><s:property /></td>
					</s:if>
					<s:else>
						<td><s:property /></td>
					</s:else>
				</s:iterator>
			</tr>
		</s:iterator>
	</table>
</div>
