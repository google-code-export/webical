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

import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.ExRule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webical.Event;

/**
 * Recurrence utilities
 * @author ivo
 *
 */
public class RecurrenceUtils {
	private static Log log = LogFactory.getLog(RecurrenceUtils.class);

	private RecurrenceUtils() {}

	/**
	 * Determines if the event is recurrent
	 * @param event the {@link Event} to check
	 * @return true if the {@link Event} is recurrent
	 */
	public static boolean isRecurrent(Event event) {
		if(!event.getrRule().isEmpty() || !event.getrDate().isEmpty()){
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if the {@link Event} is applicable for the given range taking into account recurrence information
	 * @param event the {@link Event}
	 * @param startDate the start of the range
	 * @param endDate the end of the range
	 * @return true if the {@link Event} is applicable for this range
	 * @throws ParseException
	 */
	public static boolean isApplicableForDateRange(Event event, Date startDate, Date endDate) throws ParseException {

		if(event.getDtStart() != null) {
			if(isRecurrent(event)) {

				//Property: RRULE
				Recur recur = null;
				try {
					if(event.getrRule() != null){
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
				Set<Date> dates = event.getrDate();

				//Property RDate
				for(Date date : dates){
					datesFromRecur.add(date);
				}

				if(datesFromRecur.size() > 0){

					//Property: EXDATE
					for(Iterator i = event.getExDate().iterator(); i.hasNext(); ){
						if(startDate.equals(new net.fortuna.ical4j.model.Date(((Date)i.next()).getTime()))){
							return false;
						}
					}

					//PROPERTY: EXRULE
					if(event.getExRule().iterator().hasNext()){

						Recur exRecur;
						try {
							exRecur = new Recur(event.getExRule().iterator().next());
						} catch (ParseException e) {
							log.error("Could not parse recurrence information", e );
							throw e;
						}

						DateList datesFromExRule = exRecur.getDates(fromDate, dtStart, dtEnd,Value.DATE_TIME);

						if(datesFromExRule.size() > 0){
							return false;
						}
					}

					return true;
				}

			} else if((event.getDtStart().before(startDate) && event.getDtEnd().before(startDate)) || (event.getDtStart().after(endDate) && event.getDtEnd().after(endDate))){
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
	public static boolean isApplicableOnDate(Event event, Date date) throws ParseException {
		return isApplicableForDateRange(event, date, date);
	}


}
