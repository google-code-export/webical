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

package org.webical.web.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.util.tester.ITestPageSource;
import org.webical.ApplicationSettings;
import org.webical.Calendar;
import org.webical.TestUtils;
import org.webical.User;
import org.webical.UserSettings;
import org.webical.manager.WebicalException;
import org.webical.manager.impl.mock.MockCalendarManager;
import org.webical.manager.impl.mock.MockEventManager;
import org.webical.manager.impl.mock.MockSettingsManager;
import org.webical.manager.impl.mock.MockUserManager;
import org.webical.web.WebicalApplicationTest;
import org.webical.web.app.WebicalSession;
import org.webical.web.component.HeaderPanel;
import org.webical.web.component.calendar.CalendarListPanel;
import org.webical.web.component.calendar.CalendarPanel;
import org.webical.web.component.settings.SettingsPanelsPanel;

/**
 * Tester for the basePage
 * @author ivo
 *
 */
public class BasePageTest extends WebicalApplicationTest {



	/* (non-Javadoc)
	 * @see org.webical.web.WebicalApplicationTest#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ApplicationSettings applicationSettings = new ApplicationSettings();
		TestUtils.initializeApplicationSettingsFactory(applicationSettings);

		annotApplicationContextMock.putBean("calendarManager", new MockCalendarManager() {

			@Override
			public List<Calendar> getCalendars(User user) throws WebicalException {
				return new ArrayList<Calendar>();
			}

		});
		annotApplicationContextMock.putBean("eventManager", new MockEventManager());
		annotApplicationContextMock.putBean("userManager", new MockUserManager() {

			@Override
			public User getUser(String userId) throws WebicalException {
				return webicalSession.getUser();
			}

		});
		annotApplicationContextMock.putBean("settingsManager", new MockSettingsManager() {

			@Override
			public UserSettings getUserSettings(User user) throws WebicalException {
				UserSettings mockSettings = new UserSettings(user);
				mockSettings.createDefaultSettings();
				return mockSettings;
			}

		});

	}


	/**
	 * Tests if the page renders without errors
	 */
	public void testBasePageRenders() {

		//Set the testPage
		wicketTester.startPage(getTestPageSource());

		//Assert that the page renders without error messagges
		wicketTester.assertNoErrorMessage();

		//Check if all the components are there
		wicketTester.assertComponent("headerPanel", HeaderPanel.class);
		wicketTester.assertComponent("contentPanel", SettingsPanelsPanel.class);
	}


	/**
	 * Tests the navigation links
	 */
	public void testBasePageLinks() {

		//Set the testPage
		wicketTester.startPage(getTestPageSource());

		//Try the settings link
		wicketTester.clickLink("headerPanel:settingsPanelLink");
		wicketTester.assertComponent("contentPanel", SettingsPanelsPanel.class);

		//Try the CalendarViews Link
		wicketTester.clickLink("headerPanel:calendarViewsPanelLink");
		wicketTester.assertComponent("contentPanel", CalendarPanel.class);

	}

	/**
	 * Creates an ITestPageSource returning a BasePage ready to be tested
	 * @return an Extended BasePage, overriding the initSession method
	 */
	private ITestPageSource getTestPageSource() {
		/* override the initSession method because there wont be an UserPrincipal in the test Request */
		return new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				 return new BasePage() {
					private static final long serialVersionUID = 1L;

					@Override
					public void initSession() {
						WebicalSession webicalSession = (WebicalSession)getSession();
						webicalSession.setUser(new User());
					}
				 };
			};
		};
	}

}
