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
import java.util.GregorianCalendar;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.webical.util.CalendarUtils;
import org.webical.web.action.IAction;

/**
 * Repeater to repeat the calendar columns
 *
 * @author Mattijs Hoitink
 *
 */
public abstract class DatePickerColumnRepeater extends RepeatingView {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	protected static Class[] PANELACTIONS = new Class[] { };

	/**
	 * CSS Class name for current day
	 */
	private static final String TODAY_CSS_CLASS = "calendarTodayItem";

	private GregorianCalendar currentDate;
	private GregorianCalendar weekCalendar;

	public DatePickerColumnRepeater(String id, Calendar weekCalendar, Calendar currentDate) {
		super(id);

		this.currentDate = new GregorianCalendar();
		this.currentDate.setTime(currentDate.getTime());
		this.weekCalendar = new GregorianCalendar();
		this.weekCalendar.setTime(weekCalendar.getTime());

		addColumns();
	}

	private void addColumns() {
		GregorianCalendar todayDate = new GregorianCalendar();

		for(int i = 0; i < 7; i++) {

			DatePickerColumnPanel columnPanel = new DatePickerColumnPanel("day" + weekCalendar.get(Calendar.DAY_OF_YEAR), this.weekCalendar, this.currentDate) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onAction(IAction action) {
					DatePickerColumnRepeater.this.onAction(action);
				}

			};
			if(CalendarUtils.getStartOfDay(weekCalendar.getTime()).equals(CalendarUtils.getStartOfDay(todayDate.getTime()))) {
				columnPanel.add(new AttributeAppender("class", true, new Model(TODAY_CSS_CLASS), " "));
			}
			addOrReplace(columnPanel);

			// add a day to the week calendar
			this.weekCalendar.add(Calendar.DAY_OF_WEEK, 1);
			//weekCalendar.setTime(CalendarUtils.addDays(weekCalendar.getTime(), 1));
		}
	}

	/**
	 * Handle actions generated by this panel.
	 *
	 * @param action The action to handle
	 */
	public abstract void onAction(IAction action);

}