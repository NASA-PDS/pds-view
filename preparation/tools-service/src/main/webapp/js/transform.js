function findGetParameter(parameterName) {
	var result = null, tmp = [];
	var items = location.search.substr(1).split("&");
	for (var index = 0; index < items.length; index++) {
		tmp = items[index].split("=");
		if (tmp[0] === parameterName)
			result = decodeURIComponent(tmp[1]);
	}
	return result;
}

function getBaseURL() {
	//var baseurl = window.location.origin+window.location.pathname;
	var baseurl = window.location.origin + "/tools-service";
	//console.log("window.location.origin = " + window.location.origin);
	//console.log("window.location.pathname = " + window.location.pathname);
	console.log("baseURL = " + baseurl);
}

function transform(toolsServiceUrl) {
	//var cmd = $("#command").val();
	//console.log("cmd: " + cmd);
	var cmd = "transform";
	//var base = 'http://localhost:48080/ts/'
	//if (typeof toolsServiceUrl !== 'undefined')
	var base = toolsServiceUrl;
	console.log("tools service url base = " + base);
	
	var args = "target=" + findGetParameter("fileName");

	var format = "format-type=" + $("#format :selected").text();
	console.log("format type text = " + format);
	
	var outPath = findGetParameter("fileName");
	var outdir = "outdir=" + outPath.substring(0, outPath.lastIndexOf("/"));

	var listObjsFlag = "";
	if ($("#listObjs").is(':checked')) { 
		// Code in the case checkbox is checked. 
		listObjsFlag = "O";
	}
	console.log("list objects = " + listObjsFlag);

	//var fullUrl = base + cmd + '?' + args + '&' + format;
	var fullUrl = base + cmd + '?' + args + '&' + format + '&' + outdir;

	console.log("fullUrl: " + fullUrl);
	if (listObjsFlag!="")
    	fullUrl += "&" + listObjsFlag;

	var res = encodeURI(fullUrl);
	console.log("refreshed URL = " + res);

	var outputs = $("#outputs").val();
	$.getJSON(res, function(data) {
		var completeResponse = JSON.stringify(data, undefined, 2);
		console.log("data   = " + completeResponse);
		//$("#response").html(completeResponse);

		var stdout = JSON.stringify(data.result.stdout, undefined, 2);
		stdout = stdout.replace(/\\n/g, '<br/>');
		console.log("stdout = " + stdout);
		$("#stdout").html(stdout);

		var outputs = stdout.substring(stdout.indexOf("outputs = "), 
				stdout.indexOf("Processing"));
		console.log("outputs = " + outputs);

		outputs = outputs.substring(outputs.indexOf("[") + 1, outputs.indexOf("]"));
		//$("#outputs").html(outputs);
		console.log("++++++++++++outputs = " + outputs);

		var hash = JSON.stringify(data.result.hash, undefined, 2);
		console.log("hash = " + hash);
		//$("#hash").html(hash);

		if (outputs!=null) {
			var filename = outputs.substring(outputs.lastIndexOf("/")+1);
			$("#download").html("<br>Download: <b>" +
					"<a href=\"UploadDownloadFileServlet?fileName=" + outputs
					+ "\">" + filename + "</a></b>");
		}
	});
}
