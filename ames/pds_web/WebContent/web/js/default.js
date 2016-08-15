//return object if passed ID or object - allows for easier use of references on page
function returnObject(objectReference) {
	var type = typeof(objectReference);
	switch(type) {
		case "string":
			var foundObject = document.getElementById(objectReference);
			if(foundObject !== null) {
				if(foundObject.id != objectReference) {
					throw "Collision between element name and id.";
				}
				return foundObject;
			}
			return null;
		case "object":
			return objectReference;
		default:
			return null;
	}
}

function setInnerHTML(object, value) {
	try {
		object.innerHTML = value;
	} catch (e) {
		//if this is xhtml, it doesn't like to set by using the innerHTML property, try to append a text node instead
		try {
			var textNode = document.createTextNode(value);
			object.appendChild(textNode);
		} catch (e) {
			alert(e.message + " " + typeof(object.innerHTML) + " " +  value);
		}
	}	
}

/* AJAX */
//TODO: formalize error format and throw an error if an error string or error is returned
//TODO: add property for number of top level results
function asynchronousRequest() {
	var _instanceReference = this;
	
	var _handler = function(){};
	//URL to request data from
	var _URL = null;
	//Method to send data
	var _transferMethod = "POST";
	//Whether request should run asynchrounously or wait for completion to continue running page scripts
	var _isAsynch = true;
	//params to post, tied to _transferMethod *reccomend not using for now due to support issue*
	var _paramString = '';
	//number of seconds to allow before timeout
	var _timeoutSeconds = 20;
	
	//holder for request
	var _request = null;
	//indication of load state
	var _loaded = false;
	//holder for timeoutID
	var _timeoutID;
	
	//set transfer method
	this.setTransferMethod = function(transferMethod) {
		if (transferMethod == "GET" || transferMethod == "POST") {
			_transferMethod = transferMethod;
		} else {
			throw new Error(1,"invalid transferMethod type");
		}
	};
	
	//set transfer method
	this.setIsAsynch = function(isAsynch) {
		if (isAsynch == true || isAsynch == false) {
			_isAsynch = isAsynch;
		}
	};
	
	//set params
	this.setParamString = function(paramString) {
		//TODO: add testing to insure proper format
		_paramString = paramString;
	};
	
	//set url to request data from
	this.setURL = function(URL) {
		_URL = URL;
	};
	
	//set handler function that will make use of returned data
	this.setHandlerFunction = function(handler) {
		_handler = handler;
	};
	
	//set number of seconds to allow before timing out
	this.setTimeoutSeconds = function(numSeconds) {
		_timeoutSeconds = numSeconds;
	};
	
	//getter for timeout period
	this.getTimeout = function() {
		return _timeoutSeconds * 1000;
	};
	
	this.checkTimeout = function() {
		if (!_loaded) {
			_request.abort();
		} else {
			alert("loaded in time");
		}
	};
	
	function handleRequest() {
		if (_request.readyState == 4) {
			if (_request.status == 200) {
				//url exists and there were no errors
				var requestData = _request.responseText;
				try {
					eval("var requestObject = (" + requestData + ")");
				//returned string resulted in invalid js, just return as string
				} catch (e) {
					var requestObject = requestData;
				}
				_handler(requestObject);
			} else {
				var requestObject = { "error" : {"errorCode" : _request.status, "errorText" : _request.statusText} };
				_handler(requestObject);
			}
			_loaded = true;
			//clear attached events now that completed
			clearTimeout(_timeoutID);
			try {
				_request.onreadystatechange = null;
			//ie complains about a type mismatch here, catching and ignoring error
			} catch (e) {
				_request.onreadystatechange = function(){};
			}
		}
	}
	
	this.run = function() {
		if (!_request && typeof XMLHttpRequest!='undefined') {
			_request = new XMLHttpRequest();
		}
		_request.onreadystatechange = handleRequest;
		_request.open(_transferMethod, _URL, _isAsynch);
		_request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		_request.setRequestHeader("Content-length", _paramString.length);
		_request.setRequestHeader("Connection", "close");
		_request.send(_paramString);
	};
}

function redirect(location) {
	window.location = location;
}

function openNew(location) {
	window.open(location, "new");
}

/* COOKIE HANDLER */
/*
Copyright (c) 2005 Tim Taylor Consulting <http://tool-man.org/>

Permission is hereby granted, free of charge, to any person obtaining a
copy of this software and associated documentation files (the "Software"),
to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
IN THE SOFTWARE.*/
var cookieManager = {

	set : function(name, value, expiration, path, domain, secure) {
		if (!expiration) {
			expiration = 365;
		}
		var date = new Date();
		date.setTime(date.getTime() + (expiration * 24 * 60 * 60 * 1000));

		if(!path) {
			path = '/';	
		}
		var cookieString = name + "=" +escape(value) +
		";expires=" + date.toGMTString()+
		((path) ? ";path=" + path : "") + 
		((domain) ? ";domain=" + domain : "") +
		((secure) ? ";secure" : "");
		document.cookie = cookieString;
	},

	get : function(name) {
		var namePattern = name + "=";
		var cookies = document.cookie.split(';');
		for(var i = 0, n = cookies.length; i < n; i++) {
			var c = cookies[i];
			while (c.charAt(0) == ' ') {
				c = c.substring(1, c.length);
			}
			if (c.indexOf(namePattern) == 0) {
				return c.substring(namePattern.length, c.length);
			}
		}
		return null;
	},

	remove : function(name) {
		cookieManager.set(name, "", -1);
	},
	
	//get pseudo unique identifier per page to make objects sticky to a given page
	getPageID : function() {
		//ASSEMBLE COOKIE PREPEND IDENTIFIER
		//get the url past the hostname minus the get params
		var sectionString = document.location.pathname;
		//strip out the index.php if there
		//WARNING: SPECIFIC TO MERLIN CONFIGURATION - IF REUSED, MAY NEED TO BE MODIFIED
		//TODO: see if still makes sense for new struts version
			//remove the trailing forward slash if there
			sectionString = sectionString.replace(/\//gi, "");
			//get the search params
			var searchString = document.location.search;
			//extract the action from the search params
			searchString = searchString.replace(/^.*action=([^&]*).*/gi, "$1");
			//if the action was not found, assume index
			if(searchString == '') {
				searchString = 'index';
			}
		//make the cookie prepender be something like "settings_notifications"
		var cookieBase = sectionString + "_" + searchString + "_";
		return cookieBase;
	}
};

// array functions
function contains(a, obj) {
  var i = a.length;
  while (i--) {
    if (a[i] === obj) {
      return true;
    }
  }
  return false;
}
