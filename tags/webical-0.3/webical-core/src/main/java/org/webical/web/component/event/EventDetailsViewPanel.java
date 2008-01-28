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

package org.webical.web.component.event;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.util.time.Time;
import org.webical.Event;
import org.webical.web.action.EditEventAction;
import org.webical.web.action.IAction;
import org.webical.web.action.ShowCalendarAction;
import org.webical.web.component.AbstractBasePanel;

/**
 * Panel to show all the properties of an event
 *
 * @author paul
 * @author Mattijs Hoitink
 */
public abstract class EventDetailsViewPanel extends AbstractBasePanel {
	private static final long serialVersionUID = 1L;

	// Markup ID's
	private static final String WHAT_MARKUP_ID = "whatLabel";
	private static final String WHEN_MARKUP_ID = "whenLabel";
	private static final String WHERE_MARKUP_ID = "whereLabel";
	private static final String DESCRIPTION_MARKUP_ID = "descriptionLabel";
	private static final String DELETE_LINK_MARKUP_ID = "deleteLink";
	private static final String EDIT_LINK_MARKUP_ID = "editLink";
	private static final String BACK_LINK_MARKUP_ID = "backLink";

	//TODO mattijs: remove and replace with Locale dependant solution
	public static final String DATE_STRING = "dd-MM-yyyy";

	// Event data
	private Event event;

	// Panel components
	private Label whatLabel, whenLabel, whereLabel, descriptionLabel;
	private Link editLink, deleteLink, backLink;

	/**
	 * Constructs the panel with the event details
	 * @param markupId the id used for the markup
	 * @param event the event to be displayed
	 */
	public EventDetailsViewPanel(String markupId, Event event) {
		super(markupId, EventDetailsViewPanel.class);
		this.event = event;
	}

	public void setupCommonComponents() {
		// What Label
		whatLabel = new Label(WHAT_MARKUP_ID,event.getSummary());

		// When Label
		String whenText = "";
		if(event.getDtStart() != null) {
			whenText += (Time.valueOf(event.getDtStart().getTime())).toString(DATE_STRING);
		}
		if(event.getDtEnd() != null) {
			whenText += " - " + (Time.valueOf(event.getDtEnd().getTime())).toString(DATE_STRING);
		}
		whenLabel = new Label(WHEN_MARKUP_ID, whenText);

		// Where Label
		whereLabel = new Label(WHERE_MARKUP_ID, event.getLocation());

		// Description Label
		descriptionLabel = new Label(DESCRIPTION_MARKUP_ID,event.getDescription());

		add(whatLabel);
		add(whenLabel);
		add(whereLabel);
		add(descriptionLabel);
	}

	public void setupAccessibleComponents() {
		//Add link to edit an event
		editLink = new Link(EDIT_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				onAction(new EditEventAction(event));
			}

		};

		//Add link to remove the event
		deleteLink = new Link(DELETE_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			/* (non-Javadoc)
			 * @see org.apache.wicket.markup.html.link.Link#onClick()
			 */
			@Override
			public void onClick() {
				// TODO mattijs: display confirmation window
				/*try {
					eventManager.removeEvent(event);
				} catch (WebicalException e) {
					throw new WebicalWebAplicationException(e);
				}*/

				//EventDetailsViewPanel.this.changeContent(BasePage.CALENDAR_VIEWS_PANEL, null);
			}

		};

		// Return to calendar link
		backLink = new Link(BACK_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				EventDetailsViewPanel.this.onAction(new ShowCalendarAction(null));
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

}
