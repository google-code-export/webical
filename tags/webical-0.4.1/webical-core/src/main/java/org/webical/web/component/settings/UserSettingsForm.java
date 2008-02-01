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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.webical.User;
import org.webical.UserSettings;
import org.webical.web.component.calendar.CalendarPanel;

/**
 * Form for editing the users information and application settings
 *
 * @author Mattijs Hoitink
 *
 */
public abstract class UserSettingsForm extends Form {
	private static final long serialVersionUID = 1L;

	// MARKUP ID's
	private static final String FEEDBACK_PANEL_MARKUP_ID = "feedbackPanel";
	private static final String FIRST_NAME_TEXTFIELD_MARKUP_ID = "firstName";
	private static final String LAST_NAME_PREFIX_TEXTFIELD_MARKUP_ID = "lastNamePrefix";
	private static final String LAST_NAME_TEXTFIELD_MARKUP_ID = "lastName";
	private static final String BIRTHDATE_TEXTFIELD_MARKUP_ID = "birthDate";
	private static final String FIRST_DAY_OF_WEEK_DROPDOWN_MARKUP_ID = "firstDayOfWeek";
	private static final String DEFAULT_CALENDAR_VIEW_DROPDOWN_MARKUP_ID = "defaultCalendarView";
	private static final String NUMBER_OF_AGENDA_DAYS_DROPDOWN_MARKUP_ID = "numberOfAgendaDays";
	private static final String DATE_FORMAT_DROPDOWN_MARKUP_ID = "dateFormat";
	private static final String TIME_FORMAT_DROPDOWN_MARKUP_ID = "timeFormat";
	private static final String SUBMIT_BUTTON_MARKUP_ID = "submitButton";
	private static final String BIRTHDATE_FORMAT_LABEL_MARKUP_ID = "birthDateFormatLabel";
	
	// RESOURCE ID's
	private static final String WEEKDAY_MONDAY_RESOURCE_ID = "Weekday.Monday";
	private static final String WEEKDAY_TUESDAY_RESOURCE_ID = "Weekday.Tuesday";
	private static final String WEEKDAY_WEDNESDAY_RESOURCE_ID = "Weekday.Wednesday";
	private static final String WEEKDAY_THURSDAY_RESOURCE_ID = "Weekday.Thursday";
	private static final String WEEKDAY_FRIDAY_RESOURCE_ID = "Weekday.Friday";
	private static final String WEEKDAY_SATURDAY_RESOURCE_ID = "Weekday.Saturday";
	private static final String WEEKDAY_SUNDAY_RESOURCE_ID = "Weekday.Sunday";
	private static final String CALENDAR_VIEW_DAY_RESOURCE_ID = "CalendarView.Day";
	private static final String CALENDAR_VIEW_WEEK_RESOURCE_ID = "CalendarView.Week";
	private static final String CALENDAR_VIEW_MONTH_RESOURCE_ID = "CalendarView.Month";
	private static final String CALENDAR_VIEW_AGENDA_RESOURCE_ID = "CalendarView.Agenda";
	private static final String SUBMIT_BUTTON_RESOURCE_ID = "submitButton.text";

	/** The user */
	private User user;
	/** The application settings for this user */
	private UserSettings userSettings;

	// Form elements
	private FeedbackPanel feedbackPanel;
	private TextField firstNameTextField, lastNamePrefixTextField, lastNameTextField, birthDateTextField;
	private DropDownChoice firstDayOfWeekDropDown, defaultCalendarViewDropDown, numberOfAgendaDaysDropDown, dateFormatDropDown, timeFormatDropDown;
	private Button submitButton;
	private Label birthDateHelpLabel;

	/**
	 * Default constructor
	 * @param markupId The ID to use in markup
	 * @param user The {@link User}
	 * @param userSettings The settings for the {@link User}
	 */
	public UserSettingsForm(String markupId, User user, UserSettings userSettings) {
		super(markupId);
		this.user = user;
		this.userSettings = userSettings;

		setupFormElements();
	}

