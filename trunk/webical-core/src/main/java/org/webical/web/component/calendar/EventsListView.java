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
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.webical.Event;
import org.webical.web.action.EventSelectedAction;
import org.webical.web.action.IAction;
import org.webical.web.app.WebicalSession;
import org.webical.web.component.calendar.model.EventsModel;

/**
 * List of events on a day in the Month View
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
	@SuppressWarnings("unchecked")
	protected  static Class[] PANELACTIONS = new Class[] {  };

	private GregorianCalendar listDate;
	
	// Panel components
	private Label timeLabel;
	private Link eventLink;

	/**
	 * Detemines if the events end time should be displayed. Default is false.
	 */
	// TODO mattijs: add showEndTime to user settings
	private boolean showEndTime = false;

	/**
	 * Constructor.
	 * @param id The ID to use in markup
	 * @param model The {@code EventsModel} to use
	 * @param listDate the date this list is representing
	 */
	public EventsListView(String id, EventsModel model, Calendar listDate) {
		super(id, model);
		this.listDate = (GregorianCalendar) listDate;
	}

	/**
	 * Add the events to the list
	 * @see org.apache.wicket.markup.html.list.ListView#populateItem(org.apache.wicket.markup.html.list.ListItem)
	 */
	@Override
	protected void populateItem(ListItem item) {
		final Event currentEvent = (Event) item.getModelObject();
		final GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.setTime(currentEvent.getDtStart());

		SimpleDateFormat dateFormat = new SimpleDateFormat(WebicalSession.getWebicalSession().getUserSettings().getTimeFormat(), getLocale());
		
		String timeLabelText = "";
		if(!currentEvent.isAllDay()) {
			//timeLabelText += dateFormat.format(CalendarUtils.getCalendarTimeZoneCorrectedDate(currentEvent.getDtStart(), currentEvent.getCalendar()));
			timeLabelText += dateFormat.format(currentEvent.getDtStart());
		}
		if(showEndTime && !currentEvent.isAllDay()) {
			//timeLabelText += " - " + dateFormat.format(CalendarUtils.getCalendarTimeZoneCorrectedDate(currentEvent.getDtEnd(), currentEvent.getCalendar()));
			timeLabelText += " - " + dateFormat.format(currentEvent.getDtEnd());
		}

		timeLabel = new Label(EVENT_TIME_LABEL_MARKUP_ID, timeLabelText);
		eventLink = new Link(EVENT_LINKL_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				EventsListView.this.onAction(new EventSelectedAction(currentEvent, listDate));
			}
		};
		eventLink.add(new Label(EVENT_TITLE_LABEL_MARKUP_ID, currentEvent.getSummary()));
		eventLink.addOrReplace(timeLabel);
		
		if(currentEvent.isAllDay()) {
			item.add(new AttributeAppender("class", true, new Model("allDay"), " "));
		}

		// Add an unique ID to the element for javascript use
		String elementId = currentEvent.getUid().toString();
		// If the string is a Hibernate ID (longer than 40), only take the unique part (better readable for javascript)
		if( currentEvent.getUid().toString().length() > 40 ) {
			elementId = "event" + currentEvent.getUid().toString().substring(17, 38);
		}
		item.add(new AttributeAppender("id", true, new Model(elementId), " "));
		// Add the element ID to the header script
		CalendarPanel.roundedItemIds.add(elementId);
		
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
	 * Handle actions generated by this panel
	 * @param action The action to handle
	 */
	public abstract void onAction(IAction action);

}
