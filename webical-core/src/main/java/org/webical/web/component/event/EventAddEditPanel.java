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

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.webical.Calendar;
import org.webical.Event;
import org.webical.User;
import org.webical.dao.DeleteConflictException;
import org.webical.dao.UpdateConflictException;
import org.webical.ical.Recurrence;
import org.webical.ical.RecurrenceUtil;
import org.webical.manager.CalendarManager;
import org.webical.manager.EventManager;
import org.webical.manager.WebicalException;
import org.webical.util.CalendarUtils;
import org.webical.web.action.FormFinishedAction;
import org.webical.web.action.IAction;
import org.webical.web.app.WebicalSession;
import org.webical.web.app.WebicalWebAplicationException;
import org.webical.web.component.AbstractBasePanel;
import org.webical.web.component.behavior.FormComponentValidationStyleBehavior;
import org.webical.web.event.ExtensionPoint;
import org.webical.web.listeners.EventSelectionListener;

/**
 * Creates an add/edit for for an event depending on whether a Event is provided or null
 * @author jochem
 * @author Mattijs Hoitink
 */

public abstract class EventAddEditPanel extends AbstractBasePanel {
	private static final long serialVersionUID = 1L;
	public  static final String FORM_EXTENSIONS_MARKUP_ID = "formExtensions";
	private static final String EVENT_ADD_EDIT_FORM_MARKUP_ID = "eventForm";

	private static Log log = LogFactory.getLog(EventAddEditPanel.class);

	private User user;
	private boolean excludeFromRange;
	private GregorianCalendar date = new GregorianCalendar();

	//Used to pass on to the updateanddeleteexceptionpanel
	private EventSelectionListener eventSelectionListener;

	@SpringBean(name="eventManager")
	private EventManager eventManager;

	@SpringBean(name="calendarManager")
	private CalendarManager calendarManager;

	private Event editEvent, oldEvent;
	private IModel eventModel;

	//TODO It isn't clear if this option is used, what it does and what it should do. This has to be clear before it can be implemented
	//True: show events with user offset
	//False: show events in ics dtstart, so calendar offset
	private boolean overruleCalendarTimeZone = false;
	private Long offSetUser = new Long(1);
	private String startTime = null;

	/**
	 * Constructor
	 * @param markupId The ID used in the markup
	 * @param event The Event to be edited or null when a new event is edited
	 * @param excludeFromRange Boolean to exclude the event from recurance and create a copy
	 * @param date
	 * @param eventSelectionListener
	 * @param startTime
	 */
	public EventAddEditPanel(String markupId, Event editEvent, boolean excludeFromRange, GregorianCalendar date, EventSelectionListener eventSelectionListener, String startTime) {

		super(markupId, EventAddEditPanel.class);

		// set variables needed by this panel
		this.eventSelectionListener = eventSelectionListener;

		// set variables needed the form
		this.user = WebicalSession.getWebicalSession().getUser();
		this.editEvent = editEvent;
		this.excludeFromRange = excludeFromRange;
		this.date.setTime(date.getTime());
		// TODO figure out what startTime is used for
		this.startTime = startTime;

	}