	/**
	 * Set up the elements for this {@link Form}
	 */
	private void setupFormElements() {
		feedbackPanel = new FeedbackPanel(FEEDBACK_PANEL_MARKUP_ID);
		// Create the name fields
		firstNameTextField = (TextField) new TextField(FIRST_NAME_TEXTFIELD_MARKUP_ID, new PropertyModel(user, "firstName")).setRequired(true);
		lastNamePrefixTextField = new TextField(LAST_NAME_PREFIX_TEXTFIELD_MARKUP_ID, new PropertyModel(user, "lastNamePrefix"));
		lastNameTextField = (TextField) new TextField(LAST_NAME_TEXTFIELD_MARKUP_ID, new PropertyModel(user, "lastName")).setRequired(true);
		
		// Create the birth date field with help
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", getLocale());
		birthDateTextField = (DateTextField) new DateTextField(BIRTHDATE_TEXTFIELD_MARKUP_ID, new PropertyModel(user, "birthDate"), sdf.toLocalizedPattern()).setRequired(true);
		birthDateHelpLabel = new Label(BIRTHDATE_FORMAT_LABEL_MARKUP_ID, sdf.toPattern());
		
		// Create the first day of week field with help
		final List<Integer> weekdays = Arrays.asList(new Integer[] { Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY });
		firstDayOfWeekDropDown = new DropDownChoice(FIRST_DAY_OF_WEEK_DROPDOWN_MARKUP_ID, new PropertyModel(userSettings, "firstDayOfWeek"), weekdays, new IChoiceRenderer() {
			private static final long serialVersionUID = 1L;

			/**
			 * Shows the day name to the user.
			 * @see org.apache.wicket.markup.html.form.IChoiceRenderer#getDisplayValue(java.lang.Object)
			 */
			public Object getDisplayValue(Object object) {
					String weekdayString = "";
	                int value = ((Integer)object).intValue();
	                switch (value)
	                {
	                    case Calendar.SUNDAY :
	                    	weekdayString = new StringResourceModel(WEEKDAY_SUNDAY_RESOURCE_ID, UserSettingsForm.this, null).getString();
	                        break;
	                    case Calendar.MONDAY :
	                    	weekdayString = new StringResourceModel(WEEKDAY_MONDAY_RESOURCE_ID, UserSettingsForm.this, null).getString();
	                        break;
	                    case Calendar.TUESDAY :
	                    	weekdayString = new StringResourceModel(WEEKDAY_TUESDAY_RESOURCE_ID, UserSettingsForm.this, null).getString();
	                        break;
	                    case Calendar.WEDNESDAY:
	                    	weekdayString = new StringResourceModel(WEEKDAY_WEDNESDAY_RESOURCE_ID, UserSettingsForm.this, null).getString();
						   	break;
						case Calendar.THURSDAY:
							weekdayString = new StringResourceModel(WEEKDAY_THURSDAY_RESOURCE_ID, UserSettingsForm.this, null).getString();
							break;
						case Calendar.FRIDAY:
							weekdayString = new StringResourceModel(WEEKDAY_FRIDAY_RESOURCE_ID, UserSettingsForm.this, null).getString();
							break;
						case Calendar.SATURDAY:
							weekdayString = new StringResourceModel(WEEKDAY_SATURDAY_RESOURCE_ID, UserSettingsForm.this, null).getString();
							break;
	                }
	                return weekdayString;
			}

			/**
			 * Returns the identifier for the day.
			 * @see org.apache.wicket.markup.html.form.IChoiceRenderer#getIdValue(java.lang.Object, int)
			 */
			public String getIdValue(Object object, int index) {
				if(index >= 0){
					return String.valueOf((index + 1));
				}
				return null;
			}

		});
		firstDayOfWeekDropDown.setRequired(true);

		// Create the defautl calendar view field with help
		final List<Integer> calendarViews = Arrays.asList(new Integer[] { CalendarPanel.DAY_VIEW, CalendarPanel.WEEK_VIEW, CalendarPanel.MONTH_VIEW, CalendarPanel.AGENDA_VIEW });
		defaultCalendarViewDropDown = new DropDownChoice(DEFAULT_CALENDAR_VIEW_DROPDOWN_MARKUP_ID, new PropertyModel(userSettings, "defaultCalendarView"), calendarViews, new IChoiceRenderer(){
			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Object object) {
				String calendarViewString = "";
                int value = ((Integer)object).intValue();
                switch (value)
                {
                    case CalendarPanel.DAY_VIEW :
                    	calendarViewString = new StringResourceModel(CALENDAR_VIEW_DAY_RESOURCE_ID, UserSettingsForm.this, null).getString();
                        break;
                    case CalendarPanel.WEEK_VIEW :
                    	calendarViewString = new StringResourceModel(CALENDAR_VIEW_WEEK_RESOURCE_ID, UserSettingsForm.this, null).getString();
                        break;
                    case CalendarPanel.MONTH_VIEW :
                    	calendarViewString = new StringResourceModel(CALENDAR_VIEW_MONTH_RESOURCE_ID, UserSettingsForm.this, null).getString();
                        break;
                    case CalendarPanel.AGENDA_VIEW:
                    	calendarViewString = new StringResourceModel(CALENDAR_VIEW_AGENDA_RESOURCE_ID, UserSettingsForm.this, null).getString();
					   	break;
                }
                return calendarViewString;
			}

			public String getIdValue(Object object, int index) {
				if(index >= 0){
					return String.valueOf((index));
				}
				return null;
			}

		});
		defaultCalendarViewDropDown.setRequired(true);
		
