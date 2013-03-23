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

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.ITestPageSource;

import org.webical.web.action.IAction;
import org.webical.web.component.calendar.CalendarFormPanel;
import org.webical.User;
import org.webical.Calendar;
import org.webical.manager.WebicalException;

import org.webical.test.TestUtils;
import org.webical.test.manager.impl.mock.MockCalendarManager;
import org.webical.test.manager.impl.mock.MockEventManager;
import org.webical.test.web.PanelTestPage;
import org.webical.test.web.WebicalApplicationTest;

/**
 * @author ivo
 *
 */
public class CalendarAddEditPanelTest extends WebicalApplicationTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		//Prepare the user
		User user = TestUtils.getJAGUser();
		getTestSession().createUser(user);
		getTestSession().getUserSettings();
	}

	/**
	 * Tests the form in edit mode
	 * @throws WebicalException
	 */
	public void testEditForm() throws WebicalException {

		//Prepare the calendar
		final Calendar calendar = new Calendar();
		calendar.setName("calendar1");
		calendar.setType("ical-webdav");
		calendar.setUrl("http://www.webical.org/calendar1.ics");
		calendar.setUser(getTestSession().getUser());
		calendar.setUsername(TestUtils.USERID_WEBICAL);

		annotApplicationContextMock.putBean("calendarManager", new MockCalendarManager());
		annotApplicationContextMock.putBean("eventManager", new MockEventManager());

		//Create the testpage with our panel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new CalendarFormPanel(PanelTestPage.PANEL_MARKUP_ID, calendar) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		// Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, CalendarFormPanel.class);

		// Check the form fields
		FormTester calendarFormTester = wicketTester.newFormTester(PanelTestPage.PANEL_MARKUP_ID + ":calendarAddEditForm");

		// Name TextField
		if (!(calendarFormTester.getTextComponentValue("name").equals(calendar.getName()))) {
			fail("Expected " + calendar.getName() + " but found " + calendarFormTester.getTextComponentValue("name"));
		}
		// Type DropDownChoice
		DropDownChoice ddc = (DropDownChoice) calendarFormTester.getForm().get("type");
		if (!ddc.getChoices().get(Integer.valueOf(ddc.getValue())).equals(calendar.getType())) {
			fail("Expected " + calendar.getType() + " but found " + ddc.getChoices().get(Integer.valueOf(ddc.getValue())));
		}
		// URL TextField
		if (!(calendarFormTester.getTextComponentValue("url").equals(calendar.getUrl()))) {
			fail("Expected " + calendar.getUrl() + " but found " + calendarFormTester.getTextComponentValue("url"));
		}
		// Username TextField
		if (!(calendarFormTester.getTextComponentValue("username").equals(calendar.getUsername()))) {
			fail("Expected " + calendar.getUsername() + " but found " + calendarFormTester.getTextComponentValue("username"));
		}
	}

	/**
	 * Tests the form in add mode with (validation)
	 */
	public void testAddForm() {

		annotApplicationContextMock.putBean("calendarManager", new MockCalendarManager());
		annotApplicationContextMock.putBean("eventManager", new MockEventManager());

		//Create the testpage with our panel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new CalendarFormPanel(PanelTestPage.PANEL_MARKUP_ID, new Calendar()) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		//Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, CalendarFormPanel.class);

		//Check the validation
		wicketTester.newFormTester(PanelTestPage.PANEL_MARKUP_ID + ":calendarAddEditForm").submit();
		wicketTester.assertErrorMessages(new String[]{"Field 'name' is required.", "Field 'url' is required."});
		/* "Field 'type' is required.";  type is filled per default from model */
	}
}
