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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.ITestPageSource;
import org.webical.Calendar;
import org.webical.Event;
import org.webical.User;
import org.webical.manager.CalendarManager;
import org.webical.manager.EventManager;
import org.webical.manager.WebicalException;
import org.webical.manager.impl.mock.MockEventManager;
import org.webical.manager.impl.mock.MockUserManager;
import org.webical.web.PanelTestPage;
import org.webical.web.WebicalApplicationTest;
import org.webical.web.action.IAction;
import org.webical.web.component.calendar.CalendarAddEditPanel;

/**
 * @author ivo
 *
 */
public class CalendarAddEditPanelTest extends WebicalApplicationTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		annotApplicationContextMock.putBean("userManager", new MockUserManager());

		//Prepare the user
		final User user = new User();
		user.setBirthDate(new Date());
		user.setFirstName("Jake");
		user.setLastNamePrefix("");
		user.setLastName("Dunn");
		user.setUserId("nomad");
		webicalSession.setUser(user);
	}

	/**
	 * Tests the form in edit mode
	 * @throws WebicalException
	 */
	public void testEditForm() throws WebicalException {

		//Prepare the calendar
		final Calendar calendar = new Calendar();
		calendar.setName("calendar1");
		calendar.setType("webdav");
		calendar.setUrl("http://www.webical.org/calendar.ics");
		calendar.setUsername("nomad");

		CalendarManager calendarManagerMock = createMock(CalendarManager.class);
		annotApplicationContextMock.putBean("calendarManager", calendarManagerMock);

		Set<String> types = new HashSet<String>();
		types.add("webdav");
		expect(calendarManagerMock.getAvailableCalendarTypes()).andReturn(types).anyTimes();

		replay(calendarManagerMock);

/*		annotApplicationContextMock.putBean("calendarManager", new MockCalendarManager() {
			@Override
			public Set<String> getAvailableCalendarTypes() throws WebicalException {
				Set<String> types = new HashSet<String>();
				types.add("webdav");
				return types;
			}
		});*/

		annotApplicationContextMock.putBean("eventManager", new MockEventManager());

		//Create the testpage with our panel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new CalendarAddEditPanel(PanelTestPage.PANEL_MARKUP_ID, calendar) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }

				});
			}

		});

		// Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, CalendarAddEditPanel.class);

		// Check the form fields
		FormTester calendarFormTester = wicketTester.newFormTester(PanelTestPage.PANEL_MARKUP_ID + ":calendarAddEditForm");

		// Name TextField
		if(!(calendarFormTester.getTextComponentValue("name").equals(calendar.getName()))) {
			fail("Expected " + calendar.getName() + " but found " + calendarFormTester.getTextComponentValue("name"));
		}
		// Type DropDownChoice
		DropDownChoice ddc = (DropDownChoice) calendarFormTester.getForm().get("type");
		if(!ddc.getChoices().get(Integer.valueOf(ddc.getValue())).equals(calendar.getType())) {
			fail("Expected " + calendar.getType() + " but found " + ddc.getChoices().get(Integer.valueOf(ddc.getValue())));
		}
		// URL TextField
		if(!(calendarFormTester.getTextComponentValue("url").equals(calendar.getUrl()))) {
			fail("Expected " + calendar.getUrl() + " but found " + calendarFormTester.getTextComponentValue("url"));
		}
		// Username TextField
		if(!(calendarFormTester.getTextComponentValue("username").equals(calendar.getUsername()))) {
			fail("Expected " + calendar.getUsername() + " but found " + calendarFormTester.getTextComponentValue("username"));
		}

		//wicketTester.assertContains("calendar1");
		//wicketTester.assertContains("webdav");
		//wicketTester.assertContains("http://www.webical.org");
		//wicketTester.assertContains("nomad");
	}

	/**
	 * Tests the form in add mode with (validation)
	 */
	public void testAddForm() {

		annotApplicationContextMock.putBean("calendarManager", new CalendarManager() {

			public void storeCalendar(Calendar calendar) throws WebicalException { }

			public void removeCalendar(Calendar calendar) throws WebicalException { }

			public List<Calendar> getCalendars(User user) throws WebicalException { return null;}

			public Set<String> getAvailableCalendarTypes() throws WebicalException { return new HashSet<String>(); }

			public Calendar getCalendarForEvent(Event event) { return null; }

			public void storeCalendar(Calendar calendar, List<Event> events) throws WebicalException {}

			public Calendar getCalendarById(String id) throws WebicalException {
				return null;
			}
		});

		annotApplicationContextMock.putBean("eventManager", new EventManager() {

			public List<Event> getAllEvents(Calendar calendar) throws WebicalException {
				return null;
			}
			public Event getEventByUid(String Uid, Calendar calendar) throws WebicalException {
				return null;
			}
			public List<Event> getEventsForPeriod(Calendar calendar, Date dtStart, Date dtEnd) throws WebicalException {
				return null;
			}
			public List<Event> getEventsForPeriod(User user, Date dtStart, Date dtEnd) throws WebicalException {
				return null;
			}
			public void removeEvent(Event event) throws WebicalException {}
			public void storeEvent(Event event) throws WebicalException {}
		});

		//Create the testpage with our panel
		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new CalendarAddEditPanel(PanelTestPage.PANEL_MARKUP_ID, null){
					private static final long serialVersionUID = 1L;

					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }

				});
			}

		});

		//Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID, CalendarAddEditPanel.class);

		//Check the validation
		wicketTester.newFormTester(PanelTestPage.PANEL_MARKUP_ID + ":calendarAddEditForm").submit();
		wicketTester.assertErrorMessages(new String[]{"Field 'name' is required.", "Field 'type' is required.", "Field 'url' is required."});
	}
}
