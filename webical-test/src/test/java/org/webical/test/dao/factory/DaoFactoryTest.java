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

package org.webical.test.dao.factory;

import junit.framework.TestCase;

import org.webical.Calendar;
import org.webical.dao.DaoException;
import org.webical.dao.factory.DaoFactory;
import org.webical.dao.hibernateImpl.EventDaoWebDavHibernateBufferedImpl;

/**
 * Tests the DaoFactory
 * @author ivo
 *
 */
public class DaoFactoryTest extends TestCase {

	private static final String WEBDAV = "webdav";
	private DaoFactory daoFactory;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		daoFactory = DaoFactory.getInstance();
	}
	
	/**
	 * Tests the getEventDaoForCalendar method for wrong input handling
	 */
	public void testGetEventDaoForCalendarWrongInput() {
		if(daoFactory.getEventDaoForCalendar(null) != null) {
			fail("Should get null result for null calendar");
		}
		
		Calendar calendar = new Calendar();
		
		if(daoFactory.getEventDaoForCalendar(calendar) != null) {
			fail("Should get null result for null type");
		}
		
		calendar.setType("");
		if(daoFactory.getEventDaoForCalendar(calendar) != null) {
			fail("Should get null result for empty type");
		}
	}
	
	/**
	 * Tests wheter the rigth Dao is returned for a webdav calendar
	 * @throws DaoException 
	 */
	public void testGetEventDaoForCalendarWebdav() throws DaoException {

		daoFactory.addEventDaoRegistration(WEBDAV, new EventDaoWebDavHibernateBufferedImpl());
	
		Calendar calendar = new Calendar();
		calendar.setType(WEBDAV);
		
		if(daoFactory.getEventDaoForCalendar(calendar) == null || 
				(daoFactory.getEventDaoForCalendar(calendar).getClass() != EventDaoWebDavHibernateBufferedImpl.class)) {
			fail("Should get a: " + EventDaoWebDavHibernateBufferedImpl.class);
		}
		
	}
	
	


}
