<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<link rel="stylesheet" href="css/jquery-ui-1.8.6.custom.css"></link>
<link rel="stylesheet" href="css/main.css"></link>

<script src="js/jquery-1.4.3.min.js"></script>
<script src="js/jquery-ui-1.8.6.custom.min.js"></script>

<script type="text/javascript">
$(function() {
	$('#node').change(function() {
		setProfile();
	});

	$('#ident').change(function() {
		setProfile();
	});

	$('#submit-btn').click(function() {
		$.post('setup', $('#transfer-form').serialize(), function(data) {
			var connect = "OK";
			var sftp = "OK";
			var html = "";
			if (data.connect == 0) {
				connect = "<div class=\"ui-state-error\">FAILED -  "+data.error+"</div>";
			} else if (data.sftp == 0) {
				sftp = "<div class=\"ui-state-error\">FAILED -  "+data.error+"</div>";
			}
			
			html += "<div class=\"form-row\"><div class=\"label\">Profile:</div>"+$('#profile').val()+"</div>";
			html += "<div class=\"form-row\"><div class=\"label\">Hostname:</div>"+$('#hostname').val()+"</div>";
			html += "<div class=\"form-row\"><div class=\"label\">Path:</div>"+$('#srcPath').val()+"/"+$('#regex').val()+"</div>";
			html += "<div class=\"form-row\"><div class=\"label\">Connect:</div>"+connect+"</div>";
			html += "<div class=\"form-row\"><div class=\"label\">SFTP:</div>"+sftp+"</div>";
			if (data.empty == 1) {
				html += "<div class=\"form-row\"><div class=\"label\">File Founds:</div>";
				html += "<div class=\"file-list\">0 Files Found<br />Verify before submitting</div></div>";
			} else {
				html += "<div class=\"form-row\"><div class=\"label\">Files found:</div>";
				html += "<div class=\"file-list\">";
				$.each(data.files, function (i, filename) {
					html += filename+"<br />";
				});
				html += "</div></div>";
			}

			$('#modal-section').html(html);
			$('#modal-section').dialog({
				modal: true,
				title: "New Profile Information",
				buttons: {
					"Submit": function() {
						$(this).dialog("close");
					},
					"Cancel": function() {
								$(this).dialog("close");
					}
				},
				width: "500px"
			});
		}, "json");
	});
	                               	
});

function setProfile() {
	$('#profile').val("pds"+$('#node').val()+"-"+$('#ident').val());
}
</script>

<title>Transfer Logs</title>
</head>
<body>
	<div class="top-bar">
		<h1>Log Transfer Set-Up</h1>
	</div>
	<form id="transfer-form" action="setup" method="POST">
		<div id="main-content">
			<h2 class="title">Profile Information</h2>
			<p class="transfer-text">Please enter information to describe the new Report Service profile.</p>
			<div class="form-section ui-corner-all">
				<div class="form-row">
					<div class="label">Node:</div>
					<select id="node" name="node" class="info-input">
						<option value="atm">ATM</option>
						<option value="cdn">CDN</option>
						<option value="eng">ENG</option>
						<option value="geo">GEO</option>
						<option value="gre">GRE</option>
						<option value="hrd">HRD</option>
						<option value="img">IMG</option>
						<option value="jpl">JPL</option>
						<option value="ldn">LDN</option>
						<option value="lol">LOL</option>
						<option value="lro">LRO</option>
						<option value="nai">NAI</option>
						<option value="ppi">PPI</option>
						<option value="psi">PSI</option>
						<option value="rng">RNG</option>
						<option value="rsf">RSF</option>
						<option value="sbn">SBN</option>
						<option value="swr">SWR</option>
						<option value="tes">TES</option>
						<option value="the">THE</option>
						<option value="uoi">UOI</option>
					</select>
					<a class="ui-state-highlight" href="#"><span class="ui-icon ui-icon-info"></span></a>
				</div>
				<div class="form-row">
					<div class="label">Machine Identifier:</div>
					<input id="ident" name="ident" type="text" class="info-input" />
					<a class="ui-state-highlight" href="#"><span class="ui-icon ui-icon-info"></span></a>
				</div>
				<div class="form-row">
					<div class="label">Profile Name:</div>
					<input id="profile" name="profile" class="info-input" type="text" disabled="disabled" />
					<a class="ui-state-highlight" href="#"><span class="ui-icon ui-icon-info"></span></a>
				</div>
			</div>
			<h2 class="title">Host Details</h2>
			<p class="transfer-text">Please enter the detailed information necessary to retrieve the log files.</p>
			<div class="form-section ui-corner-all">
				<div class="form-row">
					<div class="label">Hostname:</div>
					<input id="hostname" name="hostname" type="text" class="info-input" />
					<a class="ui-state-highlight" href="#"><span class="ui-icon ui-icon-info"></span></a>
				</div>
				<div class="form-row">
					<div class="label">Source File Path:</div>
					<input id="srcPath" name="srcPath" type="text" class="info-input" />
					<a class="ui-state-highlight" href="#"><span class="ui-icon ui-icon-info"></span></a>
				</div>
				<div class="form-row">
					<div class="label">Regular Expression:</div>
					<input id="regex" name="regex" type="text" class="info-input" />
					<a class="ui-state-highlight" href="#"><span class="ui-icon ui-icon-info"></span></a>
				</div>
					<div class="form-row">
					<div class="label">Username:</div>
					<input id="username" name="username" type="text" class="info-input" />
					<a class="ui-state-highlight" href="#"><span class="ui-icon ui-icon-info"></span></a>
				</div>
				<div class="form-row">
					<div class="label">Password:</div>
					<input id="password" name="password" type="password" class="info-input" />
					<a class="ui-state-highlight" href="#"><span class="ui-icon ui-icon-info"></span></a>
				</div>
			</div>
			<div class="button-section">
				<div class="form-row button-row">
					<input id="submit-btn" type="button" value="Submit" />
				</div>
			</div>
		</div>
	</form>
	<div id="modal-section"></div>
</body>
</html>