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

package org.webical.dao.hibernateImpl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.criterion.Restrictions;
import org.webical.Calendar;
import org.webical.Event;
import org.webical.User;
import org.webical.dao.CalendarDao;
import org.webical.dao.DaoException;
import org.webical.dao.EventDao;
import org.webical.dao.annotation.Transaction;
import org.webical.dao.factory.DaoFactory;
import org.webical.dao.util.WebDavCalendarSynchronisation;

/**
 * CalendarDaoHibernateImpl
 * @author jochem
 * 
 */
public class CalendarDaoHibernateImpl extends BaseHibernateImpl implements
		CalendarDao {

	///////////////////////////
	/// CalendarDao methods ///
	///////////////////////////

	private final static Log log = LogFactory.getLog(CalendarDaoHibernateImpl.class);

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.CalendarDao#getCalendarForEvent(org.webical.Event)
	 */
	@Transaction(readOnly=true)
	public Calendar getCalendarForEvent(Event event) throws DaoException {
		if (event.getCalendar() == null || event.getCalendar().getCalendarId() == null) {
			return null;
		}
		else {
			try {
				return (Calendar)getSession().createCriteria(Calendar.class)
					.add(Restrictions.idEq(event.getCalendar().getCalendarId()))
					.uniqueResult();
			} catch (Exception e) {
				log.error("Could not decrypt calendars password", e);
				throw new DaoException("Could not decrypt calendars password", e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.CalendarDao#storeCalendar(org.webical.Calendar)
	 */
	@Transaction(readOnly=false)
	public void storeCalendar(Calendar calendar) throws DaoException {
		try {
			if (log.isDebugEnabled()) log.debug("storeCalendar " + calendar.getName());

			//Set the refreshTime to null to refresh the events afterwards
			calendar.setLastRefreshTimeStamp(null);
			saveOrUpdate(calendar);
		} catch (Exception e) {
			log.error(e, e);
			throw new DaoException("Could not store Calendar", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.webical.aspect.dao.CalendarDao#storeCalendar(org.webical.Calendar, java.util.List)
	 */
	@Transaction(readOnly=false)
	public void storeCalendar(Calendar calendar, List<Event> events) throws DaoException {
		try {
			if (log.isDebugEnabled()) {
				log.debug("storeCalendar " + calendar.getName() + " events " + events.size());
			}

			//Set the refreshTime to null to refresh the events afterwards
			calendar.setLastRefreshTimeStamp(null);
			saveOrUpdate(calendar);

			WebDavCalendarSynchronisation synchronisation = new WebDavCalendarSynchronisation();
			synchronisation.writeToRemoteCalendarFile(calendar, events);
		} catch (Exception e) {
			log.error(e, e);
			throw new DaoException("Could not store Calendar", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.CalendarDao#getCalendars(org.webical.User)
	 */
	@Transaction(readOnly=true)
	@SuppressWarnings("unchecked")
	public List<Calendar> getCalendars(User user) throws DaoException {
		try {
			Criteria criteria = getSession().createCriteria(Calendar.class);
			criteria.add(Restrictions.eq("user", user));
			return criteria.list();
		} catch (Exception e) {
			log.error(e, e);
			throw new DaoException("Could not get Calendars", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.CalendarDao#removeCalendar(org.webical.Calendar)
	 */
	@Transaction(readOnly=false)
	public void removeCalendar(Calendar calendar) throws DaoException {
		try {
			log.info("Deleting calendar " + calendar.getName());

			getSession().buildLockRequest(new LockOptions(LockMode.NONE)).lock(calendar);

			//Cascade events in the cache
			EventDao eventDao = DaoFactory.getInstance().getEventDaoForCalendar(calendar);
			if (eventDao != null) {
				eventDao.removeAllEventsForCalendar(calendar);
			}
			delete(calendar);
		} catch (Exception e) {
			log.error(e, e);
			throw new DaoException("Could not remove Calendar", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.webical.aspect.dao.CalendarDao#getCalendarById(java.lang.String)
	 */
	@Transaction(readOnly=true)
	public Calendar getCalendarById(String id) throws DaoException {
		if (id == null) {
			return null;
		}
		Criteria criteria = getSession().createCriteria(Calendar.class);
		criteria.add(Restrictions.eq("calendarId", new Long(id)));

		Calendar calendar = null;
		if (criteria.list().size() > 0) {
			calendar =  (Calendar)criteria.uniqueResult();
		}
		return calendar;
	}

	////////////////////////////
	/// EncyptingDao methods ///
	////////////////////////////

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.EncryptingDao#updateAllEntities()
	 */
	@Transaction(readOnly=false)
	public void updateAllEntities() throws DaoException {
		getSession().setCacheMode(CacheMode.IGNORE);
		@SuppressWarnings("unchecked")
		List<Calendar> calendars = loadAll(Calendar.class);
		saveOrUpdateAll(calendars);
	}
}
