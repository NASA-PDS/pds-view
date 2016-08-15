<%@ taglib prefix="s" uri="/struts-tags" %>

<style>
.better { color: green; font-weight: bold; }
.worse { color: red; font-weight: bold; }
.zero { color: #CCCCCC; }
.numeric, .better, .worse, .zero { font-family: 'Andale Mono', monospace; }
</style>

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
					<td><s:property escape="false" /></td>
				</s:iterator>
			</tr>
		</s:iterator>
	</table>
</div>