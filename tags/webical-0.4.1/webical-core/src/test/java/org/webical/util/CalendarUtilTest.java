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

package org.webical.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import junit.framework.TestCase;

public class CalendarUtilTest extends TestCase {

	Date compareDate = new GregorianCalendar(2006, Calendar.NOVEMBER, 26).getTime();
	Date date = new GregorianCalendar(2006, Calendar.NOVEMBER, 26, 3, 30, 20).getTime();

	/**
	 * Test retrieval of Local time
	 */
	public void testGetLocalTime(){
		org.webical.util.CalendarUtils.getLocalTime(date, Locale.ENGLISH, false);
	}

	/**
	 * Test date calculations
	 */
	public void testCalendarUtil(){

		// Date in the first week of December
		Date date = org.webical.util.CalendarUtils.getFirstDayOfWeekOfMonth(new  GregorianCalendar(2006,Calendar.DECEMBER,6).getTime(),Calendar.SUNDAY);
		assertEquals(compareDate, date);

		// second week of December should be equals the month is still December
		date = org.webical.util.CalendarUtils.getFirstDayOfWeekOfMonth(new  GregorianCalendar(2006,Calendar.DECEMBER,7).getTime(),Calendar.SUNDAY);
		assertEquals(compareDate, date);

		// another month, the date should now be the first sunday from the week of Jan the first
		date = org.webical.util.CalendarUtils.getFirstDayOfWeekOfMonth(new  GregorianCalendar(2006,Calendar.JANUARY,7).getTime(),Calendar.SUNDAY);
		assertNotSame(compareDate, date);

		compareDate = new GregorianCalendar(2006,Calendar.DECEMBER,31).getTime();

		// test if the last day of December equals December 31.
		date = org.webical.util.CalendarUtils.getLastDayOfMonth(new GregorianCalendar(2006,Calendar.DECEMBER,4).getTime());
		assertEquals(compareDate, date);

		compareDate = new GregorianCalendar(2006,Calendar.DECEMBER,31).getTime();
		date = org.webical.util.CalendarUtils.getLastWeekDayOfMonth(new GregorianCalendar(2006,Calendar.DECEMBER,31).getTime(), Calendar.MONDAY);

		assertEquals(compareDate, date);

		compareDate = new GregorianCalendar(2006,Calendar.FEBRUARY,5).getTime();
		date = org.webical.util.CalendarUtils.getLastWeekDayOfMonth(new GregorianCalendar(2006,Calendar.JANUARY,21).getTime(), Calendar.MONDAY);

		assertEquals(compareDate, date);

		// Assert first day of week
		date = org.webical.util.CalendarUtils.getFirstDayOfWeek(org.webical.util.CalendarUtils.addDays(GregorianCalendar.getInstance().getTime(), 0),java.util.Calendar.MONDAY);
		GregorianCalendar dayCalendar = new GregorianCalendar();
		dayCalendar.setTime(date);

		assertEquals(java.util.Calendar.MONDAY, dayCalendar.get(java.util.Calendar.DAY_OF_WEEK));

		GregorianCalendar dtStart = new GregorianCalendar(2006, java.util.Calendar.DECEMBER, 1);
		GregorianCalendar dtEnd = new GregorianCalendar(2006, java.util.Calendar.DECEMBER, 2);

		int i = CalendarUtils.getDifferenceInDays(dtStart.getTime(), dtEnd.getTime());

		assertEquals(1,i);
	}
}
