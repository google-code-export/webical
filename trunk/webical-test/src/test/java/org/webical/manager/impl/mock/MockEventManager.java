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

import java.util.Date;
import java.util.List;

import org.webical.Calendar;
import org.webical.Event;
import org.webical.User;
import org.webical.manager.EventManager;
import org.webical.manager.WebicalException;

public class MockEventManager implements EventManager {

	public List<Event> getAllEvents(Calendar calendar) throws WebicalException {
		return null;
	}

	public Event getEventByUid(String Uid, Calendar calendar) throws WebicalException {
		return null;
	}

	public List<Event> getEventsForPeriod(Calendar calendar, Date dtStart, Date dtEnd) throws WebicalException {
		return null;
	}

	public List<Event> getEventsForPeriod(User user, Date dtStart, Date dtEnd) throws WebicalException {
		return null;
	}

	public void removeEvent(Event event) throws WebicalException {
		// NOTHING TO DO
	}

	public void storeEvent(Event event) throws WebicalException {
		// NOTHING TO DO
	}

}
