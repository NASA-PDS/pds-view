function popup(newpage) {
window.open(newpage,'window1','toolbar=0,location=1,directories=0,status=0,menubar=0,scrollbars=yes,resizable=yes,width=640,height=480');
}

function popup_sm(newpage) {
window.open(newpage,'window2','toolbar=0,location=1,directories=0,status=0,menubar=0,scrollbars=yes,resizable=yes,width=400,height=300');
}

function submitQuick()
{
	document.postForm.action = "/pds/index.jsp";
	document.postForm.submit();
}

function submitAdvanced()
{
    document.postForm.action = "/pds/advanced.jsp";
    document.postForm.submit();
}

function submitPower()
{
    document.postForm.action = "/pds/power.jsp";
    document.postForm.submit();
}

function submitText()
{
    document.postForm.action  = '/search/index.jsp';
    document.postForm.submit();
}

function isParamSelected(param)
{
  var len = param.options.length;
  var selected = false;
  for (var i=0; i<len; i++) {
     if (param.options[i].selected &&
         param.options[i].value != "All") {
         selected = true;
         break;
     }
  }
  return selected;
}

var selectionChanged = false;

function selectAll( selobj)
{
  var len = selobj.options.length;
  for (var i=0; i<len; i++)
     selobj.options[i].selected = true;
}

function applyMultiSelect( selobj)
{
  if (isParamSelected( selobj))
    changeSearchSpec();
  else alert('Please make one or more selection first');
}

// called when user switches from quick<->advanced page
function switchPage( idx)
{
  // if page was called with parameters
  // or user made changes to multiple selection
  if (document.postForm.hasParams.value=='1' || selectionChanged) {

   var msnname_sel = isParamSelected(document.postForm.msnname)
   var msn = document.postForm.msnname;
   var j = 0;
   var a = 0;
   if (msnname_sel) {
      for  (j=0; j<msn.options.length;j++) {
         if (msn.options[j].selected) {
                   document.postForm.msntext[a++].value=msn.options[j].text;
         }
       }
    } 
    else {
       for (j=0; j<msn.options.length;j++) {
                  document.postForm.msntext[a++].value=msn.options[j].text;
       }
    }


    if (idx==0) {
      // when switching to quick page, select all msnname & instname
      //selectAll( document.postForm.msnname);
      //selectAll( document.postForm.instname);
      submitQuick();
    }
    else if (idx==1) submitAdvanced();
    else if (idx==2) submitPower();
    else submitText();
  }
  else {
    // page not called with parameters, just load new page
    if (idx==0) window.location.href = '/pds/index.jsp';
    else if (idx==1) window.location.href = '/pds/advanced.jsp';
    else if (idx==2) window.location.href = '/pds/power.jsp';
    else window.location.href = '/search/index.jsp';
  }
}

// replace leading and trailing space
function trim( str)
{
  str.replace( /^\s*/, '');
  str.replace( /\s*$/, '');
  return str;
}

   function newImage(arg) {
   	if (document.images) {
   		rslt = new Image();
   		rslt.src = arg;
   		return rslt;
   	}
   }
   
   function changeImages() {
   	if (document.images && (preloadFlag == true)) {
   		for (var i=0; i<changeImages.arguments.length; i+=2) {
   			document[changeImages.arguments[i]].src = changeImages.arguments[i+1];
   		}
   	}
   }
   
   var preloadFlag = false;
   function preloadImages() {
   	if (document.images) {
		nav_home_over = newImage("/pds/images/nav_home_on.gif");
		nav_dataservices_over = newImage("/pds/images/nav_dataservices_on.gif");
		nav_tools_over = newImage("/pds/images/nav_tools_on.gif");
		nav_documents_over = newImage("/pds/images/nav_documents_on.gif");
		nav_relatedsites_over = newImage("/pds/images/nav_relatedsites_on.gif");
		nav_aboutpds_over = newImage("/pds/images/nav_aboutpds_on.gif");
		nav_sitemap_over = newImage("/pds/images/nav_sitemap_on.gif");
   		preloadFlag = true;
   	}
   }

function lastMod()
{
	var monthNames = new Array( 'Jan','Feb','Mar','Apr','May','Jun','Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' );

	var zeroPad = "";
	var lastDate = new Date( document.lastModified );
	year = y2kYear( lastDate );
	month = lastDate.getMonth();
	day = lastDate.getDate();
	if( day < 10 )
	{
		zeroPad = "0";
	}
	
	printDate = zeroPad + day + " " + monthNames[ month ] + " " + year;

	return printDate;
}

function y2kYear( theDate )
{
        wrongYear = theDate.getYear();
        var rightYear = wrongYear % 100;
        rightYear += (rightYear < 38) ? 2000 : 1900;
        return rightYear;
}
