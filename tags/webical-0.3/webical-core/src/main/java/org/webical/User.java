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

package org.webical;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.webical.web.component.calendar.CalendarPanel;

/**
 * User
 * @author ivo
 *
 */
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userId;
	private String firstName;
	private String lastNamePrefix;
	private String lastName;
	private Date birthDate;

	private int defaultCalendarView;
	private int firstWeekDay;

	/** Getters and settesr */

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLastNamePrefix() {
		return lastNamePrefix;
	}

	public void setLastNamePrefix(String lastNamePrefix) {
		this.lastNamePrefix = lastNamePrefix;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getDefaultCalendarView() {
		// TODO mattijs: create a settings page where this can be set
		//return defaultCalendarView;
		return CalendarPanel.WEEK_VIEW;
	}

	public void setDefaultCalendarView(int defaultCalendarView) {
		this.defaultCalendarView = defaultCalendarView;
	}

	public int getFirstWeekDay() {
		// TODO mattijs: create a settings page where this can be set
		//return firstWeekDay;
		return Calendar.MONDAY;
	}

	public void setFirstWeekDay(int firstWeekDay) {
		this.firstWeekDay = firstWeekDay;
	}

}