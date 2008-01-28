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

package org.webical.dao.factory;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.webical.Calendar;
import org.webical.dao.DaoException;
import org.webical.dao.EventDao;

/**
 * Factory to deliver the rigth Dao for a given Calendar
 * @author ivo
 *
 */
public class DaoFactory implements InitializingBean {
	
	private static Log log = LogFactory.getLog(DaoFactory.class);
	
	public static String EVENT_DAO_TYPE = "Event";
	public static String TASK_DAO_TYPE = "Task";
	
	/**
	 * Registrations for the EventDaos
	 */
	private Map<String, EventDao> eventDaoRegistrations = new HashMap<String, EventDao>();
	
	
	/**
	 * The instance
	 */
	private static DaoFactory instance;

	/**
	 * Private constructor
	 */
	private DaoFactory() {
		
	}
	
	/**
	 * @return the DaoFactory instance
	 */
	public static DaoFactory getInstance() {
		if(instance == null) {
			instance = new DaoFactory();
		}
		
		return instance;
	}
	
	/**
	 * Retrieves the rigth EventDao for a Calendar
	 * @param calendar the Calendar to retrieve a EventDao for
	 * @return a EventDao or null
	 */
	public EventDao getEventDaoForCalendar(Calendar calendar) {
		if(calendar == null || calendar.getType() == null || calendar.getType().length() == 0) {
			return null;
		}
		EventDao eventDao = this.eventDaoRegistrations.get(calendar.getType());
		if(log.isDebugEnabled()) {
			log.debug("Returning instance of: " + (eventDao!=null?eventDao.getClass():"null") + " for calendartype: " + calendar.getType());
		}
		return eventDao;
	}

	/**
	 * Used to add a new EventDao registration
	 * @param calendarType a String identifying the calendar type
	 * @param eventDao the EventDao for this type
	 * @throws DaoException 
	 */
	public void addEventDaoRegistration(String calendarType, EventDao eventDao) throws DaoException {
		if(calendarType == null || eventDao == null) {
			throw new DaoException("Could not register EventDao with type: " + calendarType + " and class: " + (eventDao!=null?eventDao.getClass():"null"));
		}
		
		log.info("Adding EventDao registration: " + calendarType + " - " + eventDao.getClass()) ;
		if(this.eventDaoRegistrations.containsKey(calendarType)) {
			throw new DaoException(calendarType + " already registered for class: " + eventDao.getClass());
		}
		this.eventDaoRegistrations.put(calendarType, eventDao);
	}
	
	/**
	 * Set all registered EventDaos
	 * @param eventDaoRegistrations a Map of calendarType - EventDao
	 * @throws DaoException 
	 */
	public void setEventDaoRegistrations(Map<String, EventDao> eventDaoRegistrations) throws DaoException {
		for (String registrationKey : eventDaoRegistrations.keySet()) {
			addEventDaoRegistration(registrationKey, eventDaoRegistrations.get(registrationKey));
		}
	}

	/**
	 * @return a List of Regsitrations for the EventDao interface
	 */
	public Map<String, EventDao> getEventDaoRegistrations() {
		return eventDaoRegistrations;
	}

	public void afterPropertiesSet() throws Exception {
		//Set instance
		DaoFactory.instance = this;
	}
	
	

}
