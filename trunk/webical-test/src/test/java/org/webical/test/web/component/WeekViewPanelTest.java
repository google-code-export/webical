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
import java.util.Date;
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
import org.webical.web.component.calendar.WeekDayPanel;
import org.webical.web.component.calendar.WeekViewPanel;
import org.webical.test.TestUtils;
import org.webical.test.manager.impl.mock.MockCalendarManager;
import org.webical.test.manager.impl.mock.MockEventManager;
import org.webical.test.web.PanelTestPage;
import org.webical.test.web.WebicalApplicationTest;


public class WeekViewPanelTest extends WebicalApplicationTest {

	private static Log log = LogFactory.getLog(WeekViewPanelTest.class);

	@Override
	protected void setUp() throws Exception {
		super.setUp();

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

	public void testWithoutEvents() {

		annotApplicationContextMock.putBean("eventManager", new MockEventManager());

		// Create testpage with a WeekViewPanel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new WeekViewPanel(PanelTestPage.PANEL_MARKUP_ID, 7, new GregorianCalendar()) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		// Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, WeekViewPanel.class);

		// Assert the heading label
	}

	public void testWeekUse() throws WebicalException {

		MockCalendarManager mockCalendarManager = (MockCalendarManager)	annotApplicationContextMock.getBean("calendarManager");
		Calendar calendar1 = mockCalendarManager.getCalendarById("1");

		MockEventManager mockEventManager = new MockEventManager();
		annotApplicationContextMock.putBean("eventManager", mockEventManager);

		// List for the day containing the normal event
		final List<Event> randomDayEventsList = new ArrayList<Event>();

		// List for the day containing both recurring events
		final List<Event> bothRecurringEventsList = new ArrayList<Event>();

		// List for the day containing the long recurring event
		final List<Event> oneRecurringEventList = new ArrayList<Event>();

		final List<Event> allEvents = new ArrayList<Event>();

		final GregorianCalendar currentDate = CalendarUtils.newTodayCalendar(getTestSession().getUserSettings().getFirstDayOfWeek());
		Date midWeek = CalendarUtils.getFirstDayOfWeek(currentDate.getTime(), getTestSession().getUserSettings().getFirstDayOfWeek());
		midWeek = CalendarUtils.addDays(midWeek, 3);

		/* CREATE EVENTS TO RENDER */
		GregorianCalendar cal = new GregorianCalendar();
		// Add a normal event this week
		Event event = new Event();
		event.setUid("e1");
		event.setCalendar(calendar1);
		event.setSummary("Normal Event Description");
		event.setLocation("Normal Event Location");
		event.setDescription("Event e1");

		cal.setTime(midWeek);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 12);
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.HOUR_OF_DAY, 2);
		event.setDtEnd(cal.getTime());

