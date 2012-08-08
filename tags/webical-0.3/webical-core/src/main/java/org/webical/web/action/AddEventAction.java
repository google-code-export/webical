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

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Action for adding an Event
 *
 * @author Mattijs Hoitink
 *
 */
public class AddEventAction implements IAction {
	private static final long serialVersionUID = 1L;

	/**
	 * The date of the Event
	 */
	private GregorianCalendar eventDate;

	/**
	 * Constructor.
	 * @param eventDate The date of the Event
	 */
	public AddEventAction(Calendar eventDate) {
		this.eventDate = (GregorianCalendar) eventDate;
	}

	/**
	 * Gets the date of the Event.
	 * @return The date of the Event
	 */
	public GregorianCalendar getEventDate() {
		return eventDate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AddEventAction: eventDate = " + eventDate;
	}

	public AjaxRequestTarget getAjaxRequestTarget() {
		return null;
	}

}