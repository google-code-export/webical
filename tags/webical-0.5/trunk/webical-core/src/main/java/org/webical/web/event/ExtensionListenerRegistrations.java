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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtensionListenerRegistrations {

	private static Map<Class, List<ExtensionListener>> extensionHandlerToExtensionListenersMap = new HashMap<Class, List<ExtensionListener>>();

	public static Map<Class, List<ExtensionListener>> getExtensionHandlerToExtensionListenersMap() {
		return extensionHandlerToExtensionListenersMap;
	}
	
	public static void addExtensionListenerRegistration(Class extensionHandlerImplementingClass, ExtensionListener extensionListener) {
		if(extensionHandlerToExtensionListenersMap.get(extensionHandlerImplementingClass) == null) {
			extensionHandlerToExtensionListenersMap.put(extensionHandlerImplementingClass, new ArrayList<ExtensionListener>());
		}
		
		extensionHandlerToExtensionListenersMap.get(extensionHandlerImplementingClass).add(extensionListener);
	}
	
	public static List<ExtensionListener> getExtensionListenersForExtensionHandlerClass(Class extensionHandlerImplementingClass) {
		List<ExtensionListener> addedListeners = extensionHandlerToExtensionListenersMap.get(extensionHandlerImplementingClass);
		if(addedListeners != null) {
			return addedListeners;
		} else {
			return new ArrayList<ExtensionListener>(); 
		}
		
	}
}
