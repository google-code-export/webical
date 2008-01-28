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
import org.webical.Event;

/**
 * Action for selecting an Event.
 *
 * @author Mattijs Hoitink
 */
public class EventSelectedAction implements IAction {
	private static final long serialVersionUID = 1L;

	/**
	 * The selected Event.
	 */
	private Event selectedEvent;

	/**
	 * The date of the selected Event.
	 */
	private GregorianCalendar eventDate;
	
	/**
	 * Constructor.
	 * @param selectedEvent The Event selected
	 * @param selectedEventDate The date the selected event is on
	 */
	public EventSelectedAction(Event selectedEvent, Calendar selectedEventDate) {
		this.selectedEvent = selectedEvent;
		this.eventDate = (GregorianCalendar) selectedEventDate;
	}

	/**
	 * Gets the date the selected Event is on.
	 * @return The date the selected Event is on
	 */
	public Calendar getSelectedEventDate() {
		return eventDate;
	}

	/**
	 * Gets the selected Event.
	 * @return The selected Event
	 */
	public Event getSelectedEvent() {
		return selectedEvent;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EventSelectedAction: selectedEvent = " + selectedEvent + ", eventDate = " + eventDate;
	}

	public AjaxRequestTarget getAjaxRequestTarget() {
		return null;
	}

}
