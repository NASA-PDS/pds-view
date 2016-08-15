<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<tiles:importAttribute name="autoRefresh" />
<tiles:importAttribute name="section" />
<tiles:importAttribute name="subSection" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link rel="stylesheet" type="text/css" href="<s:url value="/web/css/default.css" />" />
		<%-- <link rel="shortcut icon" href="<s:url value="/web/favicon.ico" />" /> --%>
		<script type="text/javascript" language="javascript">
			<%-- global vars --%>
			var GLOBAL_baseResourcePath = '<s:url value="/web/" />';
		</script>
		<script type="text/javascript" language="javascript" src="<s:url value="/web/js/render/full.js" />"></script>
		<script id="_fed_an_js_tag" type="text/javascript" language="javascript" src="<s:url value="/web/js/federated-analytics.all.min.js?agency=NASA&sub-agency=ARC&vcto=12" />"></script>
		
		<title><s:if test="title != null"><s:property value="title"/> - </s:if><s:text name="application.title"/></title>
	</head>
	<body>
		<noscript><link rel="stylesheet" type="text/css" href="<s:url value="/web/css/noScript.css" />" /></noscript>

		<s:if test="hasErrors">
			<div class="errors">
				<ul>
					<s:iterator value="errorMessages">
						<li><s:property escape="false" /></li>
					</s:iterator>
				</ul>
			</div>
		</s:if>
		<s:if test="hasNotices">
			<div class="notices">
				<ul>
					<s:iterator value="noticeMessages">
						<li><s:property escape="false" /></li>
					</s:iterator>
				</ul>
			</div>
		</s:if>
		<table cellpadding="0" cellspacing="0" width="100%">
			<tr>
				<td id="banner">
					<table cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<td id="logo"><img src="<s:url value="/web/images/meatballLogo.png" />" alt="NASA" /></td>
							<td id="siteTitle"><s:text name="application.title"/></td>
							<td id="bannerLinks">
								<table cellpadding="0" cellspacing="0">
									<tr>
										<td>
											<div>&bull; <a href="http://www.nasa.gov/">NASA Portal</a></div>
											<%--<div>&bull; <a href="#">Site Help</a></div>--%>
											<div>&bull; <a href="<s:url action="Contact" />">Feedback</a></div>
										</td>
									</tr>
								</table>
							</td>
							<td id="searchBox">
								<form id="dummyForm" action="http://pds.jpl.nasa.gov/" method="get"></form>
								<script type="text/javascript">
								function setSearchAction(form, actionSelector) {
									form.q.value = form.words.value;
									var baseAction = document.getElementById("dummyForm").action;
									form.action = baseAction + actionSelector.value;
									return true;
								}
								</script>

								<form action="http://pds.jpl.nasa.gov/tools/data-search/search.jsp" method="get" onsubmit="setSearchAction(this,this.search_scope)">
								<input type="hidden" name="q" value="" />
								<input type="hidden" name="facet" value="" />
								<input type="hidden" name="in" value="all" />
								<input type="hidden" name="Go" value="" />
								<table cellpadding="0" cellspacing="0">
									<tr>
										<td id="searchField"><input type="text" name="words" size="40" /></td>
									</tr>
									<tr>
										<td id="searchOptions">
											in
											<select name="search_scope">
												<option value="tools/data-search/search.jsp">PDS data</option>
												<option value="tools/phonebook/phonebook.cfm">PDS phone book</option>
												<option value="tools/ddlookup/data_dictionary_lookup.cfm">PDS Data Dictionary</option>
												<option value="tools/rlookup/reference_lookup.cfm">References</option>
											</select>
											<input type="submit" name="doSearch" value="search" />
										</td>
									</tr>
								</table>
								</form>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td id="primaryNav">
					<table cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<td<s:if test="#attr['section'] == 'home'"> class="active"</s:if>><a href="<s:url action="Home" />"><s:text name="nav.primary.home.label" /></a></td>
							<td<s:if test="#attr['section'] == 'validation'"> class="active"</s:if>><a href="<s:url action="ValidatorAbout" />"><s:text name="nav.primary.validate.label" /></a></td>
							<td<s:if test="#attr['section'] == 'slice'"> class="active"</s:if>><a href="<s:url action="TableExplorer" />"><s:text name="nav.primary.slice.label" /></a></td>
							<td<s:if test="#attr['section'] == 'images'"> class="active"</s:if>><a href="<s:text name="url.imageSearch" />"><s:text name="nav.primary.imageSearch.label" /></a></td>
							<td<s:if test="#attr['section'] == 'lace'"> class="active"</s:if>><a href="<s:text name="url.labelEditor" />"><s:text name="nav.primary.labelEditor.label" /></a></td>
							<td<s:if test="#attr['section'] == 'resources'"> class="active"</s:if>><a href="<s:url action="Resources" />"><s:text name="nav.primary.resources.label" /></a></td>
							<td class="padder">&nbsp;</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td id="subNav">
					<table cellpadding="0" cellspacing="0">
						<tr>
							<s:if test="#attr['section'] == 'manageData'">
								<td<s:if test="#attr['subSection'] == 'manageDataSets'"> class="active"</s:if>><div><div><a href="<s:url action="Validator" />">Manage Data Sets</a></div></div></td>
								<td<s:if test="#attr['subSection'] == 'createDataSet'"> class="active"</s:if>><div><div><a href="<s:url action="CreateDataSet" />">New Data Set</a></div></div></td>
							</s:if>
							<s:elseif test="#attr['section'] == 'validation'">
								<td<s:if test="#attr['subSection'] == 'manageDataSets'"> class="active"</s:if>><div><div><a href="<s:url action="Validator" />">Validate Volume</a></div></div></td>
								<td<s:if test="#attr['subSection'] == 'releaseNotes'"> class="active"</s:if>><div><div><a href="<s:url action="ValidatorReleaseNotes" />">Release Notes</a></div></div></td>
								<td<s:if test="#attr['subSection'] == 'troubleshooting'"> class="active"</s:if>><div><div><a href="<s:url action="ValidatorTroubleshooting" />">Troubleshooting</a></div></div></td>
								<td<s:if test="#attr['subSection'] == 'aboutValidation'"> class="active"</s:if>><div><div><a href="<s:url action="ValidatorAbout" />">About</a></div></div></td>
								<%-- Removing statistics, per PTOOL-228 --%>
								<%--
								<td<s:if test="#attr['subSection'] == 'validationStats'"> class="active"</s:if>><div><div><a href="<s:url action="ViewStats" />">Statistics</a></div></div></td>
								--%>
							</s:elseif>
							<s:elseif test="#attr['section'] == 'slice'">
								<td<s:if test="#attr['subSection'] == 'sliceData'"> class="active"</s:if>><div><div><a href="<s:url action="TableExplorer" />">Table Explorer</a></div></div></td>
								<td<s:if test="#attr['subSection'] == 'releaseNotes'"> class="active"</s:if>><div><div><a href="<s:url action="TableExplorerReleaseNotes" />">Release Notes</a></div></div></td>
								<td<s:if test="#attr['subSection'] == 'aboutTableExplorer'"> class="active"</s:if>><div><div><a href="<s:url action="TableExplorerAbout" />">About</a></div></div></td>
								<td<s:if test="#attr['subSection'] == 'helpContents'"> class="active"</s:if>><div><div><a href="<s:url action="TableExplorerHelp" />">Help</a></div></div></td>
								<%-- Removing statistics, per PTOOL-228 --%>
								<%--
								<td<s:if test="#attr['subSection'] == 'slicerStats'"> class="active"</s:if>><div><div><a href="<s:url action="ViewSlicerStats" />">Statistics</a></div></div></td>
								--%>
							</s:elseif>
							<s:else>
								<td><img src="<s:url value="/web/resources/images/spacer.gif" />" width="10" height="1" alt="" /></td>
							</s:else>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<table cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<td id="body">
		
<%-- START BODY --%>
<tiles:insertAttribute name="body" />
<%-- END BODY --%>

							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td id="footer">
					<table cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<td id="footerInfoLinks">
								<a href="<s:url action="Privacy" />"><s:text name="nav.footer.link.privacy" /></a><br />
								<a href="<s:url action="Contact" />"><s:text name="nav.footer.link.contact" /></a><br />
								<a href="<s:text name="url.freedomOfInformation" />">Freedom of Information Act</a>
							</td>
							<td width="100%">
								<table cellpadding="0" cellspacing="0" id="footerInfoContainer">
									<tr>
										<td><img src="<s:url value="/web/images/meatballLogoSmall.png" />" alt="NASA" /></td>
										<td id="footerInfo">
											Webmaster: <a href="mailto:mark.rose@nasa.gov">Mark Rose</a><br />
											NASA Official: Rich Keller<br />
											Last updated: June, 2013
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</body>
</html>
