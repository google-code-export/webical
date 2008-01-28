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

package org.webical.web.component.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.wicket.markup.repeater.RepeatingView;
import org.webical.util.CalendarUtils;
import org.webical.web.action.IAction;

/**
 * Repeater to repeat the calendar rows.
 *
 * @author Mattijs Hoitink
 *
 */
public abstract class DatePickerRowRepeater extends RepeatingView {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	protected static Class[] PANELACTIONS = new Class[] { };

	private static String DATE_PICKER_COLUMN_REPEATER_MARKUP_ID = "datePickerColumnRepeater";

	private GregorianCalendar currentDate;

	// The date range
	private Date startDate;
	private Date endDate;

	private double numberOfWeeks = 5;

	public DatePickerRowRepeater(String id, Calendar currentDate) {
		super(id);
		this.currentDate = (GregorianCalendar) currentDate;

		addRows();
	}

	protected void addRows() {
		startDate = CalendarUtils.getFirstDayOfWeekOfMonth(currentDate.getTime(), Calendar.MONDAY);
		endDate = CalendarUtils.getLastWeekDayOfMonth(currentDate.getTime(), Calendar.MONDAY);

		double diff = CalendarUtils.getDifferenceInDays(startDate, endDate);
		this.numberOfWeeks = Math.ceil(diff / 7.0);

		// Calendar used to hold on to the week
		GregorianCalendar weekCalendar = new GregorianCalendar();
		weekCalendar.setTime(startDate);

		for(int i = 0; i < numberOfWeeks; i++) {

			DatePickerRowPanel rowPanel = new DatePickerRowPanel("week" + weekCalendar.get(Calendar.WEEK_OF_YEAR), weekCalendar, this.currentDate) {
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