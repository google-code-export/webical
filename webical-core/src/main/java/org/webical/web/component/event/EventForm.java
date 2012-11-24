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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.webical.Calendar;
import org.webical.Event;
import org.webical.User;
import org.webical.ical.Recurrence;
import org.webical.ical.RecurrenceUtil;
import org.webical.manager.CalendarManager;
import org.webical.manager.WebicalException;
import org.webical.util.CalendarUtils;
import org.webical.web.action.IAction;
import org.webical.web.action.ShowCalendarAction;
import org.webical.web.action.StoreEventAction;
import org.webical.web.action.RemoveEventAction;
import org.webical.web.app.WebicalSession;
import org.webical.web.app.WebicalWebAplicationException;

/**
 * The form used for adding and editing events. This class has two abstract methods
 * that have to be implemented by the panel to handle the persist and discard actions
 * generated by the form.
 *
 * @author Mattijs Hoitink
 *
 */
public abstract class EventForm extends Form {

	private static final long serialVersionUID = 0L;
	private static Log log = LogFactory.getLog(EventFormPanel.class);

	// markup ID's
	private static final String HEADING_LABEL_MARKUP_ID 				= "headingLabel";
	private static final String FORM_FEEDBACK_MARKUP_ID 				= "formFeedback";

	private static final String EVENT_SUMMARY_TEXTFIELD_MARKUP_ID 		= "summary";
	private static final String EVENT_START_DATE_TEXTFIELD_MARKUP_ID 	= "dtStart";
	private static final String EVENT_END_DATE_TEXTFIELD_MARKUP_ID 		= "dtEnd";
	private static final String EVENT_START_TIME_TEXTFIELD_MARKUP_ID 	= "tmStart";
	private static final String EVENT_END_TIME_TEXTFIELD_MARKUP_ID 		= "tmEnd";
	private static final String EVENT_LOCATION_TEXTFIELD_MARKUP_ID 		= "location";
	private static final String EVENT_CALENDAT_DROPDOWN_MARKUP_ID 		= "calendar";
	private static final String EVENT_DESCRIPTION_TEXTAREA_MARKUP_ID 	= "description";
	private static final String REPEAT_FREQUENCY_DROPDOWN_MARKUP_ID 	= "frequency";
	private static final String CLEAR_FREQUENCY_BUTTON_MARKUP_ID 		= "clearFrequencyButton";
	private static final String REPEAT_INTERVAL_TEXTFIELD_MARKUP_ID 	= "interval";
	private static final String REPEAT_COUNT_TEXTFIELD_MARKUP_ID 		= "count";
	private static final String REPEAT_UNTIL_TEXTFIELD_MARKUP_ID 		= "until";
	private static final String REPEAT_UNTIL_FORMAT_LABEL_MARKUP_ID 	= "untilFormat";

	private static final String ALLDAY_BUTTON_MARKUP_ID 				= "alldayButton";
	private static final String DISCARD_BUTTON_MARKUP_ID 				= "discardButton";
	private static final String SUBMIT_BUTTON_MARKUP_ID 				= "submitButton";
	
	// Resource ID's
	private static final String ADD_EVENT_HEADING_RESOURCE_ID			= "add.heading";
	private static final String EDIT_EVENT_HEADING_RESOURCE_ID			= "edit.heading";
	private static final String ALLDAY_BUTTON_RESOURCE_ID 				= "alldayButton.text";
	private static final String CLEAR_FREQUENCY_BUTTON_RESOURCE_ID 		= "clearFrequencyButton.text";
	private static final String ADD_BUTTON_RESOURCE_ID 					= "add.button";
	private static final String EDIT_BUTTON_RESOURCE_ID 				= "edit.button";

	// form elements
	private Button allDayButton, clearRecurrenceButton, submitButton, discardButton;
	private DateTextField startDateTextField, startTimeTextField, endDateTextField, endTimeTextField;
	private DropDownChoice calendarDropDownChoice, repeatFrequencyDropDownChoice;
	private FeedbackPanel formFeedback;
	private Label headingLabel, untilFormatLabel;
	private RequiredTextField summaryTextField;
	private TextField locationTextField, repeatIntervalTextField, repeatCountTextField, repeatUntilTextField;
	private TextArea descriptionTextArea;
	
