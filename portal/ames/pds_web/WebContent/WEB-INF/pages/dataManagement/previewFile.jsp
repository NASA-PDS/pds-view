<%@ taglib prefix="s" uri="/struts-tags"%>

<div class="defaultWrapper" style="border-right-width: 1px; border-bottom-width: 1px;">
	<div class="title">File Details</div>
	<div class="basicBox">
		<div class="label">File</div>
		<div class="contents"><s:property value="path" /> (<s:property value="size" />)</div>
		
		<div class="label">Problems</div>
		<div class="contents"><s:text name="previewFile.text.numErrors"><s:param value="numErrors" /></s:text>, <s:text name="previewFile.text.numWarnings"><s:param value="numWarnings" /></s:text>, <s:text name="previewFile.text.numInfo"><s:param value="numInfo" /></s:text></div>
	</div>
</div>

<br />
<div class="fileContentsBox">
	<table cellpadding="0" cellspacing="0" width="100%">
		<s:iterator value="lines" status="status">
			<tr class="<s:property value="cssClass" />" id="line<s:property value="lineNumber" />"<s:if test="severity.toString() != 'NONE'"> title="<s:property value="problemString" />"</s:if>>
				<td><s:if test="severity.toString() != 'NONE'"><img class="icon" src="<s:url value="/web/images/icons/" /><s:property value="severityIcon" />" /></s:if><s:else>&nbsp;</s:else></td>
				<td><s:property value="lineNumber" /></td>
				<td width="100%"><pre><s:property value="contents" /></pre></td>
			</tr>
		</s:iterator>
	</table>
</div>