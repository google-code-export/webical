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

package org.webical.web.event;


import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;

/**
 * Specifies the interface for the controlling part of the plugin system
 * @author ivo
 *
 */
public interface ExtensionHandler {
	
	/**
	 * Add an extension to the component
	 * @param extension the extension holding the component and the extensionpoint
	 */
	public void addExtension(Extension extension);
	
	/**
	 * Replace an existing component with an extension
	 * @param extensionComponent the extension
	 */
	public void replaceExistingComponentWithExtension(Component extensionComponent);
	
	/**
	 * Add a tab to the tabbed panel.
	 * @param tab the AbstractTab to add
	 */
	public void addTabToTabbedPanel(AbstractTab tab);
	
	/**
	 * Register a component as replacable by plugins
	 * @param componentId the id of the component
	 */
	public void addReplaceableComponent(String componentId);
	
	/**
	 * Used to register additional extension points
	 * @return a List of extensionPoints for this Panel
	 */
	public Map<String, ExtensionPoint> getExtensionPoints();
}
