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
import org.webical.Calendar;

/**
 * Action generated when a Calendar is selected
 *
 * @author Mattijs Hoitink
 *
 */
public class CalendarSelectedAction implements IAction {
	private static final long serialVersionUID = 1L;

	private Calendar selectedCalendar;
	private AjaxRequestTarget ajaxRequestTarget;

	/**
	 * Constructor
	 * @param calendar The selected Calendar
	 * @param ajaxRequestTarget The Ajax target
	 */
	public CalendarSelectedAction(Calendar calendar, AjaxRequestTarget ajaxRequestTarget) {
		this.selectedCalendar = calendar;
		this.ajaxRequestTarget = ajaxRequestTarget;
	}

	/**
	 * Returns the selected Calendar
	 * @return The selected Calendar
	 */
	public Calendar getSelectedCalendar() {
		return selectedCalendar;
	}

	/**
	 * Returns the AjaxRequestTarget
	 * @return The Ajax target
	 */
	public AjaxRequestTarget getAjaxRequestTarget() {
		return ajaxRequestTarget;
	}

}
