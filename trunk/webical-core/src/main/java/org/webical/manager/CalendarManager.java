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

package org.webical.manager;

import java.util.List;
import java.util.Set;

import org.webical.Calendar;
import org.webical.Event;
import org.webical.User;

/**
 * @author paul
 *
 */
public interface CalendarManager {

	/**
	 * Retrieves the calendar for a given event
	 * @param event the event to retrieve the calendar for
	 * @return the calendar
	 * @throws WebicalException
	 */
	public Calendar getCalendarForEvent(Event event) throws WebicalException;

	/**
	 * Stores or updates a Calendar
	 * @param calendar the calendar to store
	 * @throws WebicalException
	 */
	public void storeCalendar(Calendar calendar) throws WebicalException;

	/**
	 * TODO remove this. This has no place here, but belongs in the dao
	 * Updates a Calendar and writes it to a remote ics file
	 * @param calendar the calendar to store
	 * @param events the events for this calendar
	 * @throws WebicalException
	 */
	public void storeCalendar(Calendar calendar, List<Event> events) throws WebicalException;
	/**
	 * Removes a Calendar
	 * @param calendar the calendar to remove
	 * @throws WebicalException
	 */
	public void removeCalendar(Calendar calendar)throws WebicalException;

	/**
	 * Retrieves all Calendars for a given User
	 * @param user the user to retrieve the calendars for
	 * @return a List of calendars
	 * @throws WebicalException
	 */
	public List<Calendar> getCalendars(User user)throws WebicalException;

	/**
	 * Looks up the different kind of calendars registered in the system
	 * @return a Set of calendar type Strings
	 * @throws WebicalException
	 */
	public Set<String> getAvailableCalendarTypes() throws WebicalException;

	/**
	 * Retrieves a calendar for a given id
	 * @param id the unique identifier for the calendar
	 * @return a calendar or null if nothing found
	 * @throws WebicalException
	 */
	public Calendar getCalendarById(String id) throws WebicalException;

	/**
	 * Refresh a calendar record from storage
	 * @param calendar - calendar record to refresh
	 * @throws WebicalException
	 */
	public void refreshCalendar(Calendar calendar) throws WebicalException;
}