		log.debug("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEvents.add(event);
		randomDayEventsList.add(event);
		mockEventManager.storeEvent(event);

		// Add a short recurring event, starting midweek - 1, ending midweek + 2
		event = new Event();
		event.setUid("e2");
		event.setCalendar(calendar1);
		event.setSummary("Recurring Event Yesterday");
		event.setLocation("Recurring Event Location");
		event.setDescription("Event e2");

		cal.setTime(midWeek);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 15);
		cal.add(GregorianCalendar.DAY_OF_MONTH, -1);
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.HOUR_OF_DAY,	2);
		cal.add(GregorianCalendar.DAY_OF_MONTH, 3);
		event.setDtEnd(cal.getTime());
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(Recurrence.DAILY, 1, cal.getTime()));

		log.debug("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
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

		cal.setTime(midWeek);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 9);
		cal.add(GregorianCalendar.WEEK_OF_YEAR, -1);
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.HOUR_OF_DAY,	2);
		cal.add(GregorianCalendar.WEEK_OF_YEAR, 3);
		event.setDtEnd(cal.getTime());
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(Recurrence.DAILY, 1, cal.getTime()));

		log.debug("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEvents.add(event);
		randomDayEventsList.add(event);
		bothRecurringEventsList.add(event);
		oneRecurringEventList.add(event);
		mockEventManager.storeEvent(event);

		// Create testpage with a WeekViewPanel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new WeekViewPanel(PanelTestPage.PANEL_MARKUP_ID, 7, currentDate) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		// Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, WeekViewPanel.class);

		// Assert number of days rendered
		GregorianCalendar weekFirstDayCalendar = new GregorianCalendar();
		weekFirstDayCalendar.setFirstDayOfWeek(getTestSession().getUserSettings().getFirstDayOfWeek());

		GregorianCalendar weekLastDayCalendar = new GregorianCalendar();
		weekLastDayCalendar.setFirstDayOfWeek(getTestSession().getUserSettings().getFirstDayOfWeek());

		GregorianCalendar normalEventCalendar = new GregorianCalendar();
		normalEventCalendar.setFirstDayOfWeek(getTestSession().getUserSettings().getFirstDayOfWeek());

		GregorianCalendar shortRecurringEventCalendar = new GregorianCalendar();
		shortRecurringEventCalendar.setFirstDayOfWeek(getTestSession().getUserSettings().getFirstDayOfWeek());

		// Set the correct dates to find the first and last day of the week
		weekFirstDayCalendar.setTime(CalendarUtils.getFirstDayOfWeek(currentDate.getTime(), getTestSession().getUserSettings().getFirstDayOfWeek()));
		weekLastDayCalendar.setTime(CalendarUtils.getLastDayOfWeek(currentDate.getTime(), getTestSession().getUserSettings().getFirstDayOfWeek()));

		// Assert the first day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + weekFirstDayCalendar.get(GregorianCalendar.DAY_OF_YEAR), WeekDayPanel.class);

		// Assert the last day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + weekLastDayCalendar.get(GregorianCalendar.DAY_OF_YEAR), WeekDayPanel.class);

		// Assert normal event
		normalEventCalendar.setTime(midWeek);

		WeekDayPanel randomDayEventsListView = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + normalEventCalendar.get(GregorianCalendar.DAY_OF_YEAR));
		wicketTester.assertListView(randomDayEventsListView.getPageRelativePath() + ":eventItem", randomDayEventsList);

		// Assert short recurring event
		shortRecurringEventCalendar.setTime(CalendarUtils.addDays(midWeek, -2));
		WeekDayPanel beforeStartDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + shortRecurringEventCalendar.get(GregorianCalendar.DAY_OF_YEAR));
		wicketTester.assertListView(beforeStartDayPanel.getPageRelativePath() + ":eventItem", oneRecurringEventList);

		shortRecurringEventCalendar.setTime(CalendarUtils.addDays(midWeek, -1));
		WeekDayPanel startDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + shortRecurringEventCalendar.get(GregorianCalendar.DAY_OF_YEAR));
		wicketTester.assertListView(startDayPanel.getPageRelativePath() + ":eventItem", bothRecurringEventsList);

		shortRecurringEventCalendar.setTime(CalendarUtils.addDays(midWeek, 1));
		WeekDayPanel endDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + shortRecurringEventCalendar.get(GregorianCalendar.DAY_OF_YEAR));
		wicketTester.assertListView(endDayPanel.getPageRelativePath() + ":eventItem", bothRecurringEventsList);

		shortRecurringEventCalendar.setTime(CalendarUtils.addDays(midWeek, 2));
		WeekDayPanel afterEndDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + shortRecurringEventCalendar.get(GregorianCalendar.DAY_OF_YEAR));
		wicketTester.assertListView(afterEndDayPanel.getPageRelativePath() + ":eventItem", oneRecurringEventList);

		// Assert long recurring event
		WeekDayPanel firstWeekDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + weekFirstDayCalendar.get(GregorianCalendar.DAY_OF_YEAR));
		WeekDayPanel lastWeekDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + weekLastDayCalendar.get(GregorianCalendar.DAY_OF_YEAR));

		wicketTester.assertListView(firstWeekDayPanel.getPageRelativePath() + ":eventItem", oneRecurringEventList);
		wicketTester.assertListView(lastWeekDayPanel.getPageRelativePath() + ":eventItem", oneRecurringEventList);
	}

	/**
	 * Test week view with less then 7 days
	 * @throws WebicalException
	 */
	public void testNonWeekUse() throws WebicalException {

		MockCalendarManager mockCalendarManager = (MockCalendarManager)	annotApplicationContextMock.getBean("calendarManager");
		Calendar calendar1 = mockCalendarManager.getCalendarById("1");

		MockEventManager mockEventManager = new MockEventManager();
		annotApplicationContextMock.putBean("eventManager", mockEventManager);

		// Define the current date
		final GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.setFirstDayOfWeek(GregorianCalendar.MONDAY);

		// The lists containing the different number of events
		List<Event> allEventsList = new ArrayList<Event>();
		List<Event> oneEventsList = new ArrayList<Event>();
		List<Event> twoEventsList = new ArrayList<Event>();

		GregorianCalendar refcal = new GregorianCalendar();
		refcal.add(GregorianCalendar.DAY_OF_MONTH, 1);
		refcal.set(GregorianCalendar.HOUR_OF_DAY, 12);
		refcal.set(GregorianCalendar.MINUTE, 0);
		refcal.set(GregorianCalendar.SECOND, 0);
		GregorianCalendar cal = new GregorianCalendar();
		
		// Add a normal event
		Event event = new Event();
		event.setUid("e1");
		event.setCalendar(calendar1);
		event.setSummary("Normal Event Description");
		event.setLocation("Normal Event Location");
		event.setDescription("Event e1");

		cal.setTime(refcal.getTime());
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.HOUR_OF_DAY, 2);
		event.setDtEnd(cal.getTime());

		log.debug("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEventsList.add(event);
		mockEventManager.storeEvent(event);

		// Add a short recurring event, starting yesterday, ending tomorrow
		event = new Event();
		event.setUid("e2");
		event.setCalendar(calendar1);
		event.setSummary("Recurring Event Yesterday");
		event.setLocation("Recurring Event Location");
		event.setDescription("Event e2");

		cal.setTime(refcal.getTime());
		cal.add(GregorianCalendar.DAY_OF_MONTH, -1);
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.HOUR_OF_DAY,	2);
		cal.add(GregorianCalendar.DAY_OF_MONTH, 3);
		event.setDtEnd(cal.getTime());
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(Recurrence.DAILY, 1, cal.getTime()));

		log.debug("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEventsList.add(event);
		twoEventsList.add(event);
		mockEventManager.storeEvent(event);

		// Add a long recurring event, starting last month, ending next month
		event = new Event();
		event.setUid("e3");
		event.setCalendar(calendar1);
		event.setSummary("Recurring Event Last Month");
		event.setLocation("Recurring Event Location");
		event.setDescription("Event e3");

		cal.setTime(refcal.getTime());
		cal.add(GregorianCalendar.WEEK_OF_YEAR, -1);
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.HOUR_OF_DAY,	2);
		cal.add(GregorianCalendar.WEEK_OF_YEAR, 3);
		event.setDtEnd(cal.getTime());
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(Recurrence.DAILY, 1, cal.getTime()));

		log.debug("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEventsList.add(event);
		oneEventsList.add(event);
		twoEventsList.add(event);
		mockEventManager.storeEvent(event);

		// Create testpage with a WeekViewPanel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new WeekViewPanel(PanelTestPage.PANEL_MARKUP_ID, 4, currentDate) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		// Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, WeekViewPanel.class);

		// Assert number of days rendered
		// Set the correct dates to find the first and last day of the week
		GregorianCalendar viewFirstDayCalendar = new GregorianCalendar();
		viewFirstDayCalendar.setTime(currentDate.getTime());

		GregorianCalendar viewLastDayCalendar = new GregorianCalendar();
		viewLastDayCalendar.setTime(CalendarUtils.addDays(currentDate.getTime(), 3));

		GregorianCalendar testDayCalendar = new GregorianCalendar();
		testDayCalendar.add(GregorianCalendar.DAY_OF_YEAR, 1);

		GregorianCalendar shortRecurringEventCalendar = new GregorianCalendar();
		shortRecurringEventCalendar.setFirstDayOfWeek(GregorianCalendar.MONDAY);

		// Assert the first day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + viewFirstDayCalendar.get(GregorianCalendar.DAY_OF_YEAR), WeekDayPanel.class);

		// Assert the last day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + viewLastDayCalendar.get(GregorianCalendar.DAY_OF_YEAR), WeekDayPanel.class);

		// Assert test day events
		WeekDayPanel testDayEventsListView = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + testDayCalendar.get(GregorianCalendar.DAY_OF_YEAR));
		wicketTester.assertListView(testDayEventsListView.getPageRelativePath() + ":eventItem", allEventsList);

		// Assert short recurring event
		WeekDayPanel recurrenceStartDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + shortRecurringEventCalendar.get(GregorianCalendar.DAY_OF_YEAR));
		wicketTester.assertListView(recurrenceStartDayPanel.getPageRelativePath() + ":eventItem", twoEventsList);

		shortRecurringEventCalendar.add(GregorianCalendar.DAY_OF_YEAR, 2);
		WeekDayPanel recurrenceEndDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + shortRecurringEventCalendar.get(GregorianCalendar.DAY_OF_YEAR));
		wicketTester.assertListView(recurrenceEndDayPanel.getPageRelativePath() + ":eventItem", twoEventsList);

		shortRecurringEventCalendar.add(GregorianCalendar.DAY_OF_YEAR, 1);
		WeekDayPanel recurrenceAfterEndDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + shortRecurringEventCalendar.get(GregorianCalendar.DAY_OF_YEAR));
		wicketTester.assertListView(recurrenceAfterEndDayPanel.getPageRelativePath() + ":eventItem", oneEventsList);

		// Assert long recurring event
		WeekDayPanel firstViewDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + viewFirstDayCalendar.get(GregorianCalendar.DAY_OF_YEAR));
		WeekDayPanel lastViewDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + viewLastDayCalendar.get(GregorianCalendar.DAY_OF_YEAR));

		wicketTester.assertListView(firstViewDayPanel.getPageRelativePath() + ":eventItem", twoEventsList);
		wicketTester.assertListView(lastViewDayPanel.getPageRelativePath() + ":eventItem", oneEventsList);
	}
}
