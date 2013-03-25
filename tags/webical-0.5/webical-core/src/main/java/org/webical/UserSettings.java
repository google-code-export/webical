/*
 *    Webical - http://code.google.com/p/webical/
 *    Copyright (C) 2012 by Cebuned
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

package org.webical;

import org.webical.manager.WebicalException;

public class UserSettings extends Settings {
	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_DEFAULT_CALENDAR_VIEW = 1;
	private static final int DEFAULT_FIRST_DAY_OF_WEEK = 1;
	private static final int DEFAULT_NUMBER_OF_AGENDA_DAYS = 4;

	private User user = null;
	private Integer defaultCalendarView, firstDayOfWeek, numberOfAgendaDays;
	private String dateFormat, timeFormat;

	public UserSettings() { }

	public UserSettings(User user) throws WebicalException {
		if(user == null) {
			throw new WebicalException("User cannot be null.");
		}
		this.user = user;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	public Integer getDefaultCalendarView() {
		return defaultCalendarView;
	}
	public void setDefaultCalendarView(Integer defaultCalendarView) {
		this.defaultCalendarView = defaultCalendarView;
	}

	public Integer getFirstDayOfWeek() {
		return firstDayOfWeek;
	}
	public void setFirstDayOfWeek(Integer firstDayOfWeek) {
		this.firstDayOfWeek = firstDayOfWeek;
	}

	public Integer getNumberOfAgendaDays() {
		return numberOfAgendaDays;
	}
	public void setNumberOfAgendaDays(Integer numberOfAgendaDays) {
		this.numberOfAgendaDays = numberOfAgendaDays;
	}

	public String getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getTimeFormat() {
		return timeFormat;
	}
	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

	// TODO mattijs: let default user settings depend on locale
	public void createDefaultSettings() {
		this.defaultCalendarView = DEFAULT_DEFAULT_CALENDAR_VIEW;
		this.firstDayOfWeek = DEFAULT_FIRST_DAY_OF_WEEK;
		this.numberOfAgendaDays = DEFAULT_NUMBER_OF_AGENDA_DAYS;
		this.dateFormat = "dd/MM/yyyy";
		this.timeFormat = "HH:mm";
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());
		sb.append(" User:" + getUser().getUserId());
		sb.append(" defaultCalendarView:" + getDefaultCalendarView());
		sb.append(" firstDayOfWeek:" + getFirstDayOfWeek());
		sb.append(" dateFormat:" + getDateFormat());
		sb.append(" timeFormat:" + getTimeFormat());
		return sb.toString();
	}
}
