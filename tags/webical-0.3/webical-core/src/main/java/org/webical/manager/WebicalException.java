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

package org.webical.manager;

/**
 * 
 * Common exception thrown by the managers
 * @author paul
 *
 */
public class WebicalException extends Exception {
	private static final long serialVersionUID = -2953739883342320279L;

	public WebicalException(){}
	
	public WebicalException(String message){
		super(message);
	}
	
	public WebicalException(String message,Throwable throwable){
		super(message, throwable);
	}
	
	public WebicalException(Throwable throwable){
		super(throwable);
	}
}
