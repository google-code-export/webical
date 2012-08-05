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

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.webical.Event;
import org.webical.util.CalendarUtils;
import org.webical.web.action.EventSelectedAction;
import org.webical.web.action.IAction;
import org.webical.web.app.WebicalSession;
import org.webical.web.component.calendar.model.EventsModel;

/**
 * List of events on a day in the View
 *
 * @author Mattijs Hoitink
 *
 */
public abstract class EventsListView extends ListView {
	private static final long serialVersionUID = 1L;

	// Markup ID's
	private static final String EVENT_TIME_LABEL_MARKUP_ID = "eventTime";
	private static final String EVENT_LINKL_MARKUP_ID = "eventLink";
	private static final String EVENT_TITLE_LABEL_MARKUP_ID = "eventTitle";

	/** Contains the actions this panel can handle */
	protected  static Class[] PANELACTIONS = new Class[] {  };

	private GregorianCalendar listDate;

	// Panel components
	private Label timeLabel;
	private Link eventLink;

	/** Determines if the events end time should be displayed. Default is false. */
	private boolean showEndTime = false;
	/** Determines if the events long event description should be displayed. Default is false. */
	private boolean longEventDescription = false;

	/**
	 * Constructor.
	 * @param id The ID to use in markup
	 * @param model The {@code EventsModel} to use
	 * @param listDate the date this list is representing
	 */
	public EventsListView(String id, EventsModel model, GregorianCalendar listDate) {
		super(id, model);
		this.listDate = listDate;
	}

	/**
	 * Add the events to the list
	 * @see org.apache.wicket.markup.html.list.ListView#populateItem(org.apache.wicket.markup.html.list.ListItem)
	 */
	@Override
	protected void populateItem(ListItem item) {
		final Event currentEvent = (Event) item.getModelObject();
		final GregorianCalendar currentDate = CalendarUtils.newTodayCalendar(listDate.getFirstDayOfWeek());
		currentDate.setTime(currentEvent.getDtStart());

		SimpleDateFormat dateFormat = new SimpleDateFormat(WebicalSession.getWebicalSession().getUserSettings().getTimeFormat(), getLocale());

		StringBuilder timeLabelText = new StringBuilder();
		if (!currentEvent.isAllDay()) {
			//timeLabelText += dateFormat.format(CalendarUtils.getCalendarTimeZoneCorrectedDate(currentEvent.getDtStart(), currentEvent.getCalendar()));
			timeLabelText.append(dateFormat.format(currentEvent.getDtStart()));
		}
		if (showEndTime && !currentEvent.isAllDay()) {
			//timeLabelText += " - " + dateFormat.format(CalendarUtils.getCalendarTimeZoneCorrectedDate(currentEvent.getDtEnd(), currentEvent.getCalendar()));
			timeLabelText.append(" - ").append(dateFormat.format(currentEvent.getDtEnd()));
		}

		timeLabel = new Label(EVENT_TIME_LABEL_MARKUP_ID, timeLabelText.toString());
		eventLink = new Link(EVENT_LINKL_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				EventsListView.this.onAction(new EventSelectedAction(currentEvent, listDate));
			}
		};
		StringBuilder eventLabelText = new StringBuilder(currentEvent.getSummary());
		if (longEventDescription)
		{
			if (! (currentEvent.getLocation() == null || currentEvent.getLocation().isEmpty()))
			{
				eventLabelText.append(", ");
				eventLabelText.append(currentEvent.getLocation());
			}
			if (! (currentEvent.getDescription() == null || currentEvent.getDescription().isEmpty()))
			{
				eventLabelText.append(", ");
				eventLabelText.append(currentEvent.getDescription());
			}
		}
		eventLink.add(new Label(EVENT_TITLE_LABEL_MARKUP_ID, eventLabelText.toString()));
		eventLink.addOrReplace(timeLabel);

		if (currentEvent.isAllDay()) {
			item.add(new AttributeAppender("class", true, new Model("allDay"), " "));
		}

		item.addOrReplace(eventLink);
	}

	/**
	 * Re-render the list after the EventsModel has changed
	 * @see org.apache.wicket.Component#onModelChanged()
	 */
	@Override
	protected void onModelChanged() {
		// Re-render the list
		getModel().detach();
	}

	/**
	 * Enables or disables the display of the event end time.
	 * @param showEndTime
	 */
	public void showEndTime(boolean showEndTime) {
		this.showEndTime = showEndTime;
	}

	/**
	 * Enables or disables the display of the long event description.
	 * @param longEventDescription - t/f
	 */
	public void showLongEventDescription(boolean longEventDescription) {
		showEndTime(true);
		this.longEventDescription = true;
	}

	/**
	 * Handle actions generated by this panel
	 * @param action The action to handle
	 */
	public abstract void onAction(IAction action);
}
