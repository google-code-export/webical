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

package org.webical.manager.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.webical.Calendar;
import org.webical.Event;
import org.webical.User;
import org.webical.dao.CalendarDao;
import org.webical.dao.DaoException;
import org.webical.dao.factory.DaoFactory;
import org.webical.manager.CalendarManager;
import org.webical.manager.WebicalException;

/**
 * CalendarManagerImpl
 * @author paul
 */
public class CalendarManagerImpl implements CalendarManager, InitializingBean {

	private static final String CALENDAR_MANAGER_IMPL_NEEDS_CALENDAR_DAO = "CalendarManagerImpl needs a CalendarDao";

	private static Log log = LogFactory.getLog(CalendarManagerImpl.class);

	private CalendarDao calendarDao;
	private DaoFactory daoFactory;

	/* (non-Javadoc)
	 * @see org.webical.manager.CalendarManager#getCalendarForEvent(org.webical.Event)
	 */
	public Calendar getCalendarForEvent(Event event) throws WebicalException {
		try {
			return calendarDao.getCalendarForEvent(event);
		} catch (DaoException e) {
			throw new WebicalException("Could not get calendar",e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.manager.CalendarManager#removeCalendar(org.webical.Calendar)
	 */
	public void removeCalendar(Calendar calendar) throws WebicalException {
		try {
			calendarDao.removeCalendar(calendar);
		} catch (DaoException e) {
			throw new WebicalException("Could not remove calendar",e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.manager.CalendarManager#storeCalendar(org.webical.Calendar)
	 */
	public void storeCalendar(Calendar calendar) throws WebicalException {
		try {
			calendarDao.storeCalendar(calendar);
		} catch (DaoException e) {
			throw new WebicalException("Could not store calendar",e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.webical.manager.CalendarManager#storeCalendar(org.webical.Calendar, java.util.List)
	 */
	public void storeCalendar(Calendar calendar, List<Event> events) throws WebicalException {
		try {
			calendarDao.storeCalendar(calendar, events);
		} catch (DaoException e) {
			throw new WebicalException("Could not store calendar",e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.manager.CalendarManager#getCalendars(org.webical.User)
	 */
	public List<Calendar> getCalendars(User user) throws WebicalException {
		try {
			return calendarDao.getCalendars(user);
		}catch (DaoException e) {
			throw new WebicalException("Could not retrieve calendars",e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.manager.CalendarManager#getAvailableCalendarTypes()
	 */
	public Set<String> getAvailableCalendarTypes() throws WebicalException {
		Set<String> calendarTypes = new HashSet<String>();

		Set<String> eventDaoRegistrationKeys = daoFactory.getEventDaoRegistrations().keySet();
		if (eventDaoRegistrationKeys != null) {
			calendarTypes.addAll(eventDaoRegistrationKeys);
		}

		//TODO Take the TaskDaos into account

		return calendarTypes;
	}

	/* (non-Javadoc)
	 * @see org.webical.manager.CalendarManager#getCalendarById(java.lang.String)
	 */
	public Calendar getCalendarById(String id) throws WebicalException {
		try {
			return  calendarDao.getCalendarById(id);
		} catch (DaoException e) {
			throw new WebicalException("Could not find calendar", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.manager.CalendarManager#refreshCalendar(org.webical.Calendar)
	 */
	public void refreshCalendar(Calendar calendar) throws WebicalException {
		try {
			calendarDao.refreshCalendar(calendar);
		} catch (DaoException e) {
			throw new WebicalException("Could not find calendar", e);
		}
	}

	/**
	 * Checks if all properties are corectly set up
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		if (calendarDao == null) {
			throw new ExceptionInInitializerError(CALENDAR_MANAGER_IMPL_NEEDS_CALENDAR_DAO);
		} else if (log.isDebugEnabled()) {
			log.debug("Class of CalendarDao set by Spring: " + calendarDao.getClass());
		}

		if (daoFactory == null) {
			throw new ExceptionInInitializerError("CalendarManagerImpl needs a DaoFactory");
		}
	}


	///////////////////////
	/// Getters/Setters ///
	///////////////////////

	/**
	 * Getter for the DaoFactory
	 * @return the DaoFactory
	 */
	public DaoFactory getDaoFactory() {
		return daoFactory;
	}

	/**
	 * Setter for the DaoFactory
	 * @param daoFactory the DaoFactory to set
	 */
	public void setDaoFactory(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	/**
	 * returns the CalendarDao
	 * @return
	 */
	public CalendarDao getCalendarDao() {
		return calendarDao;
	}

	/**
	 * sets the CalendarDao
	 * @param calendarDao
	 */
	public void setCalendarDao(CalendarDao calendarDao) {
		this.calendarDao = calendarDao;
	}
}
