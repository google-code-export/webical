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

package org.webical.web.component.calendar;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.wicket.markup.repeater.RepeatingView;
import org.webical.util.CalendarUtils;
import org.webical.web.action.IAction;
import org.webical.web.component.calendar.model.EventsModel;
import org.webical.web.component.calendar.model.WrappingEventsModel;

/**
 * Repeater for repeating the week rows in the Month View
 *
 * @author Mattijs Hoitink
 *
 */
public abstract class MonthRowRepeater extends RepeatingView {
	private static final long serialVersionUID = 1L;

	/** The defautl number of weeks */
	private double numberOfWeeks = 4.0;

	/** Contains the actions handled by this panel */
	@SuppressWarnings("unchecked")
	protected  static Class[] PANELACTIONS = new Class[]{};

	/** EventsModel containing the events for this range */
	private EventsModel eventsModel;
	
	/** The month this range is representing */
	private int rangeMonth;

	/**
	 * Constructor
	 * @param id The ID to use in markup
	 * @param model The EventsModel to use
	 * @param rangeMonth the month this range is representing
	 */
	public MonthRowRepeater(String id, EventsModel model, int rangeMonth) {
		super(id);

		eventsModel = model;
		this.rangeMonth = rangeMonth;
		
		addWeeks();
	}

	private void addWeeks() {
		Date startDate = eventsModel.getStartDate();
		Date endDate = eventsModel.getEndDate();

		GregorianCalendar startCal = new GregorianCalendar();
		GregorianCalendar endCal = new GregorianCalendar();
		startCal.setTime(startDate);

		double diff = CalendarUtils.getDifferenceInDays(startDate, endDate);
		this.numberOfWeeks = Math.ceil(diff / 7.0);

		for(int i = 0; i < numberOfWeeks; i++) {
			Date weekStartDate = startCal.getTime();
			endCal.setTime(startCal.getTime());
			endCal.add(GregorianCalendar.DAY_OF_WEEK, 7);
			Date weekEndDate = endCal.getTime();

			// Add a MonthRowPanel for each week, with a part of the EventsModel
			MonthRowPanel monthRowPanel = new MonthRowPanel("week" + startCal.get(GregorianCalendar.WEEK_OF_YEAR), new WrappingEventsModel(weekStartDate, weekEndDate, eventsModel), rangeMonth) {
				private static final long serialVersionUID = 1L;

				/* (non-Javadoc)
				 * @see org.webical.web.component.calendar.MonthRowPanel#onAction(org.webical.web.action.IAction)
				 */
				@Override
				public void onAction(IAction action) {
					if(Arrays.asList(MonthRowRepeater.PANELACTIONS).contains(action.getClass())) {
						// Handle panel actions here
					} else {
						// Pass the action to the parent component
						MonthRowRepeater.this.onAction(action);
					}
				}

			};
			addOrReplace(monthRowPanel);

			// Add one week to the calendar
			startCal.add(GregorianCalendar.WEEK_OF_YEAR, 1);
		}
	}

	/**
	 * Renders the week rows for the new EventsModel
	 * @see org.apache.wicket.Component#onModelChanged()
	 */
	@Override
	protected void onModelChanged() {
		// Remove the old week rows
		this.removeAll();
		// Render the new week rows
		addWeeks();
	}

	/**
	 * Handle actions generated by this panel
	 * @param action The action to handle
	 */
	public abstract void onAction(IAction action);

}
