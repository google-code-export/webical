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

}
