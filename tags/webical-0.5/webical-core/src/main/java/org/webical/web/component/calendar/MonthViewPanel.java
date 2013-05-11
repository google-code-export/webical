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

package org.webical.web.component.calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.webical.util.CalendarUtils;
import org.webical.web.action.IAction;
import org.webical.web.component.calendar.model.EventsModel;

/**
 * The Month View for the Calendar.
 *
 * @author Mattijs Hoitink
 * @author Harm-Jan Zwinderman, Cebuned
 */
public abstract class MonthViewPanel extends CalendarViewPanel {
	private static final long serialVersionUID = 1L;

	// Markup ID's
	private static final String MONTH_ROW_REPEATER_MARKUP_ID = "monthRowRepeater";
	private static final String MONTH_HEADER_REPEATER_MARKUP_ID = "monthHeaderRepeater";

	/** Contains the actions this panel can handle */
	protected  static Class[] PANELACTIONS = new Class[] { };

	// The date format
	private DateFormat dateFormat;

	/**
	 * The EventsModel used by this panel
	 */
	private EventsModel eventsModel;

	// Panel components
	private MonthRowRepeater monthRowRepeater;

	/**
	 * Constructor
	 * @param markupId The ID used for markup
	 * @param months - Months to show (1)
	 * @param currentDate The Date to show
	 */
	public MonthViewPanel(String markupId, int months, GregorianCalendar currentDate) {
		super(markupId, currentDate, MonthViewPanel.class);

		this.setOutputMarkupId(true);

		setViewPeriodId(GregorianCalendar.MONTH);
		setViewPeriodLength(months);

		Date startDate = CalendarUtils.getFirstDayOfWeekOfMonth(getViewCurrentDate().getTime(), getFirstDayOfWeek());
		setPeriodStartDate(startDate);

		Date endDate = CalendarUtils.getLastWeekDayOfMonth(getViewCurrentDate().getTime(), getFirstDayOfWeek());
		setPeriodEndDate(endDate);

		setViewPeriodFormat("MMMM, yyyy");
		dateFormat = new SimpleDateFormat("E", getLocale());
		eventsModel = new EventsModel(getPeriodStartDate(), getPeriodEndDate());
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupCommonComponents()
	 */
	public void setupCommonComponents() {
		// Add a repeater to show the week day headers
		RepeatingView monthHeaderRepeater = new RepeatingView(MONTH_HEADER_REPEATER_MARKUP_ID);

		GregorianCalendar weekCal = CalendarUtils.newTodayCalendar(getFirstDayOfWeek());
		weekCal.set(GregorianCalendar.DAY_OF_WEEK, getFirstDayOfWeek());
		// TODO mattijs: get the weekdays to show from user settings
		for (int i = 0; i < 7; ++ i) {
			Label headerLabel = new Label("headerDay" + i, dateFormat.format(weekCal.getTime()));
			if (i == 6) { // Add 'last' css class
				headerLabel.add(new AttributeAppender("class", true, new Model("last"), " "));
			}
			monthHeaderRepeater.add(headerLabel);
			weekCal.add(GregorianCalendar.DAY_OF_WEEK, 1);
		}
		add(monthHeaderRepeater);
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupAccessibleComponents()
	 */
	public void setupAccessibleComponents() {
		monthRowRepeater = new MonthRowRepeater(MONTH_ROW_REPEATER_MARKUP_ID, eventsModel, getViewCurrentDate().get(GregorianCalendar.MONTH)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction(IAction action) {
				if (Arrays.asList(MonthViewPanel.PANELACTIONS).contains(action.getClass())) {
					// Handle panel actions here
				} else {
					// Pass the action to the parent component
					MonthViewPanel.this.onAction(action);
				}
			}
		};
		addOrReplace(monthRowRepeater);
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupNonAccessibleComponents()
	 */
	public void setupNonAccessibleComponents() {
		// NOTHING TO DO
	}

	/**
	 * Update time range for the EventModel
	 * @see org.webical.web.component.AbstractBasePanel#onBeforeRender()
	 */
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();

		Date startDate = CalendarUtils.getFirstDayOfWeekOfMonth(getViewCurrentDate().getTime(), getFirstDayOfWeek());
		setPeriodStartDate(startDate);
		Date endDate = CalendarUtils.getLastWeekDayOfMonth(getViewCurrentDate().getTime(), getFirstDayOfWeek());
		setPeriodEndDate(endDate);

		monthRowRepeater.modelChanged();
	}

	/**
	 * Handle actions generated by this panel
	 * @param action The action to handle
	 */
	public abstract void onAction(IAction action);
}
