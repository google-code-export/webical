//override wickets alert.
if (typeof DOMParser == "undefined" && Wicket.Browser.isSafari()) {
   DOMParser = function () {}

   DOMParser.prototype.parseFromString = function (str, contentType) {
   		//Do nothing!
   }
}

function callbackOnload() {
	var transport = null;
    if (window.ActiveXObject) {
        transport = new ActiveXObject("Microsoft.XMLHTTP");
    } else if (window.XMLHttpRequest) {
        transport = new XMLHttpRequest();
    }
    if(transport != null && !(typeof DOMParser == "undefined" && Wicket.Browser.isSafari())) {
    	${CALLBACK_SCRIPT}
    }
}
addLoadEvent(callbackOnload);