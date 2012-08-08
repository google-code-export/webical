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

package org.webical.dao.impl;

import java.util.Date;
import java.util.List;

import org.webical.Calendar;
import org.webical.User;
import org.webical.dao.DaoException;
import org.webical.dao.factory.DaoFactory;
import org.webical.dao.hibernateImpl.CalendarDaoHibernateImpl;
import org.webical.dao.hibernateImpl.EventDaoWebDavHibernateBufferedImpl;
import org.webical.dao.hibernateImpl.UserDaoHibernateImpl;


/**
 * Tests the UserDaoHibernateImplTest and the cascading relation with the calendars
 * @author ivo
 *
 */
public class UserDaoHibernateImplTest extends DataBaseTest {

	private UserDaoHibernateImpl userDaoHibernateImpl;

	private CalendarDaoHibernateImpl calendarDaoHibernateImpl;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userDaoHibernateImpl = new UserDaoHibernateImpl();
		calendarDaoHibernateImpl = new CalendarDaoHibernateImpl();
	}

	/**
	 * Tests the store capability
	 */
	public void testStoreUser() {
		System.out.println("Store user");
		try {
			userDaoHibernateImpl.storeUser(getNewUser());
		} catch (DaoException e) {
			fail("Store failed: " + e);
		}
	}

	/**
	 * Tests the get Functionality
	 */
	public void testGetUser() {
		// Store a User
		try {
			userDaoHibernateImpl.storeUser(getNewUser());
		} catch (DaoException se) {
			fail("Unable to store user: " + se);
		}

		User user = null;

		try {
			user = userDaoHibernateImpl.getUser("demo-user");
		} catch (DaoException ge) {
			fail("Could not retrieve user: " + ge);
		}

		assertNotNull(user);
		assertTrue("demo-user".equals(user.getUserId()));
	}

	/**
	 * Tests the remove Functionality
	 * @throws DaoException
	 */
	public void testRemoveUser() throws DaoException {
		//Store a user
		try {
			userDaoHibernateImpl.storeUser(getNewUser());
		} catch (DaoException e) {
			fail("Store failed: " + e);
		}

		userDaoHibernateImpl.setCalendarDao(calendarDaoHibernateImpl);

		//register the eventDao with the daoFactory
		EventDaoWebDavHibernateBufferedImpl eventDaoWebDavHibernateBufferedImpl = new EventDaoWebDavHibernateBufferedImpl();

		DaoFactory.getInstance().getEventDaoRegistrations().clear();
		DaoFactory.getInstance().addEventDaoRegistration("ical-webdav", eventDaoWebDavHibernateBufferedImpl);

		//Retrieve the user
		User user = null;
		try {
			user = userDaoHibernateImpl.getUser(getNewUser().getUserId());
		} catch (DaoException e) {
			fail("Get failed: " + e);
		}
		assertNotNull(user);

		//Remove the user again
		try {
			userDaoHibernateImpl.removeUser(user);
		} catch (DaoException e) {
			fail("Remove failed: " + e);
		}

		//Retrieve the user (again)
		try {
			user = userDaoHibernateImpl.getUser(getNewUser().getUserId());
		} catch (DaoException e) {
			fail("Get failed: " + e);
		}
		assertNull(user);
	}

	/**
	 * Tests whether the calendars asscociated with a user a removed as well
	 * @throws DaoException
	 */
	public void testRemoveUserCascadeCalendar() throws DaoException {
		// Store a User
		try {
			userDaoHibernateImpl.storeUser(getNewUser());
		} catch (DaoException se) {
			fail("Unable to store user: " + se);
		}

		userDaoHibernateImpl.setCalendarDao(calendarDaoHibernateImpl);

		//register the eventDao with the daoFactory
		DaoFactory.getInstance().getEventDaoRegistrations().clear();
		EventDaoWebDavHibernateBufferedImpl eventDaoWebDavHibernateBufferedImpl = new EventDaoWebDavHibernateBufferedImpl();
		DaoFactory.getInstance().addEventDaoRegistration("ical-webdav", eventDaoWebDavHibernateBufferedImpl);

		//Retrieve a user
		User user = null;

		try {
			user = userDaoHibernateImpl.getUser("demo-user");
		} catch (DaoException e) {
			fail("get user failed: " + e);
		}
		assertNotNull(user);

		//GetCalenders for user
		List<Calendar> calendarsForUser = null;
		try {
			calendarsForUser = calendarDaoHibernateImpl.getCalendars(user);
		} catch (Exception e) {
			fail("could not retrieve calendars: " + e);
		}
		assertNotNull(calendarsForUser);
		assertTrue(calendarsForUser.size() > 0);

		//Remove user
		try {
			userDaoHibernateImpl.removeUser(user);
		} catch (DaoException e) {
			fail("Remove failed: " + e);
		}

		//Check if users Calendars are also removed
		calendarsForUser = null;
		try {
			calendarsForUser = calendarDaoHibernateImpl.getCalendars(user);
		} catch (Exception e) {
			fail("could not retrieve calendars: " + e);
		}
		assertEquals(0, calendarsForUser.size());
	}

	private User getNewUser() {
		User user = new User();
		user.setUserId("demo-user");
		user.setFirstName("James");
		user.setLastNamePrefix("A");
		user.setLastName("Gosling");
		user.setBirthDate(new Date());
		return user;
	}

}
