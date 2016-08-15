<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="spacedWrapper">
	<div class="title"><s:text name="manageDataSets.label.validateLocal" /></div>
	<div class="basicBox">
		<div class="contents"><s:text name="manageDataSets.text.validateLocal" /></div>

		<script src="https://www.java.com/js/deployJava.js"></script>
		<script>
		    var attributes = {
		    		code: 'gov.nasa.pds.web.applets.Validator',
			        width: 400, height: 100,
			        id: 'validationAppletNorm',
			        mayscript: 'true',
			        scriptable: 'true'
			};
		    var parameters = {
		    		jnlp_href: 'web/applets/validator.jnlp',
		    };
		    var version = '1.6';
		    deployJava.runApplet(attributes, parameters, version);
		</script>

		<div class="label" id="titleContainer" style="display: none;"></div>
		<div class="contents" id="statusContainer" style="display: none;"></div>
		<div class="contents" id="cancelContainer" style="display: none;"><input type="button" value="<s:text name="generic.button.cancel" />" onclick="cancel()" /></div>

		<s:if test="safari">
		<h2>Additional Requirements for Safari Users</h2>
		<p>Safari has an additional level of security for Java applets. If this is the first time you are using the Validator you must allow access to your local computer as follows:</p>
		<ol>
			<li>Open to the Safari Preferences dialog.</li>
			<li>Go to the Security tab.</li>
			<li>Next to <em>Internet plug-ins</em>, make sure <em>Allow plug-ins</em> is checked.</li>
			<li>Next to <em>Internet plug-ins</em>, press the Manage Website Settings button.</li>
			<li>For the website <em>pdstools.arc.nasa.gov</em>, select Run in Unsafe Mode. This allows the Validator to read your data volumes to check them for compliance.</li>
			<li>Confirm that you want to run the Validator in unsafe mode by clicking Trust.</li>
			<li>Click Done and then close the Preferences dialog.</li>
			<li>Reload the page to restart the applet in unsafe mode.</li>
		</ol>
		</s:if>

		<div class="label">Version</div>
		<div class="contents">
			<s:text name="validator.versionInfo">
				<s:param value="appVersionInfo.volumeValidatorVersion" />
				<s:param value="dictionaryVersion" />
				<s:param value="appVersionInfo.libraryVersion" />
				<s:param><s:url action="ValidatorReleaseNotes" /></s:param>
				<s:param><s:url action="Contact" /></s:param>
			</s:text>
		</div>
	</div>
</div>
 
<s:if test="volumes.size() > 0">
	<div class="listBox">
		<div class="title"><s:text name="manageDataSets.title.hostedVolumes" /></div>
		<table cellpadding="0" cellspacing="0" width="100%">
			<tr>
				<th><s:text name="manageDataSets.table.volumes.column.volumeName" /></th>
				<th><s:text name="manageDataSets.table.volumes.column.volumeID" /></th>
				<th><s:text name="manageDataSets.table.volumes.column.dataSetID" /></th>
				<th class="linkColumn">&nbsp;</th>
			</tr>
			
			<s:iterator value="volumes" status="status">
				<tr<s:if test="#status.odd"> class="alt"</s:if>>
					<td><s:property value="name" /></td>
					<td><s:property value="volId" /></td>
					<td><s:property value="setId" /></td>
					<td>
						<span class="listLink"><a href="<s:url action="ValidateVolume" />?volumePath=<s:property value="volumePath" />"><img src="<s:url value="/web/images/icons/checkIn.gif" />" width="12" height="12" /> <s:text name="manageDataSets.table.volumes.link.validate" /></a></span>
					</td>
				</tr>
			</s:iterator>
		</table>
	</div>
</s:if>