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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.wicket.Page;
import org.apache.wicket.util.tester.ITestPageSource;
import org.apache.wicket.util.time.Time;
import org.webical.Event;
import org.webical.manager.impl.mock.MockEventManager;
import org.webical.manager.impl.mock.MockUserManager;
import org.webical.web.PanelTestPage;
import org.webical.web.WebicalApplicationTest;
import org.webical.web.action.IAction;
import org.webical.web.app.WebicalSession;
import org.webical.web.component.event.EventDetailsPanel;

/**
 * Test the EventDetailsViewPanel
 * @author paul
 *
 */
public class EventDetailsViewPanelTest extends WebicalApplicationTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		annotApplicationContextMock.putBean("userManager", new MockUserManager());
		annotApplicationContextMock.putBean("eventManager", new MockEventManager());
	}

	/**
	 *  Test if the panel get rendered with all the event details
	 */
	public void testSingleDayEventDetails(){

		// Define the start date
		GregorianCalendar startCalendar = new GregorianCalendar();
		startCalendar.set(2006, GregorianCalendar.OCTOBER, 17);
		startCalendar.set(Calendar.HOUR_OF_DAY, 12);
		startCalendar.set(Calendar.MINUTE, 0);
		startCalendar.set(Calendar.SECOND, 0);
		// Define the end date
		GregorianCalendar endCalendar = new GregorianCalendar();
		endCalendar.setTime(startCalendar.getTime());
		endCalendar.add(Calendar.HOUR_OF_DAY, 1);
		// Define the two date formatters
		SimpleDateFormat dateFormatter = new SimpleDateFormat(WebicalSession.getWebicalSession().getUserSettings().getDateFormat(), WebicalSession.getWebicalSession().getLocale());
		SimpleDateFormat timeFormatter = new SimpleDateFormat(WebicalSession.getWebicalSession().getUserSettings().getTimeFormat(), WebicalSession.getWebicalSession().getLocale());
		
		// Create an Event
		final Event event = new Event();
		event.setDescription("Event Description");
		event.setLocation("Event Location");
		event.setSummary("Event Summary");
		event.setDtStart(startCalendar.getTime());
		event.setDtEnd(endCalendar.getTime());

		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new EventDetailsPanel(PanelTestPage.PANEL_MARKUP_ID, event, new GregorianCalendar()) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		// Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID,EventDetailsPanel.class);

		// Assert the labels
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":whatLabel", event.getSummary());
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":whenLabel", dateFormatter.format(startCalendar.getTime()) + " " + timeFormatter.format(startCalendar.getTime()) + " - " + timeFormatter.format(endCalendar.getTime()));
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":whereLabel", event.getLocation());
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":descriptionLabel", event.getDescription());
	}

	public void testMultipleDayEventsDetails() {
		// TODO mattijs: implement this test
	}
	
	public void testAllDayEventDetails() {
		// TODO mattijs: implement this test
	}
	

}
