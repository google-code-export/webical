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

package org.webical.ical;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.ExRule;
import net.fortuna.ical4j.model.property.RRule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webical.Event;
import org.webical.manager.WebicalException;

/**
 * Collection of utility methods used on Recurrence
 * @author Ivo van Dongen
 * @author Mattijs Hoitink
 *
 */
public class RecurrenceUtil {
	private static Log log = LogFactory.getLog(RecurrenceUtil.class);

	/**
	 * Sets the recurrence information on the event's RRule
	 * @param event the event to update
	 * @param recurrence the recurrence information to set on the event
	 */
	public static void setRecurrenceRule(Event event, Recurrence recurrence) {
		if (event == null || recurrence == null) {
			return;
		}

		if (recurrence.getFrequency() > 0 && recurrence.getInterval() > 0) {
			//Remove the recurrence to update
			if (event.getrRule().size() > 0) {
				List<String> rrules = new ArrayList<String>(event.getrRule());
				rrules.remove(0);
				event.setrRule(new HashSet<String>(rrules));
			}

			//Add the new recurrence
			Recur recur = null;
			if (recurrence.getCount() != null && recurrence.getCount() > 0) {
				recur = new Recur(getFrequentcyString(recurrence.getFrequency()), recurrence.getCount());
			} else if (recurrence.getEndDay() != null) {
				recur = new Recur(getFrequentcyString(recurrence.getFrequency()), new Date(recurrence.getEndDay()));
			} else {
				recur = new Recur(getFrequentcyString(recurrence.getFrequency()), null);
			}
			recur.setInterval(recurrence.getInterval());
			RRule rRule = new RRule(recur);
			event.getrRule().add(rRule.getValue());
		}
	}

	/**
	 * Sets the recurrence information on the event's EXRule
	 * @param event the event to update
	 * @param recurrence the recurrence information to set on the event
	 */
	public static void setExclusionRule(Event event, Recurrence recurrence) {
		if (recurrence.getFrequency() > 0 && recurrence.getInterval() > 0) {
			//Remove the recurrence to update
			if (event.getrRule().size() > 0) {
				List<String> exrules = new ArrayList<String>(event.getExRule());
				exrules.remove(0);
				event.setrRule(new HashSet<String>(exrules));
			}

			//Add the new recurrence
			Recur recur = null;
			if (recurrence.getCount() > 0) {
				recur = new Recur(getFrequentcyString(recurrence.getFrequency()), recurrence.getCount());
			} else {
				recur = new Recur(getFrequentcyString(recurrence.getFrequency()), new Date(recurrence.getEndDay()));
			}
			recur.setInterval(recurrence.getInterval());
			ExRule exRule = new ExRule(recur);
			event.getExRule().add(exRule.getValue());
		}
	}

	/**
	 * Clears the event of Recurrence information
	 * @param event the event to clean
	 */
	public static void clearRecurrence(Event event) {
		//Clear all recurrence attributes
		/*event.getrDate().clear();
		event.getrRule().clear();
		event.getExDate().clear();
		event.getExRule().clear();*/

		// Reset all recurrence attributes
		event.setrDate(null);
		event.setrRule(null);
		event.setExDate(null);
		event.setExRule(null);
	}

	/**
	 * Returns Recurrence formed from the Set of recurrency strings
	 * @param recurrenceRules the rules to parse
	 * @return a Recurrence
	 * @throws WebicalException with wrapped exception
	 */
	public static Recurrence getRecurrenceFromRecurrenceRuleSet(Set<String> recurrenceRules) throws WebicalException {
		if (recurrenceRules == null || recurrenceRules.size() == 0) {
			return null;
		}

		Recurrence recurrence = new Recurrence();
		String recurrenceString = recurrenceRules.iterator().next();
		Recur recur = null;
		try {
			recur = new Recur(recurrenceString);
		} catch (ParseException e) {
			throw new WebicalException("Could not parse recurrence string: " + recurrenceString);
		}
		recurrence.setCount(recur.getCount());
		recurrence.setEndDay(recur.getUntil());
		recurrence.setInterval(recur.getInterval());
		recurrence.setFrequency(getFrequency(recur.getFrequency()));

		return recurrence;
	}

