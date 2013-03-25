/*
 *    Webical - http://www.webical.org
 *    Copyright (C) 2007 Func. Internet Integration
 *
 *    This file is part of Webical.
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.webical.web.components.ajax.menu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.extensions.ajax.markup.html.WicketAjaxIndicatorAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.template.PackagedTextTemplate;
import org.webical.web.components.ajax.calendar.YUICalendar;

/**
 * Class to create a nice context menu on a dom element
 * @author ivo
 *
 */
public class YUIContextMenu extends Panel {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(YUIContextMenu.class);
	
	//Unique number used in previous instance
	private static int previousId = 0;
	
	//Markup ids
	private static final String JAVASCRIPT_INIT_SCRIPT_MARKUP_ID = "javascriptInitScript";
	
	//Resources
	private static final String MENU_CSS = "assets/menu.css";
	private static final String CONTEXT_MENU_INIT_JS = "contextMenuInit.js";
	
	//JavaScript template variables
	private static final String CONTEXT_MENU_ID_VARIABLE = "CONTEXT_MENU_ID";
	private static final String MENU_ARRAY_VARIABLE = "MENU_ARRAY";
	private static final String MENU_CONFIGURATION_VARIABLE = "CONFIGURATION";
	private static final String CALLBACK_URL_VARIABLE = "CALLBACK_URL";
	private static final String BUSY_INDICATOR_ID_VARIABLE = "BUSY_INDICATOR_ID";
	private static final String JAVASCRIPT_DEBUG_ENABLED = "DEBUG_ENABLED";
	private static final String TRIGGER_VARIABLE = "TRIGGER";
	
	//Takes care of the nice indicator gif
	private final WicketAjaxIndicatorAppender indicatorAppender = new WicketAjaxIndicatorAppender();
	
	//The menu configuration object
	private YUIContextMenuConfiguration configuration;
	
	//The ajax callback handler
	private YUIMenuHandler menuHandler;
	
	//Unique id for this contextmenu
	private int uniqueId;
	

	/**
	 * @param id the id for the component
	 * @param configuration the configuration used to build the context menu
	 */
	public YUIContextMenu(String id, YUIContextMenuConfiguration configuration) {
		super(id);
		this.configuration = configuration;
		
		//Create a unique id
		if(previousId == Integer.MAX_VALUE) {
			previousId = 0;
		} else {
			previousId++;
			uniqueId = previousId;
		}
		
		//Add the javascript to initialize the contextmenu
		Label initScript = new Label(JAVASCRIPT_INIT_SCRIPT_MARKUP_ID, new LoadableDetachableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected Object load() {
				return getJavaScriptInitScript();
			}
			
		});
		
		//Make sure the script is rendered correctly
		initScript.setEscapeModelStrings(false);
		this.add(initScript);
		
		//Add handler
		this.menuHandler = new YUIMenuHandler(this.getId());
		this.add(menuHandler);
		
		//Add some style to the menu
		add(HeaderContributor.forCss(YUICalendar.class, MENU_CSS));
		
		//Add Indicator appender
		add(indicatorAppender);
	}
	
	/**
	 * Callback endpoint
	 * @param trigger the trigger to refresh components
	 */
	protected void onItemClicked(AjaxRequestTarget target) {
		int uniqueId = Integer.parseInt(getRequest().getParameter("uniqueId"));
		if(log.isDebugEnabled()) {
			log.debug("Selected menu item: " + uniqueId);
		}
		
		//Find the selected item
		for(YUIContextMenuItem menuItem : configuration.getMenu()) {
			YUIContextMenuItem selectedItem = getSelectedMenuItem(menuItem, uniqueId);
			if(selectedItem != null) {
				//Execute the onClick of the selected item
				selectedItem.onClick(target);
				break;
			}
		}
	}
	
	/**
	 * Finds the selected menu item recursively
	 * @param menuItem the menuItem to check
	 * @param uniqueId the unique id to look for
	 * @return a YUIContextMenuItem or null
	 */
	private YUIContextMenuItem getSelectedMenuItem(YUIContextMenuItem menuItem, int uniqueId) {
		YUIContextMenuItem selectedItem = null;
		
		if(menuItem.getSubmenuItems() != null && menuItem.getSubmenuItems().size() > 0) {
			for(YUIContextMenuItem submenuItem : menuItem.getSubmenuItems()) {
				selectedItem = getSelectedMenuItem(submenuItem, uniqueId);
				if(selectedItem != null) {
					break;
				}
			}
		} else if(menuItem.getUniqueId() == uniqueId) {
			return menuItem;
		}
		return selectedItem;
	}
	
	/**
	 * Generates the script for this ContextMenu
	 * @return the javascript init script
	 */
	private String getJavaScriptInitScript() {
		Map<String, String> variables = new HashMap<String, String>();
		
		variables.put(CONTEXT_MENU_ID_VARIABLE, "" + uniqueId);
		variables.put(TRIGGER_VARIABLE, configuration.getTrigger());
		variables.put(MENU_CONFIGURATION_VARIABLE, configuration.toJavaScriptString());
		variables.put(MENU_ARRAY_VARIABLE, getJavaScriptMenuArray());
		variables.put(CALLBACK_URL_VARIABLE, menuHandler.getCallbackUrl(true).toString());
		variables.put(BUSY_INDICATOR_ID_VARIABLE, indicatorAppender.getMarkupId());
		variables.put(JAVASCRIPT_DEBUG_ENABLED, configuration.isDebugEnabled()?"true":"false");
		
		return new PackagedTextTemplate(YUIContextMenu.class, CONTEXT_MENU_INIT_JS).asString(variables);
	}
	
	/**
	 * Creates a part of the script with the menu array
	 * @return a String containing the menu js array
	 */
	private String getJavaScriptMenuArray() {
		String menuArray = "var aItems = [";
		for (Iterator iter = configuration.getMenu().iterator(); iter.hasNext();) {
			YUIContextMenuItem menuItem = (YUIContextMenuItem) iter.next();
			menuArray += menuItem.toJavaScriptString();
			if(iter.hasNext()) {
				menuArray += ",";
			}
		}
		return menuArray + "]; ";
	}

}
