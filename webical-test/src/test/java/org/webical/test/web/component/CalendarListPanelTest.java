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
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.tester.ITestPageSource;
import org.webical.User;
import org.webical.Calendar;
import org.webical.manager.WebicalException;
import org.webical.web.action.IAction;
import org.webical.web.component.calendar.CalendarListPanel;
import org.webical.test.TestUtils;
import org.webical.test.manager.impl.mock.MockCalendarManager;
import org.webical.test.web.PanelTestPage;
import org.webical.test.web.WebicalApplicationTest;

/**
 *
 * @author paul
 *
 */
public class CalendarListPanelTest extends WebicalApplicationTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		//Prepare the user
		User user = TestUtils.getJAGUser();
		getTestSession().createUser(user);
		getTestSession().getUserSettings();
	}

	/**
	 * Test whether the panel renders correct without calendars
	 */
	public void testWithoutCalendar() {

		annotApplicationContextMock.putBean("calendarManager", new MockCalendarManager());

		//Create the testpage with our panel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;
			public Page getTestPage() {
				return new PanelTestPage(new CalendarListPanel(PanelTestPage.PANEL_MARKUP_ID, new ArrayList<Calendar>()) {
					private static final long serialVersionUID = 1L;
					@Override
					public void enableAllCalendars(AjaxRequestTarget target) { /* NOTHING TO DO */ }
					@Override
					public void enableOnlyThisCalendar(Calendar calendar, AjaxRequestTarget target) { /* NOTHING TO DO */ }
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		//Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, CalendarListPanel.class);

		// Assert the number of calendars in the CalendarListPanel
		wicketTester.assertListView(PanelTestPage.PANEL_MARKUP_ID + ":calendarListview", new ArrayList<Calendar>());
	}

	/**
	 * Test whether the panel renders correctly with calendars
	 */
	public void testWithCalendars() throws WebicalException {

		final List<Calendar> calendars = new ArrayList<Calendar>();

		MockCalendarManager mockCalendarManager = new MockCalendarManager();
		annotApplicationContextMock.putBean("calendarManager", mockCalendarManager);

		Calendar calendar1 = new Calendar();
		calendar1.setName("Calendar one");
		calendar1.setType("ical-webdav");
		calendar1.setUrl("http://www.webical.org/calendar1.ics");
		calendar1.setUser(getTestSession().getUser());
		mockCalendarManager.storeCalendar(calendar1);
		calendars.add(calendar1);
		Calendar calendar2 = new Calendar();
		calendar2.setName("Calendar two");
		calendar2.setType("ical-webdav");
		calendar2.setUrl("http://www.webical.org/calendar2.ics");
		calendar2.setUser(getTestSession().getUser());
		mockCalendarManager.storeCalendar(calendar2);
		calendars.add(calendar2);

		//Create the testpage with our panel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new CalendarListPanel(PanelTestPage.PANEL_MARKUP_ID, calendars) {
					private static final long serialVersionUID = 1L;
					@Override
					public void enableAllCalendars(AjaxRequestTarget target) { /* NOTHING TO DO */ }
					@Override
					public void enableOnlyThisCalendar(Calendar calendar, AjaxRequestTarget target) { /* NOTHING TO DO */ }
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		//Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, CalendarListPanel.class);

		// Assert the calendars in the list
		wicketTester.assertListView(PanelTestPage.PANEL_MARKUP_ID + ":calendarListview", calendars);

		// Click the "add calendar" link
		wicketTester.clickLink(PanelTestPage.PANEL_MARKUP_ID + ":addCalendarLink");
	}
}
