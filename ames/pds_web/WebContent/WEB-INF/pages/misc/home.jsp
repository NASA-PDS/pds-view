<%@ taglib prefix="s" uri="/struts-tags" %>
<div class="defaultWrapper">
	<div class="basicBox" style="font-size: 12px;">
		<div class="label"><s:text name="home.label.about" /></div>
		<div class="contents"><s:text name="home.text.about" /></div>
		
		<div style="padding-left: 25px;">
			<div class="label"><a href="<s:url action="ValidatorAbout" />"><s:text name="home.label.validation" /></a></div>
			<div class="contents"><s:text name="home.text.validation" /></div>
			
			<div class="label"><a href="<s:url action="TableExplorer" />"><s:text name="home.label.dataSlicer" /></a></div>
			<div class="contents"><s:text name="home.text.dataSlicer" /></div>
			
			<div class="label"><a href="<s:text name="url.imageSearch" />" target="_blank"><s:text name="home.label.imageSearch" /></a></div>
			<div class="contents"><s:text name="home.text.imageSearch" /></div>
			
			<div class="label"><a href="<s:text name="url.labelEditor" />" target="_blank"><s:text name="home.label.labelEditor" /></a></div>
			<div class="contents"><s:text name="home.text.labelEditor" /></div>
		</div>
		
		<div class="contents"><s:text name="home.text.contact"><s:param><s:text name="email.webmaster" /></s:param></s:text></div>
	</div>
</div>