	/**
	 * Used by Spring to set the Calendar Manager
	 */
	@SpringBean(name="calendarManager")
	private CalendarManager calendarManager;
	
	/**
	 * The Event for this form
	 */
	private Event formEvent;
	
	/**
	 * The event wrapper for the Event
	 */
	private EventWrapper eventWrapper;

	/**
	 * A deep copy of the event to exclude it from the range if neccesary
	 */
	private Event oldEvent;

	/**
	 * The date the selected event is on, or the date the new event should be on
	 */
	private GregorianCalendar selectedDate;

	/**
	 * Whether this is an edit form or not
	 */
	private boolean editForm;

	//TODO It isn't clear if this option is used, what it does and what it should do. This has to be clear before it can be implemented
	// Add it to the EventWrapper? or to the user settings?
	//True: show events with user offset
	//False: show events in ics dtstart, so calendar offset
	private boolean overruleCalendarTimeZone = false;
	private Long offSetUser = new Long(1);
	
	private SimpleDateFormat dateFormat, timeFormat;

	/**
	 * Constructor
	 * @param id the markup id
	 * @param editEvent the {@link Event} to be edited, null for a new Event
	 * @param selectedDate The date the selected event is on, or the date the new event should be on
	 *
	 */
	public EventForm(String id, Event event, GregorianCalendar selectedDate) {
		super(id);
		this.formEvent = event;
		this.selectedDate = selectedDate;

		// Check if we are editing an event
		if(formEvent != null) {
			// Clone the event and keep it for later reference
			try {
				this.oldEvent = (Event) BeanUtils.cloneBean(formEvent);
			} catch (Exception e) {
				log.error("could not clone event: " + formEvent.getUid(), e);
				throw new WebicalWebAplicationException(e);
			}
			
			// Create a new empty ExtendedEvent
			this.eventWrapper = new EventWrapper(formEvent);
			this.editForm = true;
			
			// Check if the event is recurring
			/* Disabled for milestone 0.4
			 if(RecurrenceUtil.isRecurrent(eventWrapper.getEvent())) {
				// Override the event start and end date to the date the selected event instance is on
				eventWrapper.setEventStartDate(selectedDate.getTime());
				eventWrapper.setEventEndDate(selectedDate.getTime());
			}*/
			
			// XXX mattijs: leave this comment here until extensive testing has been done
			/*if(eventWrapper.getrRule().size() != 0){

				Recurrence recurrence = null;
				try {
					recurrence = RecurrenceUtil.getRecurrenceFromRecurrenceRuleSet(eventWrapper.getrRule());
				} catch (WebicalException e) {
					throw new WebicalWebAplicationException(e);
				}
				if(recurrence != null && recurrence.getFrequency() > 0 && recurrence.getInterval() != -1){

					int frequency = recurrence.getFrequency();

					RecurrenceFrequentyStringResourceModel myStringResourceModel = RecurrenceFrequentyStringResourceModel.getStringResourceModel(frequency, frequencyModels);
					if(myStringResourceModel != null){
						eventWrapper.setFrequency(myStringResourceModel);
					}

					eventWrapper.setInterval(new Integer(recurrence.getInterval()));

					if(recurrence.getCount() != -1){
						eventWrapper.setCount(new Integer(recurrence.getCount()));
					}
					if(recurrence.getEndDay() != null){
						eventWrapper.setUntil(recurrence.getEndDay());
					}
				}
			}*/
			
		} else {
			// create a new event
			this.editForm = false;
			createEmptyEvent();
		}

		// Set up the Form
		createFormElements();
		if(this.editForm) {
			alterFormForEditing();
		}
		addFormElements();
	}

