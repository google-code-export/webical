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

package org.webical.test.web.pages;

import org.apache.wicket.Page;
import org.apache.wicket.util.tester.ITestPageSource;
import org.webical.web.pages.BasePage;
import org.webical.web.component.HeaderPanel;
import org.webical.web.component.calendar.CalendarPanel;
import org.webical.web.component.settings.SettingsPanelsPanel;
import org.webical.test.manager.impl.mock.MockCalendarManager;
import org.webical.test.manager.impl.mock.MockEventManager;
import org.webical.test.web.WebicalApplicationTest;

/**
 * Tester for the basePage
 * @author ivo
 *
 */
public class BasePageTest extends WebicalApplicationTest {

	/* (non-Javadoc)
	 * @see org.webical.test.test.web.WebicalApplicationTest#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		annotApplicationContextMock.putBean("calendarManager", new MockCalendarManager());
		annotApplicationContextMock.putBean("eventManager", new MockEventManager());
	}

	/**
	 * Tests if the page renders without errors
	 */
	public void testBasePageRenders() {

		//Set the testPage
		wicketTester.startPage(getTestPageSource());

		//Assert that the page renders without error messages
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
				 return new BasePage();
			};
		};
	}
}
