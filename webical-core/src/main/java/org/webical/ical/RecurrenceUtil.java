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

package org.webical.ical;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.ExRule;
import net.fortuna.ical4j.model.property.RRule;

import org.webical.Event;
import org.webical.manager.WebicalException;

/**
 * Collection of utility methods used on Recurrence
 * @author ivo
 *
 */
public class RecurrenceUtil {

	/**
	 * Sets the recurrence information on the event's RRule
	 * @param event the event to update
	 * @param recurrence the recurrence information to set on the event
	 */
	public static void setRecurrenceRule(Event event, Recurrence recurrence) {
		if(event == null || recurrence == null) {
			return;
		}

		if(recurrence.getFrequency() > 0 && recurrence.getInterval() > 0) {
			//Remove the recurrence to update
			if(event.getrRule().size() > 0) {
				List<String> rrules = new ArrayList<String>(event.getrRule());
				rrules.remove(0);
				event.setrRule(new HashSet<String>(rrules));
			}

			//Add the new recurrence
			Recur recur = null;
			if(recurrence.getCount() != -1 && recurrence.getCount() > 0) {
				recur = new Recur(getFrequentcyString(recurrence.getFrequency()), recurrence.getCount());
			} else if(recurrence.getEndDay() != null){
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
		if(recurrence.getFrequency() > 0 && recurrence.getInterval() > 0) {
			//Remove the recurrence to update
			if(event.getrRule().size() > 0) {
				List<String> exrules = new ArrayList<String>(event.getExRule());
				exrules.remove(0);
				event.setrRule(new HashSet<String>(exrules));
			}

			//Add the new recurrence
			Recur recur = null;
			if(recurrence.getCount() > 0) {
				recur = new Recur(getFrequentcyString(recurrence.getFrequency()), recurrence.getCount());
			} else{
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
		event.getrDate().clear();
		event.getrRule().clear();
		event.getExDate().clear();
		event.getExRule().clear();
	}

	/**
	 * Returns Recurrence formed from the Set of recurrency strings
	 * @param recurrenceRules the rules to parse
	 * @return a Recurrence
	 * @throws WebicalException with wrapped exception
	 */
	public static Recurrence getRecurrenceFromRecurrenceRuleSet(Set<String> recurrenceRules) throws WebicalException {
		if(recurrenceRules == null || recurrenceRules.size() == 0) {
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
		if(Recur.DAILY.equals(frequency)) {
			return Recurrence.DAILY;
		} else if(Recur.WEEKLY.equals(frequency)) {
			return Recurrence.WEEKLY;
		} else if(Recur.MONTHLY.equals(frequency)) {
			return Recurrence.MONTHLY;
		} else if(Recur.YEARLY.equals(frequency)) {
			return Recurrence.YEARLY;
		}
		return -1;
	}


}
