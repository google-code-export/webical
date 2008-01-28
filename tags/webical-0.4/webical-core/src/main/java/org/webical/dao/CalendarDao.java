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

package org.webical.dao;

import java.util.List;

import org.webical.Calendar;
import org.webical.Event;
import org.webical.User;

/**
 * CalendarDao
 * @author jochem
 * 
 */


public interface CalendarDao extends EncryptingDao {
	
	/**
	 * Retrieves the calendar for a given event
	 * @param event the event to retrieve the calendar for
	 * @return the calendar
	 * @throws DaoException
	 */
	public Calendar getCalendarForEvent(Event event) throws DaoException;
	
	/**
	 * Stores or updates a Calendar
	 * @param calendar the calendar to store. User may not be null
	 * @throws DaoException
	 */
	public void storeCalendar(Calendar calendar) throws DaoException;
	
	/**
	 *Updates a Calendar and writes it a remote ics file
	 * @param calendar the calendar to store. User may not be null
	 * @param events the events for the calendar
	 * @throws DaoException
	 */
	public void storeCalendar(Calendar calendar, List<Event> events) throws DaoException;
	
	/**
	 * Removes a Calendar
	 * @param calendar the calendar to remove
	 * @throws DaoException
	 */
	public void removeCalendar(Calendar calendar) throws DaoException;
	
	/**
	 * Retrieves all Calendars for a given User
	 * @param user the user to retrieve the calendars for
	 * @return a List of calendars 
	 * @throws DaoException
	 */
	public List<Calendar> getCalendars(User user) throws DaoException;
	
	/**
	 * Retrieve a calendar by its unique id
	 * @param id the unique identifier
	 * @return a calendar or null if nothing found
	 * @throws DaoException
	 */
	public Calendar getCalendarById(String id) throws DaoException;
}
