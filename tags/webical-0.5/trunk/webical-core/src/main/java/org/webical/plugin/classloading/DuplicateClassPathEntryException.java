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

package org.webical.plugin.classloading;

/**
 * Thrown when a duplicate entry is detected in the classloader
 * @author ivo
 *
 */
public class DuplicateClassPathEntryException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DuplicateClassPathEntryException() { }

	/**
	 * @param message
	 * @param cause
	 */
	public DuplicateClassPathEntryException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public DuplicateClassPathEntryException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DuplicateClassPathEntryException(Throwable cause) {
		super(cause);
	}
	
}