	public void setupCommonComponents() {
		// Create a new EventForm
		EventForm eventForm = new EventForm(EVENT_ADD_EDIT_FORM_MARKUP_ID, this.getAvailableCalendars(user), editEvent, this.date, excludeFromRange, startTime) {
			private static final long serialVersionUID = 1L;

			/**
			 * Validates inter field constraints and persists the event
			 * @see org.webical.web.component.event.EventForm#persistEvent(org.webical.Event)
			 */
			@Override
			public void persistEvent(Event storeEvent) {

				if(overruleCalendarTimeZone && !storeEvent.isAllDay()){
					//Display dtstart and dtend in usertimezone
					Long offSetCalendar = storeEvent.getCalendar().getOffSetFrom();

					if(offSetCalendar != null){
						float difference = offSetUser.floatValue() - offSetCalendar.floatValue();
						if(storeEvent.getDtStart() != null){
							storeEvent.setDtStart(CalendarUtils.addHours(storeEvent.getDtStart(), - difference));
						}
						if(storeEvent.getDtEnd() != null){
							storeEvent.setDtEnd(CalendarUtils.addHours(storeEvent.getDtEnd(), - difference));
						}
					}
				}

				if(storeEvent.isAllDay()){
					storeEvent.setDtStart(CalendarUtils.getStartOfDay(storeEvent.getDtStart()));

					java.util.Calendar tempCalendar = GregorianCalendar.getInstance();
					tempCalendar.setTime(storeEvent.getDtEnd());
					tempCalendar.add(GregorianCalendar.DAY_OF_MONTH, 1);

					storeEvent.setDtEnd(CalendarUtils.getStartOfDay(tempCalendar.getTime()));
				}

				if(getFrequency() != null && getInterval() != null) {
					if(getCount() == null){
						RecurrenceUtil.setRecurrenceRule(storeEvent, new Recurrence(getFrequency().getFrequenty(), getInterval(), getUntil()));
					} else {
						RecurrenceUtil.setRecurrenceRule(storeEvent, new Recurrence(getFrequency().getFrequenty(), getCount(), getInterval(), getUntil()));
					}
				} else {
					//Clear the recurrence
					RecurrenceUtil.clearRecurrence(storeEvent);
				}

				//Refresh the event calendar, or else there is a lazilyexception
				Calendar storeCalendar;
				try {
					storeCalendar = calendarManager.getCalendarForEvent(storeEvent);
				} catch (WebicalException e1) {
					 throw new WebicalWebAplicationException(e1);
				}

				//Save the event
				try {
					//If the is a gCalendar than put this remove this event from its serie
					if(excludeFromRange){
						oldEvent.getExDate().add(date.getTime());
						eventManager.storeEvent(oldEvent);
					}
					eventManager.storeEvent(storeEvent);

					if(log.isDebugEnabled()) {
						log.debug("Event saved: " + storeEvent.getSummary() + " for calendar: " + storeEvent.getCalendar().getName());
					}

					//Change back to the list
					EventAddEditPanel.this.onAction(new FormFinishedAction(null));


				} catch (WebicalException e) {
					if(e.getCause().getClass() == UpdateConflictException.class){
						if(log.isDebugEnabled()){
							log.debug("update exception");
						}
						eventSelectionListener.eventUpdateandDeleteException(storeEvent, storeCalendar, true);
					} else if(e.getCause().getClass() == DeleteConflictException.class){
						if(log.isDebugEnabled()){
							log.debug("delete exception");
						}
						eventSelectionListener.eventUpdateandDeleteException(storeEvent, storeCalendar, false);
					} else {
						error("Could not store the event");
						throw new WebicalWebAplicationException("Event could not be saved: " + storeEvent.getSummary(), e);
					}
				}
			}

			/**
			 * handels the form cancel
			 * @see org.webical.web.component.event.EventForm#discardEvent()
			 */
			@Override
			public void onDiscard() {
				EventAddEditPanel.this.onAction(new FormFinishedAction(null));
			}

		};
		eventForm.add(new FormComponentValidationStyleBehavior());

		//Create and register a new extensionpoint for plugins
		RepeatingView extensionPoint = new RepeatingView(FORM_EXTENSIONS_MARKUP_ID);
		getExtensionPoints().put(FORM_EXTENSIONS_MARKUP_ID, new ExtensionPoint(extensionPoint, eventModel));

		//Add the components
		eventForm.add(extensionPoint);
		addOrReplace(eventForm);
	}

	public void setupAccessibleComponents() {
		// NOT IMPLEMENTED
	}

	public void setupNonAccessibleComponents() {
		// NOT IMPLEMENTED
	}

	/**
	 * Handle actions generated by this panel
	 * @param action The action to handle
	 */
	public abstract void onAction(IAction action);

	/**
	 * Get a list of calendars accesible to this user
	 * @param user the User
	 * @return List<Calendar> the list with user calendars
	 */
	private List<Calendar> getAvailableCalendars(User user) {
		List<Calendar> availableCalendars = null;
		try {
			availableCalendars = calendarManager.getCalendars(user);
			if(availableCalendars == null){
				availableCalendars = new ArrayList<Calendar>();
			}
		} catch (WebicalException e) {
			availableCalendars = new ArrayList<Calendar>();
		}
		return availableCalendars;
	}
}
