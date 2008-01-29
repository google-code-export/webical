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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.wicket.model.IModel;
import org.webical.util.CalendarUtils;
import org.webical.web.app.WebicalSession;
import org.webical.web.component.calendar.CalendarViewPanel;

/**
 * Model for the DateSwitcherPanel to use.
 * This model returns the new range displayed on the
 * DateSwitcherPanel.
 *
 * @author Mattijs Hoitink
 */
public class DateSwitcherModel implements IModel {
	private static final long serialVersionUID = 1L;

	/**
	 * The current date
	 */
	private GregorianCalendar currentDate;

	/**
	 * The current view panel. This panel holds the information for the
	 * range. This information is use in the getObject() method to return
	 * the correct range text.
	 */
	private CalendarViewPanel currentViewPanel;

	/**
	 * Constructor.
	 * @param currentDate The current date
	 */
	public DateSwitcherModel(Calendar currentDate) {
		if(currentDate != null) {
			this.currentDate = (GregorianCalendar) currentDate;
		} else {
			this.currentDate = new GregorianCalendar();
		}
	}

	/**
	 * Constructor.
	 * @param currentDate The current date
	 * @param currentViewPanel The calendar view panel to use
	 */
	public DateSwitcherModel(Calendar currentDate, CalendarViewPanel currentViewPanel) {
		this(currentDate);
		this.currentViewPanel = currentViewPanel;
	}

	/**
	 * Return a new range text.
	 * @return A range text for the current calendar view panel
	 */
	public Object getObject() {
		String printableRangeText = "range goes here";
		if(currentViewPanel != null) {
			int rangeIdentifier = currentViewPanel.getViewPeriodId();
			int rangeLength = currentViewPanel.getViewPeriodLength();
			String rangeFormat = currentViewPanel.getViewPeriodFormat();

			GregorianCalendar rangeStartCal = new GregorianCalendar();
			rangeStartCal.setTime(currentDate.getTime());

			//DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, getLocale());
			SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
			sdf.applyPattern(rangeFormat);

			printableRangeText = sdf.format(rangeStartCal.getTime());
			if(rangeLength > 1) {
				if(rangeLength == 7) { // This is the fixed length of a week ( 0 -> 6 = 7 days)
					// reset the start calendar to the beginning of the week
					rangeStartCal.setTime(CalendarUtils.getFirstDayOfWeek(currentDate.getTime(), WebicalSession.getWebicalSession().getUserSettings().getFirstDayOfWeek()));
				}

				printableRangeText = sdf.format(rangeStartCal.getTime()) + " - ";
				GregorianCalendar rangeEndCal = new GregorianCalendar();
				rangeEndCal.setTime(rangeStartCal.getTime());
				rangeEndCal.add(rangeIdentifier, rangeLength - 1);
				printableRangeText += sdf.format(rangeEndCal.getTime());
			}
		}
		return printableRangeText;
	}

	/**
	 * Gets the current view panel.
	 * @return The current view panel
	 */
	public CalendarViewPanel getCurrentViewPanel() {
		return this.currentViewPanel;
	}
	
	public Calendar getCurrentDate() {
		return this.currentDate;
	}

	/* (non-Javadoc)
	 * @see org.apache.wicket.model.IModel#setObject(java.lang.Object)
	 */
	public void setObject(Object object) {
		// NOTHING TO DO
	}

	/* (non-Javadoc)
	 * @see org.apache.wicket.model.IDetachable#detach()
	 */
	public void detach() {
		// NOTHING TO DO
	}
}
