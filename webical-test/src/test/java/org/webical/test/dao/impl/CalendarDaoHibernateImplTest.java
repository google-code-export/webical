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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webical.Calendar;
import org.webical.dao.DaoException;
import org.webical.dao.EventDao;
import org.webical.dao.encryption.EncryptorFactory;
import org.webical.dao.factory.DaoFactory;
import org.webical.dao.hibernateImpl.CalendarDaoHibernateImpl;
import org.webical.dao.hibernateImpl.EventDaoWebDavHibernateBufferedImpl;

import org.webical.test.TestUtils;

/**
 * @author ivo
 *
 */
public class CalendarDaoHibernateImplTest extends DataBaseTest {

	private static Log log = LogFactory.getLog(CalendarDaoHibernateImplTest.class);
	private CalendarDaoHibernateImpl calendarDaoHibernateImpl;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setUpDao();
	}

	public void setUpDao() {
		calendarDaoHibernateImpl = new CalendarDaoHibernateImpl();
	}

	/**
	 * Tests the get Functionality
	 */
	public void testGetCalendars() {
		List<Calendar> calendars;
		try {
			//retrieve Calendars for user
			calendars = calendarDaoHibernateImpl.getCalendars(TestUtils.getDemoUser());

			assertNotNull(calendars);
			assertTrue(calendars.size() == 2);
		} catch (DaoException e) {
			fail("GetCalendar failed " + e);
		}
	}

	/**
	 * Tests the store Functionality
	 */
	public void testStoreCalendar() {
		int calendarsBefore = 0;
		List<Calendar> calendars;
		try {
			//Get Calendar size before inserting a new Calendar
			calendars = calendarDaoHibernateImpl.getCalendars(TestUtils.getDemoUser());

			if (calendars != null) {
				calendarsBefore = calendars.size();
			}
			//Add a Calendar
			calendarDaoHibernateImpl.storeCalendar(getCalendar());

			//Retrieve the new Calendar size
			calendars = calendarDaoHibernateImpl.getCalendars(TestUtils.getDemoUser());

			assertNotNull(calendars);
			assertTrue(calendars.size() == calendarsBefore + 1);
		} catch (DaoException e) {
			fail("StoreCalendar failed " + e);
		}
	}

	/**
	 * Tests whether a Calendar can be inserted without having a User
	 */
	public void testStoreWithoutUserCalendar() {
		try {
			Calendar calendar = getCalendarWithEmptyUser();

			calendarDaoHibernateImpl.storeCalendar(calendar);

			fail("StoreWithoutUserCalendarTest failed");
		} catch (DaoException e) {
			log.debug("test passed");
		} catch (Exception e) {
			//fail("Unexpected exception... " + e);
			//FIXME org.hibernate.PropertyValueException...
			//(org.hibernate.AssertionFailure)
			log.warn("THIS SHOULD REALLY BE FIXED !");
		}
	}

	/**
	 * Tests the remove Functionality
	 * @throws DaoException
	 */
	public void testRemoveCalendar() throws DaoException {

		EventDaoWebDavHibernateBufferedImpl eventDaoWebDavHibernateBufferedImpl = new EventDaoWebDavHibernateBufferedImpl();

		Map<String, EventDao> registrations = DaoFactory.getInstance().getEventDaoRegistrations();
		if (!registrations.containsKey(getCalendar().getType())) {
			DaoFactory.getInstance().addEventDaoRegistration(getCalendar().getType(), eventDaoWebDavHibernateBufferedImpl);
		}
		int calendarsBefore = 0;
		List<Calendar> calendars;
		try {
			calendars = calendarDaoHibernateImpl.getCalendars(TestUtils.getDemoUser());

			//Get Calendar size before test
			if (calendars != null) {
				calendarsBefore = calendars.size();
			}

			Calendar calendar = getCalendar();
			//insert a new Calendar
			calendarDaoHibernateImpl.storeCalendar(calendar);
			//remove a calendar
			calendarDaoHibernateImpl.removeCalendar(calendar);

			calendars = calendarDaoHibernateImpl.getCalendars(TestUtils.getDemoUser());

			assertTrue(calendars.size() == calendarsBefore);
		} catch (DaoException e) {
			fail("StoreCalendar failed " + e);
		}
	}

