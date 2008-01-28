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

package org.webical.web.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.tester.ITestPageSource;
import org.webical.Calendar;
import org.webical.Event;
import org.webical.User;
import org.webical.manager.CalendarManager;
import org.webical.manager.WebicalException;
import org.webical.manager.impl.mock.MockUserManager;
import org.webical.web.PanelTestPage;
import org.webical.web.WebicalApplicationTest;
import org.webical.web.component.calendar.CalendarListPanel;

/**
 *
 * @author paul
 *
 */
public class CalendarListPanelTest extends WebicalApplicationTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		annotApplicationContextMock.putBean("userManager", new MockUserManager());
	}

	/**
	 * Test whether the panel renders correct without calendars
	 */
	public void testWithoutCalendar() {
		// Prepare a User
		final User user = new User();
		user.setFirstName("James");
		user.setLastName("Gosling");
		user.setUserId("jag");

		annotApplicationContextMock.putBean("calendarManager", getMockCalendarManager());

		//Create the testpage with our panel
		wicketTester.startPage(new ITestPageSource(){
			private static final long serialVersionUID = 1L;
			public Page getTestPage() {
				return new PanelTestPage(new CalendarListPanel(PanelTestPage.PANEL_MARKUP_ID, new ArrayList<Calendar>()) {
					private static final long serialVersionUID = 1L;
					@Override
					public void enableAllCalendars(AjaxRequestTarget target) { /* NOTHING TO DO */ }
					@Override
					public void enableOnlyThisCalendar(Calendar calendar, AjaxRequestTarget target) { /* NOTHING TO DO */ }
					@Override
					public void selectCalendarForEdit(Calendar calendar, AjaxRequestTarget target) { /* NOTHING TO DO */ }
					@Override
					public void switchCalendarVisibility(Calendar calendar, AjaxRequestTarget target) { /* NOTHING TO DO */ }

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
	public void testWithCalendars() {
		// Prepare a User
		final User user = new User();
		user.setFirstName("James");
		user.setLastName("Gosling");
		user.setUserId("jag");

		final List<Calendar> calendars = new ArrayList<Calendar>();

		annotApplicationContextMock.putBean("calendarManager", new CalendarManager() {
			public void storeCalendar(Calendar calendar) throws WebicalException { /* NOTHING TO DO */ }
			public void removeCalendar(Calendar calendar) throws WebicalException { /* NOTHING TO DO */ }

			public List<Calendar> getCalendars(User user) throws WebicalException {
				Calendar calendar = new Calendar();
				calendar.setName("Calendar One");
				calendars.add(calendar);
				calendar = new Calendar();
				calendar.setName("Calendar Two");
				calendars.add(calendar);

				return calendars;
			}

			public Set<String> getAvailableCalendarTypes() throws WebicalException { return null; }
			public Calendar getCalendarForEvent(Event event) { return null; }
			public void storeCalendar(Calendar calendar, List<Event> events) throws WebicalException { /* NOTHING TO DO */ }
			public Calendar getCalendarById(String id) throws WebicalException { return null; }
		});

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
					public void selectCalendarForEdit(Calendar calendar, AjaxRequestTarget target) { /* NOTHING TO DO */ }
					@Override
					public void switchCalendarVisibility(Calendar calendar, AjaxRequestTarget target) { /* NOTHING TO DO */ }

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

	/**
	 * Creates a CalendarManager
	 * @return A CalendarManager
	 */
	private CalendarManager getMockCalendarManager() {
		return new CalendarManager() {
			public void storeCalendar(Calendar calendar) throws WebicalException { /* NOTHING TO DO */ }
			public void removeCalendar(Calendar calendar) throws WebicalException { /* NOTHING TO DO */ }
			public List<Calendar> getCalendars(User user) throws WebicalException { return new ArrayList<Calendar>(); }
			public Set<String> getAvailableCalendarTypes() throws WebicalException { return null; }
			public Calendar getCalendarForEvent(Event event) { return null; }
			public void storeCalendar(Calendar calendar, List<Event> events) throws WebicalException { /* NOTHING TO DO */	}
			public Calendar getCalendarById(String id) throws WebicalException { return null; }
		};
	}

}
