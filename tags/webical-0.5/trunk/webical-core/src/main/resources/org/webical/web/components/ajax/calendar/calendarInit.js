function createUrl(e) {
	return '${CALLBACK_URL}'  + '&selectedDate=' + e;
}

YAHOO.namespace("${CALENDAR_NAMESPACE}");
		
var onSelectHandlerFunction = function(type, args, obj) {
	var wicketPanelURL = createUrl(args[0][0]);
	wicketShow("${BUSY_INDICATOR_ID}");
	wicketAjaxGet(wicketPanelURL, function() { wicketHide('${BUSY_INDICATOR_ID}'); }, function() { wicketHide('${BUSY_INDICATOR_ID}'); });
}
		
YAHOO.${CALENDAR_NAMESPACE}.${CALENDAR_ID} = new YAHOO.widget.Calendar("YAHOO.${CALENDAR_NAMESPACE}.${CALENDAR_ID}", "${MARKUP_ID}");
YAHOO.${CALENDAR_NAMESPACE}.${CALENDAR_ID}.render();
YAHOO.${CALENDAR_NAMESPACE}.${CALENDAR_ID}.selectEvent.subscribe(onSelectHandlerFunction, YAHOO.${CALENDAR_NAMESPACE}.${CALENDAR_ID}, true);
