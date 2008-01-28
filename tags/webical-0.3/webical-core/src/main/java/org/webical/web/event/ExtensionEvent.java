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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * The event send to the ExtensionListeners
 * @author ivo
 *
 */
public class ExtensionEvent implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private List<String> replaceableComponents;
	private Map<String, ExtensionPoint> extensionPoints;
	private ExtensionHandler source;
	private List additionalResources;

	/**
	 * Constructor
	 * @param source the Source ExtensionHandler
	 * @param replaceableComponents a List of identifiers from Components that are replaceable for the source Component 
	 * @param additionalResources A List of arbitrary resources (Listeners for example)
	 * @param extensionPoints a Map of available ExtensionPoints for this Panel
	 */
	public ExtensionEvent(ExtensionHandler source, List<String> replaceableComponents, List additionalResources, Map<String, ExtensionPoint> extensionPoints) {
		this.replaceableComponents  = replaceableComponents;
		this.source = source;
		this.additionalResources = additionalResources;
		this.extensionPoints = extensionPoints;
	}

	public List<String> getReplaceableComponents() {
		return replaceableComponents;
	}

	public void setReplaceableComponents(List<String> replaceableComponents) {
		this.replaceableComponents = replaceableComponents;
	}

	public ExtensionHandler getSource() {
		return source;
	}

	public void setSource(ExtensionHandler source) {
		this.source = source;
	}

	public List getAdditionalResources() {
		return additionalResources;
	}

	public void setAdditionalResources(List additionalResources) {
		this.additionalResources = additionalResources;
	}

	public Map<String, ExtensionPoint> getExtensionPoints() {
		return extensionPoints;
	}

	public void setExtensionPoints(Map<String, ExtensionPoint> extensionPoints) {
		this.extensionPoints = extensionPoints;
	}

}
