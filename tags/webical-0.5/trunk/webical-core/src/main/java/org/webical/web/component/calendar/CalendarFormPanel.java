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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.webical.Calendar;
import org.webical.Event;
import org.webical.manager.CalendarManager;
import org.webical.manager.EventManager;
import org.webical.manager.WebicalException;
import org.webical.web.action.FormFinishedAction;
import org.webical.web.action.IAction;
import org.webical.web.action.ShowSettingsAction;
import org.webical.web.app.WebicalWebAplicationException;
import org.webical.web.component.AbstractBasePanel;
import org.webical.web.component.behavior.FormComponentValidationStyleBehavior;
import org.webical.web.component.settings.SettingsPanelsPanel;
import org.webical.web.event.ExtensionPoint;

/**
 * Creates an add/edit for for a Calendar depending on whether a Calendar is provided or null
 *
 * @author Ivo van Dongen
 * @author Mattijs Hoitink
 */
public abstract class CalendarFormPanel extends AbstractBasePanel {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(CalendarFormPanel.class);

	public  static final String FORM_EXTENSIONS_MARKUP_ID = "formExtensions";
	private static final String CALENDAR_ADD_EDIT_FORM_MARKUP_ID = "calendarAddEditForm";

	/** The calendar to edit */
	private Calendar calendar;

	/** Calendar manager instantiated by Spring */
	@SpringBean(name="calendarManager")
	private CalendarManager calendarManager;

	/** Event Manager instantiatd by Spring */
	@SpringBean(name="eventManager")
	private EventManager eventManager;

	// remember this because if you change the url you dont want update the ics (which happens
	// because the timezone can be changed). After the new ics is parsed the timezone is
	// updated anyway.
	private String oldUrl = null;

	/**
	 * Constructor
	 * @param markupId the id used in the markup
	 * @param calendar the Calendar to be edited or null when a new caledar is edited
	 */
	public CalendarFormPanel(String markupId, Calendar calendar)
	{
		super(markupId, CalendarFormPanel.class);
		this.calendar = calendar;
		if(this.calendar != null) {
			this.oldUrl = calendar.getUrl();
		}
	}

	public void setupCommonComponents()
	{
		// Create an ArrayList with calendar types for use in the form
		ArrayList<String> calendarTypes;
		try {
			calendarTypes = new ArrayList<String>(calendarManager.getAvailableCalendarTypes());
		} catch (WebicalException e) {
			throw new WebicalWebAplicationException(e);
		}

		// Create the Calendar Form
		CalendarForm calendarForm = new CalendarForm(CALENDAR_ADD_EDIT_FORM_MARKUP_ID, calendar, calendarTypes)
		{
			private static final long serialVersionUID = 1L;

			/* (non-Javadoc)
			 * @see org.webical.web.components.CalendarForm#onDiscard()
			 */
			@Override
			protected void onDiscard() {
				// Let the parent know we are finished with the form
				CalendarFormPanel.this.onAction(new FormFinishedAction(null));
			}

			/* (non-Javadoc)
			 * @see org.webical.web.components.CalendarForm#persistCalendar(org.webical.Calendar)
			 */
			@Override
			protected void persistCalendar(Calendar storeCalendar) {
				try {
					List<Event> events = null;

					boolean urlChanged = false;

					if (oldUrl != null && oldUrl.equals(storeCalendar.getUrl()))
					{
						urlChanged = false;
						events = eventManager.getAllEvents(storeCalendar);
					} else {
						urlChanged = true;
					}

					if (events != null && !urlChanged) {
						//TODO this shouldn't be done here, but in the dao.
						log.debug("Storing calendar with events.");
						calendarManager.storeCalendar(storeCalendar, events);
					} else {
						log.debug("Storing calendar without events.");
						calendarManager.storeCalendar(storeCalendar);
					}

					if (log.isDebugEnabled()) {
						log.debug("Calendar saved: " + storeCalendar.getName() + " for user: " + storeCalendar.getUser().getUserId());
					}

					// Let the parent know we are finished with the form
					CalendarFormPanel.this.onAction(new ShowSettingsAction(SettingsPanelsPanel.CALENDAR_SETTINGS_TAB_INDEX));

				} catch (WebicalException e) {
					error("Could not store the calendar");
					throw new WebicalWebAplicationException("Calendar could not be saved: " + storeCalendar.getName() + " for user: " + storeCalendar.getUser().getUserId(), e);
				}
			}
		};
		// Add validation to the form
		calendarForm.add(new FormComponentValidationStyleBehavior());

		//Create and register a new extensionpoint
		RepeatingView extensionPoint = new RepeatingView(FORM_EXTENSIONS_MARKUP_ID);
		getExtensionPoints().put(FORM_EXTENSIONS_MARKUP_ID, new ExtensionPoint(extensionPoint, new CompoundPropertyModel(this.calendar)));

		//Add the extensionpoint to the form and the form to the panel
		calendarForm.add(extensionPoint);
		addOrReplace(calendarForm);
	}

	public void setupAccessibleComponents() {
		// NOTHING TO DO
	}

	public void setupNonAccessibleComponents() {
		// NOTHING TO DO
	}

	/**
	 * Notify the parent the user is finished with the form
	 * @param target The Ajax target of the panel
	 */
	public abstract void onAction(IAction action);

	/**
	 * Used by Spring to set the CalendarManager
	 * @param calendarManager a CalendarManager
	 */
	public void setCalendarManager(CalendarManager calendarManager) {
		this.calendarManager = calendarManager;
	}
	/**
	 * Used by Spring to set the EventManager
	 * @param eventManager an EventManager
	 */
	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}
}
