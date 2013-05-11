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

package org.webical.test;

import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webical.ApplicationSettings;
import org.webical.User;
import org.webical.dao.hibernateImpl.UserDaoHibernateImpl;
import org.webical.manager.ApplicationSettingsManager;
import org.webical.manager.WebicalException;
import org.webical.settings.ApplicationSettingsFactory;

/**
 * Coolection of helpfull methods
 * @author ivo
 *
 */
public class TestUtils {

	/** port number tests */
	public static final int portNoTest = 10202;

	/** Base webical-core package */
	public static final String WEBICAL_BASE_PACKAGE = "org.webical";
	/** Base webical-test package */
	public static final String WEBICAL_TEST_BASE_PACKAGE = WEBICAL_BASE_PACKAGE + ".test";

	/** user base directory */
	public static final String SYSTEM_USER_DIR = "user.dir";

	/** Resource directory relative to the user.dir */
	public static final String RESOURCE_DIRECTORY = "target/test-classes/";

	public static String WORKINGDIRECTORY = "workingdirectory";

	/** log4j.properties file name */
	public static final String LOG4J_CONFIGURATION_FILE = "log4j.properties";

	public static final String USERID_WEBICAL = "webical";
	public static final String USERID_DEMO = "demo-user";
	public static final String USERID_JAG = "jag";

	private static Log log = LogFactory.getLog(TestUtils.class);

	/** Initialize the log4j configuration */
	static {
		System.setProperty("log4j.configuration", System.getProperty(SYSTEM_USER_DIR) + "/" + RESOURCE_DIRECTORY + LOG4J_CONFIGURATION_FILE);
	}

	/** Initializes the ApplicationSettingsFactory with a dummy ApplicationSettingsManager */
	public static void initializeApplicationSettingsFactory(final ApplicationSettings applicationSettings) {

		ApplicationSettingsFactory.getInstance().setApplicationSettingsManager(new ApplicationSettingsManager() {
			public ApplicationSettings getApplicationSettings() throws WebicalException {
				return applicationSettings;
			}
			public void storeApplicationSettings(ApplicationSettings applicationSettings) throws WebicalException {
			}
		});
	}

	/**
	 * Retrieve a user from the database
	 *
	 * @param userId - userId to retrieve
	 *
	 * @return user corresponding to userId, null if not found
	 */
	public static final User retrieveUser(String userId) {
		UserDaoHibernateImpl userDao = new UserDaoHibernateImpl();
		User user = null;
		try {
			user = userDao.getUser(userId);
		} catch (Exception e) {
			log.error(e,e);
		}
		return user;
	}

	/** get the webical user */
	public static final User getWebicalUser() {
		return retrieveUser(TestUtils.USERID_WEBICAL);
	}
	/** get the demo user */
	public static final User getDemoUser() {
		return retrieveUser(TestUtils.USERID_DEMO);
	}
	/** get the jag user */
	public static final User getJAGUser() {
		User user = retrieveUser(TestUtils.USERID_JAG);
		if (user == null)
		{
			user = new User();
			user.setUserId(USERID_JAG);
			user.setFirstName("James");
			user.setLastNamePrefix("A.");
			user.setLastName("Goslinga");
			user.setBirthDate(new GregorianCalendar(1960, GregorianCalendar.APRIL, 23).getTime());
		}
		return user;
	}
}
