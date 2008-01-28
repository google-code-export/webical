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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.webical.Option;
import org.webical.PluginSettings;
import org.webical.Settings;
import org.webical.User;
import org.webical.UserPluginSettings;
import org.webical.dao.DaoException;
import org.webical.dao.hibernateImpl.SettingsDaoHibernateImpl;
import org.webical.dao.hibernateImpl.UserDaoHibernateImpl;

/**
 * Tests the {@link SettingsDaoHibernateImpl}
 * @author ivo
 *
 */
public class SettingsDaoHibernateImplTest extends DataBaseTest {
	
	private static String PLUGIN_NAME = "dummy plugin";
	
	private SettingsDaoHibernateImpl settingsDao;
	private UserDaoHibernateImpl userDao;

	/* (non-Javadoc)
	 * @see org.webical.dao.impl.DataBaseTest#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		settingsDao = new SettingsDaoHibernateImpl();
		userDao = new UserDaoHibernateImpl();
	}
	
	/**
	 * Tests the storing and retrieving of {@link PluginSettings}
	 */
	public void testPluginSettings() {
		//Check that the db is empty
		PluginSettings settings = null;
		try {
			settings = settingsDao.getPluginSettings(PLUGIN_NAME);
			assertNull(settings);
		} catch (DaoException e) {
			fail(e.getMessage());
		}
		
		//Store one
		try {
			settingsDao.storePluginSettings(getPluginSettings());
		} catch (DaoException e) {
			fail(e.getMessage());
		}
		
		//Retrieve it and check
		try {
			settings = settingsDao.getPluginSettings(PLUGIN_NAME);
			assertNotNull(settings);
		} catch (DaoException e) {
			fail(e.getMessage());
		}
		
		PluginSettings referencePluginSettings = getPluginSettings();
		
		assertEquals(referencePluginSettings.getPluginClass(), settings.getPluginClass());
		assertNotNull(referencePluginSettings.getOptions());
		assertEquals(referencePluginSettings.getOptions().size(), settings.getOptions().size());
	}
	
	/**
	 * Tests the storing and retrieving of {@link UserPluginSettings}
	 */
	public void testUserPluginSettings() {
		UserPluginSettings settings = null;
		
		//Make sure the db is empty
		try {
			settings = settingsDao.getUserPluginSettings(PLUGIN_NAME, getUser());
			assertNull(settings);
		} catch (DaoException e) {
			fail(e.getMessage());
		}
		
		//Store it
		try {
			settingsDao.storeUserPluginSettings(getUserPluginSettings());
		} catch (DaoException e) {
			fail(e.getMessage());
		}
		
		//Retrieve and compare it
		try {
			settings = settingsDao.getUserPluginSettings(PLUGIN_NAME, getUser());
			assertNotNull(settings);
		} catch (DaoException e) {
			fail(e.getMessage());
		}
		
		UserPluginSettings referenceSettings = getUserPluginSettings();
		
		assertEquals(referenceSettings.getPluginClass(), settings.getPluginClass());
		assertNotNull(referenceSettings.getOptions());
		assertEquals(referenceSettings.getOptions().size(), settings.getOptions().size());
		assertEquals(getUser().getUserId(), referenceSettings.getUser().getUserId());
	}
	
	/**
	 * Tests removing {@link Settings}
	 */
	public void testRemoveSettings() {
		
		//Store it
		try {
			settingsDao.storeUserPluginSettings(getUserPluginSettings());
			settingsDao.storePluginSettings(getPluginSettings());
		} catch (DaoException e) {
			fail(e.getMessage());
		}
		
		//Retrieve them
		PluginSettings pluginSettings = null;
		UserPluginSettings userPluginSettings = null;
		
		try {
			userPluginSettings = settingsDao.getUserPluginSettings(PLUGIN_NAME, getUser());
			pluginSettings = settingsDao.getPluginSettings(PLUGIN_NAME);
		} catch (DaoException e) {
			fail(e.getMessage());
		}
		
		//Remove it
		try {
			settingsDao.removeSettings(userPluginSettings);
			settingsDao.removeSettings(pluginSettings);
		} catch (DaoException e) {
			fail(e.getMessage());
		}
		
		//Check that they are gone
		try {
			assertNull(settingsDao.getUserPluginSettings(PLUGIN_NAME, getUser()));
			assertNull(settingsDao.getPluginSettings(PLUGIN_NAME));
		} catch (DaoException e) {
			fail(e.getMessage());
		}
		
	}
	
	///////////////
	// Test data //
	///////////////
	
	private UserPluginSettings getUserPluginSettings() {
		UserPluginSettings userPluginSettings = new UserPluginSettings();
		userPluginSettings.setUser(getUser());
		userPluginSettings.setOptions(getOptions(userPluginSettings));
		userPluginSettings.setPluginClass(PLUGIN_NAME);
		return userPluginSettings;
	}
	
	private PluginSettings getPluginSettings() {
		PluginSettings pluginSettings = new PluginSettings();
		pluginSettings.setPluginClass(PLUGIN_NAME);
		pluginSettings.setOptions(getOptions(pluginSettings));
		return pluginSettings;
	}
	
	private Set<Option> getOptions(Settings settings) {
		Set<Option> options = new HashSet<Option>();
		
		options.add(new Option(settings, "option 1", "a value"));
		options.add(new Option(settings, "option 2", new Boolean(true)));
		options.add(new Option(settings, "option 3", new Integer(2)));
		options.add(new Option(settings, "option 4", new SomeObject("some")));
		
		return options;
	}
	
	private User getUser() {
		User user = null;
		
		try {
			user = userDao.getUser("userid");
		} catch (DaoException e) {
			fail("Could not get user");
		}
		
		if(user == null) {
			user = new User();
			user.setUserId("userid");
			try {
				userDao.storeUser(user);
			} catch (DaoException e) {
				fail("Could not store user");
			}
		}
		
		try {
			user = userDao.getUser("userid");
		} catch (DaoException e) {
			fail("Could not get user");
		}
		
		return user;
	}
	
	private static class SomeObject implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private String someValue;

		/**
		 * @param someValue
		 */
		public SomeObject(String someValue) {
			super();
			this.someValue = someValue;
		}

		/**
		 * @return the someValue
		 */
		public String getSomeValue() {
			return someValue;
		}

		/**
		 * @param someValue the someValue to set
		 */
		public void setSomeValue(String someValue) {
			this.someValue = someValue;
		}
		
		
	}
	
}
