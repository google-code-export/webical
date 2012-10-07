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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webical.ApplicationSettings;
import org.webical.dao.DaoException;
import org.webical.dao.hibernateImpl.ApplicationSettingsDaoHibernateImpl;

/**
 * 
 * Tests the Application dao hibernate implementation
 * @author ivo
 *
 */
public class ApplicationSettingsDaoHibernateImplTest extends DataBaseTest {
	private static Log log = LogFactory.getLog(CalendarDaoHibernateImplTest.class);
	private static final boolean TEST_CLEANUP_ENABLED = true;
	private static final int TEST_CALENDAR_REFRESH_TIME = 2000;
	private static final String TEST_PLUGIN_WORK_PATH = "TEST_PLUGIN_WORK_PATH";
	private static final String TEST_EXTENSION = "TEST_EXTENSION";
	private static final String TEST_PAGE_TITLE = "TEST PAGE TITLE";
	private ApplicationSettingsDaoHibernateImpl applicationSettingsDaoHibernateImpl;

	/**
	 * Setup the dao
	 * @see org.webical.test.aspect.dao.hibernateImpl.DataBaseTest#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		applicationSettingsDaoHibernateImpl = new ApplicationSettingsDaoHibernateImpl();
	}

	/**
	 * Tests storing and removing the settings
	 */
	public void testStore() {
		try {
			assertNull(applicationSettingsDaoHibernateImpl.getApplicationSettings());
		} catch (DaoException e) {
			fail("Could not retrieve application settings");
		}
		log.info("ApplicationSettingsDaoHibernateImplTest:testStore:ClassPath " + System.getenv("CLASSPATH"));
		try {
			applicationSettingsDaoHibernateImpl.storeApplicationSettings(getFullApplicationSettings());
		} catch (DaoException e) {
			fail("Could not store application settings");
		}
		
		ApplicationSettings applicationSettings = null;
		try {
			applicationSettings = applicationSettingsDaoHibernateImpl.getApplicationSettings();
		} catch (DaoException e) {
			fail("Could not retrieve application settings");
		}
		
		assertNotNull(applicationSettings);
		assertEquals(TEST_PAGE_TITLE, applicationSettings.getCustomPageTitle());
		assertEquals(TEST_EXTENSION, applicationSettings.getPluginPackageExtension());
		assertEquals(TEST_PLUGIN_WORK_PATH, applicationSettings.getPluginWorkPath());
		assertEquals(TEST_CLEANUP_ENABLED, applicationSettings.isPluginCleanupEnabled());
		assertEquals(TEST_CALENDAR_REFRESH_TIME, applicationSettings.getCalendarRefreshTimeMs());
		assertEquals(2, applicationSettings.getResourcePaths().size());
		assertEquals(2, applicationSettings.getPluginPaths().size());
	}
	
	/**
	 * @return a Fully loaded configuration
	 */
	private ApplicationSettings getFullApplicationSettings() {
		Set<String> resourcePaths = new HashSet<String>();
		resourcePaths.add("1");
		resourcePaths.add("2");
		
		Set<String> pluginPaths = new HashSet<String>();
		pluginPaths.add("1");
		pluginPaths.add("2");
		
		ApplicationSettings settings = new ApplicationSettings();
		settings.setCalendarRefreshTimeMs(TEST_CALENDAR_REFRESH_TIME);
		settings.setCustomPageTitle(TEST_PAGE_TITLE);
		settings.setPluginCleanupEnabled(TEST_CLEANUP_ENABLED);
		settings.setPluginPackageExtension(TEST_EXTENSION);
		settings.setPluginWorkPath(TEST_PLUGIN_WORK_PATH);
		settings.setResourcePaths(resourcePaths);
		settings.setPluginPaths(pluginPaths);
		
		return settings;
	}
	
}
