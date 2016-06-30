<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="spacedWrapper">
    <div class="title">Validation Progress</div>
    <div class="basicBox">
        <div class="contents">Validating Volume</div>
        
        <div class="label">Validating "<s:property value="volumePath" />"</div>
		<div class="contents" id="statusContainer" style="display: none;"></div>
		<div class="contents"><input type="button" value="<s:text name="generic.button.cancel" />" onclick="cancel()" /></div>
		
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

<script type="text/javascript" language="javascript">
/* <![CDATA[ */
    var statusContainer = returnObject('statusContainer');

    function updateStatusHandler(result) {
        if(result.validJSON) {
            if(result.done) {
            	updateStatus(result.status);
            	redirectAction("ShowResults");
            } else {
                updateStatus(result.status);
                timeoutHandler = window.setTimeout(statusConnection.run, 1000);
            }
        } else {
        	updateStatus("Unable to retrieve status with asynchronous request.");
        	timeoutHandler = window.setTimeout(statusConnection.run, 3000);
        }
    }

    function cancelHandler(result) {
        if(result.validJSON) {
            if(result.cancelled) {
                redirectAction("Validator");
            } else {
            	updateStatus("Unable to cancel process, please submit a bug.");
            }
        } else {
            updateStatus("Unable to get a valid result from cancel request.");
        }
    }

    var statusConnection = new asynchronousRequest();
    statusConnection.setURL("<s:url action="ValidationStatus" />");
    statusConnection.setHandlerFunction(updateStatusHandler);
    statusConnection.setParamString("procId=<s:property value="procId" />");
    statusConnection.run();

    var timeoutHandler = null;
    function updateStatus(message) {
        if(statusContainer.style.display == 'none') {
            statusContainer.style.display = 'block';
        }
        setInnerHTML(statusContainer, message);
    }
    
    function cancel() {
        window.clearTimeout(timeoutHandler);
        var cancelConnection = new asynchronousRequest();
        cancelConnection.setURL("<s:url action="ValidationStatus" />");
        cancelConnection.setHandlerFunction(cancelHandler);
        cancelConnection.setParamString("procId=<s:property value="procId" />&cancel=true");
        cancelConnection.run();
        var location = document.location.href;
        var newLocation = location.replace(/^(.*)\/[a-zA-Z0-9]+(\.action)?(.*)$/gi, "$1/Validator");
       document.location.replace(newLocation);
    }
    
    function clearStatus() {
        statusContainer.style.display = 'none';
    }

    function redirectAction(actionName) {
        // get full url, do a regex replace for anything /XXX.action?asdfas=asdf
        var location = document.location.href;
        var newLocation = location.replace(/^(.*)\/[a-zA-Z0-9]+\.action(.*)$/gi, "$1/" + actionName + ".action" + "$2");
       document.location.replace(newLocation);
    }
    
    // TODO: add onabort and unload event that cancels the action BUT, make sure it doesn't snarl when being redirected by the applet... have cancelable boolean?
/* ]]> */
</script>