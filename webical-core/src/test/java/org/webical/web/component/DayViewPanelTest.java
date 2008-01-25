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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.util.tester.ITestPageSource;
import org.webical.Calendar;
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
import org.webical.web.component.calendar.DayViewPanel;

/**
 *
 * @author jochem
 *
 */
public class DayViewPanelTest extends WebicalApplicationTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		annotApplicationContextMock.putBean("userManager", new MockUserManager());
	}

	/**
	 * Test whether the panel renders correct without events
	 */
	public void testWithoutEvents() {
		final List<Calendar> calendars = new ArrayList<Calendar>();

		// Add a CalendarManager
		annotApplicationContextMock.putBean("calendarManager", new MockCalendarManager() {
			@Override
			public List<Calendar> getCalendars(User user) throws WebicalException {
				Calendar calendar = new Calendar();
				calendar.setName("calendar1");
				calendars.add(calendar);
				calendar = new Calendar();
				calendar.setName("calendar2");
				calendars.add(calendar);
				return calendars;
			}
		});

		// Add an EventManager
		annotApplicationContextMock.putBean("eventManager", new MockEventManager());

		// Create the testpage with with a DayViewPanel
		wicketTester.startPage(new ITestPageSource(){
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new DayViewPanel(PanelTestPage.PANEL_MARKUP_ID, new GregorianCalendar()){
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
		DateFormat dateFormat = new SimpleDateFormat("EEEE");
		GregorianCalendar headingDate = new GregorianCalendar();
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":dayHeadingLabel", dateFormat.format(headingDate.getTime()));

		// Assert the number of events rendered
		wicketTester.assertListView(PanelTestPage.PANEL_MARKUP_ID + ":eventItem", new ArrayList<Event>());
	}

	public void testWithEvents() throws WebicalException {

		// Prepare a User
		final User user = new User();
		user.setFirstName("James");
		user.setLastName("Gossling");
		user.setUserId("jag");
		webicalSession.setUser(user);

		GregorianCalendar refcal = new GregorianCalendar();
		refcal.set(java.util.Calendar.HOUR_OF_DAY, 12);
		refcal.set(java.util.Calendar.MINUTE, 0);
		refcal.set(java.util.Calendar.SECOND, 0);
		GregorianCalendar cal = new GregorianCalendar();
		
		// Create list with events for the manager
		final List<Event> events = new ArrayList<Event>();
		// Add a normal event
		Event event = new Event();
		event.setUid("e1");
		event.setSummary("Normal Event Summary");
		event.setDescription("Normal Event Description");
		event.setDtStart(refcal.getTime());
		event.setDtEnd(CalendarUtils.addHours(refcal.getTime(), 2));
		events.add(event);

		// Add a recurring event, starting yesterday, ending tommorrow
		event = new Event();
		event.setUid("e2");
		event.setSummary("Recurring Event Yesterday Summary");
		event.setDescription("Recurring Event Yesterday Description");
		cal.setTime(refcal.getTime());
		cal.add(java.util.Calendar.DAY_OF_MONTH, -1);
		event.setDtStart(cal.getTime());
		cal.add(java.util.Calendar.HOUR_OF_DAY,	2);
		event.setDtEnd(cal.getTime());
		cal.add(java.util.Calendar.DAY_OF_MONTH, 2);
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(0, 1, cal.getTime()));
		events.add(event);

		// Add a recurring event, starting last month, ending next month
		event = new Event();
		event.setUid("e3");
		event.setSummary("Recurring Event Last Month Summary");
		event.setDescription("Recurring Event Last Month Description");
		cal.setTime(refcal.getTime());
		cal.add(java.util.Calendar.MONTH, -1);
		event.setDtStart(cal.getTime());
		cal.add(java.util.Calendar.HOUR_OF_DAY,	2);
		event.setDtEnd(cal.getTime());
		cal.add(java.util.Calendar.MONTH, 2);
		RecurrenceUtil.setRecurrenceRule(event, new Recurrence(0, 1, cal.getTime()));
		events.add(event);

		final GregorianCalendar currentDate = new GregorianCalendar();
		Date startDate = CalendarUtils.getStartOfDay(currentDate.getTime());
		Date endDate = CalendarUtils.getEndOfDay(currentDate.getTime());

		// Add an EventManager through EasyMock with the Events.
		EventManager eventManagerMock = createMock(EventManager.class);
		annotApplicationContextMock.putBean("eventManager", eventManagerMock);
		// temporarily set to times(1, 2) because of assertListView second call
		expect(eventManagerMock.getEventsForPeriod(WebicalSession.getWebicalSession().getUser(), startDate, endDate)).andReturn(events).times(1, 2);
		replay(eventManagerMock);

		// Add a CalendarManager.
		annotApplicationContextMock.putBean("calendarManager", new MockCalendarManager());

		// Create the testpage with a DayViewPanel
		wicketTester.startPage(new ITestPageSource(){
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new DayViewPanel(PanelTestPage.PANEL_MARKUP_ID, currentDate) {
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
		DateFormat dateFormat = new SimpleDateFormat("EEEE");
		GregorianCalendar headingDate = new GregorianCalendar();
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":dayHeadingLabel", dateFormat.format(headingDate.getTime()));

		// Assert the number of events rendered
		/*
		 * FIXME assertListView calls the EventManager a second time
		 * resulting in an error at the EasyMock eventManagerMock.
		 * Apperently there is a bug in either DayViewPanel or EventsModel. This
		 * does not seem to happen with a normal run.
		 */
		wicketTester.assertListView(PanelTestPage.PANEL_MARKUP_ID + ":eventItem", events);


	}

}
