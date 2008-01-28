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
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.util.tester.ITestPageSource;
import org.webical.Calendar;
import org.webical.User;
import org.webical.manager.WebicalException;
import org.webical.manager.impl.mock.MockCalendarManager;
import org.webical.manager.impl.mock.MockEventManager;
import org.webical.manager.impl.mock.MockUserManager;
import org.webical.web.PanelTestPage;
import org.webical.web.WebicalApplicationTest;
import org.webical.web.action.IAction;
import org.webical.web.component.calendar.CalendarPanel;
import org.webical.web.component.calendar.DayViewPanel;
import org.webical.web.component.calendar.MonthViewPanel;
import org.webical.web.component.calendar.WeekViewPanel;

/**
* Tests for the SettingsPanelsPanel
* @author jochem
*
*/
public class CalendarPanelTest extends WebicalApplicationTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// Prepare a User
		final User user = new User();
		user.setFirstName("James");
		user.setLastName("Gossling");
		user.setUserId("jag");
		webicalSession.setUser(user);

		annotApplicationContextMock.putBean("eventManager", new MockEventManager());
		annotApplicationContextMock.putBean("userManager", new MockUserManager());
		annotApplicationContextMock.putBean("calendarManager", new MockCalendarManager() {

			@Override
			public List<Calendar> getCalendars(User user) throws WebicalException {
				List<Calendar> calendars = new ArrayList<Calendar>();
				Calendar calendar = new Calendar();
				calendar.setName("only_calednar");
				calendar.setUser(user);
				calendars.add(calendar);
				return calendars;
			}

		});

	}

	/**
	 * Test whether the EventViewsPanel Renders correctly
	 */
	public void testDefaultRender() {

		// Create testpage with a CalendarPanel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new CalendarPanel(PanelTestPage.PANEL_MARKUP_ID, new GregorianCalendar()) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		// Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, CalendarPanel.class);
	}

	/**
	 * Test view switching
	 */
	public void testViewSwitch() {

		// Create testpage with a CalendarPanel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new CalendarPanel(PanelTestPage.PANEL_MARKUP_ID, new GregorianCalendar()) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		// Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, CalendarPanel.class);

		// Check default view
		switch(webicalSession.getUser().getDefaultCalendarView()) {
			case CalendarPanel.DAY_VIEW:
				wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewsTabs:panel", DayViewPanel.class);
			break;
			case CalendarPanel.WEEK_VIEW:
				wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewsTabs:panel", WeekViewPanel.class);
			break;
			case CalendarPanel.MONTH_VIEW:
				wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewsTabs:panel", MonthViewPanel.class);
			break;
			case CalendarPanel.AGENDA_VIEW:
				wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewsTabs:panel", WeekViewPanel.class);
			break;
		}

		// Click the Day tab
		wicketTester.clickLink(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewsTabs:tabs-container:tabs:0:link");
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewsTabs:panel", DayViewPanel.class);

		// Click the Week tab
		wicketTester.clickLink(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewsTabs:tabs-container:tabs:1:link");
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewsTabs:panel", WeekViewPanel.class);

		// Click the Month tab
		wicketTester.clickLink(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewsTabs:tabs-container:tabs:2:link");
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewsTabs:panel", MonthViewPanel.class);

		// Click the Agenda tab
		wicketTester.clickLink(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewsTabs:tabs-container:tabs:3:link");
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewsTabs:panel", WeekViewPanel.class);

	}

	/**
	 * Test action handling of the panel
	 */
	public void testActionHandling() {

		// Create testpage with a CalendarPanel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new CalendarPanel(PanelTestPage.PANEL_MARKUP_ID, new GregorianCalendar()) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		// Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, CalendarPanel.class);

		// Fire Actions
		// TODO mattijs: implement this
		//WeekViewPanel weekViewPanel = (WeekViewPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewsTabs:panel");
		//System.out.println(weekViewPanel);
		//weekViewPanel.onAction(new DaySelectedAction(new GregorianCalendar()));

	}
}
