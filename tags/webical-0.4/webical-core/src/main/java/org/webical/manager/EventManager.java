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

package org.webical.manager;

import java.util.Date;
import java.util.List;

import org.webical.Calendar;
import org.webical.Event;
import org.webical.User;

/**
 * 
 * @author jochem
 *
 */

public interface EventManager {

	/**
	 * Stores an Event
	 * @param event the event to store
	 * @throws WebicalException
	 */
	public void storeEvent(Event event) throws WebicalException;

	/**
	 * Removes an Event
	 * @param event the event to remove
	 * @throws WebicalException
	 */
	public void removeEvent(Event event) throws WebicalException;
	
	/**
	 * Retrieves all Events for a given Calendar
	 * @param calendar the Calendar to retrieve Events for
	 * @return a List of Events
	 * @throws WebicalException
	 */
	public List<Event> getAllEvents(Calendar calendar) throws WebicalException;
	
	/**
	 * Retrieves all Events for a given Calendar within a given period
	 * @param calendar the Calendar to retrieve Events for
	 * @param dtStart the date to start from (inclusive)
	 * @param dtEnd the endate (inclusive)
	 * @return a List of Events
	 * @throws WebicalException
	 */
	public List<Event> getEventsForPeriod(Calendar calendar, Date dtStart, Date dtEnd) throws WebicalException;
	
	/**
	 * Retrieves all Events for a given User within a given period
	 * @param user the User to retrieve Events for
	 * @param dtStart the date to start from (inclusive)
	 * @param dtEnd the endate (inclusive)
	 * @return a List of Events
	 * @throws WebicalException
	 */
	public List<Event> getEventsForPeriod(User user, Date dtStart, Date dtEnd) throws WebicalException;
	
	/**
	 * Retrieves an event for a given Uid
	 * @param uid the unique identifier for the Event
	 * @param calendar the Calendar where the Event has to be found
	 * @return an event or null if nothing found
	 * @throws WebicalException
	 */
	public Event getEventByUid(String Uid, Calendar calendar) throws WebicalException;
}
