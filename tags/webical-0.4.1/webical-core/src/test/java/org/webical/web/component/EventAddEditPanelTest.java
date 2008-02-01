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
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.ITestPageSource;
import org.webical.Calendar;
import org.webical.Event;
import org.webical.User;
import org.webical.manager.WebicalException;
import org.webical.manager.impl.mock.MockCalendarManager;
import org.webical.manager.impl.mock.MockEventManager;
import org.webical.manager.impl.mock.MockUserManager;
import org.webical.web.PanelTestPage;
import org.webical.web.WebicalApplicationTest;
import org.webical.web.action.IAction;
import org.webical.web.component.event.EventFormPanel;

public class EventAddEditPanelTest extends WebicalApplicationTest{

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		annotApplicationContextMock.putBean("userManager", new MockUserManager());
		annotApplicationContextMock.putBean("eventManager", new MockEventManager());

		// Prepare a User
		final User user = new User();
		user.setFirstName("James");
		user.setLastName("Gossling");
		user.setUserId("jag");

	}

	/**
	 * Tests the form in edit mode
	 */
	public void testEditForm() {

		// Prepare the calendar
		final Calendar calendar = new Calendar();
		calendar.setName("Calendar one");
		calendar.setType("webdav");
		calendar.setUrl("http://www.webical.org/calendar.ics");
		calendar.setUsername("jag");

		//prepare the event
		final Event event = new Event();
		event.setSummary("Event one summary");
		event.setCalendar(calendar);
		event.setDescription("Event one description");

		annotApplicationContextMock.putBean("calendarManager", new MockCalendarManager() {

			@Override
			public List<Calendar> getCalendars(User user) throws WebicalException {
				ArrayList<Calendar> calendars = new ArrayList<Calendar>();
				calendars.add(calendar);
				return calendars;
			}
			
		});

		// Create testpage with an EventAddEditPanel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new EventFormPanel(PanelTestPage.PANEL_MARKUP_ID, event, new GregorianCalendar()) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		// Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, EventFormPanel.class);

		// Check the fields
		FormTester eventFormTester = wicketTester.newFormTester(PanelTestPage.PANEL_MARKUP_ID + ":eventForm");
		// Summary
		if(!(eventFormTester.getTextComponentValue("summary").equals(event.getSummary()))) {
			fail("Expected " + event.getSummary() + " but found " + eventFormTester.getTextComponentValue("summary"));
		}
		// Description
		if(!(eventFormTester.getTextComponentValue("description").equals(event.getDescription()))) {
			fail("Expected " + event.getDescription() + " but found " + eventFormTester.getTextComponentValue("description"));
		}

	}

	/**
	 * Tests the validation in add mode
	 */
	public void testAddForm() {

		annotApplicationContextMock.putBean("calendarManager", new MockCalendarManager() {

			@Override
			public List<Calendar> getCalendars(User user) throws WebicalException {
				List<Calendar> calendars = new ArrayList<Calendar>();

				// Prepare the calendar
				Calendar calendar = new Calendar();
				calendar.setName("Calendar two");
				calendar.setType("webdav");
				calendar.setUrl("http://www.webical.org/calendar.ics");
				calendar.setUsername("jag");

				calendars.add(calendar);

				return calendars;
			}
		});

		//Create the testpage with our panel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {

				return new PanelTestPage(new EventFormPanel(PanelTestPage.PANEL_MARKUP_ID, null, new GregorianCalendar()) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		// Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, EventFormPanel.class);

		// Check the validation
		wicketTester.newFormTester(PanelTestPage.PANEL_MARKUP_ID + ":eventForm").submit();
		wicketTester.assertErrorMessages(new String[]{"'What' is a required field."});
	}

}
