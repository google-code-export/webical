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
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Page;
import org.apache.wicket.util.tester.ITestPageSource;
import org.webical.User;
import org.webical.Calendar;
import org.webical.Event;
import org.webical.ical.Recurrence;
import org.webical.ical.RecurrenceUtil;
import org.webical.manager.WebicalException;
import org.webical.util.CalendarUtils;
import org.webical.web.action.IAction;
import org.webical.web.component.calendar.MonthDayPanel;
import org.webical.web.component.calendar.MonthViewPanel;
import org.webical.test.TestUtils;
import org.webical.test.manager.impl.mock.MockCalendarManager;
import org.webical.test.manager.impl.mock.MockEventManager;
import org.webical.test.web.PanelTestPage;
import org.webical.test.web.WebicalApplicationTest;


/**
 * Test for the CalendarMonthViewPanel
 *
 * @author paul
 * @author Mattijs Hoitink
 */
public class MonthViewPanelTest extends WebicalApplicationTest {

	private static Log log = LogFactory.getLog(MonthViewPanelTest.class);

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		log.debug("setUp");

		//Prepare the user
		User user = TestUtils.getJAGUser();
		getTestSession().createUser(user);
		getTestSession().getUserSettings();

		//Prepare the calendars
		MockCalendarManager mockCalendarManager = new MockCalendarManager();
		annotApplicationContextMock.putBean("calendarManager", mockCalendarManager);

