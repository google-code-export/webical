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
import org.hibernate.LockMode;
import org.webical.Calendar;
import org.webical.User;
import org.webical.dao.CalendarDao;
import org.webical.dao.DaoException;
import org.webical.dao.UserDao;
import org.webical.dao.annotation.Transaction;

/**
 * @author ivo
 */
public class UserDaoHibernateImpl extends BaseHibernateImpl implements UserDao {

	private static Log log = LogFactory.getLog(CalendarDaoHibernateImpl.class);

	/** Set by Spring */
	CalendarDao calendarDao;

	///////////////////
	/// Api methods ///
	///////////////////

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.UserDao#storeUser(org.webical.User)
	 */
	@Transaction(readOnly=false)
	public void storeUser(User user) throws DaoException {
		try {
			saveOrUpdate(user);
		} catch (Exception e) {
			log.error(e,e);
			throw new DaoException("Could not store user", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.UserDao#removeUser(org.webical.User)
	 */
	@Transaction(readOnly=false)
	public void removeUser(User user) throws DaoException {
		try {
			log.info("removeUser " + user.getUserId());

			getSession().lock(user, LockMode.NONE);
			//Cascade calendars
			List<Calendar> calendars = calendarDao.getCalendars(user);
			if (calendars != null && calendars.size() > 0) {
				for (Calendar calendar : calendars) {
					calendarDao.removeCalendar(calendar);
				}
			}
			//Remove the user
			delete(user);
		} catch (Exception e) {
			log.error(e,e);
			throw new DaoException("Could not delete user", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.aspect.dao.UserDao#getUser(java.lang.String)
	 */
	@Transaction(readOnly=true)
	public User getUser(String userId) throws DaoException {
		User user = null;
		try {
			user = (User) get(User.class, userId);
		} catch (Exception e) {
			log.error(e,e);
			throw new DaoException("Could not load user", e);
		}
		return user;
	}

	/////////////////////////
	/// Getters / Setters ///
	/////////////////////////

	/**
	 * @return the CalendarDao
	 */
	public CalendarDao getCalendarDao() {
		return calendarDao;
	}
	/**
	 * @param calendarDao the CalendarDao used by this dao
	 */
	public void setCalendarDao(CalendarDao calendarDao) {
		this.calendarDao = calendarDao;
	}
}
