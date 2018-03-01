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
	var baseurl = window.location.origin + "/tools-service";
	console.log("baseURL = " + baseurl);
}

function transform(toolsServiceUrl) {
	//var cmd = $("#command").val();
	//console.log("cmd: " + cmd);
	var cmd = "transform";
	//var base = 'http://localhost:48080/ts/'
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
        stdout = stdout.substring(1, stdout.length-1);
        var outputs = stdout.substring(stdout.indexOf("outputs = ")+10,
                stdout.indexOf("]")+1);       
        //outputs = outputs.substring(outputs.indexOf("[") + 1, outputs.indexOf("]"));
        outputs = outputs.substring(1, outputs.length-1);
        console.log("*******outputs = " + outputs);

        // trim whitespace end of string
        stdout = stdout.replace(/\s*$/,"");
        stdout = stdout.replace(/\\n/g, '<br/>');
        console.log("stdout = " + stdout);
        $("#stdout").html(stdout);

        var outputs2 = stdout.substring(stdout.indexOf("following output:")+18);
        outputs2 = outputs2.replace(/\\n/g, '');
        //$("#outputs").html(outputs);
        outputs2 = outputs2.substring(0, outputs2.indexOf("<br/>"));
        console.log("++++++++++++outputs2 = " + outputs2);

        var hash = JSON.stringify(data.result.hash, undefined, 2);
        console.log("hash = " + hash);
        //$("#hash").html(hash);

        var outdir = JSON.stringify(data.input.keyed.outdir, undefined, 2);
        console.log("outdir = " + outdir);

        if (outputs!=null) {
            var filename = outputs.substring(outputs.lastIndexOf("/")+1);
            $("#download").html("<hr><b>Download: " +
                    "<a href=\"UploadDownloadFileServlet?fileName=" + outputs
                    + "\">" + filename + "</a></b>");
        }
	});
}
