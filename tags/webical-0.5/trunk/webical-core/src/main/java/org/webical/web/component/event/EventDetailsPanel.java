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

package org.webical.web.component.event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.webical.Event;
import org.webical.ical.RecurrenceUtil;
import org.webical.util.CalendarUtils;
import org.webical.web.action.EditEventAction;
import org.webical.web.action.IAction;
import org.webical.web.action.RemoveEventAction;
import org.webical.web.action.ShowCalendarAction;
import org.webical.web.action.StoreEventAction;
import org.webical.web.app.WebicalSession;
import org.webical.web.component.AbstractBasePanel;
import org.webical.web.component.ConfirmActionPanel;

/**
 * Panel to show all the properties of an event
 *
 * @author paul
 * @author Mattijs Hoitink
 */
public abstract class EventDetailsPanel extends AbstractBasePanel
{
	private static final long serialVersionUID = 1L;

	// Markup ID's
	private static final String WHAT_MARKUP_ID = "whatLabel";
	private static final String WHEN_MARKUP_ID = "whenLabel";
	private static final String WHERE_MARKUP_ID = "whereLabel";
	private static final String CALENDAR_MARKUP_ID = "calendarLabel";
	private static final String DESCRIPTION_MARKUP_ID = "descriptionLabel";
	private static final String DELETE_LINK_MARKUP_ID = "deleteLink";
	private static final String EDIT_LINK_MARKUP_ID = "editLink";
	private static final String BACK_LINK_MARKUP_ID = "backLink";

	// Event data
	private Event event;

	private GregorianCalendar selectedEventDate;

	private boolean overrideEventDate = false;

	// Panel components
	private Label whatLabel, whenLabel, whereLabel, calendarLabel, descriptionLabel;
	private Link editLink, deleteLink, backLink;

	/**
	 * Constructs the panel with the event details overriding the event date.
	 * This constuctor can be used to display recurring events with the correct date.
	 * @param markupId the id used for the markup
	 * @param event the event to be displayed
	 * @param selectedEventDate the date the selected event is on
	 */
	public EventDetailsPanel(String markupId, Event event, GregorianCalendar selectedEventDate)
	{
		super(markupId, EventDetailsPanel.class);
		this.event = event;
		this.selectedEventDate = selectedEventDate;

		// Override the event date with the date of the day the selected event is on if the event is recurring
		if (RecurrenceUtil.isRecurrent(event)) {
			this.overrideEventDate = true;
		}
	}

	public void setupCommonComponents()
	{
		// What Label
		whatLabel = new Label(WHAT_MARKUP_ID,event.getSummary());

		// When Label
		SimpleDateFormat dateFormatter = new SimpleDateFormat(WebicalSession.getWebicalSession().getUserSettings().getDateFormat(), getLocale());
		SimpleDateFormat timeFormatter = new SimpleDateFormat(WebicalSession.getWebicalSession().getUserSettings().getTimeFormat(), getLocale());

		// Wrap the event so we can handle the event date and time separately
		EventWrapper eventWrapper = new EventWrapper(event);

		Date startDate, endDate;
		if (overrideEventDate) {
			startDate = selectedEventDate.getTime();
			endDate = selectedEventDate.getTime();
		} else {
			startDate = eventWrapper.getEventStartDate();
			endDate = eventWrapper.getEventEndDate();
		}

		String startDisplayDate = dateFormatter.format(startDate);
		String endDisplayDate = "";
		// Only add the end date if it differs from the start date
		if (!CalendarUtils.getStartOfDay(endDate).equals(CalendarUtils.getStartOfDay(startDate))) {
			endDisplayDate += dateFormatter.format(endDate);
		}
		// Add start and end times id event is not all day
		if (!eventWrapper.isAllDay()) {
			startDisplayDate += " " + timeFormatter.format(eventWrapper.getEventStartTime());
			endDisplayDate += " - " + timeFormatter.format(eventWrapper.getEventEndTime());
		}

		whenLabel = new Label(WHEN_MARKUP_ID, startDisplayDate + endDisplayDate);

		// Where Label
		whereLabel = new Label(WHERE_MARKUP_ID, event.getLocation());

		// Where Label
		calendarLabel = new Label(CALENDAR_MARKUP_ID, event.getCalendar().getName());

		// Description Label
		descriptionLabel = new Label(DESCRIPTION_MARKUP_ID,event.getDescription());

		add(whatLabel);
		add(whenLabel);
		add(whereLabel);
		add(calendarLabel);
		add(descriptionLabel);
	}

	public void setupAccessibleComponents() {
		//Add link to edit an event
		editLink = new Link(EDIT_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				EventDetailsPanel.this.onAction(new EditEventAction(event, selectedEventDate));
			}
		};
		editLink.setEnabled(! event.getCalendar().getReadOnly());

		//Add link to remove the event
		deleteLink = new Link(DELETE_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			/* (non-Javadoc)
			 * @see org.apache.wicket.markup.html.link.Link#onClick()
			 */
			@Override
			public void onClick() {
				if (RecurrenceUtil.isRecurrent(event)) {
					// Removing singel events from a recurrence rule is disabled for milestone 0.4
					//showRecurringConfirmation();
					showSingleConfirmation();
				} else {
					showSingleConfirmation();
				}
			}
		};
		deleteLink.setEnabled(! event.getCalendar().getReadOnly());

		// Return to calendar link
		backLink = new Link(BACK_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				EventDetailsPanel.this.onAction(new ShowCalendarAction(false));
			}
		};

		add(editLink);
		add(deleteLink);
		add(backLink);
	}

	public void setupNonAccessibleComponents() {
		// NOTHING TO DO (YET?)
	}

	public abstract void onAction(IAction action);

	/**
	 * Shows confirmation screen for removing normal events
	 */
	private void showSingleConfirmation() {
		EventDetailsPanel.this.replaceWith(new ConfirmActionPanel(EventDetailsPanel.this.getId(), "Are you sure you want to delete " + event.getSummary() + "?")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onCancel() {
				this.replaceWith(EventDetailsPanel.this);
			}

			@Override
			public void onConfirm() {
				EventDetailsPanel.this.onAction(new RemoveEventAction(event));
			}
		});
	}

	/**
	 * Shows confirmation screen for removing recurring events
	 */
	@SuppressWarnings("unused")
	private void showRecurringConfirmation() {
		EventDetailsPanel.this.replaceWith(new ConfirmRecurringActionPanel(EventDetailsPanel.this.getId(), "Which instance(s) of " + event.getSummary() + " do you want to delete?") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onCancel() {
				this.replaceWith(EventDetailsPanel.this);
			}

			@Override
			public void onConfirmThis() {
				// clear the selected date from the recurrence range
				RecurrenceUtil.excludeDateFromRecurrenceRule(selectedEventDate.getTime(), event);

				// Store the event and redirect to the calendar
				EventDetailsPanel.this.onAction(new StoreEventAction(event));
			}

			@Override
			public void onConfirmAll() {
				EventDetailsPanel.this.onAction(new RemoveEventAction(event));
			}

			@Override
			public void onConfirmFollowing() {
			}
		});
	}
}
