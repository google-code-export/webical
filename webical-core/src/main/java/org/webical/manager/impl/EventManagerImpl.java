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

package org.webical.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webical.Calendar;
import org.webical.Event;
import org.webical.User;
import org.webical.dao.DaoException;
import org.webical.dao.DeleteConflictException;
import org.webical.dao.EventDao;
import org.webical.dao.UpdateConflictException;
import org.webical.dao.factory.DaoFactory;
import org.webical.manager.CalendarManager;
import org.webical.manager.EventManager;
import org.webical.manager.WebicalException;

public class EventManagerImpl implements EventManager {

	private static final String EVENT_MANAGER_IMPL_NEEDS_DAO_FACTORY = "EventManagerImpl needs EventDao";

	private static Log log = LogFactory.getLog(EventManagerImpl.class);
	
	private DaoFactory daoFactory;
	
	private CalendarManager calendarManager;
	
	/* (non-Javadoc)
	 * @see org.webical.manager.EventManager#getAllEvents(org.webical.Calendar)
	 */
	public List<Event> getAllEvents(Calendar calendar) throws WebicalException {
		try {
			EventDao eventDao =  getEventDaoForCalendar(calendar);
			return eventDao.getAllEvents(calendar);
		} catch (DaoException e) {
			throw new WebicalException("Could not getAllEvents for calendar",e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.webical.manager.EventManager#getEventsForPeriod(org.webical.Calendar, java.util.Date, java.util.Date)
	 */
	public List<Event> getEventsForPeriod(Calendar calendar, Date dtStart,
			Date dtEnd) throws WebicalException {
		try {
			EventDao eventDao =  getEventDaoForCalendar(calendar);
			return eventDao.getEventsForPeriod(calendar, dtStart, dtEnd);
		} catch (DaoException e) {
			throw new WebicalException("Could not getEventsForPeriod for calendar",e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.webical.manager.EventManager#getEventsForPeriod(org.webical.User, java.util.Date, java.util.Date)
	 */
	public List<Event> getEventsForPeriod(User user, Date dtStart, Date dtEnd)
			throws WebicalException {
		try {
			//Result
			List<Event> events = new ArrayList<Event>();
			
			//Set<Calendar> calendars = user.getCalendars();
			List<Calendar> calendars = calendarManager.getCalendars(user);
			
			if(calendars == null){
				return new ArrayList<Event>();
			} else {
				Iterator<Calendar> iterator = calendars.iterator();

				EventDao eventDao;
				
				while(iterator.hasNext()){
					Calendar calendar = iterator.next();
					eventDao = getEventDaoForCalendar(calendar);

					if(eventDao != null && calendar.getVisible()){	
						List<Event> temp_events = eventDao.getEventsForPeriod(calendar, dtStart, dtEnd);
						if(temp_events != null)	events.addAll(temp_events);
					} 
				}
				
				return events;
			}
		} catch (DaoException e) {
			throw new WebicalException("Could not getEventsForPeriod for user",e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.webical.manager.EventManager#removeEvent(org.webical.Event)
	 */
	public void removeEvent(Event event) throws WebicalException {
		try {
			EventDao eventDao =  getEventDaoForCalendar(event.getCalendar());
			eventDao.removeEvent(event);
		} catch (DaoException e) {
			throw new WebicalException("Could not removeEvent",e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.webical.manager.EventManager#storeEvent(org.webical.Event)
	 */
	public void storeEvent(Event event) throws WebicalException {
		EventDao eventDao =  getEventDaoForCalendar(event.getCalendar());
		try {
			eventDao.storeEvent(event);
		} catch (DeleteConflictException e) {
			throw new WebicalException("Could not storeEvent",e);
		} catch (UpdateConflictException e) {
			throw new WebicalException("Could not storeEvent",e);
		} catch (DaoException e) {
			throw new WebicalException("Could not storeEvent",e);
		} 
	}
	/* (non-Javadoc)
	 * @see org.webical.manager.EventManager#getEventByUid(String)
	 */
	public Event getEventByUid(String Uid, Calendar calendar) throws WebicalException {
		
		EventDao eventDao =  getEventDaoForCalendar(calendar);
	
		Event event;
		try {
			event = eventDao.getEventByUid(calendar, Uid);
			return event;
		} catch (DaoException e) {
			throw new WebicalException("Could not getEventByUid",e);
		}
	}

	/**
	 * returns the DaoFactory
	 * @return daoFactory
	 */
	public DaoFactory getDaoFactory() {
		return daoFactory;
	}
	/**
	 * sets the DaoFactory
	 * @param daoFactory
	 */
	public void setDaoFactory(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {			
		if(daoFactory == null) {
			throw new ExceptionInInitializerError(EVENT_MANAGER_IMPL_NEEDS_DAO_FACTORY);
		}
		
		if(log.isDebugEnabled()) {
			log.debug("Class of UserDao set by Spring: " + daoFactory.getClass());
		}
	}
	/**
	 * Returns the EventDao for the Calendar
	 * @param calendar
	 * @return EventDao
	 */
	public EventDao getEventDaoForCalendar(Calendar calendar) {
		return daoFactory.getEventDaoForCalendar(calendar);
	}

	public CalendarManager getCalendarManager() {
		return calendarManager;
	}

	public void setCalendarManager(CalendarManager calendarManager) {
		this.calendarManager = calendarManager;
	}

}
