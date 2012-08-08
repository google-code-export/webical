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

package org.webical.test.dao.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.webical.Calendar;
import org.webical.User;
import org.webical.dao.DaoException;
import org.webical.dao.CalendarDao;
import org.webical.dao.factory.DaoFactory;
import org.webical.dao.hibernateImpl.UserDaoHibernateImpl;
import org.webical.dao.hibernateImpl.CalendarDaoHibernateImpl;
import org.webical.dao.hibernateImpl.EventDaoWebDavHibernateBufferedImpl;

import org.webical.test.TestUtils;


/**
 * Tests the UserDaoHibernateImplTest and the cascading relation with the calendars
 * @author ivo
 *
 */
public class UserDaoHibernateImplTest extends DataBaseTest {

	private static Log log = LogFactory.getLog(UserDaoHibernateImplTest.class);

	private UserDaoHibernateImpl userDao = null;
	private CalendarDao calendarDao = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		DaoFactory.getInstance().getEventDaoRegistrations().clear();
		DaoFactory.getInstance().addEventDaoRegistration("ical-webdav", new EventDaoWebDavHibernateBufferedImpl());
		userDao = new UserDaoHibernateImpl();
		userDao.setCalendarDao(new CalendarDaoHibernateImpl());
		calendarDao = userDao.getCalendarDao();
	}

	/**
	 * Tests the store capability
	 */
	public void testStoreUser() {
		log.debug("testStoreUser");
		try {
			userDao.storeUser(TestUtils.getDemoUser());
		} catch (DaoException e) {
			fail("Store failed: " + e);
		}
	}

	/**
	 * Tests the get Functionality
	 */
	public void testGetUser() {
		log.debug("testGetUser");
		try {
			userDao.storeUser(TestUtils.getDemoUser());
		} catch (DaoException se) {
			fail("Unable to store user: " + se);
		}

		User user = null;
		try {
			user = userDao.getUser(TestUtils.getDemoUser().getUserId());
		} catch (DaoException ge) {
			fail("Could not retrieve user: " + ge);
		}

		assertNotNull(user);
		assertTrue(TestUtils.USERID_DEMO.equals(user.getUserId()));
	}

	/**
	 * Tests the remove Functionality
	 * @throws DaoException
	 */
	public void testRemoveUser() throws DaoException {
		//Store a user
		try {
			userDao.storeUser(TestUtils.getDemoUser());
		} catch (DaoException e) {
			fail("Store failed: " + e);
		}

		//Retrieve the user
		User user = null;
		try {
			user = userDao.getUser(TestUtils.getDemoUser().getUserId());
		} catch (DaoException e) {
			fail("Get failed: " + e);
		}
		assertNotNull(user);

		//Remove the user again
		try {
			userDao.removeUser(user);
		} catch (DaoException e) {
			fail("Remove failed: " + e);
		}

		//Retrieve the user (again)
		try {
			user = userDao.getUser(TestUtils.getDemoUser().getUserId());
		} catch (DaoException e) {
			fail("Get failed: " + e);
		}
		assertNull(user);
	}

	/**
	 * Tests whether the calendars associated with a user a removed as well
	 * @throws DaoException
	 */
	public void testRemoveUserCascadeCalendar() throws DaoException {
		// Store a User
		try {
			userDao.storeUser(TestUtils.getDemoUser());
		} catch (DaoException se) {
			fail("Unable to store user: " + se);
		}

		//Retrieve a user
		User user = null;
		try {
			user = userDao.getUser(TestUtils.getDemoUser().getUserId());
		} catch (DaoException e) {
			fail("get user failed: " + e);
		}
		assertNotNull(user);

		//GetCalenders for user
		List<Calendar> calendarsForUser = null;
		try {
			calendarsForUser = calendarDao.getCalendars(user);
		} catch (Exception e) {
			fail("could not retrieve calendars: " + e);
		}
		assertNotNull(calendarsForUser);
		assertTrue(calendarsForUser.size() > 0);

		//Remove user
		try {
			userDao.removeUser(user);
		} catch (DaoException e) {
			fail("Remove failed: " + e);
		}

		//Check if users Calendars are also removed
		calendarsForUser = null;
		try {
			calendarsForUser = calendarDao.getCalendars(user);
		} catch (Exception e) {
			fail("could not retrieve calendars: " + e);
		}
		assertEquals(0, calendarsForUser.size());
	}
}
