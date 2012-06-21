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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.webical.web.component.AbstractBasePanel;
import org.webical.web.app.WebicalSession;

/**
 * Class for calendar views to extend so availability of these methods is garantueed
 *
 * @author Mattijs Hoitink
 * @author Harm-Jan Zwinderman, Cebuned
 */
public abstract class CalendarViewPanel extends AbstractBasePanel {
	private static final long serialVersionUID = 1L;

	/**
	 * The identifier for the period this panel covers
	 * This Period must be similar to a static identifier from {@code java.util.Calendar}.
	 */
	private int viewPeriodId = Calendar.DAY_OF_MONTH;
	private int viewPeriodLength = 1;

	/**
	 * The Date range this panel covers
	 */
	private GregorianCalendar currentDate;
	private Date periodStartDate;
	private Date periodEndDate;

	// The date format
	private String viewPeriodFormat;

	/**
	 * Constructor
	 * @param markupId - The ID to use in markup
	 * @param currentDate - The currentDate
	 * @param implementingClass - The implementing class
	 */
	public CalendarViewPanel(String markupId, Calendar currentDate, Class implementingClass) {
		super(markupId, implementingClass);

		this.currentDate = (GregorianCalendar) currentDate;
		this.currentDate.setFirstDayOfWeek(WebicalSession.getWebicalSession().getUserSettings().getFirstDayOfWeek());
		this.periodStartDate = this.currentDate.getTime();
		this.periodEndDate = this.currentDate.getTime();
	}

	/**
	 * Gets the period Identifier for this Calendar View.
	 * @return The ID for the period
	 */
	public int getViewPeriodId() {
		return viewPeriodId;
	}
	/**
	 * Sets the period Identifier for this Calendar View.
	 * @param viewPeriodId - The new viewPeriodId
	 */
	public void setViewPeriodId(int viewPeriodId) {
		this.viewPeriodId = viewPeriodId;
	}

	/**
	 * Gets the length for the period of this Calendar View
	 * @return The period length
	 */
	public int getViewPeriodLength() {
		return viewPeriodLength;
	}
	/**
	 * Sets the length for the period of this Calendar View
	 * @param periodLength - The new periodLength
	 */
	public void setViewPeriodLength(int viewPeriodLength) {
		this.viewPeriodLength = viewPeriodLength;
	}

	/**
	 * Gets the current date for this Calendar View.
	 * @return the View CurrentDate
	 */
	public GregorianCalendar getViewCurrentDate() {
		return currentDate;
	}
	/**
	 * Sets the current date for this Calendar View.
	 * @param currentDate - The new currentDate
	 */
	public void setViewCurrentDate(GregorianCalendar currentDate) {
		// Update the time in the object, don't change the reference or else changes are not reflected in the models
		this.currentDate.setTime(currentDate.getTime());
	}

	/**
	 * Gets the start date for this Calendar View.
	 * @return the View StartDate
	 */
	public Date getPeriodStartDate() {
		return periodStartDate;
	}
	/**
	 * Sets the start date for this Calendar View.
	 * @param periodStartDate - The new View StartDate
	 */
	public void setPeriodStartDate(Date periodStartDate) {
		this.periodStartDate.setTime(periodStartDate.getTime());
	}

	/**
	 * Gets the end date for this Calendar View.
	 * @return the View EndDate
	 */
	public Date getPeriodEndDate() {
		return periodEndDate;
	}
	/**
	 * Sets the end date for this Calendar View.
	 * @param periodEndDate - The new View EndDate
	 */
	public void setPeriodEndDate(Date periodEndDate) {
		this.periodEndDate.setTime(periodEndDate.getTime());
	}

	/**
	 * Gets the display format for the period of this Calendar View
	 * @return The period display format
	 */
	public String getViewPeriodFormat() {
		return viewPeriodFormat;
	}
	/**
	 * Sets the display format for the period of this Calendar View
	 * @param viewPeriodFormat - The new View Display Format
	 */
	public void setViewPeriodFormat(String viewPeriodFormat) {
		this.viewPeriodFormat = viewPeriodFormat;
	}
}