		Integer[] numberOfAgendaDays = { 1, 2, 3, 4, 5, 6 };
		numberOfAgendaDaysDropDown = new DropDownChoice(NUMBER_OF_AGENDA_DAYS_DROPDOWN_MARKUP_ID, new PropertyModel(userSettings, "numberOfAgendaDays"), Arrays.asList(numberOfAgendaDays));
		numberOfAgendaDaysDropDown.setRequired(true);
		
		final String[] dateFormats = { "dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd"};
		dateFormatDropDown = new DropDownChoice(DATE_FORMAT_DROPDOWN_MARKUP_ID, new PropertyModel(userSettings, "dateFormat"), Arrays.asList(dateFormats), new IChoiceRenderer() {
			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Object object) {
				Date sampleDate = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat();
				String dateFormat = ((String) object);
				
				List<String> dateFormatList = Arrays.asList(dateFormats);
				for(String currentDateFormat : dateFormatList) {
					if(dateFormat.equals(currentDateFormat)) {
						sdf.applyPattern(currentDateFormat);
					}
				}
				
				return sdf.format(sampleDate);
			}

			public String getIdValue(Object object, int index) {
				List<String> dateFormatList = Arrays.asList(dateFormats);
				
				return dateFormatList.get(dateFormatList.indexOf(((String) object)));
			}
			
		});
		
		final String[] timeFormats = { "HH:mm", "hh:mm a" };
		timeFormatDropDown = new DropDownChoice(TIME_FORMAT_DROPDOWN_MARKUP_ID, new PropertyModel(userSettings, "timeFormat"), Arrays.asList(timeFormats), new IChoiceRenderer(){
			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Object object) {
				Date sampleDate = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat();
				String timeFormat = ((String) object);
				
				List<String> timeFormatList = Arrays.asList(timeFormats);
				for(String currentTimeFormat : timeFormatList) {
					if(timeFormat.equals(currentTimeFormat)) {
						sdf.applyPattern(currentTimeFormat);
					}
				}
				return sdf.format(sampleDate);
			}

			public String getIdValue(Object object, int index) {
				List<String> timeFormatList = Arrays.asList(timeFormats);
				
				return timeFormatList.get(timeFormatList.indexOf(((String) object)));
			}
			
		});

		submitButton = new Button(SUBMIT_BUTTON_MARKUP_ID, new StringResourceModel(SUBMIT_BUTTON_RESOURCE_ID, UserSettingsForm.this, new Model("Cancel")));

		// Add the form elements
		addOrReplace(feedbackPanel);
		addOrReplace(firstNameTextField);
		addOrReplace(lastNamePrefixTextField);
		addOrReplace(lastNameTextField);
		addOrReplace(birthDateTextField);
		addOrReplace(birthDateHelpLabel);
		addOrReplace(firstDayOfWeekDropDown);
		addOrReplace(defaultCalendarViewDropDown);
		addOrReplace(numberOfAgendaDaysDropDown);
		addOrReplace(dateFormatDropDown);
		addOrReplace(timeFormatDropDown);
		addOrReplace(submitButton);
	}

	/* (non-Javadoc)
	 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
	 */
	@Override
	protected void onSubmit() {
		storeUserSettings(this.user, this.userSettings);
	}

	/**
	 * Store the user information and application settings
	 * @param user The user to store
	 * @param userSettings The users application settings to store
	 */
	public abstract void storeUserSettings(User user, UserSettings userSettings);

}
