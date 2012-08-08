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

package org.webical.test.web.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.util.tester.ITestPageSource;
import org.webical.Calendar;
import org.webical.User;
import org.webical.manager.WebicalException;
import org.webical.web.action.IAction;
import org.webical.web.component.settings.SettingsCalendarListPanel;
import org.webical.test.TestUtils;
import org.webical.test.manager.impl.mock.MockCalendarManager;
import org.webical.test.web.PanelTestPage;
import org.webical.test.web.WebicalApplicationTest;

/**
 * @author ivo
 *
 */
public class SettingsCalendarListPanelTest extends WebicalApplicationTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		//Prepare the user
		User user = TestUtils.getJAGUser();
		getTestSession().createUser(user);
		getTestSession().getUserSettings();
	}

	/**
	 * Test whether the panel renders correctly without calendars
	 */
	public void testWithoutCalendars() {
		annotApplicationContextMock.putBean("calendarManager", new MockCalendarManager());

		// Create testpage with a SettingsCalendarListPanel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new SettingsCalendarListPanel(PanelTestPage.PANEL_MARKUP_ID) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		// Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, SettingsCalendarListPanel.class);
	}

	/**
	 * Test whether the panel renders correctly with calendars
	 */
	public void testWithCalendars() throws WebicalException {
		List<Calendar> calendars = new ArrayList<Calendar>();

		MockCalendarManager mockCalendarManager = new MockCalendarManager();
		annotApplicationContextMock.putBean("calendarManager", mockCalendarManager);

		Calendar calendar1 = new Calendar();
		calendar1.setName("Calendar one");
		calendar1.setUser(getTestSession().getUser());
		mockCalendarManager.storeCalendar(calendar1);
		calendars.add(calendar1);
		Calendar calendar2 = new Calendar();
		calendar2.setName("Calendar two");
		calendar2.setUser(getTestSession().getUser());
		mockCalendarManager.storeCalendar(calendar2);
		calendars.add(calendar2);

		// Create testpage with a SettingsCalendarListPanel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new SettingsCalendarListPanel(PanelTestPage.PANEL_MARKUP_ID) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		// Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, SettingsCalendarListPanel.class);

		// Assert the number of calendars on the panel
		wicketTester.assertListView(PanelTestPage.PANEL_MARKUP_ID + ":calendarListView", calendars);
	}
}
