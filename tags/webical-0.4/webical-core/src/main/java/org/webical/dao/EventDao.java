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

import java.util.Date;
import java.util.List;

import org.webical.Calendar;
import org.webical.Event;

/**
 * 
 * Dao interface for Event
 * @author ivo
 *
 */
public interface EventDao {
	
	/**
	 * Stores an Event
	 * @param event the event to store
	 * @throws DaoException with wrapped exception
	 */
	public void storeEvent(Event event) throws DaoException;
	
	/**
	 * Stores a List of Events
	 * @param events the events to store
	 * @throws DaoException with wrapped exception
	 */
	public void storeEvents(List<Event> events) throws DaoException;

	/**
	 * Removes an Event
	 * @param event the event to remove
	 * @throws DaoException with wrapped exception
	 */
	public void removeEvent(Event event) throws DaoException;
	
	/**
	 * Retrieves all Events for a given Calendar
	 * @param calendar the Calendar to retrieve Events for
	 * @return a List of Events
	 * @throws DaoException with wrapped exception
	 */
	public List<Event> getAllEvents(Calendar calendar) throws DaoException;
	
	/**
	 * Retrieves all Events for a given Calendar within a given period
	 * @param calendar the Calendar to retrieve Events for
	 * @param dtStart the date to start from (inclusive)
	 * @param dtEnd the endate (inclusive)
	 * @return a List of Events
	 * @throws DaoException with wrapped exception
	 */
	public List<Event> getEventsForPeriod(Calendar calendar, Date dtStart, Date dtEnd) throws DaoException;
	
	/**
	 * Retrieves an event for a given Uid
	 * @param calendar the Calendar where the Event has to be found
	 * @param uid the unique identifier for the Event
	 * @return an event or null if nothing found
	 * @throws DaoException with wrapped exception
	 */
	public Event getEventByUid(Calendar calendar, String uid) throws DaoException;
	
}
