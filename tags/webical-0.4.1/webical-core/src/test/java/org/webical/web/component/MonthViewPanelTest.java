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
import org.webical.web.component.calendar.MonthDayPanel;
import org.webical.web.component.calendar.MonthViewPanel;


/**
 * Test for the CalendarMonthViewPanel
 *
 * @author paul
 * @author Mattijs Hoitink
 */
public class MonthViewPanelTest extends WebicalApplicationTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		annotApplicationContextMock.putBean("userManager", new MockUserManager());
		annotApplicationContextMock.putBean("calendarManager", new MockCalendarManager());

		// Prepare a User
		final User user = new User();
		user.setFirstName("James");
		user.setLastName("Gossling");
		user.setUserId("jag");
		WebicalSession.getWebicalSession().setUser(user);
	}

	/**
	 * Test rendering of the panel without events.
	 */
	public void testRenderingWithoutEvents(){
		// Add an EventManager
		annotApplicationContextMock.putBean("eventManager", new MockEventManager());

		// render start page with a MonthViewPanel
		wicketTester.startPage(new ITestPageSource(){
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new MonthViewPanel(PanelTestPage.PANEL_MARKUP_ID, new GregorianCalendar()) {
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
		event.setSummary("Normal Event Description");

		GregorianCalendar cal = new GregorianCalendar();
		cal.set(java.util.Calendar.DAY_OF_MONTH, 15);
		cal.set(java.util.Calendar.HOUR_OF_DAY, 12);
		event.setDtStart(cal.getTime());
		cal.add(java.util.Calendar.HOUR_OF_DAY, 2);
		event.setDtEnd(cal.getTime());
		allEvents.add(event);
		randomDayEventsList.add(event);

		// Add a short recurring event, starting yesterday, ending tommorrow
		event = new Event();
		event.setUid("e2");
		event.setSummary("Recurring Event Yesterday");

		cal = new GregorianCalendar();
		cal.set(java.util.Calendar.DAY_OF_MONTH, 14);
		cal.set(java.util.Calendar.HOUR_OF_DAY, 12);
		event.setDtStart(cal.getTime());
		cal.add(java.util.Calendar.HOUR_OF_DAY,	2);
		event.setDtEnd(cal.getTime());
		cal.set(java.util.Calendar.DAY_OF_MONTH, 17);
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(Recurrence.DAILY, 1, cal.getTime()));

		allEvents.add(event);
		randomDayEventsList.add(event);
		bothRecurringEventsList.add(event);

		// Add a long recurring event, starting last month, ending next month
		event = new Event();
		event.setUid("e3");
		event.setSummary("Recurring Event Last Month");

		cal = new GregorianCalendar();
		cal.set(java.util.Calendar.DAY_OF_MONTH, 15);
		cal.set(java.util.Calendar.HOUR_OF_DAY, 12);
		cal.add(java.util.Calendar.MONTH, -1);
		event.setDtStart(cal.getTime());
		cal.add(java.util.Calendar.HOUR_OF_DAY,	2);
		event.setDtEnd(cal.getTime());
		cal.add(java.util.Calendar.MONTH, 2);
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(Recurrence.DAILY, 1, cal.getTime()));

		allEvents.add(event);
		randomDayEventsList.add(event);
		bothRecurringEventsList.add(event);
		oneRecurringEventsList.add(event);

		final GregorianCalendar currentDate = new GregorianCalendar();
		Date startDate = CalendarUtils.getFirstDayOfWeekOfMonth(currentDate.getTime(), Calendar.SUNDAY);
		Date endDate = CalendarUtils.getEndOfDay(CalendarUtils.getLastWeekDayOfMonth(currentDate.getTime(), Calendar.SUNDAY));

		// Add an EventManager through EasyMock with the Events.
		EventManager eventManagerMock = createMock(EventManager.class);
		annotApplicationContextMock.putBean("eventManager", eventManagerMock);
		expect(eventManagerMock.getEventsForPeriod(WebicalSession.getWebicalSession().getUser(), startDate, endDate)).andReturn(allEvents).times(1);
		replay(eventManagerMock);

		// Render test page with a MonthViewPanel
		wicketTester.startPage(new ITestPageSource(){
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new MonthViewPanel(PanelTestPage.PANEL_MARKUP_ID, new GregorianCalendar()) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		// Basic Assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, MonthViewPanel.class);

		// Assert number of days rendered

		/*
		 * There are some issues with the Locales. We assume the week starts on monday, but the
		 * default (english) starts on sunday. So if the last day of our "Monday" week is a monday,
		 * it's ectually in the week after on an english calendar
		 */
		GregorianCalendar monthFirstDayDate = new GregorianCalendar();
		GregorianCalendar monthLastDayDate = new GregorianCalendar();
		GregorianCalendar normalEventCal = new GregorianCalendar();
		GregorianCalendar shortRecurringEventCal = new GregorianCalendar();

		// Set the correct dates to find the first and last day of the month
		monthFirstDayDate.setTime(CalendarUtils.getFirstDayOfWeekOfMonth(currentDate.getTime(), java.util.Calendar.SUNDAY));
		monthLastDayDate.setTime(CalendarUtils.getLastWeekDayOfMonth(currentDate.getTime(), java.util.Calendar.SUNDAY));

		// Assert the first day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + monthFirstDayDate.get(java.util.Calendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + monthFirstDayDate.get(java.util.Calendar.DAY_OF_YEAR), MonthDayPanel.class);

		// Assert the last day in the view
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + monthLastDayDate.get(java.util.Calendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + monthLastDayDate.get(java.util.Calendar.DAY_OF_YEAR), MonthDayPanel.class);

		// Assert normal event
		normalEventCal.set(java.util.Calendar.DAY_OF_MONTH, 15);

		MonthDayPanel normalDayEventsListView = (MonthDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + normalEventCal.get(java.util.Calendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + normalEventCal.get(java.util.Calendar.DAY_OF_YEAR));
		wicketTester.assertListView(normalDayEventsListView.getPageRelativePath() + ":eventItem", randomDayEventsList);

		// Assert short recurring event
		shortRecurringEventCal.set(java.util.Calendar.DAY_OF_MONTH, 13);
		MonthDayPanel beforeStartDayPanel = (MonthDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + shortRecurringEventCal.get(java.util.Calendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + shortRecurringEventCal.get(java.util.Calendar.DAY_OF_YEAR));
		wicketTester.assertListView(beforeStartDayPanel.getPageRelativePath() + ":eventItem", oneRecurringEventsList);

		shortRecurringEventCal.set(java.util.Calendar.DAY_OF_MONTH, 14);
		MonthDayPanel startDayPanel = (MonthDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + shortRecurringEventCal.get(java.util.Calendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + shortRecurringEventCal.get(java.util.Calendar.DAY_OF_YEAR));
		wicketTester.assertListView(startDayPanel.getPageRelativePath() + ":eventItem", bothRecurringEventsList);

		shortRecurringEventCal.set(java.util.Calendar.DAY_OF_MONTH, 16);
		MonthDayPanel endDayPanel = (MonthDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + shortRecurringEventCal.get(java.util.Calendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + shortRecurringEventCal.get(java.util.Calendar.DAY_OF_YEAR));
		wicketTester.assertListView(endDayPanel.getPageRelativePath() + ":eventItem", bothRecurringEventsList);

		shortRecurringEventCal.set(java.util.Calendar.DAY_OF_MONTH, 17);
		MonthDayPanel afterEndDayPanel = (MonthDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + shortRecurringEventCal.get(java.util.Calendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + shortRecurringEventCal.get(java.util.Calendar.DAY_OF_YEAR));
		wicketTester.assertListView(afterEndDayPanel.getPageRelativePath() + ":eventItem", oneRecurringEventsList);

		// Assert long recurring event
		MonthDayPanel firstMonthDayPanel = (MonthDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + monthFirstDayDate.get(java.util.Calendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + monthFirstDayDate.get(java.util.Calendar.DAY_OF_YEAR));
		MonthDayPanel lastMonthDayPanel = (MonthDayPanel) wicketTester.getLastRenderedPage().get(PanelTestPage.PANEL_MARKUP_ID + ":monthRowRepeater:week" + monthLastDayDate.get(java.util.Calendar.WEEK_OF_YEAR) +":monthDayRepeater:day" + monthLastDayDate.get(java.util.Calendar.DAY_OF_YEAR));

		wicketTester.assertListView(firstMonthDayPanel.getPageRelativePath() + ":eventItem", oneRecurringEventsList);
		wicketTester.assertListView(lastMonthDayPanel.getPageRelativePath() + ":eventItem", oneRecurringEventsList);
	}

}
