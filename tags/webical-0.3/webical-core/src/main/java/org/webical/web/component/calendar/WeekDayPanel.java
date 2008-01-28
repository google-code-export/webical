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
import java.util.GregorianCalendar;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.webical.web.action.AddEventAction;
import org.webical.web.action.DaySelectedAction;
import org.webical.web.action.IAction;
import org.webical.web.component.AbstractBasePanel;
import org.webical.web.component.calendar.model.EventsModel;

public abstract class WeekDayPanel extends AbstractBasePanel {
	private static final long serialVersionUID = 1L;

	// Markup ID's
	private static final String DAY_LINK_MARKUP_ID = "dayLink";
	private static final String DAY_LABEL_MARKUP_ID = "dayLabel";
	private static final String EVENT_ITEM_MARKUP_ID = "eventItem";

	/** The {@code EventsModel} to use */
	private EventsModel eventsModel;

	/** The date for the day this panel is representing */
	private GregorianCalendar dayDate;

	/** Contains the actions this panel can handle */
	@SuppressWarnings("unchecked")
	protected  static Class[] PANELACTIONS = new Class[] { };

	private Link dayLink, addEventLink;
	private EventsListView dayEventsListView;

	public WeekDayPanel(String markupId, EventsModel eventsModel) {
		super(markupId, WeekDayPanel.class);
		this.eventsModel = eventsModel;
		this.dayDate = new GregorianCalendar();
		this.dayDate.setTime(eventsModel.getStartDate());
	}

	public void setupCommonComponents() {

	}

	public void setupAccessibleComponents() {
		dayLink = new Link(DAY_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				WeekDayPanel.this.onAction(new DaySelectedAction(dayDate));
			}

		};
		dayLink.add(new Label(DAY_LABEL_MARKUP_ID, String.valueOf(dayDate.get(GregorianCalendar.DAY_OF_MONTH))));
		add(dayLink);

		addEventLink = new Link("addEventLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				WeekDayPanel.this.onAction(new AddEventAction(dayDate));
			}

		};
		// TODO mattijs: should this be determined by a static?
		addEventLink.setVisible(CalendarPanel.enableAddEvent);
		add(addEventLink);

		dayEventsListView = new EventsListView(EVENT_ITEM_MARKUP_ID, eventsModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction(IAction action) {
				// Check if the action is in the ACTIONS array for this panel
				if(Arrays.asList(MonthDayPanel.PANELACTIONS).contains(action.getClass())) {
					// Handle panel actions here
				} else {
					// Pass the action to the parent component
					WeekDayPanel.this.onAction(action);
				}
			}

		};
		dayEventsListView.showEndTime(false);
		add(dayEventsListView);
	}

	public void setupNonAccessibleComponents() {

	}

	public abstract void onAction(IAction action);

}
