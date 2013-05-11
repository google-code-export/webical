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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Item discribing the javascript menu item
 * @author ivo
 */
public class YUIContextMenuItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static Log log = LogFactory.getLog(YUIContextMenuItem.class);
	
	//Unique number used in previous instance
	private static int previousId = 0;
	
	//Attributes used in the menu
	private String title;
	private String helptext;
	private boolean checked;
	private boolean disabled;
	private boolean emphasis;
	private boolean strongemphasis;
	
	//The list of submenu items or null or empty
	private List<YUIContextMenuItem> submenuItems;
	
	//Unique id for this contextmenu
	private int uniqueId;

	/**
	 * @param title the title of the submenu
	 */
	public YUIContextMenuItem(String title) {
		this.title = title;
		setUniqueId();
	}
	
	/**
	 * @param title the title of the submenu
	 * @param submenuItems a list of submenu items
	 */
	public YUIContextMenuItem(String title, List<YUIContextMenuItem> submenuItems) {
		this.title = title;
		this.submenuItems = submenuItems;
		setUniqueId();
	}
	
	/**
	 * Created a js menu array entry from this YUIContextMenuItem
	 * @return a js menu array entry
	 */
	public String toJavaScriptString() {
		//Add this menuitem's config
		String itemString = "{ text: \"" + getTitle() + "\", uniqueId: \"" + getUniqueId() +"\"";
		if(helptext != null && helptext.length() > 0) { itemString += ", helptext: \"" + helptext + "\""; }
		if(checked) { itemString += ", checked: true"; }
		if(disabled) { itemString += ", disabled: true"; }
		if(emphasis) { itemString += ", emphasis: true"; }
		if(strongemphasis) { itemString += ", strongemphasis: true"; }
		
		//The submenu items if any
		if(getSubmenuItems().size() > 0) {
			itemString += ", submenu: { id:\"" + getTitle() + "\", itemdata: [";
			for (Iterator iter = getSubmenuItems().iterator(); iter.hasNext();) {
				YUIContextMenuItem subMenuItem = (YUIContextMenuItem) iter.next();
				itemString += subMenuItem.toJavaScriptString();
				if(iter.hasNext()) {
					itemString += ",";
				}
			}
			itemString += "] }";
		}
		return itemString += "}";
	}
	
	/**
	 * Default method to overide when a callback is to be handled.
	 * This method does nothing by default besides creating a log message.
	 * If the menuitem has a submenu, this is never called!
	 * @param target the AjaxRequestTarget
	 */
	protected void onClick(AjaxRequestTarget target) {
		log.debug("onclick: " + this.getTitle() + " - " + this.getUniqueId());
	}
	
	/**
	 * Create a unique id for the callback
	 */
	private void setUniqueId() {
		if(previousId == Integer.MAX_VALUE) {
			previousId = 0;
		} else {
			previousId++;
			uniqueId = previousId;
		}
	}
	
	
	///////////////////////////
	/// Getters and Setters ///
	///////////////////////////

	public String getTitle() {
		return title!=null?title:"";
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<YUIContextMenuItem> getSubmenuItems() {
		if(submenuItems == null) { 
			return new ArrayList<YUIContextMenuItem>();
		} else {
			return submenuItems;
		}
	}

	public void setSubmenuItems(List<YUIContextMenuItem> submenuItems) {
		this.submenuItems = submenuItems;
	}
	
	/**
	 * @return the unique id for this menu item
	 */
	public int getUniqueId() {
		return uniqueId;
	}

	public boolean isChecked() {
		return checked;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public boolean isEmphasis() {
		return emphasis;
	}

	public String getHelptext() {
		return helptext;
	}

	public boolean isStrongemphasis() {
		return strongemphasis;
	}
	
	public YUIContextMenuItem setDisabled(boolean disabled) {
		this.disabled = disabled;
		return this;
	}
	
	public YUIContextMenuItem setEmphasis(boolean emphasis) {
		this.emphasis = emphasis;
		return this;
	}
	
	public YUIContextMenuItem setHelptext(String helptext) {
		this.helptext = helptext;
		return this;
	}
	
	public YUIContextMenuItem setChecked(boolean checked) {
		this.checked = checked;
		return this;
	}

	public YUIContextMenuItem setStrongemphasis(boolean strongemphasis) {
		this.strongemphasis = strongemphasis;
		return this;
	}

}