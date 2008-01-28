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
import org.apache.wicket.markup.html.link.Link;
import org.webical.util.CalendarUtils;
import org.webical.web.action.AddEventAction;
import org.webical.web.action.IAction;
import org.webical.web.component.calendar.model.EventsModel;

/**
 * Panel displays the events of the user selected calendars for a particular day
 *
 * @author Mattijs Hoitink
 */

public abstract class DayViewPanel extends CalendarViewPanel {
	private static final long serialVersionUID = 1L;

	// Markup ID's
	private static final String ADD_EVENT_LINK_MARKUP_ID 	= "dayViewAddEventLink";
	private static final String DAY_HEADING_LABEL_MARKUP_ID = "dayHeadingLabel";
	private static final String EVENT_ITEM_MARKUP_ID = "eventItem";

	/** Contains the actions this panel can handle */
	@SuppressWarnings("unchecked")
	protected  static Class[] PANELACTIONS = new Class[] { };

	/**
	 * The identifier for the period this panel covers
	 */
	private final int viewPeriodId = Calendar.DAY_OF_MONTH;

	/**
	 * Length for the period this panel covers
	 */
	private final int viewPeriodLength = 1;

	/**
	 * Format of the period this panel covers
	 */
	private final String viewPeriodFormat = "MMMM dd, yyyy";

	/** The current date */
	private GregorianCalendar currentDate;

	private Label dayHeadingLabel;
	private Link addLink;
	private EventsListView eventsListView;

	/** The EventsModel for this panel */
	private EventsModel eventsModel;

	// The date range
	private Date startDate;
	private Date endDate;
	private SimpleDateFormat dateFormat;

	/**
	 * Constructor
	 * @param markupId The ID used in markup
	 * @param date The current date
	 */
	public DayViewPanel(String markupId, Calendar currentDate) {
		super(markupId, DayViewPanel.class);

		this.currentDate = (GregorianCalendar) currentDate;

		// Set the start time for the range
		startDate = CalendarUtils.getStartOfDay(this.currentDate.getTime());
		endDate = CalendarUtils.getEndOfDay(this.currentDate.getTime());

		eventsModel = new EventsModel(startDate, endDate);
		dateFormat = new SimpleDateFormat("EEEE", getLocale());

	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupCommonComponents()
	 */
	public void setupCommonComponents() {
		// NOTHING TO DO
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupAccessibleComponents()
	 */
	public void setupAccessibleComponents() {

		eventsListView = new EventsListView(EVENT_ITEM_MARKUP_ID, eventsModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction(IAction action) {
				// Check if the action is in the ACTIONS array for this panel
				if(Arrays.asList(DayViewPanel.PANELACTIONS).contains(action.getClass())) {
					// Handle panel actions here
				} else {
					// Pass the action to the parent component
					DayViewPanel.this.onAction(action);
				}
			}

		};
		addOrReplace(eventsListView);

		//Add link to add an event
		addLink = new Link(ADD_EVENT_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			/**
			 * Change the content to the AddEditEventPanel
			 * @see wicket.markup.html.link.Link#onClick()
			 */
			@Override
			public void onClick() {
				// generate AddEventAction
				DayViewPanel.this.onAction(new AddEventAction(currentDate));
			}

		};
		add(addLink);
		renderModelDependentLabels();

	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupNonAccessibleComponents()
	 */
	public void setupNonAccessibleComponents() {
		// NOTHING TO DO
	}

	/**
	 * Renders the labels that are subject to model changes.
	 * This method is called from onBeforeRender(), after setupCommonComponents(),
	 * setupAccessibleComponents() and setupNonAccessibleComponents() are executed
	 * and every time the page is loaded.
	 */
	private void renderModelDependentLabels() {
		// Render the label for the day heading with the (updated) current date
		dayHeadingLabel = new Label(DAY_HEADING_LABEL_MARKUP_ID, dateFormat.format(currentDate.getTime()));
		addOrReplace(dayHeadingLabel);
	}

	/**
	 * Updates the EventsModel range and re-renders components subject to model changes
	 * @see org.webical.web.component.AbstractBasePanel#onBeforeRender()
	 */
	/*@Override
	public void onBeforeRender() {
		super.onBeforeRender();

		// Update the range for the event model
		//startDate.setTime(currentDate.getTime().getTime());
		// Calculate the end of the range for the event model
		//GregorianCalendar rangeEndCal = new GregorianCalendar();
		//rangeEndCal.setTime(startDate);
		//rangeEndCal.add(Calendar.DAY_OF_WEEK, 1);
		//endDate.setTime(rangeEndCal.getTime().getTime());

		// (Re-)render the list with events
		//eventsListView.modelChanged();

		// Render model dependent labels
		renderModelDependentLabels();
	}*/

	/**
	 * Handles actions generated by this panel.
	 * @param action The action to handle
	 */
	public abstract void onAction(IAction action);

	/* (non-Javadoc)
	 * @see org.webical.web.component.calendar.CalendarViewPanel#getViewPeriodFormat()
	 */
	@Override
	public String getViewPeriodFormat() {
		return this.viewPeriodFormat;
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.calendar.CalendarViewPanel#getViewPeriodId()
	 */
	@Override
	public int getViewPeriodId() {
		return this.viewPeriodId;
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.calendar.CalendarViewPanel#getViewPeriodLength()
	 */
	@Override
	public int getViewPeriodLength() {
		return this.viewPeriodLength;
	}

}
