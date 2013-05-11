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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
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
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.RepeatingView;
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
public class MonthViewPanelTest extends WebicalApplicationTest
{
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

	public int getFirstDayOfWeek()
	{
		return getTestSession().getUserSettings().getFirstDayOfWeek();
	}

	/**
	 * Test rendering of the panel without events.
	 */
	public void testRenderingWithoutEvents() {
		log.debug("testRenderingWithoutEvents");

		// Add an EventManager
		annotApplicationContextMock.putBean("eventManager", new MockEventManager());

		final GregorianCalendar currentDate = CalendarUtils.newTodayCalendar(getFirstDayOfWeek());

		// render start page with a MonthViewPanel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new MonthViewPanel(PanelTestPage.PANEL_MARKUP_ID, 1, currentDate) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		// Basic Assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, MonthViewPanel.class);

		// Weekday headers
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID +":monthHeaderRepeater", RepeatingView.class);

		DateFormat dateFormat = new SimpleDateFormat("E", getTestSession().getLocale());
		GregorianCalendar weekCal = CalendarUtils.duplicateCalendar(currentDate);
		weekCal.setTime(CalendarUtils.getFirstDayOfWeek(currentDate.getTime(), getFirstDayOfWeek()));
		for (int i = 0; i < 7; ++ i)
		{
			wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":monthHeaderRepeater:headerDay" + i, dateFormat.format(weekCal.getTime()));
			weekCal.add(GregorianCalendar.DAY_OF_WEEK, 1);
		}

		// Set the correct dates to find the first and last day of the month
		GregorianCalendar monthFirstDayDate = CalendarUtils.duplicateCalendar(currentDate);
		monthFirstDayDate.setTime(CalendarUtils.getFirstDayOfWeekOfMonth(currentDate.getTime(), getFirstDayOfWeek()));
		log.debug("testRenderingWithoutEvents:monthFirstDayDate " + monthFirstDayDate.getTime());
		// Assert the first day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + monthFirstDayDate.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + monthFirstDayDate.get(GregorianCalendar.DAY_OF_YEAR), MonthDayPanel.class);

		GregorianCalendar monthLastDayDate = CalendarUtils.duplicateCalendar(currentDate);
		monthLastDayDate.setTime(CalendarUtils.getLastWeekDayOfMonth(currentDate.getTime(), getFirstDayOfWeek()));
		log.debug("testRenderingWithoutEvents:monthLastDayDate " + monthLastDayDate.getTime());
		// Assert the last day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + monthLastDayDate.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + monthLastDayDate.get(GregorianCalendar.DAY_OF_YEAR), MonthDayPanel.class);
	}

	/**
	 * Test rendering with events.
	 * @throws WebicalException
	 */
	public void testRenderingWithEvents() throws WebicalException
	{
		log.debug("testRenderingWithEvents");

		String path = null;
		MockCalendarManager mockCalendarManager = (MockCalendarManager)	annotApplicationContextMock.getBean("calendarManager");
		Calendar calendar1 = mockCalendarManager.getCalendarById("1");

		MockEventManager mockEventManager = new MockEventManager();
		annotApplicationContextMock.putBean("eventManager", mockEventManager);

		GregorianCalendar cal = CalendarUtils.newTodayCalendar(getFirstDayOfWeek());
		cal.set(GregorianCalendar.DAY_OF_MONTH, 15);
		final GregorianCalendar currentDate = CalendarUtils.duplicateCalendar(cal);

		// FIXME mattijs: test fails when run at 23:30 (probably also between 22:00 and 00:00)

		// all events
		List<Event> allEvents = new ArrayList<Event>();

		// Add a normal event
		Event event = new Event();
		event.setUid("e1");
		event.setCalendar(calendar1);
		event.setSummary("Normal Event Description");
		event.setLocation("Normal Event Location");
		event.setDescription("Event e1");
		cal.set(GregorianCalendar.DAY_OF_MONTH, 15);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 13);
		cal.set(GregorianCalendar.MINUTE, 30);
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.HOUR_OF_DAY, 2);
		event.setDtEnd(cal.getTime());
		log.debug("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEvents.add(event);
		mockEventManager.storeEvent(event);

		// Add a short recurring event, starting yesterday, ending tomorrow
		event = new Event();
		event.setUid("e2");
		event.setCalendar(calendar1);
		event.setSummary("Recurring Event Yesterday");
		event.setLocation("Recurring Event Location");
		event.setDescription("Event e2");
		cal.set(GregorianCalendar.DAY_OF_MONTH, 14);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 10);
		cal.set(GregorianCalendar.MINUTE, 0);
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.HOUR_OF_DAY,	2);
		cal.set(GregorianCalendar.DAY_OF_MONTH, 17);
		event.setDtEnd(cal.getTime());
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(Recurrence.DAILY, 1, CalendarUtils.getEndOfDay(cal.getTime())));
		log.debug("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEvents.add(event);
		mockEventManager.storeEvent(event);

		// Add a long recurring event, starting last month, ending next month
		event = new Event();
		event.setUid("e3");
		event.setCalendar(calendar1);
		event.setSummary("Recurring Event Last Month");
		event.setLocation("Recurring Event Location");
		event.setDescription("Event e3");
		cal.set(GregorianCalendar.DAY_OF_MONTH, 15);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 16);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.add(GregorianCalendar.MONTH, -1);
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.HOUR_OF_DAY, 1);
		cal.set(GregorianCalendar.MINUTE, 30);
		cal.add(GregorianCalendar.MONTH, 2);
		event.setDtEnd(cal.getTime());
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(Recurrence.DAILY, 1, CalendarUtils.getEndOfDay(cal.getTime())));
		log.debug("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEvents.add(event);
		mockEventManager.storeEvent(event);

		// Add a (pseudo) all day event, starting 21 00:00 hours, ending 22 00:00 hours
		event = new Event();
		event.setUid("e4");
		event.setCalendar(calendar1);
		event.setSummary("Pseudo All Day Event");
		event.setLocation("All Day Event Location");
		event.setDescription("Starting 21 00:00 hours, ending 22 00:00 hours");
		cal = CalendarUtils.duplicateCalendar(currentDate);
		cal.set(GregorianCalendar.DAY_OF_MONTH, 21);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		cal.set(GregorianCalendar.MILLISECOND, 0);
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.DAY_OF_MONTH, 1);
		event.setDtEnd(cal.getTime());
		log.debug("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEvents.add(event);
		mockEventManager.storeEvent(event);

		// Add a long event this week starting 23 14.00 hours and ending 25 16.00 hours
		event = new Event();
		event.setUid("e5");
		event.setCalendar(calendar1);
		event.setSummary("Long Event Description");
		event.setLocation("Long Event Location");
		event.setDescription("Event e5");
		cal.set(GregorianCalendar.DAY_OF_MONTH, 23);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 14);
		cal.set(GregorianCalendar.MINUTE, 0);
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.DAY_OF_MONTH, 2);
		cal.add(GregorianCalendar.HOUR_OF_DAY, 2);
		event.setDtEnd(cal.getTime());
		log.debug("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEvents.add(event);
		mockEventManager.storeEvent(event);

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
		// Set the correct dates to find the first and last day of the month
		GregorianCalendar monthFirstDayDate = CalendarUtils.duplicateCalendar(currentDate);
		monthFirstDayDate.setTime(CalendarUtils.getFirstDayOfWeekOfMonth(currentDate.getTime(), getFirstDayOfWeek()));
		// Assert the first day in the view
		path = PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + monthFirstDayDate.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + monthFirstDayDate.get(GregorianCalendar.DAY_OF_YEAR);
		wicketTester.assertComponent(path, MonthDayPanel.class);

		GregorianCalendar monthLastDayDate = CalendarUtils.duplicateCalendar(currentDate);
		monthLastDayDate.setTime(CalendarUtils.getLastWeekDayOfMonth(currentDate.getTime(), getFirstDayOfWeek()));
		// Assert the last day in the view
		path = PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + monthLastDayDate.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + monthLastDayDate.get(GregorianCalendar.DAY_OF_YEAR);
		wicketTester.assertComponent(path, MonthDayPanel.class);

		// Check events
		List<Event> dayEvents = new ArrayList<Event>();

		cal = CalendarUtils.duplicateCalendar(currentDate);

		// Assert events day 13
		cal.set(GregorianCalendar.DAY_OF_MONTH, 13);
		dayEvents.clear();
		dayEvents.add(allEvents.get(2));	// e3
		path = PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + cal.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + cal.get(GregorianCalendar.DAY_OF_YEAR);
		MonthDayPanel monthDayEventsListView = (MonthDayPanel) wicketTester.getLastRenderedPage().get(path);
		wicketTester.assertListView(monthDayEventsListView.getPageRelativePath() + ":eventItem", dayEvents);

		// Assert events day 14
		cal.set(GregorianCalendar.DAY_OF_MONTH, 14);
		dayEvents.clear();
		path = PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + cal.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + cal.get(GregorianCalendar.DAY_OF_YEAR);
		monthDayEventsListView = (MonthDayPanel) wicketTester.getLastRenderedPage().get(path);
		dayEvents.add(allEvents.get(1));	// e2
		dayEvents.add(allEvents.get(2));	// e3
		wicketTester.assertListView(monthDayEventsListView.getPageRelativePath() + ":eventItem", dayEvents);

		// Assert events day 15
		cal.set(GregorianCalendar.DAY_OF_MONTH, 15);
		dayEvents.clear();
		dayEvents.add(allEvents.get(0));	// e1
		dayEvents.add(allEvents.get(1));	// e2
		dayEvents.add(allEvents.get(2));	// e3
		path = PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + cal.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + cal.get(GregorianCalendar.DAY_OF_YEAR);
		monthDayEventsListView = (MonthDayPanel) wicketTester.getLastRenderedPage().get(path);
		wicketTester.assertListView(monthDayEventsListView.getPageRelativePath() + ":eventItem", dayEvents);

		cal.set(GregorianCalendar.DAY_OF_MONTH, 16);
		dayEvents.clear();
		dayEvents.add(allEvents.get(1));	// e2
		dayEvents.add(allEvents.get(2));	// e3
		path = PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + cal.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + cal.get(GregorianCalendar.DAY_OF_YEAR);
		monthDayEventsListView = (MonthDayPanel) wicketTester.getLastRenderedPage().get(path);
		wicketTester.assertListView(monthDayEventsListView.getPageRelativePath() + ":eventItem", dayEvents);

		cal.set(GregorianCalendar.DAY_OF_MONTH, 17);
		dayEvents.clear();
		dayEvents.add(allEvents.get(1));	// e2
		dayEvents.add(allEvents.get(2));	// e3
		path = PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + cal.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + cal.get(GregorianCalendar.DAY_OF_YEAR);
		monthDayEventsListView = (MonthDayPanel) wicketTester.getLastRenderedPage().get(path);
		wicketTester.assertListView(monthDayEventsListView.getPageRelativePath() + ":eventItem", dayEvents);

		// Only long recurring event
		cal.set(GregorianCalendar.DAY_OF_MONTH, 18);
		dayEvents.clear();
		dayEvents.add(allEvents.get(2));	// e3
		path = PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + cal.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + cal.get(GregorianCalendar.DAY_OF_YEAR);
		monthDayEventsListView = (MonthDayPanel) wicketTester.getLastRenderedPage().get(path);
		wicketTester.assertListView(monthDayEventsListView.getPageRelativePath() + ":eventItem", dayEvents);

		// Assert long recurring event first and last view day
		dayEvents.clear();
		dayEvents.add(allEvents.get(2));	// e3
		path = PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + monthFirstDayDate.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + monthFirstDayDate.get(GregorianCalendar.DAY_OF_YEAR);
		monthDayEventsListView = (MonthDayPanel) wicketTester.getLastRenderedPage().get(path);
		wicketTester.assertListView(monthDayEventsListView.getPageRelativePath() + ":eventItem", dayEvents);
		monthDayEventsListView = (MonthDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + monthLastDayDate.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + monthLastDayDate.get(GregorianCalendar.DAY_OF_YEAR));
		wicketTester.assertListView(monthDayEventsListView.getPageRelativePath() + ":eventItem", dayEvents);

		// Assert events day 21
		cal.set(GregorianCalendar.DAY_OF_MONTH, 21);
		dayEvents.clear();
		dayEvents.add(allEvents.get(2));	// e3
		dayEvents.add(allEvents.get(3));	// e4
		path = PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + cal.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + cal.get(GregorianCalendar.DAY_OF_YEAR);
		monthDayEventsListView = (MonthDayPanel) wicketTester.getLastRenderedPage().get(path);
		path = monthDayEventsListView.getPageRelativePath() + ":eventItem";
		wicketTester.assertListView(path, dayEvents);

		ListView listView = (ListView) wicketTester.getComponentFromLastRenderedPage(path);
		Iterator<?> lvIt = listView.iterator();
		while (lvIt.hasNext())
		{
			ListItem item = (ListItem) lvIt.next();
			Event evt = (Event) item.getModelObject();
			List<?> bhvs = item.getBehaviors();
			if (evt.getUid().equals("e4")) assertEquals(1, bhvs.size());	// e4 is an all day event on day 21
			else assertEquals(0, bhvs.size());
		}

		// Assert events day 22
		cal.set(GregorianCalendar.DAY_OF_MONTH, 22);
		dayEvents.clear();
		dayEvents.add(allEvents.get(2));	// e3
		path = PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + cal.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + cal.get(GregorianCalendar.DAY_OF_YEAR);
		monthDayEventsListView = (MonthDayPanel) wicketTester.getLastRenderedPage().get(path);
		wicketTester.assertListView(monthDayEventsListView.getPageRelativePath() + ":eventItem", dayEvents);

		// Assert events day 23
		cal.set(GregorianCalendar.DAY_OF_MONTH, 23);
		dayEvents.clear();
		dayEvents.add(allEvents.get(2));	// e3
		dayEvents.add(allEvents.get(4));	// e5
		path = PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + cal.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + cal.get(GregorianCalendar.DAY_OF_YEAR);
		monthDayEventsListView = (MonthDayPanel) wicketTester.getLastRenderedPage().get(path);
		path = monthDayEventsListView.getPageRelativePath() + ":eventItem";
		wicketTester.assertListView(path, dayEvents);

		listView = (ListView) wicketTester.getComponentFromLastRenderedPage(path);
		lvIt = listView.iterator();
		while (lvIt.hasNext())
		{
			ListItem item = (ListItem) lvIt.next();
			List<?> bhvs = item.getBehaviors();
			assertEquals(0, bhvs.size());									// no all day events
		}

		// Assert events day 24
		cal.set(GregorianCalendar.DAY_OF_MONTH, 24);
		dayEvents.clear();
		dayEvents.add(allEvents.get(2));	// e3
		dayEvents.add(allEvents.get(4));	// e5
		path = PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + cal.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + cal.get(GregorianCalendar.DAY_OF_YEAR);
		monthDayEventsListView = (MonthDayPanel) wicketTester.getLastRenderedPage().get(path);
		path = monthDayEventsListView.getPageRelativePath() + ":eventItem";
		wicketTester.assertListView(path, dayEvents);

		listView = (ListView) wicketTester.getComponentFromLastRenderedPage(path);
		lvIt = listView.iterator();
		while (lvIt.hasNext())
		{
			ListItem item = (ListItem) lvIt.next();
			Event evt = (Event) item.getModelObject();
			List<?> bhvs = item.getBehaviors();
			if (evt.getUid().equals("e5")) assertEquals(1, bhvs.size());	// e5 is an all day event on day 24
			else assertEquals(0, bhvs.size());
		}

		// Assert events day 25
		cal.set(GregorianCalendar.DAY_OF_MONTH, 25);
		dayEvents.clear();
		dayEvents.add(allEvents.get(2));	// e3
		dayEvents.add(allEvents.get(4));	// e5
		path = PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + cal.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + cal.get(GregorianCalendar.DAY_OF_YEAR);
		monthDayEventsListView = (MonthDayPanel) wicketTester.getLastRenderedPage().get(path);
		path = monthDayEventsListView.getPageRelativePath() + ":eventItem";
		wicketTester.assertListView(path, dayEvents);

		listView = (ListView) wicketTester.getComponentFromLastRenderedPage(path);
		lvIt = listView.iterator();
		while (lvIt.hasNext())
		{
			ListItem item = (ListItem) lvIt.next();
			List<?> bhvs = item.getBehaviors();
			assertEquals(0, bhvs.size());									// no all day events
		}

		// Assert events day 26
		cal.set(GregorianCalendar.DAY_OF_MONTH, 26);
		dayEvents.clear();
		dayEvents.add(allEvents.get(2));	// e3
		path = PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + cal.get(GregorianCalendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + cal.get(GregorianCalendar.DAY_OF_YEAR);
		monthDayEventsListView = (MonthDayPanel) wicketTester.getLastRenderedPage().get(path);
		wicketTester.assertListView(monthDayEventsListView.getPageRelativePath() + ":eventItem", dayEvents);
	}
}
