<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="defaultWrapper">
	<form action="<s:url action="LoadLabel" />" method="post">
		<input type="hidden" name="procId" value="<s:property value="procId" />" />

		<div class="title" ><s:text name="importTabularData.title" /></div>	
		<div class="subTitle">A visualization tool for viewing, filtering, and downloading PDS tabular data.</div>
			
		<div class="basicBox">
			<div class="contents"><s:text name="importTabularData.text.instructions" /></div>
			<div class="contents"><input style="height:20px;" type="text" name="labelURLString" value="<s:property value="labelURLString" />" size="124"></div>
			<div class="contents"><input style="height:25px" type="submit" name="save" value="<s:text name="importTabularData.button.getStarted" />"></div>
			<div class="label"><s:text name="importTabularData.label.version" /></div>
			<div class="content">
				<s:text name="slicer.versionInfo">
					<s:param value="appVersionInfo.slicerVersion" />
					<s:param><s:url action="TableExplorerNotes" /></s:param>
					<s:param><s:text name="email.dataSlicerDeveloper" /></s:param>
				</s:text>
			</div>
		</div>
	</form>
</div>