	/**
	 * Create the form elements
	 */
	private void createFormElements() {
		// Heading
		headingLabel = new Label(HEADING_LABEL_MARKUP_ID, new StringResourceModel(ADD_EVENT_HEADING_RESOURCE_ID, EventForm.this, new Model("Add event")));
		// Feedback Panel
		formFeedback = new FeedbackPanel(FORM_FEEDBACK_MARKUP_ID);
		//Title
		summaryTextField = new RequiredTextField(EVENT_SUMMARY_TEXTFIELD_MARKUP_ID, new PropertyModel(eventWrapper, "summary"));

		dateFormat = new SimpleDateFormat(WebicalSession.getWebicalSession().getUserSettings().getDateFormat(), getLocale());
		timeFormat = new SimpleDateFormat(WebicalSession.getWebicalSession().getUserSettings().getTimeFormat(), getLocale());

		// Event Start fields
		startDateTextField = new DateTextField(EVENT_START_DATE_TEXTFIELD_MARKUP_ID, new PropertyModel(eventWrapper, "eventStartDate"), dateFormat.toLocalizedPattern());
		startDateTextField.setRequired(true);
		startTimeTextField = new DateTextField(EVENT_START_TIME_TEXTFIELD_MARKUP_ID, new PropertyModel(eventWrapper, "eventStartTime"), timeFormat.toLocalizedPattern());
		startTimeTextField.setRequired(true);

		// Event End fields
		endDateTextField = (DateTextField) new DateTextField(EVENT_END_DATE_TEXTFIELD_MARKUP_ID, new PropertyModel(eventWrapper, "eventEndDate"), dateFormat.toLocalizedPattern()).setRequired(true);
		endTimeTextField = (DateTextField) new DateTextField(EVENT_END_TIME_TEXTFIELD_MARKUP_ID, new PropertyModel(eventWrapper, "eventEndTime"), timeFormat.toLocalizedPattern());
		endTimeTextField.setRequired(true);
		
		// All Day Button
		allDayButton = new Button(ALLDAY_BUTTON_MARKUP_ID, new StringResourceModel(ALLDAY_BUTTON_RESOURCE_ID, EventForm.this, new Model("All day"))) {
			private static final long serialVersionUID = 1L;

			/*(non-Javadoc)
			 * @see org.apache.wicket.markup.html.form.Button#onSubmit()
			 */
			@Override
			public void onSubmit() {
				if(eventWrapper.isAllDay()) {
					setNormalDayEventFields();
				}
				else {
					setAllDayEventFields();
				}
			}

		};
		allDayButton.setDefaultFormProcessing(false);

		// Location
		locationTextField = new TextField(EVENT_LOCATION_TEXTFIELD_MARKUP_ID, new PropertyModel(eventWrapper, "location"));
		// Calendar
		List<Calendar> availableCalendars = getAvailableCalendars(WebicalSession.getWebicalSession().getUser());
		calendarDropDownChoice = (DropDownChoice) new DropDownChoice(EVENT_CALENDAT_DROPDOWN_MARKUP_ID, new PropertyModel(eventWrapper, "calendar"), availableCalendars).setRequired(true);
		if(availableCalendars.size() > 0) {
			if (! editForm) eventWrapper.setCalendar(availableCalendars.get(0));
		}
		// Description
		descriptionTextArea = new TextArea(EVENT_DESCRIPTION_TEXTAREA_MARKUP_ID, new PropertyModel(eventWrapper, "description"));

		// Frequency
		final List<Integer> recurrenceOptions = Arrays.asList(new Integer[] { 0, Recurrence.DAILY, Recurrence.WEEKLY, Recurrence.MONTHLY, Recurrence.YEARLY });
		repeatFrequencyDropDownChoice = new DropDownChoice(REPEAT_FREQUENCY_DROPDOWN_MARKUP_ID, new PropertyModel(eventWrapper, "frequency"), recurrenceOptions, new IChoiceRenderer() {
			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Object object) {
				String recurrenceString = "";
                int value = ((Integer)object).intValue();
                switch (value)
                {
                    case Recurrence.DAILY :
                    	recurrenceString = new StringResourceModel("recur.daily", EventForm.this, null).getString();
                        break;
                    case Recurrence.WEEKLY :
                    	recurrenceString = new StringResourceModel("recur.weekly", EventForm.this, null).getString();
                        break;
                    case Recurrence.MONTHLY :
                    	recurrenceString = new StringResourceModel("recur.monthly", EventForm.this, null).getString();
                        break;
                    case Recurrence.YEARLY:
                    	recurrenceString = new StringResourceModel("recur.yearly", EventForm.this, null).getString();
					   	break;
                    case 0:
                    	recurrenceString = new StringResourceModel("recur.none", EventForm.this, null).getString();
                    	break;
					default:
						recurrenceString = new StringResourceModel("recur.none", EventForm.this, null).getString();
						break;
                }
                return recurrenceString;
			}

			public String getIdValue(Object object, int index) {
				if(index >= 0){
					return String.valueOf((index));
				}
				return null;
			}
			
		});
		if(!editForm || eventWrapper.getFrequency() == null) {
			eventWrapper.setFrequency(0);
		}
		
