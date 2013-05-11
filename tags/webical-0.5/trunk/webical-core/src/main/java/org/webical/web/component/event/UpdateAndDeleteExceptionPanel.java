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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.webical.Calendar;
import org.webical.Event;
import org.webical.manager.EventManager;
import org.webical.manager.WebicalException;
import org.webical.util.CalendarUtils;
import org.webical.web.app.WebicalWebAplicationException;
import org.webical.web.component.AbstractBasePanel;
import org.webical.web.pages.BasePage;

/**
 * use this panel to ask a user whether he wants to ignore update and delete exception
 * @author jochem
 * @author Mattijs Hoitink
 */
public abstract class UpdateAndDeleteExceptionPanel extends AbstractBasePanel {

	private static final long serialVersionUID = 1L;

	private static final String OVERWRITE_CHANGES_LINK_MARKUP_ID = "overwritechanges";
	private static final String CANCEL_CHANGES_LINK_MARKUP_ID = "overwritecancelchanges";

	private Event event;
	private boolean isUpdateException;

	@SpringBean(name="eventManager")
	private EventManager eventManager;

	/**
	 * Constructor
	 * @param markupId The ID used in the markup
	 * @param event The event
	 * @param calendar
	 * @param isUpdateException
	 */
	public UpdateAndDeleteExceptionPanel(String markupId ,Event event, Calendar calendar, boolean isUpdateException) {
		super(markupId, UpdateAndDeleteExceptionPanel.class);
		this.event = event;
		this.isUpdateException = isUpdateException;
	}

	public void setupAccessibleComponents() {
		// NOTHING TO DO
	}

	public void setupCommonComponents() {
		//Add link to edit only this event
		Link editOnlyThisEventLink = new Link(OVERWRITE_CHANGES_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			/**
			 * Change the contentpanel to the AddEditEventPanel
			 * @see wicket.markup.html.link.Link#onClick()
			 */
			@Override
			public void onClick() {
				//force a save event
				try {
					if(isUpdateException){
						//decrease last mod date so it gets written over the old one
						event.setLastMod(CalendarUtils.addDays(event.getLastMod(),-1));
					} else {
						//re-insert the event
						event.setUid(null);
					}
					//TODO check if this is still working
					//eventManager.storeEvent(event, calendar);
					eventManager.storeEvent(event);
				} catch (WebicalException e) {
					error("Could not force store the event");
					throw new WebicalWebAplicationException("Could not force to save the event: " + event.getSummary(), e);
				}
				UpdateAndDeleteExceptionPanel.this.changeContent(BasePage.CALENDAR_PANEL, null);
			}

		};
		addOrReplace(editOnlyThisEventLink);
		//Add link to edit the entire event
		Link editAllEventsLink = new Link(CANCEL_CHANGES_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			/**
			 * Change the contentpanel to the AddEditEventPanel
			 * @see wicket.markup.html.link.Link#onClick()
			 */
			@Override
			public void onClick() {
				UpdateAndDeleteExceptionPanel.this.changeContent(BasePage.CALENDAR_PANEL, null);
			}

		};
		addOrReplace(editAllEventsLink);
	}

	public void setupNonAccessibleComponents() {
		// NOTHING TO DO
	}

	/**
	 * Notify the parent the content of the main panel has to be changed
	 * @param panelId The ID of the panel to set as the new content
	 * @param target The Ajax Target of the panel
	 */
	public abstract void changeContent(int panelId, AjaxRequestTarget target);

	/**
	 * Used by Spring to set the EventManager
	 * @param eventManager an EventManager
	 */
	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}
}
