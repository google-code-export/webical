//Object to create the menu			
function MenuLoader_${CONTEXT_MENU_ID}(id, debugEnabled) { 
	YAHOO.util.Event.onAvailable(id, this.handleOnAvailable, this);
	this.log = "";
	this.debug = debugEnabled;
	
	if(this.debug) {
		alert("Created the menu loader");
	}
}

MenuLoader_${CONTEXT_MENU_ID}.prototype.onMenuItemClicked = function(p_sType, p_aArgs, p_oMenu)  {
	var url = '${CALLBACK_URL}'  + '&uniqueId=' + p_oMenu.itemData[p_aArgs[1].index].uniqueId;
	wicketShow("${BUSY_INDICATOR_ID}");
	wicketAjaxGet(url, function() { wicketHide('${BUSY_INDICATOR_ID}'); }, function() { wicketHide('${BUSY_INDICATOR_ID}'); });
}

MenuLoader_${CONTEXT_MENU_ID}.prototype.registerMenu = function(me, menu) {
	if(me.debug) {
		me.log += "registering a menu\n";
	}
	
	//Register submenus
	if(menu.itemData != null) {
	    if(me.debug) {
			me.log += "\tlooping over the menuItems\n";
		}
		
	    for(var menuItemNr = 0; menuItemNr < menu.itemData.length; menuItemNr++) {
	        var menuItem = menu.getItem(menuItemNr);
	        
	        if(me.debug) {
				me.log += "\t\t" + menuItemNr + ": " + menuItem + " has submenu: " + (menuItem._oSubmenu !=null) + " \n";
			}
			
	        if(menuItem._oSubmenu != null) {
	            me.registerMenu(me, menuItem._oSubmenu);
	        }
	    }
	}
	 
	 //Register this menu
	menu.clickEvent.subscribe(me.onMenuItemClicked, menu, true);
}

//Fuction is called when the dom object is available
MenuLoader_${CONTEXT_MENU_ID}.prototype.handleOnAvailable = function(me) {
	if(me.debug) {
		alert("creating the menu");
	}
	
	//Insertion of the menu array
	${MENU_ARRAY}
	
	//Create the menu
	me.contextMenu_${CONTEXT_MENU_ID} = 
		new YAHOO.widget.ContextMenu(
			"contextMenu_${CONTEXT_MENU_ID}", 
			{ 
				${CONFIGURATION}
				itemdata: aItems
			} );
	
	me.contextMenu_${CONTEXT_MENU_ID}.render(document.body);
	
	//Register onclick handler for all leaves
	me.registerMenu(me, me.contextMenu_${CONTEXT_MENU_ID});
	
	if(me.debug) {
		alert(me.log);
	}
}

//Create the menuloader so that the menu is created when the target is available in the dom
var menuLoader_${CONTEXT_MENU_ID} = new MenuLoader_${CONTEXT_MENU_ID}("${TRIGGER}", ${DEBUG_ENABLED});
