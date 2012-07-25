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

package org.webical.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Class with Calendar util methods
 * @author Ivo van Dongen
 * @author Mattijs Hoitink
 *
 */
public abstract class CalendarUtils {

	private static long dayInMs = (60 * 60 * 24 * 1000);
	private static long hourInMs = (60 * 60 * 1000);

	/**
	 * Create a new GregorianCalendar for today taking into account first day of the week.
	 *
	 * @param firstDayOfWeek the firstDayOfWeek for the new GregorianCalendar: eg. CALENDAR.MONDAY
	 * @return the new GregorianCalendar
	 */
	public static GregorianCalendar newTodayCalendar(int firstDayOfWeek)
	{
		GregorianCalendar today = new GregorianCalendar();
		today.setFirstDayOfWeek(firstDayOfWeek);
		return today;
	}

	/**
	 * Duplicate a GregorianCalendar taking into account first day of the week.
	 *
	 * @param date the GregorianCalendar to duplicate
	 * @return the duplicated GregorianCalendar
	 */
	public static GregorianCalendar duplicateCalendar(GregorianCalendar date)
	{
		GregorianCalendar calendar = newTodayCalendar(date.getFirstDayOfWeek());
		calendar.setTime(date.getTime());
		return calendar;
	}

	/**
	 * Returns the start of the day
	 * @param day the day
	 * @return the start of the day
	 */
	public static Date getStartOfDay(Date day) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(day);
		calendar.set(GregorianCalendar.HOUR, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		calendar.set(GregorianCalendar.MILLISECOND, 0);
		calendar.set(GregorianCalendar.AM_PM, GregorianCalendar.AM);

		return calendar.getTime();
	}

	/**
	 * Returns a date with 0 ms
	 * @param date
	 * @return the date without ms
	 */
	public static Date getDateWithoutMs(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(GregorianCalendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/**
	 * Returns the number of ms in a day
	 * @return the number of ms in a day
	 */
	public static long getHourInMs() {
		return hourInMs;
	}

	/**
	 * Returns the end of the day
	 * @param day the day
	 * @return the end of the day
	 */
	public static Date getEndOfDay(Date day) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(day);
		calendar.set(GregorianCalendar.HOUR, 11);
		calendar.set(GregorianCalendar.MINUTE, 59);
		calendar.set(GregorianCalendar.SECOND, 59);
		calendar.set(GregorianCalendar.AM_PM, GregorianCalendar.PM);

		return calendar.getTime();
	}

	/**
	 * @param day the given day
	 * @param firstDayOfWeek use the Calendar static instance for the days
	 * @return the date of the first day of the week of the given date
	 */
	public static Date getFirstDayOfWeek(Date day, int firstDayOfWeek) {

		GregorianCalendar calendar = newTodayCalendar(firstDayOfWeek);
		calendar.setTime(day);

		int firstDay = calendar.getFirstDayOfWeek();
		int currentDay = calendar.get(GregorianCalendar.DAY_OF_WEEK);

		int diff = currentDay - firstDay;

		long dayInMs = (60 * 60 * 24 * 1000);
		long l = dayInMs * diff;

		if (diff >= 0) {
			calendar.setTimeInMillis(day.getTime() - l);
		} else {
			calendar.setTimeInMillis(day.getTime()-((7 + diff) * dayInMs));
		}

		return getStartOfDay(calendar.getTime());
	}

	/**
	 * Returns the last day of the week the given day is in.
	 * @param day The current day.
	 * @param firstDayOfWeek The first day of the week
	 * @return The date of the last day in the week
	 */
	public static Date getLastDayOfWeek(Date day, int firstDayOfWeek) {
		GregorianCalendar calendar = newTodayCalendar(firstDayOfWeek);
		calendar.setTime(getFirstDayOfWeek(day, firstDayOfWeek));
		calendar.add(GregorianCalendar.DAY_OF_WEEK, 6);

		return getEndOfDay(calendar.getTime());
	}

	/**
	 * @param date
	 * @return Date the first day of the month
	 */
	public static Date getFirstDayOfMonth(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(GregorianCalendar.DAY_OF_MONTH, 1);

		return getStartOfDay(calendar.getTime());
	}

	/**
	 * @param date
	 * @param firstDayOfWeek
	 * @return Date the first day of the week from the first day of the month
	 */
	public static Date getFirstDayOfWeekOfMonth(Date date, int firstDayOfWeek) {

		return getFirstDayOfWeek(getFirstDayOfMonth(date), firstDayOfWeek);
	}

	public static Date getLastDayOfMonth(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(GregorianCalendar.DAY_OF_MONTH, calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));

		return getEndOfDay(calendar.getTime());
	}

