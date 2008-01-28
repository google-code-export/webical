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

package org.webical.web.pages;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.webical.ApplicationSettings;
import org.webical.Calendar;
import org.webical.Event;
import org.webical.TestUtils;
import org.webical.User;
import org.webical.manager.CalendarManager;
import org.webical.manager.EventManager;
import org.webical.manager.UserManager;
import org.webical.manager.WebicalException;
import org.webical.manager.impl.mock.MockUserManager;
import org.webical.web.WebicalApplicationTest;
import org.webical.web.app.WebicalSession;
import org.webical.web.component.HeaderPanel;
import org.webical.web.component.calendar.CalendarListPanel;
import org.webical.web.component.calendar.CalendarPanel;
import org.webical.web.component.settings.SettingsPanelsPanel;

import org.apache.wicket.Page;
import org.apache.wicket.util.tester.ITestPageSource;

/**
 * Tester for the basePage
 * @author ivo
 *
 */
public class BasePageTest extends WebicalApplicationTest {

	
	
	/* (non-Javadoc)
	 * @see org.webical.web.WebicalApplicationTest#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ApplicationSettings applicationSettings = new ApplicationSettings();
		TestUtils.initializeApplicationSettingsFactory(applicationSettings);
	}


	/**
	 * Tests if the page renders without errors
	 */
	public void testBasePageRenders() {
		
		annotApplicationContextMock.putBean("calendarManager", getMockCalendarManager());
		annotApplicationContextMock.putBean("userManager", getMockUserManager());
		annotApplicationContextMock.putBean("eventManager", getMockEventManager());
		
		//Set the testPage
		wicketTester.startPage(getTestPageSource());
		
		//Assert that the page renders without error messagges
		wicketTester.assertNoErrorMessage();
		
		//Check if all the components are there
		wicketTester.assertComponent("headerPanel", HeaderPanel.class);
		wicketTester.assertComponent("contentPanel", SettingsPanelsPanel.class);
		wicketTester.assertComponent("calendarPanel", CalendarListPanel.class);
	}
	
	
	/**
	 * Tests the navigation links
	 */
	public void testBasePageLinks() {
		
		annotApplicationContextMock.putBean("calendarManager", getMockCalendarManager());
		annotApplicationContextMock.putBean("eventManager", getMockEventManager());
		annotApplicationContextMock.putBean("userManager", new MockUserManager());
		
		//Set the testPage
		wicketTester.startPage(getTestPageSource());
		
		//Try the settings link
		wicketTester.clickLink("headerPanel:settingsPanelLink");
		wicketTester.assertComponent("contentPanel", SettingsPanelsPanel.class);
				
		//Try the CalendarViews Link
		wicketTester.clickLink("headerPanel:calendarViewsPanelLink");
		wicketTester.assertComponent("contentPanel", CalendarPanel.class);
		
	}
	
	/**
	 * Creates an ITestPageSource returning a BasePage ready to be tested 
	 * @return an Extended BasePage, overriding the initSession method 
	 */
	private ITestPageSource getTestPageSource() {
		/* override the initSession method because there wont be an UserPrincipal in the test Request */
		return new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				 return new BasePage() {
					private static final long serialVersionUID = 1L;

					public void initSession() {
						WebicalSession webicalSession = (WebicalSession)getSession();
						webicalSession.setUser(new User());
					}
				 };
			};
		};
	}
	
	/**
	 * Creates an CalendarManager
	 * @return a CalendarManager
	 */
	private CalendarManager getMockCalendarManager() {
		return new CalendarManager() {

			public void storeCalendar(Calendar calendar) throws WebicalException { }

			public void removeCalendar(Calendar calendar) throws WebicalException { }

			public List<Calendar> getCalendars(User user) throws WebicalException { return new ArrayList<Calendar>(); }

			public Set<String> getAvailableCalendarTypes() throws WebicalException { return null; }

			public Calendar getCalendarForEvent(Event event) {
				return null;
			}

			public void storeCalendar(Calendar calendar, List<Event> events) throws WebicalException {
				
			}

			public Calendar getCalendarById(String id) throws WebicalException {
				return null;
			}
			
		};
	}
	
	private EventManager getMockEventManager() {
		return new EventManager() {

			public List<Event> getAllEvents(Calendar calendar) throws WebicalException {return null; }

			public List<Event> getEventsForPeriod(Calendar calendar, Date dtStart, Date dtEnd) 
				throws WebicalException { return null; }
			
			public List<Event> getEventsForPeriod(User user, Date dtStart, Date dtEnd) 
				throws WebicalException {return null; }

			public void removeEvent(Event event) throws WebicalException { }

			public void storeEvent(Event event) throws WebicalException { }

			public Event getEventByUid(String Uid, Calendar calendar) throws WebicalException {
				return null;
			}			
		};
	}
	
	private UserManager getMockUserManager() {
		return new UserManager() {

			public User getUser(String userId) throws WebicalException {return null;}

			public void removeUser(User user) throws WebicalException {}

			public void storeUser(User user) throws WebicalException {}
			
		};
	}

}
