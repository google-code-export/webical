/*
 *    Webical - http://www.webical.org
 *    Copyright (C) 2007 Func. Internet Integration
 *
 *    This file is part of Webical.
 *
 *    $Id$
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

import java.util.GregorianCalendar;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Action for selecting a day.
 *
 * @author Mattijs Hoitink
 */
public class DaySelectedAction implements IAction {
	private static final long serialVersionUID = 1L;

	/**
	 * The date of the selected day.
	 */
	private GregorianCalendar daySelected;

	/**
	 * Constructor.
	 * @param daySelected The date of the selected day
	 */
	public DaySelectedAction(GregorianCalendar daySelected) {
		this.daySelected = daySelected;
	}

	/**
	 * Gets the date for the selected day.
	 * @return The date for the selected day
	 */
	public GregorianCalendar getDaySelected() {
		return daySelected;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DaySelectedAction: daySelected = " + daySelected;
	}

	public AjaxRequestTarget getAjaxRequestTarget() {
		return null;
	}
}
