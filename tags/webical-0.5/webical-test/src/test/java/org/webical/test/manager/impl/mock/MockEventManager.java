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

package org.webical.test.manager.impl.mock;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Iterator;

import org.webical.User;
import org.webical.Calendar;
import org.webical.Event;
import org.webical.manager.EventManager;
import org.webical.manager.WebicalException;

public class MockEventManager implements EventManager {

	public List<Event> getAllEvents(Calendar calendar) throws WebicalException {
		List<Event> events = new ArrayList<Event>();
		Iterator<Entry<String, Event>> evtIt = m_eventMap.entrySet().iterator();
		while (evtIt.hasNext())
		{
			Entry<String, Event> entry = evtIt.next();
			if (entry.getValue().getCalendar() != null  &&
				entry.getValue().getCalendar().getName().equals(calendar.getName())) events.add(entry.getValue());
		}
		return events;
	}

	public Event getEventByUid(String Uid, Calendar calendar) throws WebicalException {
		if (m_eventMap.containsKey(Uid)) return m_eventMap.get(Uid);
		return null;
	}

	public List<Event> getEventsForPeriod(Calendar calendar, Date dtStart, Date dtEnd) throws WebicalException {
		List<Event> events = new ArrayList<Event>();
		Iterator<Entry<String, Event>> evtIt = m_eventMap.entrySet().iterator();
		while (evtIt.hasNext())
		{
			Entry<String, Event> entry = evtIt.next();
			if (entry.getValue().getCalendar() != null  &&
				entry.getValue().getCalendar().getName().equals(calendar.getName()))
			{
				if (entry.getValue().getDtEnd().after(dtStart) &&
					entry.getValue().getDtStart().before(dtEnd) ) events.add(entry.getValue());
			}
		}
		return events;
	}

	public List<Event> getEventsForPeriod(User user, Date dtStart, Date dtEnd) throws WebicalException {
		List<Event> events = new ArrayList<Event>();
		Iterator<Entry<String, Event>> evtIt = m_eventMap.entrySet().iterator();
		while (evtIt.hasNext())
		{
			Entry<String, Event> entry = evtIt.next();
			if (entry.getValue().getCalendar() != null  &&
				entry.getValue().getCalendar().getUser() != null  &&
				entry.getValue().getCalendar().getUser().getUserId().equals(user.getUserId()))
			{
				if (entry.getValue().getDtEnd().after(dtStart) &&
					entry.getValue().getDtStart().before(dtEnd) ) events.add(entry.getValue());
			}
		}
		return events;
	}

	public void removeEvent(Event event) throws WebicalException {
		String wuid = event.getUid();
		if (m_eventMap.containsKey(wuid)) m_eventMap.remove(wuid);
	}

	public void storeEvent(Event event) throws WebicalException {
		Calendar calendar = event.getCalendar();
		if (calendar != null)
		{
			Long idl = calendar.getCalendarId();
			if (idl == null)
			{
				idl = m_maxId.longValue() + 1L;
				calendar.setCalendarId(idl);
			}
			if (idl.compareTo(m_maxId) > 0) m_maxId = idl;
		}

		String wuid = event.getUid();
		if (wuid == null)
		{
			m_maxId = m_maxId.longValue() + 1L;
			wuid = m_maxId.toString();
			event.setUid(wuid);
		}
		else
		{
			if (m_eventMap.containsKey(wuid)) m_eventMap.remove(wuid);
		}
		m_eventMap.put(wuid, event);
	}

	private Long m_maxId = new Long(0L);
	private HashMap<String, Event> m_eventMap = new HashMap<String, Event>();
}

