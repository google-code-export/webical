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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.webical.web.component.AbstractBasePanel;

/**
 * Class for calendar views to extend so availability of these methods is garantued
 *
 * @author Mattijs Hoitink
 */
public abstract class CalendarViewPanel extends AbstractBasePanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * @param markupId The ID to use in markup
	 * @param implementingClass The implementing class
	 */
	@SuppressWarnings("unchecked")
	public CalendarViewPanel(String markupId, Class implementingClass) {
		super(markupId, implementingClass);
	}

	/**
	 * Gets the period Identifier for this Calendar View. This Period must be similar to a static indentifier from {@code java.util.Calendar}.
	 * @return The ID for the period
	 */
	public int getViewPeriodId() {
		return 0;
	}

	/**
	 * Gets the length for the period of this Calendar View
	 * @return The period length
	 */
	public int getViewPeriodLength() {
		return 0;
	}

	/**
	 * Gets the display format for the period of this Calendar View
	 * @return The period display format
	 */
	public String getViewPeriodFormat() {
		SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, getLocale());
		return dateFormat.toLocalizedPattern();
	}

}
