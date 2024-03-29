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

package org.webical.web.component.calendar;

import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.wicket.markup.repeater.RepeatingView;
import org.webical.util.CalendarUtils;
import org.webical.web.action.IAction;
import org.webical.web.component.calendar.model.DatePickerModel;

/**
 * Repeater to repeat the calendar rows.
 *
 * @author Mattijs Hoitink
 *
 */
public abstract class DatePickerRowRepeater extends RepeatingView {
	private static final long serialVersionUID = 1L;

	protected static Class[] PANELACTIONS = new Class[] { };

	private DatePickerModel datePickerModel;

	// The date range
	private Date startDate;
	private Date endDate;

	private int numberOfWeeks = 5;

	public DatePickerRowRepeater(String id, DatePickerModel datePickerModel) {
		super(id);
		this.datePickerModel = datePickerModel;

		addRows();
	}

	protected void addRows() {
		int fdow = datePickerModel.getFirstDayOfWeek();
		startDate = CalendarUtils.getFirstDayOfWeekOfMonth(datePickerModel.getCurrentDate().getTime(), fdow);
		endDate = CalendarUtils.getLastWeekDayOfMonth(datePickerModel.getCurrentDate().getTime(), fdow);

		int diff = CalendarUtils.getDifferenceInDays(startDate, endDate);
		this.numberOfWeeks = (diff + 6) / 7;

		// Calendar used to hold on to the week
		GregorianCalendar weekCalendar = CalendarUtils.newTodayCalendar(fdow);
		weekCalendar.setTime(startDate);

		for (int i = 0; i < numberOfWeeks; ++ i)
		{
			DatePickerRowPanel rowPanel = new DatePickerRowPanel("week" + weekCalendar.get(GregorianCalendar.WEEK_OF_YEAR), weekCalendar, datePickerModel) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onAction(IAction action) {
					DatePickerRowRepeater.this.onAction(action);
				}
			};
			addOrReplace(rowPanel);

			// Add one week to the week calendar
			weekCalendar.add(GregorianCalendar.WEEK_OF_YEAR, 1);
		}
	}

	/**
	 * Renders the week rows for the new date range
	 * @see org.apache.wicket.Component#onModelChanged()
	 */
	@Override
	protected void onModelChanged() {
		// Remove the old week rows
		this.removeAll();
		// Render the new week rows
		addRows();
	}

	/**
	 * Handle actions generated by this panel.
	 *
	 * @param action The action to handle
	 */
	public abstract void onAction(IAction action);
}
