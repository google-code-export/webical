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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;

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
import org.webical.web.component.calendar.DayViewPanel;
import org.webical.test.TestUtils;
import org.webical.test.manager.impl.mock.MockCalendarManager;
import org.webical.test.manager.impl.mock.MockEventManager;
import org.webical.test.web.PanelTestPage;
import org.webical.test.web.WebicalApplicationTest;

/**
 *
 * @author jochem
 *
 */
public class DayViewPanelTest extends WebicalApplicationTest
{
	private static Log log = LogFactory.getLog(DayViewPanelTest.class);

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
		Calendar calendar2 = new Calendar();
		calendar2.setCalendarId(2L);
		calendar2.setName("Calendar two");
		calendar2.setType("ical-webdav");
		calendar2.setUrl("http://www.webical.org/calendar2.ics");
		calendar2.setUser(getTestSession().getUser());
		mockCalendarManager.storeCalendar(calendar2);
	}

	public int getFirstDayOfWeek()
	{
		return getTestSession().getUserSettings().getFirstDayOfWeek();
	}

	/**
	 * Test whether the panel renders correct without events
	 */
	public void testWithoutEvents() throws WebicalException {

		// Add an EventManager
		annotApplicationContextMock.putBean("eventManager", new MockEventManager());

		final GregorianCalendar currentDate = CalendarUtils.newTodayCalendar(getFirstDayOfWeek());

		// Create the testpage with with a DayViewPanel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new DayViewPanel(PanelTestPage.PANEL_MARKUP_ID, 1, currentDate) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		//Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, DayViewPanel.class);

		// Assert the heading label
		DateFormat dateFormat = new SimpleDateFormat("EEEE", getTestSession().getLocale());
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":dayLink", dateFormat.format(currentDate.getTime()));

		// Assert the number of events rendered
		wicketTester.assertListView(PanelTestPage.PANEL_MARKUP_ID + ":eventItem", new ArrayList<Event>());
	}

	public void testWithEvents() throws WebicalException {

		// Get the CalendarManager.
		MockCalendarManager mockCalendarManager = (MockCalendarManager) annotApplicationContextMock.getBean("calendarManager");
		Calendar calendar1 = mockCalendarManager.getCalendarById("1");

		// Add an EventManager for the test Events.
		MockEventManager mockEventManager = new MockEventManager();
		annotApplicationContextMock.putBean("eventManager", mockEventManager);

		final GregorianCalendar currentDate = CalendarUtils.newTodayCalendar(getFirstDayOfWeek());

		GregorianCalendar cal = CalendarUtils.duplicateCalendar(currentDate);
		GregorianCalendar refcal = CalendarUtils.duplicateCalendar(currentDate);
		refcal.set(GregorianCalendar.HOUR_OF_DAY, 12);
		refcal.set(GregorianCalendar.MINUTE, 0);
		refcal.set(GregorianCalendar.SECOND, 0);

		// Create list with events for the manager
		final List<Event> events = new ArrayList<Event>();
		// Add a normal event
		Event event = new Event();
		event.setUid("e1");
		event.setCalendar(calendar1);
		event.setSummary("Normal Event Summary");
		event.setLocation("Normal Event Location");
		event.setDescription("Normal Event Description");
		event.setDtStart(refcal.getTime());
		event.setDtEnd(CalendarUtils.addHours(refcal.getTime(), 2));
		log.debug("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		events.add(event);
		mockEventManager.storeEvent(event);

		// Add a recurring event, starting yesterday, ending tommorrow
		event = new Event();
		event.setUid("e2");
		event.setCalendar(calendar1);
		event.setSummary("Recurring Event Yesterday Summary");
		event.setLocation("Recurring Event Location");
		event.setDescription("Recurring Event Yesterday Description");
		cal.setTime(refcal.getTime());
		cal.add(GregorianCalendar.DAY_OF_MONTH, -1);
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.HOUR_OF_DAY,	2);
		cal.add(GregorianCalendar.DAY_OF_MONTH, 2);
		event.setDtEnd(cal.getTime());
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(0, 1, cal.getTime()));
		log.debug("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		events.add(event);
		mockEventManager.storeEvent(event);

		// Add a recurring event, starting last month, ending next month
		event = new Event();
		event.setUid("e3");
		event.setCalendar(calendar1);
		event.setSummary("Recurring Event Last Month Summary");
		event.setLocation("Recurring Event Location");
		event.setDescription("Recurring Event Last Month Description");
		cal.setTime(refcal.getTime());
		cal.add(GregorianCalendar.MONTH, -1);
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.HOUR_OF_DAY,	2);
		cal.add(GregorianCalendar.MONTH, 2);
		event.setDtEnd(cal.getTime());
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(0, 1, CalendarUtils.getEndOfDay(cal.getTime())));
		log.debug("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		events.add(event);
		mockEventManager.storeEvent(event);

		// Add a (pseudo) all day event, starting today 00:00 hours, ending today 00:00 hours
		event = new Event();
		event.setUid("e4");
		event.setCalendar(calendar1);
		event.setSummary("Pseudo All Day Event");
		event.setLocation("All Day Event Location");
		event.setDescription("Starting yesterday midnight, ending today 00:00 hours");
		cal.setTime(refcal.getTime());
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		event.setDtStart(cal.getTime());
		cal.add(GregorianCalendar.DAY_OF_MONTH, 1);
		event.setDtEnd(cal.getTime());
		log.debug("Adding event: " + event.getDescription() + " -> " + event.getDtStart() + " - " + event.getDtEnd());
		events.add(event);
		mockEventManager.storeEvent(event);

		// Create the test page with a DayViewPanel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new DayViewPanel(PanelTestPage.PANEL_MARKUP_ID, 1, currentDate) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		// Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, DayViewPanel.class);

		// Assert the heading label
		DateFormat dateFormat = new SimpleDateFormat("EEEE", getTestSession().getLocale());
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":dayLink", dateFormat.format(currentDate.getTime()));

		// Assert the number of events rendered
		String panelPath = PanelTestPage.PANEL_MARKUP_ID + ":eventItem";
		wicketTester.assertListView(panelPath, events);
	}
}
