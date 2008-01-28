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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.webical.util.CalendarUtils;
import org.webical.web.action.IAction;
import org.webical.web.component.calendar.model.EventsModel;
import org.webical.web.component.calendar.model.WrappingEventsModel;

public abstract class WeekColumnRepeater extends RepeatingView {
	private static final long serialVersionUID = 1L;

	/**
	 * CSS Class name for current day.
	 */
	private static final String TODAY_CSS_CLASS = "todayWeekColumn";
	/**
	 * CSS Class name for the first item in the repeater.
	 */
	private static final String FIRST_ITEM_CSS_CLASS = "firstItem";
	/**
	 * CSS Class name for the last item in the repeater.
	 */
	private static final String LAST_ITEM_CSS_CLASS = "lastItem";

	private EventsModel eventsModel;

	public WeekColumnRepeater(String id, EventsModel eventsModel) {
		super(id);
		this.eventsModel = eventsModel;

		addDays();
	}

	private void addDays() {
		int numberOfDays = CalendarUtils.getDifferenceInDays(eventsModel.getStartDate(), eventsModel.getEndDate());

		Date startDate = eventsModel.getStartDate();
		Date endDate = eventsModel.getEndDate();
		GregorianCalendar todayCal = new GregorianCalendar();
		Date todayStartDate = CalendarUtils.getStartOfDay(todayCal.getTime());

		GregorianCalendar startCal = new GregorianCalendar();
		GregorianCalendar endCal = new GregorianCalendar();
		startCal.setTime(startDate);

		for(int i = 0; i <= numberOfDays; i++) {
			Date dayStartDate = startCal.getTime();
			endCal.setTime(startCal.getTime());
			endCal.add(GregorianCalendar.DAY_OF_WEEK, 1);
			Date dayEndDate = endCal.getTime();


			WeekDayPanel weekDayPanel = new WeekDayPanel("day"+ startCal.get(Calendar.DAY_OF_YEAR), new WrappingEventsModel(dayStartDate, dayEndDate, eventsModel)) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onAction(IAction action) {
					WeekColumnRepeater.this.onAction(action);
				}

			};

			// Check if we need to add some extra css styles
			if(dayStartDate.compareTo(todayStartDate) == 0) {
				weekDayPanel.add(new AttributeAppender("class", true, new Model(TODAY_CSS_CLASS), " "));
			}
			if(i == 0) {
				weekDayPanel.add(new AttributeAppender("class", true, new Model(FIRST_ITEM_CSS_CLASS), " "));
			}
			if(i == numberOfDays) {
				weekDayPanel.add(new AttributeAppender("class", true, new Model(LAST_ITEM_CSS_CLASS), " "));
			}

			add(weekDayPanel);
			startCal.add(GregorianCalendar.DAY_OF_WEEK, 1);
		}
	}


	@Override
	protected void onModelChanged() {
		// Remove all children
		this.removeAll();
		// Add the day panels again
		addDays();
	}

	public abstract void onAction(IAction action);
}
