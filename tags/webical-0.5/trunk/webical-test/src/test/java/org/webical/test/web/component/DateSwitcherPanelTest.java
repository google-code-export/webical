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


import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.tester.ITestPageSource;
import org.webical.User;
import org.webical.util.CalendarUtils;
import org.webical.web.action.IAction;
import org.webical.web.component.calendar.DateSwitcherPanel;
import org.webical.web.component.calendar.DayViewPanel;
import org.webical.web.component.calendar.WeekViewPanel;
import org.webical.web.component.calendar.model.DateSwitcherModel;
import org.webical.test.TestUtils;
import org.webical.test.manager.impl.mock.MockCalendarManager;
import org.webical.test.manager.impl.mock.MockEventManager;
import org.webical.test.web.PanelTestPage;
import org.webical.test.web.WebicalApplicationTest;


/**
 * Tests the DateSwitcherPanel in both modes
 * @author ivo
 *
 */
public class DateSwitcherPanelTest extends WebicalApplicationTest {

	private DateSwitcherModel dateSwitcherModel;
	private GregorianCalendar currentDate, todayLinkClickedDate, nextLinkCLickedDate, previousLinkCLickedDate;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		//Prepare the user
		User user = TestUtils.getJAGUser();
		getTestSession().createUser(user);
		getTestSession().getUserSettings();

		annotApplicationContextMock.putBean("calendarManager", new MockCalendarManager());
		annotApplicationContextMock.putBean("eventManager", new MockEventManager());
		currentDate = new GregorianCalendar();
	}

	/**
	 * Tests the Single Day mode and the links
	 */
	public void testSingleDayView() throws Exception {

		previousLinkCLickedDate = new GregorianCalendar();
		nextLinkCLickedDate = new GregorianCalendar();
		todayLinkClickedDate = new GregorianCalendar();

		// Set up a DayViewPanel for the model to use
		final DayViewPanel calendarDayViewPanel = new DayViewPanel("test", 1, new GregorianCalendar()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction(IAction action) { /* NOTHING TO DO */ }
		};

		// Create the model for the date switcher to use
		dateSwitcherModel = new DateSwitcherModel(new GregorianCalendar(), calendarDayViewPanel);

		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new DateSwitcherPanel(PanelTestPage.PANEL_MARKUP_ID, dateSwitcherModel) {
					private static final long serialVersionUID = 1L;

					@Override
					public void nextPeriod(AjaxRequestTarget target) {
						nextLinkCLickedDate.add(calendarDayViewPanel.getViewPeriodId(), calendarDayViewPanel.getViewPeriodLength());
					}

					@Override
					public void previousPeriod(AjaxRequestTarget target) {
						previousLinkCLickedDate.add(calendarDayViewPanel.getViewPeriodId(), calendarDayViewPanel.getViewPeriodLength() * -1);
					}

					@Override
					public void todaySelected(AjaxRequestTarget target) {
						GregorianCalendar todayCalendar = new GregorianCalendar();
						todayLinkClickedDate.setTime(todayCalendar.getTime());
					}
				});
			}
		});

		// SimpleDateFormat to format the date
		SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
		sdf.applyPattern(calendarDayViewPanel.getViewPeriodFormat());
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":rangeLabel", sdf.format(currentDate.getTime()));

		wicketTester.clickLink(PanelTestPage.PANEL_MARKUP_ID + ":previousLink");
		wicketTester.clickLink(PanelTestPage.PANEL_MARKUP_ID + ":todayButton");
		wicketTester.clickLink(PanelTestPage.PANEL_MARKUP_ID + ":nextLink");

		assertTrue("New date not before old date", previousLinkCLickedDate.before(currentDate));
		assertTrue("New date doesnot equals old date", CalendarUtils.getStartOfDay(todayLinkClickedDate.getTime()).equals(CalendarUtils.getStartOfDay(currentDate.getTime())));
		assertTrue("New date not after old date", nextLinkCLickedDate.after(currentDate));
	}

	/**
	 * Tests the range mode
	 */
	public void testRangeView() throws Exception {

		GregorianCalendar assumedRangeStartCal = new GregorianCalendar();
		assumedRangeStartCal.setFirstDayOfWeek(GregorianCalendar.SUNDAY);
		assumedRangeStartCal.setTime(CalendarUtils.getFirstDayOfWeek(assumedRangeStartCal.getTime(), GregorianCalendar.SUNDAY));
		GregorianCalendar assumedRangeEndCal = new GregorianCalendar();
		assumedRangeEndCal.setFirstDayOfWeek(GregorianCalendar.SUNDAY);
		assumedRangeEndCal.setTime(assumedRangeStartCal.getTime());
		assumedRangeEndCal.add(GregorianCalendar.DAY_OF_WEEK, 7 - 1);

		final GregorianCalendar currentDate = CalendarUtils.newTodayCalendar(getTestSession().getUserSettings().getFirstDayOfWeek());

		WeekViewPanel calendarWeekViewPanel = new WeekViewPanel("test", 7, currentDate) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction(IAction action) { /* NOTHING TO DO */ }
		};

		// Create the model for the date switcher to use
		dateSwitcherModel = new DateSwitcherModel(new GregorianCalendar(), calendarWeekViewPanel);

		wicketTester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new DateSwitcherPanel(PanelTestPage.PANEL_MARKUP_ID, dateSwitcherModel) {
					private static final long serialVersionUID = 1L;
					@Override
					public void nextPeriod(AjaxRequestTarget target) { /* NOTHING TO DO */ }
					@Override
					public void previousPeriod(AjaxRequestTarget target) { /* NOTHING TO DO */ }
					@Override
					public void todaySelected(AjaxRequestTarget target) { /* NOTHING TO DO */ }
				});
			}
		});

		SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
		sdf.applyPattern(calendarWeekViewPanel.getViewPeriodFormat());
		wicketTester.assertLabel(PanelTestPage.PANEL_MARKUP_ID + ":rangeLabel", sdf.format(assumedRangeStartCal.getTime())  + " - " + sdf.format(assumedRangeEndCal.getTime()));
	}
}
