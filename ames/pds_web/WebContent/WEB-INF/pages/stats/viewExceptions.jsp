<%@ taglib prefix="s" uri="/struts-tags"%>

<div class="listBox">
	<div class="title">Logged Exceptions</div>
	<table cellpadding="0" cellspacing="0" width="100%">
		<tr>
			<th>Date</th>
			<th>Message</th>
			<th>URL</th>
			<th class="linkColumn">&nbsp;</th>
		</tr>
		<s:iterator value="exceptions" status="status">
			<tr <s:if test="#status.odd"> class="alt"</s:if>>
				<td><s:property value="date" /></td>
				<td><a href="<s:url action="ViewException" />?id=<s:property value="id" />" target="_blank"><s:property value="message" /></a></td>
				<td><s:property value="targetUrl" /></td>
				<td><a href="<s:url action="DeleteException" />?id=<s:property value="id" />">delete</a></td>
			</tr>
		</s:iterator>
	</table>
</div>