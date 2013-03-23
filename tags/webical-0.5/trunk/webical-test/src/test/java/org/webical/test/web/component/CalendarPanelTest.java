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

import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Page;
import org.apache.wicket.util.tester.ITestPageSource;
import org.webical.User;
import org.webical.Calendar;
import org.webical.web.action.IAction;
import org.webical.web.component.calendar.CalendarListPanel;
import org.webical.web.component.calendar.CalendarPanel;
import org.webical.web.component.calendar.DayViewPanel;
import org.webical.web.component.calendar.MonthViewPanel;
import org.webical.web.component.calendar.WeekViewPanel;
import org.webical.test.TestUtils;
import org.webical.test.manager.impl.mock.MockCalendarManager;
import org.webical.test.manager.impl.mock.MockEventManager;
import org.webical.test.web.PanelTestPage;
import org.webical.test.web.WebicalApplicationTest;

/**
* Tests for the SettingsPanelsPanel
* @author jochem
*
*/
public class CalendarPanelTest extends WebicalApplicationTest {
	private static Log log = LogFactory.getLog(CalendarPanelTest.class);

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		//Prepare the user
		User user = TestUtils.getJAGUser();
		getTestSession().createUser(user);
		getTestSession().getUserSettings();

		MockCalendarManager mockCalendarManager = new MockCalendarManager();

		Calendar calendar1 = new Calendar();
		calendar1.setName("Calendar one");
		calendar1.setType("ical-webdav");
		calendar1.setUrl("http://www.webical.org/calendar1.ics");
		calendar1.setUser(getTestSession().getUser());
		mockCalendarManager.storeCalendar(calendar1);

		annotApplicationContextMock.putBean("calendarManager", mockCalendarManager);
		annotApplicationContextMock.putBean("eventManager", new MockEventManager());
	}

	/**
	 * Test whether the EventViewsPanel Renders correctly
	 */
	public void testDefaultRender() {

		log.debug("testDefaultRender");
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
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":calendarPanel", CalendarListPanel.class);
	}

	/**
	 * Test view switching
	 */
	public void testViewSwitch() {

		log.debug("testViewSwitch");
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
		switch (getTestSession().getUserSettings().getDefaultCalendarView()) {
			case CalendarPanel.DAY_VIEW:
				wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewPanel:panel", DayViewPanel.class);
				break;
			case CalendarPanel.WEEK_VIEW:
				wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewPanel:panel", WeekViewPanel.class);
				break;
			case CalendarPanel.MONTH_VIEW:
				wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewPanel:panel", MonthViewPanel.class);
				break;
			case CalendarPanel.AGENDA_VIEW:
				wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewPanel:panel", WeekViewPanel.class);
				break;
		}

		// Click the Day tab
		wicketTester.clickLink(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewPanel:tabs-container:tabs:0:link");
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewPanel:panel", DayViewPanel.class);

		// Click the Week tab
		wicketTester.clickLink(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewPanel:tabs-container:tabs:1:link");
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewPanel:panel", WeekViewPanel.class);

		// Click the Month tab
		wicketTester.clickLink(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewPanel:tabs-container:tabs:2:link");
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewPanel:panel", MonthViewPanel.class);

		// Click the Agenda tab
		wicketTester.clickLink(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewPanel:tabs-container:tabs:3:link");
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":calendarViewPanel:panel", WeekViewPanel.class);
	}

	/**
	 * Test action handling of the panel
	 */
	public void testActionHandling() {

		log.debug("testActionHandling");
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
		// TODO mattijs: implement this with onAction
	}
}
