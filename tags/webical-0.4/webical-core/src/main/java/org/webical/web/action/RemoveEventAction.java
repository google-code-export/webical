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

package org.webical.web.action;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.webical.Event;

public class RemoveEventAction implements IAction {
	private static final long serialVersionUID = 1L;
	
	private Event eventToRemove;
	
	public RemoveEventAction(Event eventToRemove) {
		this.eventToRemove = eventToRemove;
	}
	
	public AjaxRequestTarget getAjaxRequestTarget() {
		return null;
	}

	public Event getEventToRemove() {
		return eventToRemove;
	}

	public void setEventToRemove(Event eventToRemove) {
		this.eventToRemove = eventToRemove;
	}

}