	/**
	 * turns a frequency into a string representation
	 * @param frequency the frequency
	 * @return
	 */
	private static String getFrequentcyString(int frequency) {
		switch (frequency) {
			case Recurrence.DAILY : return Recur.DAILY;
			case Recurrence.WEEKLY : return Recur.WEEKLY;
			case Recurrence.MONTHLY : return Recur.MONTHLY;
			case Recurrence.YEARLY : return Recur.YEARLY;
			default: return "";
		}
	}

	/**
	 * turns a frequency string into the int representation
	 * @param frequency
	 * @return
	 */
	private static int getFrequency(String frequency) {
		if (Recur.DAILY.equals(frequency)) {
			return Recurrence.DAILY;
		} else if (Recur.WEEKLY.equals(frequency)) {
			return Recurrence.WEEKLY;
		} else if (Recur.MONTHLY.equals(frequency)) {
			return Recurrence.MONTHLY;
		} else if (Recur.YEARLY.equals(frequency)) {
			return Recurrence.YEARLY;
		}
		return -1;
	}

	/**
	 * Converts the frequency to a field identifier used by {@see java.util.Calendar}.
	 * @param frequency The frequency identifier
	 * @return the @see java.util.Calendar} field identifier
	 */
	private static int getCalendarIdentifierForFrequency(int frequency) {
		switch (frequency) {
		case Recurrence.DAILY : return GregorianCalendar.DAY_OF_YEAR;
		case Recurrence.WEEKLY : return GregorianCalendar.WEEK_OF_YEAR;
		case Recurrence.MONTHLY : return GregorianCalendar.MONTH;
		case Recurrence.YEARLY : return GregorianCalendar.YEAR;
		default: return -1;
		}
	}

	/**
	 * Determines if the event is recurrent
	 * @param event the {@link Event} to check
	 * @return true if the {@link Event} is recurrent
	 */
	public static boolean isRecurrent(Event event) {
		if (!event.getrRule().isEmpty() || !event.getrDate().isEmpty()){
			return true;
		}
		return false;
	}

