<%@ taglib prefix="s" uri="/struts-tags" %>
<div class="defaultWrapper">
	<div class="title"><s:text name="error.label.error" /></div>

	<div class="basicBox">
		<div class="contents"><s:text name="error.text.description" /></div>
		
		<div class="label"><s:text name="error.label.sendDetails" /></div>
		<div class="contents">
			We would appreciate it if you provided some details about the circumstances of this error to the <strong><a href="mailto:pds_operator@jpl.nasa.gov">PDS Operator</a></strong>: 
			<ul>
				<li>The stack trace appearing below</li>
				<li>The current URL</li>
				<li>As much detail as possible about what you were doing when the error occurred. For instance, if this occurred when attempting to preview a specific file, it would be helpful to have a description of the error you were trying to preview in the file and have the file attached to the email.</li>
			</ul>
		</div>

		<div class="label"><s:text name="error.label.stacktrace" /></div>
	
		<div class="contents" style="padding-left: 30px; white-space: pre;"><s:property value="exceptionStackString" /></div>
	</div>
</div>