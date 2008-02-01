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


/**
 * Interface for any class that wants to add extensions to a Component
 * @author ivo
 *
 */
public interface ExtensionListener {
	
	/**
	 * Called when its time to setup the extensions
	 * @param extensionEvent an Event with the source ExtensionHandler
	 * 
	 */
	public void addExtensionsBeforeComponentSetup(ExtensionEvent extensionEvent) ;
	
	
	/**
	 * Called when its time to setup the extensions
	 * @param extensionEvent an Event with the source ExtensionHandler
	 * 
	 */
	public void addExtensionsAfterComponentSetup(ExtensionEvent extensionEvent) ;
}
