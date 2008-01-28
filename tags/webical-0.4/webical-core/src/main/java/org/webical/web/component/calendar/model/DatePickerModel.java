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

package org.webical.web.component.calendar.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.webical.util.CalendarUtils;
import org.webical.web.app.WebicalSession;
import org.webical.web.component.calendar.CalendarViewPanel;

/**
 * Model for the DatePickerPanel. This model contains the date for the DatePickerPanel
 * and the data for the form field.
 *
 * @author Mattijs Hoitink
 */
public class DatePickerModel implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The current date
	 */
	private GregorianCalendar currentDate;
	
	@SuppressWarnings("unused")
	private GregorianCalendar currentMonth;
	
	private Date rangeStartDate, rangeEndDate;
	

	/**
	 * Constructor.
	 * @param currentDate The current date
	 */
	public DatePickerModel(Calendar currentDate) {
		if(currentDate != null) {
			this.currentDate = (GregorianCalendar) currentDate;
		} else {
			this.currentDate = new GregorianCalendar();
		}
	}
	
	public DatePickerModel(Calendar currentDate, CalendarViewPanel viewPanel) {
		this(currentDate);
		setRange(viewPanel);
	}

	/**
	 * Gets the current date for the changeDateField.
	 * @return The current date
	 */
	public Date getChangeDateField() {
		return currentDate.getTime();
	}

	/**
	 * Sets the current date from the currentDateField.
	 * @param newCurrentDate The current date
	 */
	public void setChangeDateField(Date newCurrentDate) {
		this.currentDate.setTime(newCurrentDate);
	}

	/**
	 * Returns the current date object
	 * @return The current date
	 */
	public Calendar getCurrentDate() {
		return this.currentDate;
	}

	/**
	 * Sets the range start and end date based on the selected CalendarViewPanel 
	 * @param calendarView The Calendar View
	 */
	public void setRange(CalendarViewPanel calendarView) {
		// Declare the range start and end calendar
		GregorianCalendar rangeStartCal = new GregorianCalendar();
		GregorianCalendar rangeEndCal = new GregorianCalendar();
		
		if(calendarView != null) {
			int rangeIdentifier = calendarView.getViewPeriodId();
			int rangeLength = calendarView.getViewPeriodLength();

			rangeStartCal.setTime(currentDate.getTime());

			if(rangeIdentifier == Calendar.DAY_OF_WEEK) {
				if(rangeLength == 7) { // This is the fixed length of a week ( 0 -> 6 = 7 days)
					// reset the start calendar to the beginning of the week
					rangeStartCal.setTime(CalendarUtils.getFirstDayOfWeek(currentDate.getTime(), WebicalSession.getWebicalSession().getUserSettings().getFirstDayOfWeek()));
				} 
				
				rangeEndCal.setTime(rangeStartCal.getTime());
				rangeEndCal.add(rangeIdentifier, rangeLength - 1);
				
			} else if(rangeIdentifier == Calendar.DAY_OF_MONTH) {
				// we are viewing a single day
				rangeEndCal.setTime(rangeStartCal.getTime());
			} else if (rangeIdentifier == Calendar.MONTH) {
				// reset the range start calendar to the beginning of the month
				rangeStartCal.setTime(CalendarUtils.getFirstDayOfWeekOfMonth(currentDate.getTime(), WebicalSession.getWebicalSession().getUserSettings().getFirstDayOfWeek()));
				// set the range end calendar to the end of the month
				rangeEndCal.setTime(CalendarUtils.getLastWeekDayOfMonth(currentDate.getTime(), WebicalSession.getWebicalSession().getUserSettings().getFirstDayOfWeek()));
			}
		}
		
		setRangeStartDate(rangeStartCal.getTime());
		setRangeEndDate(rangeEndCal.getTime());
	}
	
	public Date getRangeStartDate() {
		return rangeStartDate;
	}

	public void setRangeStartDate(Date rangeStartDate) {
		this.rangeStartDate = rangeStartDate;
	}

	public Date getRangeEndDate() {
		return rangeEndDate;
	}

	public void setRangeEndDate(Date rangeEndDate) {
		this.rangeEndDate = rangeEndDate;
	}
	
}