		// Clear frequency button
		clearRecurrenceButton = new Button(CLEAR_FREQUENCY_BUTTON_MARKUP_ID, new StringResourceModel(CLEAR_FREQUENCY_BUTTON_RESOURCE_ID, EventForm.this, new Model("Clear"))){
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				// Reset the recurrence
				eventWrapper.setFrequency(0);
				repeatFrequencyDropDownChoice.modelChanged();
				eventWrapper.setInterval(null);
				repeatIntervalTextField.modelChanged();
				eventWrapper.setCount(null);
				repeatCountTextField.modelChanged();
				eventWrapper.setUntil(null);
				repeatUntilTextField.modelChanged();
				
				super.onSubmit();
			}
			
		};
		clearRecurrenceButton.setDefaultFormProcessing(false);
		
		// Interval
		repeatIntervalTextField = new TextField(REPEAT_INTERVAL_TEXTFIELD_MARKUP_ID, new PropertyModel(eventWrapper, "interval"), Integer.class);
		// Count
		repeatCountTextField = new TextField(REPEAT_COUNT_TEXTFIELD_MARKUP_ID, new PropertyModel(eventWrapper, "count"), Integer.class);
		// Until
		repeatUntilTextField = new DateTextField(REPEAT_UNTIL_TEXTFIELD_MARKUP_ID, new PropertyModel(eventWrapper, "until"), WebicalSession.getWebicalSession().getUserSettings().getDateFormat());
		untilFormatLabel = new Label(REPEAT_UNTIL_FORMAT_LABEL_MARKUP_ID, WebicalSession.getWebicalSession().getUserSettings().getDateFormat());
		
		discardButton = new Button(DISCARD_BUTTON_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				EventForm.this.onAction(new ShowCalendarAction());
			}
			
		};
		discardButton.setDefaultFormProcessing(false);

		// Submit Button
		submitButton = new Button(SUBMIT_BUTTON_MARKUP_ID, new StringResourceModel(ADD_BUTTON_RESOURCE_ID, EventForm.this, new Model("Add")));
	}

	/**
	 * Alters the form elements for rendering an edit form
	 */
	private void alterFormForEditing() {
		// Change the form header
		headingLabel.setModel(new StringResourceModel(EDIT_EVENT_HEADING_RESOURCE_ID, EventForm.this, new Model("Edit event")));
		// Change the submit button text
		submitButton.setModel(new StringResourceModel(EDIT_BUTTON_RESOURCE_ID, EventForm.this, new Model("Edit")));
		// set the correct calendar for the drop down choice
		calendarDropDownChoice.setModel(new PropertyModel(eventWrapper, "calendar"));
		if(this.eventWrapper.isAllDay()) {
			setAllDayEventFields();
		}
	}

	/**
	 * Add the form elements to the form
	 */
	private void addFormElements() {
		addOrReplace(headingLabel);
		addOrReplace(formFeedback);
		addOrReplace(summaryTextField);
		addOrReplace(startTimeTextField);
		addOrReplace(startDateTextField);
		addOrReplace(endTimeTextField);
		addOrReplace(endDateTextField);
		addOrReplace(allDayButton);
		addOrReplace(calendarDropDownChoice);
		addOrReplace(locationTextField);
		addOrReplace(descriptionTextArea);
		addOrReplace(repeatFrequencyDropDownChoice);
		addOrReplace(clearRecurrenceButton);
		addOrReplace(repeatIntervalTextField);
		addOrReplace(repeatCountTextField);
		addOrReplace(repeatUntilTextField);
		addOrReplace(untilFormatLabel);
		addOrReplace(discardButton);
		addOrReplace(submitButton);
	}

	/**
	 * Create an empty Event and set the start and end times
	 * @return Event
	 */
	private EventWrapper createEmptyEvent() {
		this.formEvent = new Event();
		this.eventWrapper = new EventWrapper(formEvent);

		GregorianCalendar dateCalendar = CalendarUtils.duplicateCalendar(selectedDate);
		GregorianCalendar timeCalendar = CalendarUtils.newTodayCalendar(dateCalendar.getFirstDayOfWeek());
		dateCalendar.set(GregorianCalendar.HOUR_OF_DAY, timeCalendar.get(GregorianCalendar.HOUR_OF_DAY));
		dateCalendar.set(GregorianCalendar.MINUTE, timeCalendar.get(GregorianCalendar.MINUTE) / 5 * 5);

		//Fill the start and end times with proper values
		eventWrapper.setDtStart(dateCalendar.getTime());
		dateCalendar.add(GregorianCalendar.HOUR_OF_DAY, 1);
		eventWrapper.setDtEnd(dateCalendar.getTime());

		return this.eventWrapper;
	}

	/**
	 * Update the form and event for an all day event
	 */
	public void setAllDayEventFields() {
		eventWrapper.setAllDay();
		
		// Replace the DateTextField
		replaceFormDateComponents(true);
	}

	/**
	 * Update the form and event for a non all day event
	 */
	public void setNormalDayEventFields() {
		eventWrapper.resetAllDay();

		// Replace the DateTextFields
		replaceFormDateComponents(false);
	}

	/**
	 * Replace the DateTextFields on the form, and update the allDay button
	 * @param allDay wheter the form is used for an all day event
	 */
	public void replaceFormDateComponents(boolean allDay) {
		if(allDay) {
			allDayButton.setModel(new StringResourceModel("AllDay_label_False", this, null));
			startTimeTextField.setEnabled(false);
			startTimeTextField.setVisible(false);
			endTimeTextField.setEnabled(false);
			endTimeTextField.setVisible(false);
		} else {
			allDayButton.setModel(new StringResourceModel("AllDay_label_True", this, null));
			startTimeTextField.setEnabled(true);
			startTimeTextField.setVisible(true);
			endTimeTextField.setEnabled(true);
			endTimeTextField.setVisible(true);
		}

		addOrReplace(startTimeTextField);
		addOrReplace(endTimeTextField);
	}
	
	/**
	 * Show confirmation panel when editing a recurring event 
	 */
	@SuppressWarnings("unused")
	private void showConfirmation() {
		final EventFormPanel parent = (EventFormPanel) EventForm.this.getParent();
		parent.replaceWith(new ConfirmRecurringActionPanel(parent.getId(), "Which instances of " + formEvent.getSummary() + " do you want to alter?") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onCancel() {
				this.replaceWith(parent);
			}
			
			@Override
			public void onConfirmThis() {
				EventForm.this.alterThisEventInstance();
			}
				
			@Override
			public void onConfirmAll() {
				// Update all the event instances
				EventForm.this.alterAllEventInstances();
			}

			@Override
			public void onConfirmFollowing() {
				EventForm.this.alterFollowingEventInstances();
			}

		});
	}
	
	/**
	 * Stores a new event and excludes it from the old range.
	 */
	private void alterThisEventInstance() {
		// Exclude the new event from the recurrence rule
		RecurrenceUtil.excludeEventFromRecurrenceRule(eventWrapper.getEvent(), oldEvent, true);
		
		// set the UID from the new event to null so it will be saved as a new event
		eventWrapper.getEvent().setUid(null);
		
		// set the eventId to null so hibernate won't override the old event
		eventWrapper.getEvent().setEventId(null);
		
		// Store the events
		EventForm.this.onAction(new StoreEventAction(Arrays.asList(new Event[] {oldEvent, eventWrapper.getEvent()})));
	}

	private void alterFollowingEventInstances() {
		log.info("Altering following instance only");
	}

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

	/* (non-Javadoc)
	 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
	 */
	@Override
	public void onSubmit() {
		// Do some checks to see if the dates are filled in correct
		if(eventWrapper.getDtEnd().before(eventWrapper.getDtStart())) {
			error(new StringResourceModel("Exception.endbeforestart", this, null).getString());
		} 
		else if(!eventWrapper.isAllDay() && eventWrapper.getDtEnd().equals(eventWrapper.getDtStart())){
			error(new StringResourceModel("Exception.endbeforestart", this, null).getString());
		} 
		else if(findSubmittingButton().equals(submitButton)) {

			// Check if we are editing a recurring event
			if(editForm && RecurrenceUtil.isRecurrent(eventWrapper.getEvent())) {
				// Editing specific events in a recurrence rule is disabled for milestone 0.4
				//showConfirmation();
				alterAllEventInstances();
			} else { 
				// Store the event as normal
				alterAllEventInstances();
			}
			
		}
	}

	/**
	 * Stores all the event instances
	 */
	private void alterAllEventInstances() {
		if(overruleCalendarTimeZone && !eventWrapper.getEvent().isAllDay()){
			//Display dtstart and dtend in usertimezone
			Integer offSetCalendar = eventWrapper.getEvent().getCalendar().getOffSetFrom();

			if(offSetCalendar != null){
				float difference = offSetUser.floatValue() - offSetCalendar.floatValue();
				if(eventWrapper.getEvent().getDtStart() != null){
					eventWrapper.getEvent().setDtStart(CalendarUtils.addHours(eventWrapper.getEvent().getDtStart(), - difference));
				}
				if(eventWrapper.getEvent().getDtEnd() != null){
					eventWrapper.getEvent().setDtEnd(CalendarUtils.addHours(eventWrapper.getEvent().getDtEnd(), - difference));
				}
			}
		}

		eventWrapper.storeRecurrence();

		/*if(eventWrapper.getFrequency() != null && eventWrapper.getInterval() != null) {
			if(eventWrapper.getCount() == null){
				RecurrenceUtil.setRecurrenceRule(storeEvent, new Recurrence(eventWrapper.getFrequency(), eventWrapper.getInterval(), eventWrapper.getUntil()));
			} else {
				RecurrenceUtil.setRecurrenceRule(storeEvent, new Recurrence(eventWrapper.getFrequency(), eventWrapper.getCount(), eventWrapper.getInterval(), eventWrapper.getUntil()));
			}
		} else {
			//Clear the recurrence
			RecurrenceUtil.clearRecurrence(storeEvent);
		}*/

		//Refresh the event calendar, or else there is a lazilyexception
		/*Calendar storeCalendar;
		try {
			storeCalendar = calendarManager.getCalendarForEvent(storeEvent);
		} catch (WebicalException e1) {
			 throw new WebicalWebAplicationException(e1);
		}*/

		//Save the event
		/*
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
		*/
		// Store the event
		if (editForm  &&
			oldEvent.getCalendar().getCalendarId() != eventWrapper.getCalendar().getCalendarId())
		{
			log.info("Change Event Calendar " + eventWrapper.getCalendar().getName());
			EventForm.this.onAction(new RemoveEventAction(oldEvent));
			eventWrapper.setEventId(null);
			eventWrapper.setUid(null);
			EventForm.this.onAction(new StoreEventAction(eventWrapper.getEvent()));
		}
		else
		{
			EventForm.this.onAction(new StoreEventAction(eventWrapper.getEvent()));
		}
	}

	/**
	 * Handles actions generated by this form
	 * @param action The action to handle
	 */
	public abstract void onAction(IAction action);
}
