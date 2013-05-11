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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;

/**
 * 
 * Configuration to pass to a YUIContextMenu
 * @author ivo
 *
 */
public class YUIContextMenuConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//Collection of menuitems
	private List<YUIContextMenuItem> menu;
	
	//Enable this to get some nice popups with debug info
	private boolean debugEnabled;
	
	//The menus configuration
	
	//One of these is used
	private String trigger;
	private Component triggerComponent;
	
	//Other config attributes;
	private boolean autosubmenudisplay = true;
	private boolean clicktohide = true;
	private boolean constraintoviewport = true;
	private String container;
	private int hidedelay;
	private int showdelay;
	private String position;
	private String submenualignment;
	private boolean visible;
	
	/**
	 * @param triggerComponent the component that can trigger the menu (uses setOutputMarkupId(true) on this component)
	 * @param menu the menu
	 */
	public YUIContextMenuConfiguration(Component triggerComponent, List<YUIContextMenuItem> menu) {
		super();
		this.triggerComponent = triggerComponent;
		triggerComponent.setOutputMarkupId(true);
		this.menu = menu;
	}

	/**
	 * @param trigger the id or the object (document.body for example) that can trigger the menu. 
	 * @param menu the menu
	 */
	public YUIContextMenuConfiguration(String target, List<YUIContextMenuItem> menu) {
		this.trigger = target;
		this.menu = menu;
	}

	
	/**
	 * Turns this configuration into a JavaScript String
	 * @return the js String
	 */
	public String toJavaScriptString() {
		String configuration = "trigger: \"" + getTrigger() + "\", ";
		
		if(position != null && position.length() > 0) { configuration += "position: \"" + position + "\", "; };
		if(container != null && container.length() > 0) { configuration += "container: \"" + container + "\", "; };
		if(submenualignment != null && submenualignment.length() > 0) { configuration += "submenualignment: \"" + submenualignment + "\", "; };
		if(hidedelay > 0) { configuration += "visible: " + hidedelay + ", "; };
		if(showdelay > 0) { configuration += "showdelay: " + showdelay + ", "; };
		
		configuration += "clicktohide: " + clicktohide + ", ";
		configuration += "constraintoviewport: " + constraintoviewport + ", ";
		configuration += "visible: " + visible + ", ";
		
		return configuration;
	}
	
	/////////////////////////////////////
	/// Getters and Setters (Chained) ///
	/////////////////////////////////////
	
	public List<YUIContextMenuItem> getMenu() {
		if(menu == null) {
			return new ArrayList<YUIContextMenuItem>();
		} else {
			return menu;
		}
	}

	public String getContainer() {
		return container;
	}

	public int getHidedelay() {
		return hidedelay;
	}

	public String getSubmenualignment() {
		return submenualignment;
	}

	public String getPosition() {
		return position;
	}

	public Component getTriggerComponent() {
		return triggerComponent;
	}

	/**
	 * @return the markupid from the triggerComponent if set. Otherwise the trigger string if not null. Otherwise the default: document.body
	 */
	public String getTrigger() {
		if(triggerComponent != null) {
			return triggerComponent.getMarkupId();
		} else if(trigger != null && trigger.length() > 0) {
			return trigger;
		} else {
			return "document.body";
		}
	}

	public boolean isClicktohide() {
		return clicktohide;
	}

	public boolean isConstraintoviewport() {
		return constraintoviewport;
	}

	public boolean isAutosubmenudisplay() {
		return autosubmenudisplay;
	}

	public boolean isVisible() {
		return visible;
	}

	public boolean isDebugEnabled() {
		return debugEnabled;
	}
	
	public int getShowdelay() {
		return showdelay;
	}

	/**
	 * Enables the debug output
	 * @param debugEnabled
	 * @return this (for chaining)
	 */
	public YUIContextMenuConfiguration setDebugEnabled(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
		return this;
	}
	
	/**
	 * @param autosubmenudisplay Boolean indicating if submenus are automatically made visible when the user mouses over the menu's items. 
	 * Default Value: true
	 * @return this (for chaining)
	 */
	public YUIContextMenuConfiguration setAutosubmenudisplay(boolean autosubmenudisplay) {
		this.autosubmenudisplay = autosubmenudisplay;
		return this;
	}

	/**
	 * @param clicktohide Boolean indicating if the menu will automatically be hidden if the user clicks outside of it. 
	 * Default Value: true 
	 * @return this (for chaining)
	 */
	public YUIContextMenuConfiguration setClicktohide(boolean clicktohide) {
		this.clicktohide = clicktohide;
		return this;
	}

	/**
	 * @param constraintoviewport Boolean indicating if the menu will try to remain inside the boundaries of the size of viewport. 
	 * Default Value: true 
	 * @return this (for chaining)
	 */
	public YUIContextMenuConfiguration setConstraintoviewport(boolean constraintoviewport) {
		this.constraintoviewport = constraintoviewport;
		return this;
	}

	/**
	 * @param container HTML element reference or string specifying the id attribute of the HTML element that the 
	 * menu's markup should be rendered into. 
	 * Default Value: document.body 
	 * @return this (for chaining)
	 */
	public YUIContextMenuConfiguration setContainer(String container) {
		this.container = container;
		return this;
	}

	/**
	 * @param hidedelay Number indicating the time (in milliseconds) that should expire before the menu is hidden.
	 * Default Value: 0 
	 * @return this (for chaining)
	 */
	public YUIContextMenuConfiguration setHidedelay(int hidedelay) {
		this.hidedelay = hidedelay;
		return this;
	}

	/**
	 * @param submenualignment Array defining how submenus should be aligned to their parent menu item. 
	 * The format is: [itemCorner, submenuCorner]. By default a submenu's top left corner is aligned to 
	 * its parent menu item's top right corner.
	 * Default Value: ["tl","tr"] 
	 * @return this (for chaining)
	 */
	public YUIContextMenuConfiguration setSubmenualignment(String submenualignment) {
		this.submenualignment = submenualignment;
		return this;
	}

	/**
	 * @param triggerComponent
	 * @return this (for chaining)
	 */
	public YUIContextMenuConfiguration setTriggerComponent(Component triggerComponent) {
		this.triggerComponent = triggerComponent;
		return this;
	}

	/**
	 * @param visible Boolean indicating whether or not the menu is visible. If the menu's "position" configuration property is set 
	 * to "dynamic" (the default), this property toggles the menu's <div> element's "visibility" style property between "visible" 
	 * (true) or "hidden" (false). If the menu's "position" configuration property is set to "static" this property toggles the 
	 * menu's <div> element's "display" style property between "block" (true) or "none" (false).
	 * Default Value: false 
	 * @return this (for chaining)
	 */
	public YUIContextMenuConfiguration setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

	/**
	 * @param menu
	 * @return this (for chaining)
	 */
	public YUIContextMenuConfiguration setMenu(List<YUIContextMenuItem> menu) {
		this.menu = menu;
		return this;
	}

	/**
	 * @param trigger
	 * @return this (for chaining)
	 */
	public YUIContextMenuConfiguration setTrigger(String trigger) {
		this.trigger = trigger;
		return this;
	}

	/**
	 * @param position String indicating how a menu should be positioned on the screen. Possible values are "static" and "dynamic." 
	 * Static menus are visible by default and reside in the normal flow of the document (CSS position: static). Dynamic menus are 
	 * hidden by default, reside out of the normal flow of the document (CSS position: absolute), and can overlay other elements 
	 * on the screen. Default Value: dynamic 
	 * @return this (for chaining)
	 */
	public YUIContextMenuConfiguration setPosition(String position) {
		this.position = position;
		return this;
	}

	/**
	 * @param showdelay Number indicating the time (in milliseconds) that should expire before a submenu is made visible when the 
	 * user mouses over the menu's items. Default Value: 0 
	 * @return this (for chaining)
	 */
	public YUIContextMenuConfiguration setShowdelay(int showdelay) {
		this.showdelay = showdelay;
		return this;
	}
}