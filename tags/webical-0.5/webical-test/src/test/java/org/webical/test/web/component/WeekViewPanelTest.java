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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.util.tester.ITestPageSource;
import org.webical.User;
import org.webical.Calendar;
import org.webical.Event;
import org.webical.ical.Recurrence;
import org.webical.ical.RecurrenceUtil;
import org.webical.manager.WebicalException;
import org.webical.util.CalendarUtils;
import org.webical.web.action.IAction;
import org.webical.web.app.WebicalSession;
import org.webical.web.component.calendar.WeekDayPanel;
import org.webical.web.component.calendar.WeekViewPanel;
import org.webical.test.TestUtils;
import org.webical.test.manager.impl.mock.MockCalendarManager;
import org.webical.test.manager.impl.mock.MockEventManager;
import org.webical.test.web.PanelTestPage;
import org.webical.test.web.WebicalApplicationTest;


public class WeekViewPanelTest extends WebicalApplicationTest
{
	private static Log log = LogFactory.getLog(WeekViewPanelTest.class);

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

	public void testWithoutEvents() {

		annotApplicationContextMock.putBean("eventManager", new MockEventManager());

		final GregorianCalendar currentDate = CalendarUtils.newTodayCalendar(getFirstDayOfWeek());

		// Create test page with a WeekViewPanel
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
		GregorianCalendar weekFirstDayCalendar = CalendarUtils.duplicateCalendar(currentDate);
		weekFirstDayCalendar.setTime(CalendarUtils.getFirstDayOfWeek(currentDate.getTime(), getFirstDayOfWeek()));
		// Assert the first day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + weekFirstDayCalendar.get(GregorianCalendar.DAY_OF_YEAR), WeekDayPanel.class);

		GregorianCalendar weekLastDayCalendar = CalendarUtils.duplicateCalendar(currentDate);
		weekLastDayCalendar.setTime(CalendarUtils.getLastDayOfWeek(currentDate.getTime(), getFirstDayOfWeek()));
		// Assert the last day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + weekLastDayCalendar.get(GregorianCalendar.DAY_OF_YEAR), WeekDayPanel.class);

		// Assert the heading label
		DateFormat dateFormat = new SimpleDateFormat("E", getTestSession().getLocale());
		GregorianCalendar weekCal = CalendarUtils.duplicateCalendar(currentDate);
		weekCal.setTime(CalendarUtils.getFirstDayOfWeek(currentDate.getTime(), getFirstDayOfWeek()));
		for (int i = 0; i < 7; ++ i)
		{
			wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":dayHeadingRepeater:headerDay" + i, dateFormat.format(weekCal.getTime()));
			weekCal.add(GregorianCalendar.DAY_OF_WEEK, 1);
		}
	}

	public void testWeekUse() throws WebicalException
	{
		MockCalendarManager mockCalendarManager = (MockCalendarManager)	annotApplicationContextMock.getBean("calendarManager");
		Calendar calendar1 = mockCalendarManager.getCalendarById("1");

		MockEventManager mockEventManager = new MockEventManager();
		annotApplicationContextMock.putBean("eventManager", mockEventManager);

		SimpleDateFormat dateFormat = new SimpleDateFormat(WebicalSession.getWebicalSession().getUserSettings().getTimeFormat(), getTestSession().getLocale());

		final GregorianCalendar currentDate = CalendarUtils.newTodayCalendar(getFirstDayOfWeek());

		// All events for this week
		List<Event> allEvents = new ArrayList<Event>();

		Date midWeek = CalendarUtils.getFirstDayOfWeek(currentDate.getTime(), getFirstDayOfWeek());
		midWeek = CalendarUtils.addDays(midWeek, 3);

		/* CREATE EVENTS TO RENDER */
		GregorianCalendar cal = CalendarUtils.duplicateCalendar(currentDate);

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
		mockEventManager.storeEvent(event);

		// Add a short recurring event, starting Tuesday, ending Friday
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
		cal.add(GregorianCalendar.HOUR_OF_DAY,	3);
		cal.add(GregorianCalendar.DAY_OF_MONTH, 3);
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
		cal.setTime(midWeek);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 9);
		cal.add(GregorianCalendar.WEEK_OF_YEAR, -1);
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.HOUR_OF_DAY,	2);
		cal.add(GregorianCalendar.WEEK_OF_YEAR, 3);
		event.setDtEnd(cal.getTime());
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(Recurrence.DAILY, 1, CalendarUtils.getEndOfDay(cal.getTime())));
		log.debug("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEvents.add(event);
		mockEventManager.storeEvent(event);

		// Add a (pseudo) all day event, starting Monday midnight, ending Tuesday 00:00 hours
		event = new Event();
		event.setUid("e4");
		event.setCalendar(calendar1);
		event.setSummary("Pseudo All Day Event");
		event.setLocation("All Day Event Location");
		event.setDescription("Starting Monday 00:00 hours, ending Tuesday 00:00 hours");
		cal.setTime(midWeek);
		cal.add(GregorianCalendar.DAY_OF_MONTH, -2);
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

		// Add a long event this week starting Friday 14.00 hours and ending Saturday 16.00 hours
		event = new Event();
		event.setUid("e5");
		event.setCalendar(calendar1);
		event.setSummary("Long Event Description");
		event.setLocation("Long Event Location");
		event.setDescription("Event e5");
		cal.setTime(midWeek);
		cal.add(GregorianCalendar.DAY_OF_MONTH, 2);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 14);
		cal.set(GregorianCalendar.MINUTE, 0);
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.DAY_OF_MONTH, 1);
		cal.add(GregorianCalendar.HOUR_OF_DAY, 2);
		event.setDtEnd(cal.getTime());
		log.debug("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEvents.add(event);
		mockEventManager.storeEvent(event);

		// Create test page with a WeekViewPanel
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

		// Set the correct dates to find the first and last day of the week

		// Assert number of days rendered
		GregorianCalendar weekFirstDayCalendar = CalendarUtils.duplicateCalendar(currentDate);
		weekFirstDayCalendar.setTime(CalendarUtils.getFirstDayOfWeek(currentDate.getTime(), getFirstDayOfWeek()));
		// Assert the first day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + weekFirstDayCalendar.get(GregorianCalendar.DAY_OF_YEAR), WeekDayPanel.class);

		GregorianCalendar weekLastDayCalendar = CalendarUtils.duplicateCalendar(currentDate);
		weekLastDayCalendar.setTime(CalendarUtils.getLastDayOfWeek(currentDate.getTime(), getFirstDayOfWeek()));
		// Assert the last day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + weekLastDayCalendar.get(GregorianCalendar.DAY_OF_YEAR), WeekDayPanel.class);

		// Events for days in this week
		List<Event> dayEvents = new ArrayList<Event>();
		// Assert weekday events
		GregorianCalendar weekCal = CalendarUtils.duplicateCalendar(currentDate);
		weekCal.setTime(CalendarUtils.getFirstDayOfWeek(currentDate.getTime(), getFirstDayOfWeek()));
		for (int i = 0; i < 7; ++ i)
		{
			WeekDayPanel weekDayEventsListView = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + weekCal.get(GregorianCalendar.DAY_OF_YEAR));
			int weekDay = getFirstDayOfWeek() + weekCal.get(GregorianCalendar.DAY_OF_WEEK) - 1;		// First day of the week is 1
			if (weekDay > 7) weekDay = 1;
			switch (weekDay)
			{
			case GregorianCalendar.SUNDAY:
				dayEvents.clear();
				dayEvents.add(allEvents.get(2));	// e3
				break;
			case GregorianCalendar.MONDAY:
				dayEvents.clear();
				dayEvents.add(allEvents.get(2));	// e3
				dayEvents.add(allEvents.get(3));	// e4
				break;
			case GregorianCalendar.TUESDAY:
				dayEvents.clear();
				dayEvents.add(allEvents.get(1));	// e2
				dayEvents.add(allEvents.get(2));	// e3
				break;
			case GregorianCalendar.WEDNESDAY:
				dayEvents.clear();
				dayEvents.add(allEvents.get(0));	// e1
				dayEvents.add(allEvents.get(1));	// e2
				dayEvents.add(allEvents.get(2));	// e3
				break;
			case GregorianCalendar.THURSDAY:
				dayEvents.clear();
				dayEvents.add(allEvents.get(1));	// e2
				dayEvents.add(allEvents.get(2));	// e3
				break;
			case GregorianCalendar.FRIDAY:
				dayEvents.clear();
				dayEvents.add(allEvents.get(1));	// e2
				dayEvents.add(allEvents.get(2));	// e3
				dayEvents.add(allEvents.get(4));	// e5
				break;
			case GregorianCalendar.SATURDAY:
				dayEvents.clear();
				dayEvents.add(allEvents.get(2));	// e3
				dayEvents.add(allEvents.get(4));	// e5
				break;
			}
			String path = weekDayEventsListView.getPageRelativePath() + ":eventItem";
			wicketTester.assertListView(path, dayEvents);

			ListView listView = (ListView) wicketTester.getComponentFromLastRenderedPage(path);
			Iterator<?> lvIt = listView.iterator();
			while (lvIt.hasNext())
			{
				ListItem item = (ListItem) lvIt.next();
				Event evt = (Event) item.getModelObject();
				List<?> bhvs = item.getBehaviors();
				if (evt.getUid().equals("e4")) assertEquals(1, bhvs.size());	// only e4 is an all day event
				else assertEquals(0, bhvs.size());

				if (evt.getUid().equals("e5"))
				{
					String timePath = item.getPageRelativePath() + ":eventLink:eventTime";
					String timeLabelText = null;
					if (weekDay == GregorianCalendar.FRIDAY) timeLabelText = dateFormat.format(evt.getDtStart());
					else timeLabelText = dateFormat.format(midWeek);
					wicketTester.assertLabel(timePath, timeLabelText);
				}
			}

			weekCal.add(GregorianCalendar.DAY_OF_WEEK, 1);
		}
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
		final GregorianCalendar currentDate = CalendarUtils.newTodayCalendar(getFirstDayOfWeek());
		currentDate.setFirstDayOfWeek(GregorianCalendar.MONDAY);

		// The list containing the different events
		List<Event> allEvents = new ArrayList<Event>();

		GregorianCalendar refcal = CalendarUtils.duplicateCalendar(currentDate);
		refcal.add(GregorianCalendar.DAY_OF_MONTH, 1);
		refcal.set(GregorianCalendar.HOUR_OF_DAY, 12);
		refcal.set(GregorianCalendar.MINUTE, 0);
		refcal.set(GregorianCalendar.SECOND, 0);

		GregorianCalendar cal = CalendarUtils.duplicateCalendar(currentDate);

		// Add a normal event tomorrow
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
		allEvents.add(event);
		mockEventManager.storeEvent(event);

		// Add a short recurring event, starting today, ending tomorrow
		event = new Event();
		event.setUid("e2");
		event.setCalendar(calendar1);
		event.setSummary("Recurring Event Today");
		event.setLocation("Recurring Event Location");
		event.setDescription("Event e2");
		cal.setTime(refcal.getTime());
		cal.add(GregorianCalendar.DAY_OF_MONTH, -1);
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.HOUR_OF_DAY,	2);
		cal.add(GregorianCalendar.DAY_OF_MONTH, 2);
		event.setDtEnd(cal.getTime());
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(Recurrence.DAILY, 1, CalendarUtils.getEndOfDay(cal.getTime())));
		log.debug("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEvents.add(event);
		mockEventManager.storeEvent(event);

		// Add a long recurring event, starting last week, ending next week
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
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(Recurrence.DAILY, 1, CalendarUtils.getEndOfDay(cal.getTime())));
		log.debug("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEvents.add(event);
		mockEventManager.storeEvent(event);

		// Add a (pseudo) all day event, starting day + 1 midnight, ending day + 2 00:00 hours
		event = new Event();
		event.setUid("e4");
		event.setCalendar(calendar1);
		event.setSummary("Pseudo All Day Event");
		event.setLocation("All Day Event Location");
		event.setDescription("Starting day + 1, ending day + 2 00:00 hours");
		cal.setTime(refcal.getTime());
		cal.add(GregorianCalendar.DAY_OF_MONTH, 1);
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

		// Create test page with a WeekViewPanel
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
		GregorianCalendar viewFirstDayCalendar = CalendarUtils.duplicateCalendar(currentDate);
		// Assert the first day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + viewFirstDayCalendar.get(GregorianCalendar.DAY_OF_YEAR), WeekDayPanel.class);

		GregorianCalendar viewLastDayCalendar = CalendarUtils.duplicateCalendar(currentDate);
		viewLastDayCalendar.add(GregorianCalendar.DAY_OF_MONTH, 3);
		// Assert the last day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + viewLastDayCalendar.get(GregorianCalendar.DAY_OF_YEAR), WeekDayPanel.class);

		// Events for days in this 4 day period
		List<Event> dayEvents = new ArrayList<Event>();
		// Assert weekday events
		GregorianCalendar weekCal = CalendarUtils.duplicateCalendar(currentDate);
		for (int i = 0; i < 4; ++ i)
		{
			WeekDayPanel weekDayEventsListView = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + weekCal.get(GregorianCalendar.DAY_OF_YEAR));
			switch (i)
			{
			case 0:
				dayEvents.clear();
				dayEvents.add(allEvents.get(1));	// e2
				dayEvents.add(allEvents.get(2));	// e3
				break;
			case 1:
				dayEvents.clear();
				dayEvents.add(allEvents.get(0));	// e1
				dayEvents.add(allEvents.get(1));	// e2
				dayEvents.add(allEvents.get(2));	// e3
				break;
			case 2:
				dayEvents.clear();
				dayEvents.add(allEvents.get(1));	// e2
				dayEvents.add(allEvents.get(2));	// e3
				dayEvents.add(allEvents.get(3));	// e4
				break;
			case 3:
				dayEvents.clear();
				dayEvents.add(allEvents.get(2));	// e3
				break;
			}
			String path = weekDayEventsListView.getPageRelativePath() + ":eventItem";
			wicketTester.assertListView(path, dayEvents);

			ListView listView = (ListView) wicketTester.getComponentFromLastRenderedPage(path);
			Iterator<?> lvIt = listView.iterator();
			while (lvIt.hasNext())
			{
				ListItem item = (ListItem) lvIt.next();
				Event evt = (Event) item.getModelObject();
				List<?> bhvs = item.getBehaviors();
				if (evt.getUid().equals("e4")) assertEquals(1, bhvs.size());	// e4 all day event
				else assertEquals(0, bhvs.size());
			}

			weekCal.add(GregorianCalendar.DAY_OF_WEEK, 1);
		}
	}
}
