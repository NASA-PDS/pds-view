<%@ taglib prefix="s" uri="/struts-tags"%>
<div class="defaultWrapper">
	<div class="title">View Exception</div>

	<div class="basicBox">
		<div class="label">Date</div>
		<div class="contents"><s:property value="exceptionObj.date" /></div>
		
		<div class="label">Requested URL</div>
		<div class="contents"><s:property value="exceptionObj.targetUrl" /></div>

		<div class="label">Stack Trace</div>
		<div class="contents" style="padding-left: 30px; white-space: pre;"><s:property value="exceptionObj.stack" /></div>
	</div>
</div>