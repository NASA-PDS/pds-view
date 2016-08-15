<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="spacedWrapper">
    <div class="title"><s:text name="manageTabularData.title" /></div>
    <div class="basicBox">
        <div class="contentsStandalone"><s:text name="downloadloadProgress.label.status" /></div>
        
        <div class="contentsStandalone" id="statusContainer" style="display: none;"><span id="statusMessage" style="font-weight: bold; padding-right: 15px; color:red;"></span><br /><input type="button" value="<s:text name="generic.button.cancel" />" onclick="cancel()" /></div>
    </div>
</div>

<script type="text/javascript" language="javascript">
/* <![CDATA[ */
    var statusMessage = returnObject('statusMessage');
    var statusContainer = returnObject('statusContainer');

    function updateStatusHandler(result) {
        if(result.validJSON) {
            if(result.done) {
            	redirectAction("Confirmation");
            } else {
                updateStatus(result.status);
            }
        } else {
        	updateStatus("Unable to retrieve status with asynchronous request.");
        }
    }

    function cancelHandler(result) {
        if(result.validJSON) {
            if(result.cancelled) {
                redirectAction("ManageTabularData");
            } else {
            	updateStatus("Unable to cancel process, please submit a bug.");
            }
        } else {
            updateStatus("Unable to get a valid result from cancel request.");
        }
    }

    var statusConnection = new asynchronousRequest();
    statusConnection.setURL("<s:url action="DownloadStatus" />");
    statusConnection.setHandlerFunction(updateStatusHandler);
    statusConnection.setParamString("procId=<s:property value="procId" />");
    statusConnection.run();

    var timeoutHandler = null;

    
    function updateStatus(message) {
        setInnerHTML(statusMessage, message);
        if(statusContainer.style.display == 'none') {
            statusContainer.style.display = 'block';
        }
        timeoutHandler = window.setTimeout(statusConnection.run, 1000);
    }
    
    function cancel() {
        window.clearTimeout(timeoutHandler);
        var cancelConnection = new asynchronousRequest();
        cancelConnection.setURL("<s:url action="DownloadStatus" />");
        cancelConnection.setHandlerFunction(cancelHandler);
        cancelConnection.setParamString("procId=<s:property value="procId" />&cancel=true");
        cancelConnection.run();
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
    
/* ]]> */
</script>