<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="defaultWrapper">
	<div class="title"><s:text name="confirmation.title"/></div>
	<div class="basicBox">
		<div class="contents">
			<s:text name="confirmation.downloadHelp"/><a href="<s:url action="ServeFile" ><s:param name="procId" value="%{procId}"/></s:url>"><s:text name="confirmation.downloadLink"/></a> <s:text name="confirmation.downloadTrouble"/>
		</div>
		
			<form action="<s:url action="ManageTabularData" />" style="display:inline;">
				<input type="hidden" name="procId" value="<s:property value="procId" />" />
				<span class="buttonWrapper"><input type="submit" name="back" value="<s:text name="confirmation.back" />" ></span>			
			</form>	
			<form action="<s:url action="TableExplorer" />" style="display:inline;">
				<span class="buttonWrapper"><input type="submit" name="startOver" value="<s:text name="confirmation.startover" />" ></span>			
			</form>
			

	</div>
	<div class="title"><s:text name="confirmation.label.feedback"/></div>
	<div class="basicBox">

		<div class="contents"><s:text name="confirmation.thankyou"/></div>	
	</div>

<form action="<s:url action="ServeFile" />" id="ServeFile">
		<input type="hidden" name="procId" value="<s:property value="procId" />" /></form>

</div>

<script type="text/javascript" language="javascript">
/* <![CDATA[ */
 
   window.onload=function() { document.forms['ServeFile'].submit(); }



/* ]]> */
</script>
