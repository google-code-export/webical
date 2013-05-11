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

import java.util.GregorianCalendar;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.link.Link;
import org.webical.Event;
import org.webical.web.component.AbstractBasePanel;
import org.webical.web.listeners.EventSelectionListener;
import org.webical.web.pages.BasePage;

/**
 * Panel to decide whether you want to edit the entire event or just
 * this single occurence (only availible when the event has recurence)
 * @author jochem
 * @author Mattijs Hoitink
 *
 */
public abstract class EditAllOrOneEventDecisionPanel extends AbstractBasePanel {

	private static final long serialVersionUID = 1L;

	private static final String EVENT_EDIT_ONLY_THIS_INSTANCE_MARKUP_ID = "eventeditonlythis";
	private static final String EVENT_EDIT_ALL_EVENTS_MARKUP_ID = "eventeditall";
	private static final String EVENT_EDIT_DISCARD_MARKUP_ID = "eventeditdiscard";

	private EventSelectionListener eventSelectionListener;
	private ModalWindow modalWindow;

	private Event event;
	private GregorianCalendar calendar;



	/**
	 * Constructs the panel with the event details
	 * @param markupId The ID used in the page markup
	 * @param event The event to be displayed
	 * @param eventSelectionListener
	 * @param calendar
	 * @param modalWindow
	 */
	public EditAllOrOneEventDecisionPanel(String markupId, Event event, EventSelectionListener eventSelectionListener, GregorianCalendar calendar, ModalWindow modalWindow) {
		super(markupId, EditAllOrOneEventDecisionPanel.class);
		this.event = event;
		this.eventSelectionListener = eventSelectionListener;
		this.calendar = calendar;
		this.modalWindow = modalWindow;
	}

	public void setupCommonComponents() {
		// NOT IMPLEMENTED
	}


	public void setupAccessibleComponents() {
		//Add link to edit only this event
		Link editOnlyThisEventLink = new Link(EVENT_EDIT_ONLY_THIS_INSTANCE_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			/**
			 * Change the contentpanel to the AddEditEventPanel
			 * @see wicket.markup.html.link.Link#onClick()
			 */
			@Override
			public void onClick() {
				EditAllOrOneEventDecisionPanel.this.changeContent(BasePage.CALENDAR_PANEL, null);
				eventSelectionListener.eventSelected(event, true, EventSelectionListener.EDIT_ONE_EVENT, calendar);
			}

		};
		addOrReplace(editOnlyThisEventLink);
		//Add link to edit the entire event
		Link editAllEventsLink = new Link(EVENT_EDIT_ALL_EVENTS_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			/**
			 * Change the contentpanel to the AddEditEventPanel
			 * @see wicket.markup.html.link.Link#onClick()
			 */
			@Override
			public void onClick() {
				EditAllOrOneEventDecisionPanel.this.changeContent(BasePage.CALENDAR_PANEL, null);
				eventSelectionListener.eventSelected(event, true, EventSelectionListener.EDIT_ALL_EVENTS, null);
			}

		};
		addOrReplace(editAllEventsLink);
		//Add link to discard editing the event
		Link editEventDiscardLink = new Link(EVENT_EDIT_DISCARD_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			/**
			 * Go back to the details
			 * @see wicket.markup.html.link.Link#onClick()
			 */
			@Override
			public void onClick() {
				EditAllOrOneEventDecisionPanel.this.changeContent(BasePage.CALENDAR_PANEL, null);
			}
		};
		addOrReplace(editEventDiscardLink);
	}

	public void setupNonAccessibleComponents() {
		IndicatingAjaxLink editOnlyThisEventLink = new IndicatingAjaxLink(EVENT_EDIT_ONLY_THIS_INSTANCE_MARKUP_ID){

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				eventSelectionListener.eventSelected(event, true, EventSelectionListener.EDIT_ONE_EVENT, calendar, target, modalWindow);
			}
		};

		addOrReplace(editOnlyThisEventLink);

		IndicatingAjaxLink editAllEventsLink = new IndicatingAjaxLink(EVENT_EDIT_ALL_EVENTS_MARKUP_ID){

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				eventSelectionListener.eventSelected(event, true, EventSelectionListener.EDIT_ALL_EVENTS, calendar, target, modalWindow);
			}
		};

		addOrReplace(editAllEventsLink);

		IndicatingAjaxLink editEventDiscardLink = new IndicatingAjaxLink(EVENT_EDIT_DISCARD_MARKUP_ID) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				// XXX Temporarily disabled
				//ModalWindow.close(target);
			}
		};

		addOrReplace(editEventDiscardLink);

	}

	/**
	 * Let the parent know the content of the main panel has to be changed
	 * @param panelId The ID of the panel to set as the new content
	 * @param target The Ajax Target of the panel
	 */
	public abstract void changeContent(int panelId, AjaxRequestTarget target);

}