		Calendar calendar1 = new Calendar();
		calendar1.setCalendarId(1L);
		calendar1.setName("Calendar one");
		calendar1.setType("ical-webdav");
		calendar1.setUrl("http://www.webical.org/calendar1.ics");
		calendar1.setUser(getTestSession().getUser());
		mockCalendarManager.storeCalendar(calendar1);
	}

	/**
	 * Test rendering of the panel without events.
	 */
	public void testRenderingWithoutEvents() {
		log.debug("testRenderingWithoutEvents");

		// Add an EventManager
		annotApplicationContextMock.putBean("eventManager", new MockEventManager());

		// render start page with a MonthViewPanel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new MonthViewPanel(PanelTestPage.PANEL_MARKUP_ID, 1, new GregorianCalendar()) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		// Basic Assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, MonthViewPanel.class);
	}

	/**
	 * Test rendering with events.
	 * @throws WebicalException
	 */
	public void testRenderingWithEvents() throws WebicalException {
		log.debug("testRenderingWithEvents");

		MockCalendarManager mockCalendarManager = (MockCalendarManager)	annotApplicationContextMock.getBean("calendarManager");
		Calendar calendar1 = mockCalendarManager.getCalendarById("1");

		MockEventManager mockEventManager = new MockEventManager();
		annotApplicationContextMock.putBean("eventManager", mockEventManager);

		// FIXME mattijs: test fails when run at 23:30 (probably also between 22:00 and 00:00)
		// List for the day containing the normal event
		List<Event> randomDayEventsList = new ArrayList<Event>();

		// List for the day containing both recurring events
		List<Event> bothRecurringEventsList = new ArrayList<Event>();

		// List for the day containing the long recurring event
		List<Event> oneRecurringEventsList = new ArrayList<Event>();

		List<Event> allEvents = new ArrayList<Event>();
		// Add a normal event
		Event event = new Event();
		event.setUid("e1");
		event.setCalendar(calendar1);
		event.setSummary("Normal Event Description");
		event.setLocation("Normal Event Location");
		event.setDescription("Event e1");

		GregorianCalendar cal = new GregorianCalendar();
		cal.set(GregorianCalendar.DAY_OF_MONTH, 15);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 12);
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.HOUR_OF_DAY, 2);
		event.setDtEnd(cal.getTime());
		allEvents.add(event);
		randomDayEventsList.add(event);
		mockEventManager.storeEvent(event);

		// Add a short recurring event, starting yesterday, ending tomorrow
		event = new Event();
		event.setUid("e2");
		event.setCalendar(calendar1);
		event.setSummary("Recurring Event Yesterday");
		event.setLocation("Recurring Event Location");
		event.setDescription("Event e2");

		cal = new GregorianCalendar();
		cal.set(GregorianCalendar.DAY_OF_MONTH, 14);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 14);
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.HOUR_OF_DAY,	2);
		cal.set(GregorianCalendar.DAY_OF_MONTH, 17);
		event.setDtEnd(cal.getTime());
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(Recurrence.DAILY, 1, cal.getTime()));

		allEvents.add(event);
		randomDayEventsList.add(event);
		bothRecurringEventsList.add(event);
		mockEventManager.storeEvent(event);

		// Add a long recurring event, starting last month, ending next month
		event = new Event();
		event.setUid("e3");
		event.setCalendar(calendar1);
		event.setSummary("Recurring Event Last Month");
		event.setLocation("Recurring Event Location");
		event.setDescription("Event e3");

		cal = new GregorianCalendar();
		cal.set(GregorianCalendar.DAY_OF_MONTH, 15);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 10);
		cal.add(GregorianCalendar.MONTH, -1);
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.HOUR_OF_DAY,	2);
		cal.add(GregorianCalendar.MONTH, 2);
		event.setDtEnd(cal.getTime());
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(Recurrence.DAILY, 1, cal.getTime()));

		allEvents.add(event);
		randomDayEventsList.add(event);
		bothRecurringEventsList.add(event);
		oneRecurringEventsList.add(event);
		mockEventManager.storeEvent(event);

		final GregorianCalendar currentDate = CalendarUtils.newTodayCalendar(getTestSession().getUserSettings().getFirstDayOfWeek());

		// Render test page with a MonthViewPanel
		wicketTester.startPage(new ITestPageSource(){
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				MonthViewPanel monthViewPanel = new MonthViewPanel(PanelTestPage.PANEL_MARKUP_ID, 1, currentDate) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				};
				return new PanelTestPage(monthViewPanel);
			}
		});

		// Basic Assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, MonthViewPanel.class);

		// Assert number of days rendered
		GregorianCalendar monthFirstDayDate = new GregorianCalendar();
		monthFirstDayDate.setFirstDayOfWeek(getTestSession().getUserSettings().getFirstDayOfWeek());
		GregorianCalendar monthLastDayDate = new GregorianCalendar();
		monthLastDayDate.setFirstDayOfWeek(getTestSession().getUserSettings().getFirstDayOfWeek());
		GregorianCalendar normalEventCal = new GregorianCalendar();
		normalEventCal.setFirstDayOfWeek(getTestSession().getUserSettings().getFirstDayOfWeek());
		GregorianCalendar shortRecurringEventCal = new GregorianCalendar();
		shortRecurringEventCal.setFirstDayOfWeek(getTestSession().getUserSettings().getFirstDayOfWeek());

		// Set the correct dates to find the first and last day of the month
		monthFirstDayDate.setTime(CalendarUtils.getFirstDayOfWeekOfMonth(currentDate.getTime(), getTestSession().getUserSettings().getFirstDayOfWeek()));
		monthLastDayDate.setTime(CalendarUtils.getLastWeekDayOfMonth(currentDate.getTime(), getTestSession().getUserSettings().getFirstDayOfWeek()));

		// Assert the first day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + monthFirstDayDate.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + monthFirstDayDate.get(GregorianCalendar.DAY_OF_YEAR), MonthDayPanel.class);

		// Assert the last day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + monthLastDayDate.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + monthLastDayDate.get(GregorianCalendar.DAY_OF_YEAR), MonthDayPanel.class);

		// Assert normal event
		normalEventCal.set(GregorianCalendar.DAY_OF_MONTH, 15);
		MonthDayPanel normalDayEventsListView = (MonthDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + normalEventCal.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + normalEventCal.get(GregorianCalendar.DAY_OF_YEAR));
		wicketTester.assertListView(normalDayEventsListView.getPageRelativePath() + ":eventItem", randomDayEventsList);

		// Assert short recurring event
		shortRecurringEventCal.set(GregorianCalendar.DAY_OF_MONTH, 13);
		MonthDayPanel beforeStartDayPanel = (MonthDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + shortRecurringEventCal.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + shortRecurringEventCal.get(GregorianCalendar.DAY_OF_YEAR));
		wicketTester.assertListView(beforeStartDayPanel.getPageRelativePath() + ":eventItem", oneRecurringEventsList);

		shortRecurringEventCal.set(GregorianCalendar.DAY_OF_MONTH, 14);
		MonthDayPanel startDayPanel = (MonthDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + shortRecurringEventCal.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + shortRecurringEventCal.get(GregorianCalendar.DAY_OF_YEAR));
		wicketTester.assertListView(startDayPanel.getPageRelativePath() + ":eventItem", bothRecurringEventsList);

		shortRecurringEventCal.set(GregorianCalendar.DAY_OF_MONTH, 16);
		MonthDayPanel endDayPanel = (MonthDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + shortRecurringEventCal.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + shortRecurringEventCal.get(GregorianCalendar.DAY_OF_YEAR));
		wicketTester.assertListView(endDayPanel.getPageRelativePath() + ":eventItem", bothRecurringEventsList);

		shortRecurringEventCal.set(GregorianCalendar.DAY_OF_MONTH, 17);
		MonthDayPanel afterEndDayPanel = (MonthDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + shortRecurringEventCal.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + shortRecurringEventCal.get(GregorianCalendar.DAY_OF_YEAR));
		wicketTester.assertListView(afterEndDayPanel.getPageRelativePath() + ":eventItem", oneRecurringEventsList);

		// Assert long recurring event
		MonthDayPanel firstMonthDayPanel = (MonthDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + monthFirstDayDate.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + monthFirstDayDate.get(GregorianCalendar.DAY_OF_YEAR));
		MonthDayPanel lastMonthDayPanel = (MonthDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + monthLastDayDate.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + monthLastDayDate.get(GregorianCalendar.DAY_OF_YEAR));

		wicketTester.assertListView(firstMonthDayPanel.getPageRelativePath() + ":eventItem", oneRecurringEventsList);
		wicketTester.assertListView(lastMonthDayPanel.getPageRelativePath() + ":eventItem", oneRecurringEventsList);
	}
}
