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


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.StringResourceModel;
import org.webical.util.CalendarUtils;
import org.webical.web.action.IAction;
import org.webical.web.component.calendar.model.EventsModel;

/**
 * Panel shows the events of the selected week
 *
 * @author Mattijs Hoitink
 */
public abstract class WeekViewPanel extends CalendarViewPanel {
	private static final long serialVersionUID = 1L;

	// Markup ID's
	private static final String DAY_HEADING_REPEATER_MARKUP_ID = "dayHeadingRepeater";
	private static final String WEEK_HEADING_LABEL_MARKUP_ID = "weekHeadingLabel";
	private static final String WEEK_COLUMN_REPEATER_MARKUP_ID = "weekColumnRepeater";

	private static final String WEEK_HEADING_RESOURCE_KEY = "weekHeadingStart";
	private static final String XDAYS_HEADING_START_RESOURCE_KEY = "xdaysHeadingStart";
	private static final String XDAYS_HEADING_END_RESOURCE_KEY = "xdaysHeadingEnd";

	/**
	 * The identifier for the period this panel covers
	 */
	private int viewPeriodId = GregorianCalendar.DAY_OF_WEEK;

	/**
	 * Length for the period this panel covers
	 */
	private int viewPeriodLength = 6;

	/**
	 * Format of the period this panel covers
	 */
	private String vierPeriodFormat = "MMMM dd, yyyy";

	DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, getLocale());

	private int daysToShow;
	private boolean weekView;

	// Date range
	private GregorianCalendar currentDate;
	private Date startDate;
	private Date endDate;

	/**
	 * The EventsModel used by this panel.
	 */
	private EventsModel eventsModel;

	// Panel components
	private WeekColumnRepeater weekColumnRepeater;
	private String weekHeadingText;
	private Label weekHeadingLabel;

	/**
	 * Constructor.
	 * @param markupId The ID to use in markup
	 * @param currentDate The current date
	 * @param daysToShow The number of days to show
	 */
	public WeekViewPanel(String markupId, Calendar currentDate, int daysToShow) {
		super(markupId, WeekViewPanel.class);

		this.currentDate = (GregorianCalendar) currentDate;
		this.daysToShow = daysToShow;
		this.viewPeriodLength = daysToShow;
		this.weekView = (daysToShow == 7);

		/*
		 * Set the correct start date. If this is a week view, set the start date
		 * to the first day of the week. Else set the start date to the current date.
		 */
		if(weekView){
			// TODO mattijs: get start of week from user settings
			startDate = CalendarUtils.getFirstDayOfWeek(this.currentDate.getTime(), Calendar.MONDAY);
		} else {
			startDate = this.currentDate.getTime();
		}

		// Calculate the end date
		Calendar endCal = new GregorianCalendar();
		endCal.setTime(startDate);
		endCal.add(viewPeriodId, viewPeriodLength - 1);
		endDate = endCal.getTime();

		eventsModel = new EventsModel(CalendarUtils.getStartOfDay(startDate), CalendarUtils.getEndOfDay(endDate));
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupCommonComponents()
	 */
	public void setupCommonComponents() {
		// Add a repeater to show the day headers
		RepeatingView dayHeadingRepeater = new RepeatingView(DAY_HEADING_REPEATER_MARKUP_ID);

		GregorianCalendar weekCal = new GregorianCalendar();
		weekCal.setTime(startDate);
		//TODO mattijs: get start of the week from user settings
		if(weekView) {
			weekCal.set(GregorianCalendar.DAY_OF_WEEK, Calendar.MONDAY);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("E", getLocale());
		// TODO mattijs: get the weekdays to show from user settings
		for(int i = 0; i < daysToShow; i++) {
			dayHeadingRepeater.add(new Label("headerDay" + i, sdf.format(weekCal.getTime())));
			weekCal.add(Calendar.DAY_OF_WEEK, 1);
		}
		add(dayHeadingRepeater);
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupAccessibleComponents()
	 */
	public void setupAccessibleComponents() {
		weekColumnRepeater = new WeekColumnRepeater(WEEK_COLUMN_REPEATER_MARKUP_ID, eventsModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction(IAction action) {
				WeekViewPanel.this.onAction(action);
			}

		};

		add(weekColumnRepeater);

	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupNonAccessibleComponents()
	 */
	public void setupNonAccessibleComponents() {
		// NOTHING TO DO (YET?)
	}

	/**
	 * Renders the labels that are subject to model changes.
	 * This method is called from onBeforeRender(), after setupCommonComponents(),
	 * setupAccessibleComponents() and setupNonAccessibleComponents() are executed
	 * and every time the page is loaded.
	 */
	private void renderModelDependentLabels() {
		// Render the label for the week heading with the (updated) weekHeadingText
		weekHeadingLabel = new Label(WEEK_HEADING_LABEL_MARKUP_ID, weekHeadingText);
		addOrReplace(weekHeadingLabel);
	}


	/**
	 * Update time range for the EventModel
	 * @see org.webical.web.component.AbstractBasePanel#onBeforeRender()
	 */
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();

		if(weekView){
			// TODO mattijs: get start of week from user settings
			startDate.setTime(CalendarUtils.getFirstDayOfWeek(this.currentDate.getTime(), Calendar.MONDAY).getTime());
			weekHeadingText= (new StringResourceModel(WEEK_HEADING_RESOURCE_KEY, this, null).getString()) + " " + currentDate.get(Calendar.WEEK_OF_YEAR);
		} else {
			startDate.setTime(this.currentDate.getTime().getTime());
			weekHeadingText = (new StringResourceModel(XDAYS_HEADING_START_RESOURCE_KEY, this, null).getString()) + " " + this.daysToShow + " " + (new StringResourceModel(XDAYS_HEADING_END_RESOURCE_KEY, this, null).getString());
		}

		// Calculate the end date
		Calendar endCal = new GregorianCalendar();
		endCal.setTime(startDate);
		endCal.add(viewPeriodId, viewPeriodLength);
		endDate.setTime(endCal.getTime().getTime());

		weekColumnRepeater.modelChanged();
		renderModelDependentLabels();
	}

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
		return this.vierPeriodFormat;
	}

	/**
	 * Handles the actions generated by this panel.
	 * @param action The action to handle
	 */
	public abstract void onAction(IAction action);

}
