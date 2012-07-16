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

package org.webical.web.component.calendar.model;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.apache.wicket.model.IModel;
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
	 * The current view panel. This panel holds the information for the range.
	 * This information is use in the getObject() method to return the correct range text.
	 */
	private CalendarViewPanel currentViewPanel = null;

	/**
	 * Constructor.
	 * @param currentDate The current date
	 */
	public DateSwitcherModel(GregorianCalendar currentDate) {
		if (currentDate == null) {
			this.currentDate = new GregorianCalendar();
		} else {
			this.currentDate = currentDate;
		}
	}

	/**
	 * Constructor.
	 * @param currentDate The current date
	 * @param currentViewPanel The calendar view panel to use
	 */
	public DateSwitcherModel(GregorianCalendar currentDate, CalendarViewPanel currentViewPanel) {
		this(currentDate);
		this.currentViewPanel = currentViewPanel;
	}

	public GregorianCalendar getCurrentDate() {
		return this.currentDate;
	}

	/**
	 * Gets the current view panel.
	 * @return The current view panel
	 */
	public CalendarViewPanel getCurrentViewPanel() {
		return this.currentViewPanel;
	}

	/**
	 * Return a new range text.
	 * @return A range text for the current calendar view panel
	 */
	public Object getObject() {

		if (getCurrentViewPanel() == null) return "range";

		int rangeLength = currentViewPanel.getViewPeriodLength();
		String rangeFormat = getCurrentViewPanel().getViewPeriodFormat();

		SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
		sdf.applyPattern(rangeFormat);

		String printableRangeText;
		if (rangeLength > 1) {
			GregorianCalendar rangeStartCal = new GregorianCalendar();
			rangeStartCal.setTime(getCurrentViewPanel().getPeriodStartDate());

			GregorianCalendar rangeEndCal = new GregorianCalendar();
			rangeEndCal.setTime(getCurrentViewPanel().getPeriodEndDate());

			printableRangeText = sdf.format(rangeStartCal.getTime());
			printableRangeText += " - " + sdf.format(rangeEndCal.getTime());
		} else {
			printableRangeText = sdf.format(getCurrentDate().getTime());
		}
		return printableRangeText;
	}

	/* (non-Javadoc)
	 * @see org.apache.wicket.model.IModel#setObject(java.lang.Object)
	 */
	public void setObject(Object object) {
		// For IModel
	}

	/* (non-Javadoc)
	 * @see org.apache.wicket.model.IDetachable#detach()
	 */
	public void detach() {
		// For IModel
	}
}
