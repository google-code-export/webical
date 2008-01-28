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
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.tester.ITestPageSource;
import org.webical.User;
import org.webical.manager.impl.mock.MockCalendarManager;
import org.webical.manager.impl.mock.MockEventManager;
import org.webical.manager.impl.mock.MockUserManager;
import org.webical.util.CalendarUtils;
import org.webical.web.PanelTestPage;
import org.webical.web.WebicalApplicationTest;
import org.webical.web.action.IAction;
import org.webical.web.component.calendar.DateSwitcherPanel;
import org.webical.web.component.calendar.DayViewPanel;
import org.webical.web.component.calendar.WeekViewPanel;
import org.webical.web.component.calendar.model.DateSwitcherModel;


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
		annotApplicationContextMock.putBean("userManager", new MockUserManager());
		annotApplicationContextMock.putBean("eventManager", new MockEventManager());
		annotApplicationContextMock.putBean("calendarManager", new MockCalendarManager());
		currentDate = new GregorianCalendar();
	}

	/**
	 * Tests the Single Day mode and the links
	 */
	public void testSingleDayView() throws Exception {

		previousLinkCLickedDate = new GregorianCalendar();
		nextLinkCLickedDate = new GregorianCalendar();
		todayLinkClickedDate = new GregorianCalendar();
		todayLinkClickedDate.add(GregorianCalendar.DAY_OF_MONTH, 5);

		// Set up a DayViewPanel for the model to use
		final DayViewPanel calendarDayViewPanel = new DayViewPanel("test", new GregorianCalendar()){
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction(IAction action) { /* NOTHING TO DO */ }

		};

		// Create the model for the date switcher to use
		dateSwitcherModel = new DateSwitcherModel(new GregorianCalendar(), calendarDayViewPanel);

		wicketTester.startPage(new ITestPageSource(){
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new DateSwitcherPanel(PanelTestPage.PANEL_MARKUP_ID, dateSwitcherModel){
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
		wicketTester.clickLink(PanelTestPage.PANEL_MARKUP_ID + ":todayLink");
		wicketTester.clickLink(PanelTestPage.PANEL_MARKUP_ID + ":nextLink");

		assertTrue("New date not before old date", previousLinkCLickedDate.before(currentDate));
		assertTrue("New date not after old date", nextLinkCLickedDate.after(currentDate));
		assertTrue("New date doesnot equals old date", CalendarUtils.getStartOfDay(todayLinkClickedDate.getTime()).equals(CalendarUtils.getStartOfDay(currentDate.getTime())));
	}

	/**
	 * Tests the range mode
	 */
	public void testRangeView() throws Exception {

		// Prepare a User
		final User user = new User();
		user.setFirstName("James");
		user.setLastName("Gossling");
		user.setUserId("jag");
		webicalSession.setUser(user);

		GregorianCalendar assumedRangeStartCal = new GregorianCalendar();
		assumedRangeStartCal.setFirstDayOfWeek(Calendar.SUNDAY);
		assumedRangeStartCal.setTime(CalendarUtils.getFirstDayOfWeek(assumedRangeStartCal.getTime(), Calendar.SUNDAY));
		GregorianCalendar assumedRangeEndCal = new GregorianCalendar();
		assumedRangeEndCal.setFirstDayOfWeek(Calendar.SUNDAY);
		assumedRangeEndCal.setTime(assumedRangeStartCal.getTime());
		assumedRangeEndCal.add(GregorianCalendar.DAY_OF_WEEK, 7 - 1);

		WeekViewPanel calendarWeekViewPanel = new WeekViewPanel("test", new GregorianCalendar(), 7) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction(IAction action) { /* NOTHING TO DO */ }

		};

		// Create the model for the date switcher to use
		dateSwitcherModel = new DateSwitcherModel(new GregorianCalendar(), calendarWeekViewPanel);

		wicketTester.startPage(new ITestPageSource(){
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new PanelTestPage(new DateSwitcherPanel(PanelTestPage.PANEL_MARKUP_ID, dateSwitcherModel){
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