	/**
	 * Checks if the {@link Event} is applicable for the given range taking into account recurrence information
	 * @param event the {@link Event}
	 * @param startDate the start of the range
	 * @param endDate the end of the range
	 * @return true if the {@link Event} is applicable for this range
	 * @throws ParseException
	 */
	public static boolean isApplicableForDateRange(Event event, java.util.Date startDate, java.util.Date endDate) throws ParseException
	{
		if (event.getDtStart() != null) {
			if (isRecurrent(event)) {
				//Property: RRULE
				Recur recur = null;
				try {
					if (event.getrRule() != null) {
						recur = new Recur(event.getrRule().iterator().next());
					}
				} catch (ParseException e) {
					log.error("Could not parse recurrence information", e );
					throw e;
				}

				ExRule exRule = new ExRule();
				exRule.setRecur(recur);

				DateTime fromDate = new DateTime(event.getDtStart());
				DateTime dtStart = new DateTime(startDate.getTime()-1000);
				DateTime dtEnd = new DateTime(endDate.getTime()-1000);

				DateList datesFromRecur = recur.getDates(fromDate, dtStart, dtEnd, Value.DATE_TIME);
				Set<java.util.Date> dates = event.getrDate();

				//Property RDate
				for (java.util.Date date : dates) {
					datesFromRecur.add(date);
				}

				if (datesFromRecur.size() > 0) {

					//Property: EXDATE
					for (Iterator i = event.getExDate().iterator(); i.hasNext(); ) {
						if (startDate.equals(new Date(((java.util.Date)i.next()).getTime()))) {
							return false;
						}
					}

					//PROPERTY: EXRULE
					if (event.getExRule().iterator().hasNext()) {
						Recur exRecur;
						try {
							exRecur = new Recur(event.getExRule().iterator().next());
						} catch (ParseException e) {
							log.error("Could not parse recurrence information", e );
							throw e;
						}

						DateList datesFromExRule = exRecur.getDates(fromDate, dtStart, dtEnd,Value.DATE_TIME);
						if (datesFromExRule.size() > 0) {
							return false;
						}
					}
					return true;
				}
			} else if ((event.getDtStart().before(startDate) && event.getDtEnd().before(startDate)) || (event.getDtStart().after(endDate) && event.getDtEnd().after(endDate)))
			{
				//Event is not in daterange
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the {@link Event} is applicable on the given day taking into account recurrence information
	 * @param event the {@link Event}
	 * @param date the {@link Date}
	 * @return true if the {@link Event} is applicable for this range
	 * @throws ParseException
	 */
	public static boolean isApplicableOnDate(Event event, java.util.Date date) throws ParseException {
		return isApplicableForDateRange(event, date, date);
	}

	/**
	 * Excludes the first Event from the second Event's recurrence rule
	 * @param eventToExclude The Event to exclude
	 * @param eventWithRange The event which recurrence rule should be updated
	 * @param clearRecurrenceFromExcludedEvent True if the recurrence from the exlcuded event should be cleared
	 */
	public static void excludeEventFromRecurrenceRule(Event eventToExclude, Event eventWithRange, boolean clearRecurrenceFromExcludedEvent)
	{
		// Exclude the new event from its old range
		eventWithRange.getExDate().add(eventToExclude.getDtStart());
		if (clearRecurrenceFromExcludedEvent) {
			clearRecurrence(eventToExclude);
		}
	}

	/**
	 * Excludes the given Date from the Events recurrence rule.
	 * @param dateToExclude The date to exclude
	 * @param eventWithRange The Event with recurrence to exclude the Date from
	 */
	public static void excludeDateFromRecurrenceRule(java.util.Date dateToExclude, Event eventWithRange) {
		eventWithRange.getExDate().add(dateToExclude);
	}

	public static java.util.Date getNextOccurrenceDate(Event event, java.util.Date occurrenceDate) throws WebicalException, ParseException {
		if (event == null || !isRecurrent(event)) {
			throw new WebicalException("This event is not recurrent: " + event);
		}

		Recurrence recurrence = getRecurrenceFromRecurrenceRuleSet(event.getrRule());
		if (recurrence != null) {
			// Check for use of count to determine the end date
			if (recurrence.getCount() > 0 && recurrence.getInterval() > 0) {
				GregorianCalendar endDateCalendar = new GregorianCalendar();

				endDateCalendar.setTime(event.getDtStart());
				int amountToAdd = (recurrence.getCount() * recurrence.getInterval());
				endDateCalendar.add(getCalendarIdentifierForFrequency(recurrence.getFrequency()), amountToAdd);

				if (isApplicableOnDate(event, endDateCalendar.getTime())) {
					return endDateCalendar.getTime();
				}
				return null;
			}
		}
		return null;
	}

	public static java.util.Date getPreviousOccurrenceDate(Event event, java.util.Date occurrenceDate) throws WebicalException
	{
		if (event == null || !isRecurrent(event)) {
			throw new WebicalException("This event is not recurrent: " + event);
		} else if (occurrenceDate == null) {
			throw new WebicalException("The occurence date can not be null");
		}
		return null;
	}

	public static java.util.Date getLastOccurrenceDate(Event event) throws WebicalException
	{
		if (event == null || !isRecurrent(event)) {
			throw new WebicalException("This event is not recurrent: " + event);
		}

		Recurrence recurrence = getRecurrenceFromRecurrenceRuleSet(event.getrRule());
		if (recurrence != null) {
			// Check if the recurrence end date is set. Use this as last date value
			if (recurrence.getEndDay() != null) {
				return recurrence.getEndDay();
			}

			// Check for use of count to determine the end date
			if (recurrence.getCount() >= 1 && recurrence.getInterval() >= 1) {
				GregorianCalendar endDateCalendar = new GregorianCalendar();
				endDateCalendar.setTime(event.getDtStart());
				int amountToAdd = (recurrence.getCount() * recurrence.getInterval());
				endDateCalendar.add(getCalendarIdentifierForFrequency(recurrence.getFrequency()), amountToAdd);

				return endDateCalendar.getTime();
			}
		}
		return null;
	}
}
