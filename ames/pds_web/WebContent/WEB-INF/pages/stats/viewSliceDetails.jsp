<%@ taglib prefix="s" uri="/struts-tags" %>


<div class="spacedWrapper">
	<div class="title"><s:property value="labelUrl"/></div>
	
	<div class="basicBox">
	 	<div class="label">Tabular data file</div>
		<div class="contents"><s:property value="tabUrl"/></div>
	 	<div class="label">Type</div>
		<div class="contents"><s:property value="tableType" /></div>
		<div class="label">Columns</div>
		<div class="contents"><s:property value="columnsOrig" /></div>
		<div class="label">Rows</div>
		<div class="contents"><s:property value="rowsOrig" /></div>
	</div>
</div>
		
<div class="listBox">
	<div class="title"><s:property value="stats.title" /></div>
	<s:if test="stats.rows.size >= 1">
	
	<table cellpadding="0" cellspacing="0" width="100%">
		<tr>
			<s:iterator value="stats.header">
				<th><s:property /></th>
			</s:iterator>
		</tr>
		<s:iterator value="stats.rows" status="status">
			<tr <s:if test="#status.odd"> class="alt"</s:if>>
				<s:iterator status="innerStatus">
					
						<td><s:property /></td>
				</s:iterator>
			</tr>
		</s:iterator>
	</table>
	</s:if>
	<s:else>
	<div class="basicBox">
		<div class="contents">
			No files were downloaded for this tabular data file during this session.
		</div>
	</div>
		</s:else>
</div>
