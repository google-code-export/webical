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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.webical.util.CalendarUtils;
import org.webical.web.action.IAction;
import org.webical.web.component.calendar.model.EventsModel;

/**
 * The Month View for the Calendar.
 *
 * @author Mattijs Hoitink
 */
public abstract class MonthViewPanel extends CalendarViewPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * The identifier for the period of this panel
	 */
	private final int viewPeriodId = GregorianCalendar.MONTH;

	/**
	 * The length for the period of this panel
	 */
	private final int viewPeriodLength = 1;

	/**
	 * The format of the period for this panel
	 */
	private final String viewPeriodFormat = "MMMM, yyyy";

	/**
	 * The current date
	 */
	private GregorianCalendar currentDate;

	/**
	 * The EventsModel used by this panel
	 */
	private EventsModel eventsModel;

	/**
	 * Contains the actions this panel can handle
	 */
	@SuppressWarnings("unchecked")
	protected  static Class[] PANELACTIONS = new Class[] { };

	// Markup ID's
	private static final String MONTH_ROW_REPEATER_MARKUP_ID = "monthRowRepeater";
	private static final String MONTH_HEADER_REPEATER_MARKUP_ID = "monthHeaderRepeater";

	// The date range
	private Date startDate;
	private Date endDate;

	private MonthRowRepeater monthRowRepeater;

	/**
	 * Constructor
	 * @param markupId The ID used for markup
	 * @param currentDate The Date to show
	 */
	public MonthViewPanel(String markupId, Calendar currentDate) {
		super(markupId, MonthViewPanel.class);
		this.setOutputMarkupId(true);
		this.currentDate = (GregorianCalendar) currentDate;

		//TODO mattijs: get start of the week from user settings
		startDate = CalendarUtils.getFirstDayOfWeekOfMonth(currentDate.getTime(), Calendar.MONDAY);
		endDate = CalendarUtils.getLastWeekDayOfMonth(currentDate.getTime(), Calendar.MONDAY);
		eventsModel = new EventsModel(CalendarUtils.getStartOfDay(startDate), CalendarUtils.getEndOfDay(endDate));

	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupCommonComponents()
	 */
	public void setupCommonComponents() {
		// Add a repeater to show the week day headers
		RepeatingView monthHeaderRepeater = new RepeatingView(MONTH_HEADER_REPEATER_MARKUP_ID);
		monthHeaderRepeater.add(new Label("headerWeek", ""));

		GregorianCalendar weekCal = new GregorianCalendar();
		//TODO mattijs: get start of the week from user settings
		weekCal.set(GregorianCalendar.DAY_OF_WEEK, Calendar.MONDAY);
		SimpleDateFormat sdf = new SimpleDateFormat("E", getLocale());
		// TODO mattijs: get the weekdays to show from user settings
		for(int i = 0; i < 7; i++) {
			monthHeaderRepeater.add(new Label("headerDay" + i, sdf.format(weekCal.getTime())));
			weekCal.add(Calendar.DAY_OF_WEEK, 1);
		}
		add(monthHeaderRepeater);
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupAccessibleComponents()
	 */
	public void setupAccessibleComponents() {
		monthRowRepeater = new MonthRowRepeater(MONTH_ROW_REPEATER_MARKUP_ID, eventsModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction(IAction action) {
				if(Arrays.asList(MonthViewPanel.PANELACTIONS).contains(action.getClass())) {
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

		startDate.setTime(CalendarUtils.getFirstDayOfWeekOfMonth(currentDate.getTime(), Calendar.MONDAY).getTime());
		endDate.setTime(CalendarUtils.getLastWeekDayOfMonth(currentDate.getTime(), Calendar.MONDAY).getTime());

		monthRowRepeater.modelChanged();
	}

	/**
	 * Handle actions generated by this panel
	 * @param action The action to handle
	 */
	public abstract void onAction(IAction action);

	/* (non-Javadoc)
	 * @see org.webical.web.component.calendar.CalendarViewPanel#getViewPeriodId()
	 */
	@Override
	public int getViewPeriodId() {
		return viewPeriodId;
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.calendar.CalendarViewPanel#getViewPeriodLength()
	 */
	@Override
	public int getViewPeriodLength() {
		return viewPeriodLength;
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.calendar.CalendarViewPanel#getViewPeriodFormat()
	 */
	@Override
	public String getViewPeriodFormat() {
		return this.viewPeriodFormat;
	}

}
