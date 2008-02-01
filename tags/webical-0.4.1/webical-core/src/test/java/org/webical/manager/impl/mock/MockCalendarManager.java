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

package org.webical.manager.impl.mock;

import java.util.List;
import java.util.Set;

import org.webical.Calendar;
import org.webical.Event;
import org.webical.User;
import org.webical.manager.CalendarManager;
import org.webical.manager.WebicalException;

public class MockCalendarManager implements CalendarManager {

	public Set<String> getAvailableCalendarTypes() throws WebicalException {
		return null;
	}

	public Calendar getCalendarById(String id) throws WebicalException {
		return null;
	}

	public Calendar getCalendarForEvent(Event event) throws WebicalException {
		return null;
	}

	public List<Calendar> getCalendars(User user) throws WebicalException {
		return null;
	}

	public void removeCalendar(Calendar calendar) throws WebicalException {
		// NOTHING TO DO
	}

	public void storeCalendar(Calendar calendar) throws WebicalException {
		// NOTHING TO DO
	}

	public void storeCalendar(Calendar calendar, List<Event> events) throws WebicalException {
		// NOTHING TO DO
	}

}