	/**
	 * @param date
	 * @param firstDayOfWeek
	 * @return the last day of the week dependent on the first day of the week
	 */
	public static Date getLastWeekDayOfMonth(Date date, int firstDayOfWeek) {

		return getEndOfDay(addDays(getFirstDayOfWeek(getLastDayOfMonth(date), firstDayOfWeek), 6));
	}

	/**
	 * @param day the start date
	 * @param amount the amount of days to add to the day
	 * @return Date the new date
	 */
	public static Date addDays(Date day, int amount) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(day);
		calendar.setTimeInMillis(calendar.getTimeInMillis() + (amount * dayInMs));
		return calendar.getTime();
	}

	public static Date addHours(Date day, float amount) {
		int rawOffset = (int) ((amount * getHourInMs()) / 60000);
		int hours = rawOffset / 60;
		int minutes = Math.abs(rawOffset) % 60;

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(day);
		calendar.add(GregorianCalendar.HOUR, hours);
		calendar.add(GregorianCalendar.MINUTE, minutes);

		return calendar.getTime();
	}

	/**
	 * @param date
	 * @return the number of the day of the week
	 */
	public static int getDayNumberOfWeek(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar.get(GregorianCalendar.DAY_OF_WEEK);
	}

	/**
	 *
	 * @param dtStart
	 * @param dtEnd
	 * @return the number of days between the two dates
	 */
	public static int getDifferenceInDays(Date dtStart, Date dtEnd) {
		GregorianCalendar gcStart = new GregorianCalendar();
		gcStart.setTime(getStartOfDay(dtStart));

		GregorianCalendar gcEnd = new GregorianCalendar();
		gcEnd.setTime(getStartOfDay(dtEnd));

		long endL = gcEnd.getTimeInMillis()+ gcEnd.getTimeZone().getOffset( gcEnd.getTimeInMillis() );
		long startL = gcStart.getTimeInMillis()+ gcStart.getTimeZone().getOffset( gcStart.getTimeInMillis() );

		int i = (int) Math.ceil((endL-startL)/dayInMs);

		return Math.abs(i);
	}

	/**
	 * @param dtStart the date/time
	 * @param locale the locale
	 * @return String with the time depended on the locale
	 */
	public static String getLocalTime(Date dtStart, Locale locale, boolean allDay) {

		String sHours = "";
		String sMinutes = "";
		String additive = "";

		if (!allDay)
		{
			if (locale.equals(Locale.ENGLISH) || locale.equals(Locale.US)) {

				try {
					int hours = Integer.parseInt(new SimpleDateFormat("h", locale).format(dtStart));
					int minutes = Integer.parseInt(new SimpleDateFormat("m", locale).format(dtStart));
					additive = new SimpleDateFormat("a", locale).format(dtStart);

					if (hours < 10) {
						sHours = "0" + hours;
					} else {
						sHours = "" + hours;
					}

					if (minutes < 10) {
						sMinutes = "0" + minutes;
					} else {
						sMinutes = "" + minutes;
					}
				} catch (NumberFormatException nfe) {
					//
				}
			} else {
				try {
					int hours = Integer.parseInt(new SimpleDateFormat("H", locale).format(dtStart));
					int minutes = Integer.parseInt(new SimpleDateFormat("m", locale).format(dtStart));
					if (hours < 10) {
						sHours = "0" + hours;
					} else {
						sHours = "" + hours;
					}

					if (minutes < 10) {
						sMinutes = "0" + minutes;
					} else {
						sMinutes = "" + minutes;
					}
				} catch (NumberFormatException nfe) {
					//
				}
			}
		} else {
			return "";
		}
		return sHours + ":" + sMinutes + " " + additive;
	}

	/**
	 * Returns a date corrected with the TimeZone of the given calendar
	 * @param date The date to correct
	 * @param calendar The Calendar containing the TimeZone
	 * @return A Date corrected according to the TimeZone
	 */
	public static Date getCalendarTimeZoneCorrectedDate(Date date, org.webical.Calendar calendar) {
		GregorianCalendar eventDate = new GregorianCalendar();
		eventDate.setTime(date);

		if (calendar.getOffSetTo() != null && calendar.getOffSetFrom() != null) {
			int diff = calendar.getOffSetFrom().intValue() + calendar.getOffSetTo().intValue();
			eventDate.add(GregorianCalendar.HOUR_OF_DAY, diff);
		}
		return eventDate.getTime();
	}
}
