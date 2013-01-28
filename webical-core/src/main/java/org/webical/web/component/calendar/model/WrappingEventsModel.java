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

package org.webical.web.component.calendar.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Date;

import org.webical.Event;
import org.webical.comparator.EventStartTimeComparator;
import org.webical.ical.RecurrenceUtil;
import org.webical.util.CalendarUtils;

/**
 * Model to put on top of an EventsModel or another WrappingEventsModel.
 * This model uses the model which it is build on top as it's data source.
 *
 * @author Mattijs Hoitink
 *
 */
public class WrappingEventsModel extends EventsModel {
	private static final long serialVersionUID = 1L;

	/** The EventsModel or WrappingEventsModel to get the list with Events from */
	private EventsModel eventsModel = null;

	/**
	 * Constructor
	 * @param startDate Start Date for the date range.
	 * @param endDate End Date for the date range.
	 * @param eventsModel EventsModel or WrappingEventsModel from where the data is collected.
	 */
	public WrappingEventsModel(Date startDate, Date endDate, EventsModel eventsModel) {
		super(startDate, endDate);
		this.eventsModel = eventsModel;
	}

	/**
	 * Override the load() method from EventsModel so the WrappingEventsModel uses the underlying Model instead of the DAO.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object load() {
		List<Event> allEvents = (List<Event>) getEventsModel().getObject();
		List<Event> eventsInRange = new ArrayList<Event>();

		if (allEvents != null && allEvents.size() > 0)
		{
			boolean oneDay = (CalendarUtils.getDifferenceInDays(getStartDate(), getEndDate()) < 2);

			GregorianCalendar periodStart = CalendarUtils.newTodayCalendar(getFirstDayOfWeek());
			periodStart.setTime(CalendarUtils.getStartOfDay(getStartDate()));
			GregorianCalendar periodEnd = CalendarUtils.duplicateCalendar(periodStart);
			periodEnd.setTime(CalendarUtils.getEndOfDay(getEndDate()));
			for (Event currentEvent : allEvents)
			{
				// Get the events for this range, including recurrent events
				try {
					if (RecurrenceUtil.isApplicableForDateRange(currentEvent, getStartDate(), getEndDate()))
					{
						long millis = CalendarUtils.hourInMs;
						if (oneDay && ! currentEvent.isAllDay())
						{
							if (! RecurrenceUtil.isRecurrent(currentEvent))
							{
								GregorianCalendar evtStart = CalendarUtils.duplicateCalendar(periodStart);
								GregorianCalendar evtEnd = CalendarUtils.duplicateCalendar(periodEnd);
								if (evtStart.getTime().compareTo(currentEvent.getDtStart()) < 0) evtStart.setTime(currentEvent.getDtStart());
								if (evtEnd.getTime().compareTo(currentEvent.getDtEnd()) > 0) evtEnd.setTime(currentEvent.getDtEnd());
								millis = evtEnd.getTimeInMillis() - evtStart.getTimeInMillis() + CalendarUtils.secondInMs;
							}
						}
						if (millis > CalendarUtils.minuteInMs) eventsInRange.add(currentEvent);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		if (eventsInRange != null) {
			Collections.sort(eventsInRange, new EventStartTimeComparator(EventStartTimeComparator.CompareMode.TIME));
		}
		return eventsInRange;
	}

	/**
	 * Returns the EventsModel used by this WrappingEventsModel
	 * @return
	 */
	public EventsModel getEventsModel() {
		return eventsModel;
	}
}
