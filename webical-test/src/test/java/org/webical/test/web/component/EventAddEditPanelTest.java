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

import org.apache.wicket.Page;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.ITestPageSource;
import org.webical.User;
import org.webical.Calendar;
import org.webical.Event;
import org.webical.manager.WebicalException;
import org.webical.web.action.IAction;
import org.webical.web.component.event.EventFormPanel;
import org.webical.test.TestUtils;
import org.webical.test.manager.impl.mock.MockCalendarManager;
import org.webical.test.manager.impl.mock.MockEventManager;
import org.webical.test.web.PanelTestPage;
import org.webical.test.web.WebicalApplicationTest;


public class EventAddEditPanelTest extends WebicalApplicationTest {

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
		calendar1.setUsername(TestUtils.USERID_JAG);
		calendar1.setUser(getTestSession().getUser());
		mockCalendarManager.storeCalendar(calendar1);
		Calendar calendar2 = new Calendar();
		calendar2.setCalendarId(2L);
		calendar2.setName("Calendar two");
		calendar2.setType("ical-webdav");
		calendar2.setUrl("http://www.webical.org/calendar2.ics");
		calendar2.setUsername(TestUtils.USERID_JAG);
		calendar2.setUser(getTestSession().getUser());
		mockCalendarManager.storeCalendar(calendar2);

		annotApplicationContextMock.putBean("eventManager", new MockEventManager());
	}

	/**
	 * Tests the form in edit mode
	 */
	public void testEditForm() throws WebicalException {

		// Get the CalendarManager.
		MockCalendarManager mockCalendarManager = (MockCalendarManager) annotApplicationContextMock.getBean("calendarManager");
		Calendar calendar1 = mockCalendarManager.getCalendarById("1");

		//prepare the event
		final Event event = new Event();
		event.setSummary("Event one summary");
		event.setCalendar(calendar1);
		event.setLocation("Event one Location");
		event.setDescription("Event one description");

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
		if (!(eventFormTester.getTextComponentValue("summary").equals(event.getSummary()))) {
			fail("Expected " + event.getSummary() + " but found " + eventFormTester.getTextComponentValue("summary"));
		}
		// Description
		if (!(eventFormTester.getTextComponentValue("description").equals(event.getDescription()))) {
			fail("Expected " + event.getDescription() + " but found " + eventFormTester.getTextComponentValue("description"));
		}
	}

	/**
	 * Tests the validation in add mode
	 */
	public void testAddForm() throws WebicalException
	{
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
