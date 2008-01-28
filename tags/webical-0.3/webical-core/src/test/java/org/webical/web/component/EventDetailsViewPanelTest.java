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
import org.webical.web.component.event.EventDetailsViewPanel;

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
	public void testRenderingWithEventDetails(){

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(2006, GregorianCalendar.OCTOBER, 17);

		final Event event = new Event();
		event.setDescription("Event Description");
		event.setLocation("EVen Location");
		event.setSummary("Event Summary");
		event.setDtStart(calendar.getTime());

		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new EventDetailsViewPanel(PanelTestPage.PANEL_MARKUP_ID, event) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		// Basic assertions
		wicketTester.assertRenderedPage(PanelTestPage.class);
		wicketTester.assertComponent(PanelTestPage.PANEL_MARKUP_ID,EventDetailsViewPanel.class);

		// Assert the labels
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":whatLabel", event.getSummary());
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":whenLabel", (Time.valueOf(event.getDtStart().getTime())).toString(EventDetailsViewPanel.DATE_STRING));
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":whereLabel", event.getLocation());
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":descriptionLabel", event.getDescription());
	}

	/**
	 *  Test if the panels get rendered without details
	 */
	public void testRenderingWithoutEventDetails(){

		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;
			public Page getTestPage() {
				return new PanelTestPage(new EventDetailsViewPanel(PanelTestPage.PANEL_MARKUP_ID, new Event()) {
					private static final long serialVersionUID = 1L;
					@Override
					public void onAction(IAction action) { /* NOTHING TO DO */ }
				});
			}
		});

		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":whatLabel", "");
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":whenLabel", "");
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":whereLabel", "");
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":descriptionLabel", "");
	}

}
