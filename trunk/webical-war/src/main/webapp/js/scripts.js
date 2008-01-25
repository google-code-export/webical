function resizeCalendarCells() {
	if (typeof document.body.style.maxHeight != "undefined") { //Not for Internet Explorer 6 and earlier versions
		if (document.getElementById("monthView")) {
			var calendarViewPanelContentBlock = document.getElementById("calendarViewPanelContentBlock");
			calendarViewPanelContentBlock.style.overflow = "hidden";
			
			var monthTableHeaders_arr = document.getElementById("monthView").getElementsByTagName("th");
			var weeks_arr = getElementsByClassName("weekHeader", "td", document);  
			var monthTableCells_arr = document.getElementById("monthView").getElementsByTagName("td");
			var tableCellHeight = (calendarViewPanelContentBlock.offsetHeight - monthTableHeaders_arr[0].offsetHeight) / weeks_arr.length;
			for (i = 0; i < monthTableCells_arr.length; i++) {
				monthTableCells_arr[i].style.height = tableCellHeight + "px";
			}
			var monthTableTops_arr = getElementsByClassName("monthTop", "div", document);
			var eventsInMonth_arr = getElementsByClassName("eventsInMonth", "div", document);
			var eventsInMonthNewHeight = (tableCellHeight - monthTableTops_arr[0].offsetHeight) + "px";
			for (k = 0; k < eventsInMonth_arr.length; k++) {
				eventsInMonth_arr[k].style.height = eventsInMonthNewHeight;
			}
		}
		
		if (document.getElementById("weekView")) {
			var calendarViewPanelContentBlock = document.getElementById("calendarViewPanelContentBlock");
			calendarViewPanelContentBlock.style.overflow = "hidden";
			
			var daysInWeek_arr = getElementsByClassName("eventsInList", "ul", document);
			for (i = 0; i < daysInWeek_arr.length; i++) {
				daysInWeek_arr[i].style.display = "none";
			}
			
			var weekTableCells_arr = document.getElementById("weekView").getElementsByTagName("td");
			var weekTableHeaders_arr = document.getElementById("weekView").getElementsByTagName("th");
			var weekTableTops_arr = getElementsByClassName("weekTop", "div", document);
			for (j = 0; j < weekTableCells_arr.length; j++) {
				weekTableCells_arr[j].style.height = calendarViewPanelContentBlock.offsetHeight - weekTableHeaders_arr[0].offsetHeight + "px";
			
			}
			var weekTableCellNewHeight = weekTableCells_arr[0].parentNode.offsetHeight;
			for (k = 0; k < daysInWeek_arr.length; k++) {
				daysInWeek_arr[k].style.display = "block";
				daysInWeek_arr[k].style.height = (weekTableCellNewHeight - weekTableTops_arr[0].offsetHeight - 8)  + "px";
			}
		}
	}
}

function getElementsByClassName(className, tag, elm) {
	var testClass = new RegExp("(^|\\\\s)" + className + "(\\\\s|$)");
	var tag = tag || "*";
	var elm = elm || document;
	var elements = (tag == "*" && elm.all)? elm.all : elm.getElementsByTagName(tag);
	var returnElements = [];
	var current;
	var length = elements.length;
	for(var i=0; i<length; i++) {
		current = elements[i];
		if(testClass.test(current.className)) {
			returnElements.push(current);
		}
	}
	return returnElements;
}

function addLoadEvent(func) {
  var oldonload = window.onload;
  if (typeof window.onload != 'function') {
    window.onload = func;
  } else {
    window.onload = function() {
      if (oldonload) {
        oldonload();
      }
      func();
    }
  }
}

function addResizeEvent(func) {
  var oldonresize = window.onresize;
  if (typeof window.onresize != 'function') {
    window.onresize = func;
  } else {
    window.onresize = function() {
      if (onresize) {
        onresize();
      }
      func();
    }
  }
}

addLoadEvent(resizeCalendarCells);
addResizeEvent(resizeCalendarCells);
addLoadEvent(function() {
  /* more code to run on page load */ 
});