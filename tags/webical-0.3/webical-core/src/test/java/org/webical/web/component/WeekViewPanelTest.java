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


import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.util.tester.ITestPageSource;
import org.webical.Event;
import org.webical.User;
import org.webical.ical.Recurrence;
import org.webical.ical.RecurrenceUtil;
import org.webical.manager.EventManager;
import org.webical.manager.WebicalException;
import org.webical.manager.impl.mock.MockCalendarManager;
import org.webical.manager.impl.mock.MockEventManager;
import org.webical.manager.impl.mock.MockUserManager;
import org.webical.util.CalendarUtils;
import org.webical.web.PanelTestPage;
import org.webical.web.WebicalApplicationTest;
import org.webical.web.action.IAction;
import org.webical.web.app.WebicalSession;
import org.webical.web.component.calendar.WeekDayPanel;
import org.webical.web.component.calendar.WeekViewPanel;


public class WeekViewPanelTest extends WebicalApplicationTest{

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		annotApplicationContextMock.putBean("userManager", new MockUserManager());
		annotApplicationContextMock.putBean("calendarManager", new MockCalendarManager());

		// Prepare a User
		User user = new User();
		user.setFirstName("James");
		user.setLastName("Gossling");
		user.setUserId("jag");
		webicalSession.setUser(user);
	}

	public void testWithoutEvents() {
		annotApplicationContextMock.putBean("eventManager", new MockEventManager());

		// Create testpage with a WeekViewPanel
		wicketTester.startPage(new ITestPageSource(){
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new WeekViewPanel(PanelTestPage.PANEL_MARKUP_ID, new GregorianCalendar(), 7) {
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

		// List for the day containing the normal event
		final List<Event> randomDayEventsList = new ArrayList<Event>();

		// List for the day containing both recurring events
		final List<Event> bothRecurringEventsList = new ArrayList<Event>();

		// List for the day containing the long recurring event
		final List<Event> oneRecurringEventList = new ArrayList<Event>();

		final List<Event> allEvents = new ArrayList<Event>();

		final GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.setFirstDayOfWeek(java.util.Calendar.MONDAY);
		Date midWeek = CalendarUtils.getFirstDayOfWeek(currentDate.getTime(), java.util.Calendar.MONDAY);
		midWeek = CalendarUtils.addDays(midWeek, 3);
		midWeek = CalendarUtils.addHours(midWeek, 6);

		/* CREATE EVENTS TO RENDER */
		GregorianCalendar cal = new GregorianCalendar();
		// Add a normal event
		Event event = new Event();
		event.setUid("e1");
		event.setSummary("Normal Event Description");

		cal.setTime(midWeek);
		event.setDtStart(cal.getTime());
		cal.add(java.util.Calendar.HOUR_OF_DAY, 2);
		event.setDtEnd(cal.getTime());

		System.out.println("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEvents.add(event);
		randomDayEventsList.add(event);

		// Add a short recurring event, starting yesterday, ending tommorrow
		event = new Event();
		event.setUid("e2");
		event.setSummary("Recurring Event Yesterday");

		cal.setTime(midWeek);
		cal.add(java.util.Calendar.DAY_OF_MONTH, -1);
		event.setDtStart(cal.getTime());
		cal.add(java.util.Calendar.HOUR_OF_DAY,	2);
		event.setDtEnd(cal.getTime());
		cal.add(java.util.Calendar.DAY_OF_MONTH, 3);
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(Recurrence.DAILY, 1, cal.getTime()));

		System.out.println("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEvents.add(event);
		randomDayEventsList.add(event);
		bothRecurringEventsList.add(event);

		// Add a long recurring event, starting last month, ending next month
		event = new Event();
		event.setUid("e3");
		event.setSummary("Recurring Event Last Month");

		cal.setTime(midWeek);
		cal.add(java.util.Calendar.WEEK_OF_YEAR, -1);
		event.setDtStart(cal.getTime());
		cal.add(java.util.Calendar.HOUR_OF_DAY,	2);
		event.setDtEnd(cal.getTime());
		cal.add(java.util.Calendar.WEEK_OF_YEAR, 3);
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(Recurrence.DAILY, 1, cal.getTime()));

		System.out.println("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEvents.add(event);
		randomDayEventsList.add(event);
		bothRecurringEventsList.add(event);
		oneRecurringEventList.add(event);

		Date startDate = CalendarUtils.getFirstDayOfWeek(currentDate.getTime(), java.util.Calendar.MONDAY);
		Date endDate = CalendarUtils.getEndOfDay(CalendarUtils.getLastDayOfWeek(currentDate.getTime(), java.util.Calendar.MONDAY));

		// Add an EventManager through EasyMock with the Events.
		EventManager eventManagerMock = createMock(EventManager.class);
		annotApplicationContextMock.putBean("eventManager", eventManagerMock);
		expect(eventManagerMock.getEventsForPeriod(WebicalSession.getWebicalSession().getUser(), startDate, endDate)).andReturn(allEvents).times(1);
		replay(eventManagerMock);
		/* END CREATE EVENTS TO RENDER */

		// Create testpage with a WeekViewPanel
		wicketTester.startPage(new ITestPageSource(){
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new WeekViewPanel(PanelTestPage.PANEL_MARKUP_ID, new GregorianCalendar(), 7) {
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
		/*
		 * There are some issues with the Locales. We assume the week starts on monday, but the
		 * default (english) starts on sunday. So if the last day of our "Monday" week is a monday,
		 * it's ectually in the week after on an english calendar
		 */
		GregorianCalendar weekFirstDayCalendar = new GregorianCalendar();
		weekFirstDayCalendar.setFirstDayOfWeek(java.util.Calendar.MONDAY);

		GregorianCalendar weekLastDayCalendar = new GregorianCalendar();
		weekLastDayCalendar.setFirstDayOfWeek(java.util.Calendar.MONDAY);

		GregorianCalendar normalEventCalendar = new GregorianCalendar();
		normalEventCalendar.setFirstDayOfWeek(java.util.Calendar.MONDAY);

		GregorianCalendar shortRecurringEventCalendar = new GregorianCalendar();
		shortRecurringEventCalendar.setFirstDayOfWeek(java.util.Calendar.MONDAY);

		// Set the correct dates to find the first and last day of the week
		weekFirstDayCalendar.setTime(CalendarUtils.getFirstDayOfWeek(currentDate.getTime(), java.util.Calendar.MONDAY));
		weekLastDayCalendar.setTime(CalendarUtils.getLastDayOfWeek(currentDate.getTime(), java.util.Calendar.MONDAY));

		// Assert the first day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + weekFirstDayCalendar.get(java.util.Calendar.DAY_OF_YEAR), WeekDayPanel.class);

		// Assert the last day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + weekLastDayCalendar.get(java.util.Calendar.DAY_OF_YEAR), WeekDayPanel.class);

		// Assert normal event
		normalEventCalendar.setTime(midWeek);

		WeekDayPanel randomDayEventsListView = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + normalEventCalendar.get(java.util.Calendar.DAY_OF_YEAR));
		wicketTester.assertListView(randomDayEventsListView.getPageRelativePath() + ":eventItem", randomDayEventsList);

		// Assert short recurring event
		shortRecurringEventCalendar.setTime(CalendarUtils.addDays(midWeek, -2));
		WeekDayPanel beforeStartDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + shortRecurringEventCalendar.get(java.util.Calendar.DAY_OF_YEAR));
		wicketTester.assertListView(beforeStartDayPanel.getPageRelativePath() + ":eventItem", oneRecurringEventList);

		shortRecurringEventCalendar.setTime(CalendarUtils.addDays(midWeek, -1));
		WeekDayPanel startDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + shortRecurringEventCalendar.get(java.util.Calendar.DAY_OF_YEAR));
		wicketTester.assertListView(startDayPanel.getPageRelativePath() + ":eventItem", bothRecurringEventsList);

		shortRecurringEventCalendar.setTime(CalendarUtils.addDays(midWeek, 1));
		WeekDayPanel endDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + shortRecurringEventCalendar.get(java.util.Calendar.DAY_OF_YEAR));
		wicketTester.assertListView(endDayPanel.getPageRelativePath() + ":eventItem", bothRecurringEventsList);

		shortRecurringEventCalendar.setTime(CalendarUtils.addDays(midWeek, 2));
		WeekDayPanel afterEndDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + shortRecurringEventCalendar.get(java.util.Calendar.DAY_OF_YEAR));
		wicketTester.assertListView(afterEndDayPanel.getPageRelativePath() + ":eventItem", oneRecurringEventList);

		// Assert long recurring event
		WeekDayPanel firstWeekDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + weekFirstDayCalendar.get(java.util.Calendar.DAY_OF_YEAR));
		WeekDayPanel lastWeekDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + weekLastDayCalendar.get(java.util.Calendar.DAY_OF_YEAR));

		wicketTester.assertListView(firstWeekDayPanel.getPageRelativePath() + ":eventItem", oneRecurringEventList);
		wicketTester.assertListView(lastWeekDayPanel.getPageRelativePath() + ":eventItem", oneRecurringEventList);

	}

	/**
	 * Test week view with less then 7 days
	 * @throws WebicalException
	 */
	public void testNonWeekUse() throws WebicalException {

		// Define the current date
		final GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.setFirstDayOfWeek(Calendar.MONDAY);

		// The lists containing the different number of events
		List<Event> allEventsList = new ArrayList<Event>();
		List<Event> oneEventsList = new ArrayList<Event>();
		List<Event> twoEventsList = new ArrayList<Event>();

		GregorianCalendar cal = new GregorianCalendar();
		cal.add(java.util.Calendar.DAY_OF_MONTH, 1);

		// Add a normal event
		Event event = new Event();
		event.setUid("e1");
		event.setSummary("Normal Event Description");

		event.setDtStart(cal.getTime());
		cal.add(java.util.Calendar.HOUR_OF_DAY, 2);
		event.setDtEnd(cal.getTime());

		System.out.println("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEventsList.add(event);

		// Add a short recurring event, starting yesterday, ending tommorrow
		event = new Event();
		event.setUid("e2");
		event.setSummary("Recurring Event Yesterday");

		cal.add(java.util.Calendar.DAY_OF_MONTH, -1);
		event.setDtStart(cal.getTime());
		cal.add(java.util.Calendar.HOUR_OF_DAY,	2);
		event.setDtEnd(cal.getTime());
		cal.add(java.util.Calendar.DAY_OF_MONTH, 3);
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(Recurrence.DAILY, 1, cal.getTime()));

		System.out.println("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEventsList.add(event);
		twoEventsList.add(event);

		// Add a long recurring event, starting last month, ending next month
		event = new Event();
		event.setUid("e3");
		event.setSummary("Recurring Event Last Month");

		cal.add(java.util.Calendar.WEEK_OF_YEAR, -1);
		event.setDtStart(cal.getTime());
		cal.add(java.util.Calendar.HOUR_OF_DAY,	2);
		event.setDtEnd(cal.getTime());
		cal.add(java.util.Calendar.WEEK_OF_YEAR, 3);
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(Recurrence.DAILY, 1, cal.getTime()));

		System.out.println("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		allEventsList.add(event);
		oneEventsList.add(event);
		twoEventsList.add(event);

		// Start and end date for the view
		Date startDate = CalendarUtils.getStartOfDay(currentDate.getTime());
		Date endDate = CalendarUtils.getEndOfDay(CalendarUtils.addDays(currentDate.getTime(), 3));

		// Add an EventManager through EasyMock with the Events.
		EventManager eventManagerMock = createMock(EventManager.class);
		annotApplicationContextMock.putBean("eventManager", eventManagerMock);
		expect(eventManagerMock.getEventsForPeriod(WebicalSession.getWebicalSession().getUser(), startDate, endDate)).andReturn(allEventsList).times(1);
		replay(eventManagerMock);

		// Create testpage with a WeekViewPanel
		wicketTester.startPage(new ITestPageSource(){
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new WeekViewPanel(PanelTestPage.PANEL_MARKUP_ID, currentDate, 4) {
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

		/*
		 * There are some issues with the Locales. We assume the week starts on monday, but the
		 * default (english) starts on sunday. So if the last day of our "Monday" week is a monday,
		 * it's ectually in the week after on an english calendar
		 */

		// Set the correct dates to find the first and last day of the week
		GregorianCalendar viewFirstDayCalendar = new GregorianCalendar();
		viewFirstDayCalendar.setFirstDayOfWeek(java.util.Calendar.MONDAY);
		viewFirstDayCalendar.setTime(currentDate.getTime());

		GregorianCalendar viewLastDayCalendar = new GregorianCalendar();
		viewLastDayCalendar.setFirstDayOfWeek(java.util.Calendar.MONDAY);
		viewLastDayCalendar.setTime(CalendarUtils.addDays(currentDate.getTime(), 3));

		GregorianCalendar testDayCalendar = new GregorianCalendar();
		testDayCalendar.setFirstDayOfWeek(java.util.Calendar.MONDAY);
		testDayCalendar.add(java.util.Calendar.DAY_OF_YEAR, 1);

		GregorianCalendar shortRecurringEventCalendar = new GregorianCalendar();
		shortRecurringEventCalendar.setFirstDayOfWeek(java.util.Calendar.MONDAY);

		// Assert the first day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + viewFirstDayCalendar.get(java.util.Calendar.DAY_OF_YEAR), WeekDayPanel.class);

		// Assert the last day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + viewLastDayCalendar.get(java.util.Calendar.DAY_OF_YEAR), WeekDayPanel.class);

		// Assert test day events
		WeekDayPanel testDayEventsListView = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + testDayCalendar.get(java.util.Calendar.DAY_OF_YEAR));
		wicketTester.assertListView(testDayEventsListView.getPageRelativePath() + ":eventItem", allEventsList);

		// Assert short recurring event
		WeekDayPanel recurrenceStartDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + shortRecurringEventCalendar.get(java.util.Calendar.DAY_OF_YEAR));
		wicketTester.assertListView(recurrenceStartDayPanel.getPageRelativePath() + ":eventItem", twoEventsList);

		shortRecurringEventCalendar.add(java.util.Calendar.DAY_OF_YEAR, 2);
		WeekDayPanel recurrenceEndDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + shortRecurringEventCalendar.get(java.util.Calendar.DAY_OF_YEAR));
		wicketTester.assertListView(recurrenceEndDayPanel.getPageRelativePath() + ":eventItem", twoEventsList);

		shortRecurringEventCalendar.add(java.util.Calendar.DAY_OF_YEAR, 1);
		WeekDayPanel recurrenceAfterEndDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + shortRecurringEventCalendar.get(java.util.Calendar.DAY_OF_YEAR));
		wicketTester.assertListView(recurrenceAfterEndDayPanel.getPageRelativePath() + ":eventItem", oneEventsList);


		// Assert long recurring event
		WeekDayPanel firstViewDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + viewFirstDayCalendar.get(java.util.Calendar.DAY_OF_YEAR));
		WeekDayPanel lastViewDayPanel = (WeekDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":weekColumnRepeater:day" + viewLastDayCalendar.get(java.util.Calendar.DAY_OF_YEAR));

		wicketTester.assertListView(firstViewDayPanel.getPageRelativePath() + ":eventItem", twoEventsList);
		wicketTester.assertListView(lastViewDayPanel.getPageRelativePath() + ":eventItem", oneEventsList);


	}

}