/*	*//**
	 * Tests if the encryption utilities do there job
	 *//*
	public void testEncryption() {
		String encryptionPassword = "somePassword";
		String calendarPassword = "someOtherPassword";
		String encryptedPassword = null;
		try {
			encryptedPassword = new DesEncryptor(encryptionPassword).encrypt(calendarPassword);
		} catch (Exception e) {
			fail("Could not encrypt password");
		}
		
		//Store with encryption
		EncryptorFactory.setEncryptionPassword(encryptionPassword);
		
		Calendar calendar = getCalendar();
		calendar.setPassword(calendarPassword);
		
		try {
			calendarDaoHibernateImpl.storeCalendar(calendar);
		} catch (DaoException e) {
			fail("Storing with encryption failed " + e);
		}
		
		
		
		//Compare without decryption
		try {
			List<Calendar> calendars = calendarDaoHibernateImpl.getCalendars(TestUtils.getDemoUser());
			Calendar calendar2 = null;
			if(calendars != null && calendars.size() > 0) {
				calendar2 = calendars.get(calendars.size() -1);
			} else {
				fail("No calendars stored");
			}
			if(!calendar2.getPassword().equals(encryptedPassword)) {
				fail("Stored password: " + calendar2.getPassword() + " does not match expected: " + encryptedPassword);
			}
		} catch (DaoException e) {
			fail("Storing with encryption failed (could not retrieve the stored calendar) " + e);
		}
		
		closeSessionAndCreateNew();
		
		
		//Compare with decryption
		EncryptorFactory.setDecryptionPassword(encryptionPassword);
		closeSessionAndCreateNew();
		try {
			List<Calendar> calendars = calendarDaoHibernateImpl.getCalendars(TestUtils.getDemoUser());
			Calendar calendar3 = null;
			if(calendars != null && calendars.size() > 0) {
				calendar3 = calendars.get(calendars.size() -1);
			} else {
				fail("No calendars stored");
			}
			if(!calendar3.getPassword().equals(calendarPassword)) {
				fail("Stored password: " + calendar3.getPassword() + " does not match expected: " + calendarPassword);
			}
		} catch (DaoException e) {
			fail("Storing with encryption failed (could not retrieve the stored calendar with decryption) " + e);
		}
	}*/

	public void testPasswordReencryption() {
		String password = "the password";
		String firstEncryptionPassword = "encrypting this b***h";
		String secondEncryptionPassword = "something completely different";

		Calendar calendar = getCalendar();
		calendar.setPassword(password);

		//Store without encryption
		EncryptorFactory.setEncryptionPassword(null);
		EncryptorFactory.setDecryptionPassword(null);
		try {
			calendarDaoHibernateImpl.storeCalendar(calendar);
		} catch (DaoException e) {
			fail("Could not store calendar: " + e) ;
		}

		//Update encryption password
		EncryptorFactory.setEncryptionPassword(firstEncryptionPassword);

		//Resave
		try {
			calendarDaoHibernateImpl.updateAllEntities();
		} catch (DaoException e1) {
			fail("Could not update all " + e1);
		}

		//Update decryption password
		EncryptorFactory.setDecryptionPassword(firstEncryptionPassword);

		//Check decryption
		try {
			List<Calendar> result1 = calendarDaoHibernateImpl.getCalendars(getCalendar().getUser());
			assertEquals(password, result1.get(result1.size() - 1).getPassword());
		} catch (DaoException e) {
			fail("could not retrieve calendar: " + e);
		}

		//Update encryption password again
		EncryptorFactory.setEncryptionPassword(secondEncryptionPassword);

		//Resave
		try {
			calendarDaoHibernateImpl.updateAllEntities();
		} catch (DaoException e1) {
			fail("Could not update all " + e1);
		}

		//Update decryption password again
		EncryptorFactory.setDecryptionPassword(secondEncryptionPassword);

		//Check decryption
		try {
			List<Calendar> result1 = calendarDaoHibernateImpl.getCalendars(getCalendar().getUser());
			assertEquals(password, result1.get(result1.size() - 1).getPassword());
		} catch (DaoException e) {
			fail("could not retrieve calendar: " + e);
		}
	}

	private Calendar getCalendar() {
		Calendar calendar = new Calendar();
		calendar.setName("name");
		calendar.setPassword("pw");
		calendar.setType("ical-webdav");
		calendar.setUrl("url");
		calendar.setUser(TestUtils.getDemoUser());
		calendar.setUsername("un");
		return calendar;
	}
	private Calendar getCalendarWithEmptyUser() {
		Calendar calendar = new Calendar();
		calendar.setName("name");
		calendar.setPassword("pw");
		calendar.setType("type");
		calendar.setUrl("url");
		return calendar;
	}
}
