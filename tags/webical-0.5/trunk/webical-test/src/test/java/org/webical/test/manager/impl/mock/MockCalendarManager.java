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

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Iterator;

import org.webical.Calendar;
import org.webical.Event;
import org.webical.User;
import org.webical.manager.CalendarManager;
import org.webical.manager.WebicalException;

public class MockCalendarManager implements CalendarManager {

	public Set<String> getAvailableCalendarTypes() throws WebicalException
	{
		Set<String> set = new HashSet<String>();
		set.add("ical-webdav");
		return set;
	}

	public Calendar getCalendarById(String id) throws WebicalException
	{
		Long idl = Long.valueOf(id);
		if (m_calendarMap.containsKey(idl)) return m_calendarMap.get(idl);
		return null;
	}

	public Calendar getCalendarForEvent(Event event) throws WebicalException
	{
		return event.getCalendar();
	}

	public List<Calendar> getCalendars(User user) throws WebicalException
	{
		List<Calendar> calendars = new ArrayList<Calendar>();
		Iterator<Entry<Long, Calendar>> calIt = m_calendarMap.entrySet().iterator();
		while (calIt.hasNext())
		{
			Entry<Long, Calendar> entry = calIt.next();
			Calendar cal = entry.getValue();
			if (cal.getUser().getUserId().equals(user.getUserId())) calendars.add(cal);
		}
		return calendars;
	}

	public void removeCalendar(Calendar calendar) throws WebicalException
	{
		Long idl = calendar.getCalendarId();
		if (m_calendarMap.containsKey(idl)) m_calendarMap.remove(idl);
	}

	public void storeCalendar(Calendar calendar) throws WebicalException
	{
		Long idl = calendar.getCalendarId();
		if (idl == null)
		{
			idl = m_maxId.longValue() + 1L;
			calendar.setCalendarId(idl);
		}
		else if (m_calendarMap.containsKey(idl))
		{
			 m_calendarMap.remove(idl);
		}
		m_calendarMap.put(idl, calendar);
		if (idl.compareTo(m_maxId) > 0) m_maxId = idl;
	}

	public void storeCalendar(Calendar calendar, List<Event> events) throws WebicalException
	{
		storeCalendar(calendar);
	}

	public void refreshCalendar(Calendar calendar) throws WebicalException
	{
		Long idl = calendar.getCalendarId();
		if (! m_calendarMap.containsKey(idl))
		{
			throw new WebicalException("Calendar not found");
		}
		Calendar mapCalendar = m_calendarMap.get(idl);
		// copy the calendar data
		calendar.setCalendarId(mapCalendar.getCalendarId());
		calendar.setName(mapCalendar.getName());
		calendar.setType(mapCalendar.getType());
		calendar.setUrl(mapCalendar.getUrl());
		calendar.setUsername(mapCalendar.getUsername());
		calendar.setPassword(mapCalendar.getPassword());
		calendar.setReadOnly(mapCalendar.getReadOnly());
		calendar.setVisible(mapCalendar.getVisible());
		calendar.setOffSetFrom(mapCalendar.getOffSetFrom());
		calendar.setOffSetTo(mapCalendar.getOffSetTo());
		calendar.setLastRefreshTimeStamp(mapCalendar.getLastRefreshTimeStamp());
		calendar.setUser(mapCalendar.getUser());
		calendar.setLastUpdateTime(mapCalendar.getLastUpdateTime());
	}

	private Long m_maxId = new Long(0L);
	private HashMap<Long, Calendar> m_calendarMap = new HashMap<Long, Calendar>();
}
