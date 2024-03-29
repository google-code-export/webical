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

package org.webical.web.component.settings;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.webical.Calendar;
import org.webical.web.action.CalendarSelectedAction;
import org.webical.web.action.IAction;
import org.webical.web.component.AbstractBasePanel;
import org.webical.web.component.calendar.CalendarFormPanel;

/**
 * Panel that either displays a List of calendars or the details of a calendar for editing
 *
 * @author Ivo van Dongen
 * @author Mattijs Hoitink
 */
public abstract class CalendarSettingsPanel extends AbstractBasePanel {
	private static final long serialVersionUID = 1L;

	// Markup IDs
	private static final String ADD_CALENDAR_LINK_MARKUP_ID = "addCalendarLink";
	private static final String CALENDAR_CONTENT_PANEL_MARKUP_ID = "calendarSettingsPanelContent";

	private Calendar calendar;

	/** The list of calendars. Hold on to this list so the paging is not lost after panel switch */
	private SettingsCalendarListPanel calendarListPanel;

	private WebMarkupContainer addCalendarLink;

	/**
	 * Constructor
	 * @param markupId the id used in the markup
	 */
	public CalendarSettingsPanel(String markupId) {
		super(markupId, CalendarSettingsPanel.class);
		setOutputMarkupId(true);
	}

	/**
	 * Constructor
	 * @param markupId the id used in the markup
	 * @param calendar the calendar to edit, or null for list
	 */
	public CalendarSettingsPanel(String markupId, Calendar calendar) {
		this(markupId);
		this.calendar = calendar;
	}

	/* (non-Javadoc)
	 * @see org.webical.web.component.IAccessibilitySwitchingComponent#setupCommonComponents()
	 */
	public void setupCommonComponents() {
		//create the panel here to use it when te discard button is pressed
		calendarListPanel = new SettingsCalendarListPanel(CALENDAR_CONTENT_PANEL_MARKUP_ID){
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction(IAction action) {
				CalendarSettingsPanel.this.onAction(action);
			}

		};
		if(calendar != null) {
			//create and show the add/edit form
			addOrReplace(new CalendarFormPanel(CALENDAR_CONTENT_PANEL_MARKUP_ID, calendar){
				private static final long serialVersionUID = 1L;

				/* (non-Javadoc)
				 * @see org.webical.web.components.CalendarAddEditPanel#formFinished(org.apache.wicket.ajax.AjaxRequestTarget)
				 */
				@Override
				public void onAction(IAction action) {
					CalendarSettingsPanel.this.onAction(action);
				}
			});
		} else {
			// show the list with calendars
			addOrReplace(calendarListPanel);
		}

	}

	/* (non-Javadoc)
	 * @see org.webical.web.components.IAccessibilitySwitchingComponent#setupAccessibleComponents()
	 */
	public void setupAccessibleComponents() {
		addCalendarLink = new Link(ADD_CALENDAR_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			/* (non-Javadoc)
			 * @see org.apache.wicket.markup.html.link.Link#onClick()
			 */
			@Override
			public void onClick() {
				CalendarSettingsPanel.this.onAction(new CalendarSelectedAction(new Calendar(), null));
			}

		};
		if(calendar != null) {
			addCalendarLink.setVisible(false);
		}
		addOrReplace(addCalendarLink);

	}

	/* (non-Javadoc)
	 * @see org.webical.web.components.IAccessibilitySwitchingComponent#setupNonAccessibleComponents()
	 */
	public void setupNonAccessibleComponents() {
		addCalendarLink = new IndicatingAjaxLink(ADD_CALENDAR_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				CalendarSettingsPanel.this.replace(new CalendarFormPanel(CALENDAR_CONTENT_PANEL_MARKUP_ID, null) {
					private static final long serialVersionUID = 1L;

					/* (non-Javadoc)
					 * @see org.webical.web.components.CalendarAddEditPanel#formFinished(org.apache.wicket.ajax.AjaxRequestTarget)
					 */
					@Override
					public void onAction(IAction action) {
							CalendarSettingsPanel.this.onAction(action);
					}
				});
				target.addComponent(CalendarSettingsPanel.this);
			}

		};
		addOrReplace(addCalendarLink);
	}

	/**
	 * Handle actions generated by this panel.
	 * @param action The action to handle
	 */
	public abstract void onAction(IAction action);

}
