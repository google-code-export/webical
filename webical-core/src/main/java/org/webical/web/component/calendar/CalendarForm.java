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

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.webical.Calendar;
import org.webical.User;
import org.webical.util.CalendarUtils;
import org.webical.web.app.WebicalSession;
import org.webical.web.component.validation.UrlValidator;

/**
 * Form used to add or edit a calendar
 * 
 * @author Mattijs Hoitink
 *
 */

public abstract class CalendarForm extends Form {

	private static final long serialVersionUID = 1L;

	// Resource ids
	private static final String HEADING_LABEL_EDIT_MODE_RESOURCE_ID = "heading_label_edit_mode";
	private static final String HEADING_LABEL_ADD_MODE_RESOURCE_ID = "heading_label_add_mode";
	private static final String SUBMIT_BUTTON_EDIT_MODE_RESOURCE_ID = "submit_button_edit_mode";
	private static final String SUBMIT_BUTTON_ADD_MODE_RESOURCE_ID = "submit_button_add_mode";

	// Markup id's
	private static final String FEEDBACK_PANEL_MARKUP_ID = "formFeedback";
	private static final String HEADING_LABEL_MARKUP_ID = "headingLabel";
	private static final String NAME_TEXTFIELD_MARKUP_ID = "name";
	private static final String TYPE_DROPDOWNCHOICE_MARKUP_ID = "type";
	private static final String URL_TEXTFIELD_MARKUP_ID = "url";
	private static final String USERNAME_TEXTFIELD_MARKUP_ID = "username";
	private static final String PASSWORD_TEXTFIELD_MARKUPID = "password";
	private static final String TIMEZONE_DROPDOWNCHOICE_MARKUP_ID = "offSetFrom";
	private static final String DISCARD_LINK_MARKUP_ID = "discardLink";
	private static final String SUBMIT_BUTTON_MARKUP_ID = "calendarSubmitButton";

	private boolean editForm;

	private User user;
	private Calendar editCalendar;
	private ArrayList<String> calendarTypes;
	private IModel calendarModel;

	// Form elements
	private FeedbackPanel feedbackPanel;
	private Label headingLabel;
	private RequiredTextField nameTextField, urlTextField;
	private DropDownChoice typeDropDownChoice, timeZoneDropDownChoice;
	private TextField usernameTextField;
	private PasswordTextField passwordTextField;
	private Link discardLink;
	private Button submitButton;

	/**
	 * Constructor for a new Calendar Form
	 * @param id The Markup ID
	 * @param calendar The calendar to edit, or null for a new Calendar
	 * @param calendarTypes The types of calendars supported by the installation
	 * @param user The user for the calendar
	 *
	 * @author Mattijs Hoitink
	 */
	public CalendarForm(String id, Calendar calendar, ArrayList<String> calendarTypes) {
		super(id);

		this.editCalendar = calendar;
		this.calendarTypes = calendarTypes;
		this.user = WebicalSession.getWebicalSession().getUser();;

		// Check if we are editing a calendar. If editCalendar is a calendar, set up the edit form
		if((this.editCalendar != null) && (this.editCalendar.getUrl() != null)) {
			
			//Use the offset (timezone) to calculate the offsetto (daylight saving)
			long offSetFromTemp = new Long(0);
			long offSetToTemp = new Long(1);
			if(editCalendar.getOffSetFrom() != null) {
				// overwrite the offset values from above
				offSetFromTemp = editCalendar.getOffSetFrom();
				offSetToTemp = new Long(editCalendar.getOffSetFrom().intValue() + 1);
			}
			if(editCalendar.getOffSetTo() != null){
				// overwrite the offSetTo as it allready exists in the calendar
				offSetToTemp = editCalendar.getOffSetTo();
			}
			// set the offsets on the calendar
			editCalendar.setOffSetFrom(offSetFromTemp);
			editCalendar.setOffSetTo(offSetToTemp);


			this.calendarModel = new CompoundPropertyModel(this.editCalendar);
			this.editForm = true;
		}
		else {
			this.calendarModel = new CompoundPropertyModel(this.createEmptyCalendar());
			this.editForm = false;
		}

		setModel(calendarModel);

		// create the form elements, edit them if nessecary and add them to the form
		createFormElements();
		if(this.editForm) {
			alterFormForEditing();
		}
		addFormElements();

	}

	/**
	 * Adds the elements from createFormElements to the form
	 */
	private void addFormElements() {
		add(headingLabel);
		add(feedbackPanel);
		add(nameTextField);
		add(typeDropDownChoice);
		add(urlTextField);
		add(usernameTextField);
		add(passwordTextField);
		add(timeZoneDropDownChoice);
		add(discardLink);
		add(submitButton);
	}

	/**
	 * Alters the form elements when the Form is used for editing
	 */
	private void alterFormForEditing() {
		headingLabel.setModel(new StringResourceModel(HEADING_LABEL_EDIT_MODE_RESOURCE_ID, this, null));
		submitButton.setModel(new StringResourceModel(SUBMIT_BUTTON_EDIT_MODE_RESOURCE_ID, this, null));

		addOrReplace(headingLabel);
		addOrReplace(submitButton);
	}

	/**
	 * Creates an empty Calendar and assingns it to the user
	 */
	private Calendar createEmptyCalendar() {
		this.editCalendar = new Calendar();
		this.editCalendar.setUser(user);
		this.editCalendar.setVisible(true);
		return this.editCalendar;
	}

	/**
	 * Creates the Elements in the Form
	 */
	private void createFormElements() {
		feedbackPanel = new FeedbackPanel(FEEDBACK_PANEL_MARKUP_ID);
		headingLabel = new Label(HEADING_LABEL_MARKUP_ID, new StringResourceModel(HEADING_LABEL_ADD_MODE_RESOURCE_ID, this, null));
		nameTextField = new RequiredTextField(NAME_TEXTFIELD_MARKUP_ID);
		typeDropDownChoice = (DropDownChoice) new DropDownChoice(TYPE_DROPDOWNCHOICE_MARKUP_ID, calendarTypes).setRequired(true);
		if(calendarTypes.size() > 0) {
			editCalendar.setType(calendarTypes.get(0));
		}

		urlTextField = new RequiredTextField(URL_TEXTFIELD_MARKUP_ID);
		urlTextField.add(new UrlValidator());
		//urlTextField.add(new PatternValidator(URL_FIELD_REGEX_PATTERN));

		usernameTextField = new TextField(USERNAME_TEXTFIELD_MARKUP_ID);

		passwordTextField = new PasswordTextField(PASSWORD_TEXTFIELD_MARKUPID);
		passwordTextField.setRequired(false);
		passwordTextField.setResetPassword(false);

		timeZoneDropDownChoice = this.createTimeZoneDropDown(TIMEZONE_DROPDOWNCHOICE_MARKUP_ID);
		discardLink = new Link(DISCARD_LINK_MARKUP_ID){

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				onDiscard();
			}

		};
		submitButton = new Button(SUBMIT_BUTTON_MARKUP_ID, new StringResourceModel(SUBMIT_BUTTON_ADD_MODE_RESOURCE_ID, this, null));
	}

	/**
	 * Creates a dropdown for the timzone
	 */
	private DropDownChoice createTimeZoneDropDown(String markupId) {
		List<Long> timeZoneList = new ArrayList<Long>();

		String[] id = TimeZone.getAvailableIDs();

		for(String s : id){
			Long timeZone = TimeZone.getTimeZone(s).getRawOffset() / CalendarUtils.getHourInMs();
			//Filter out general GMT time
			if(s.contains("GMT") && !timeZoneList.contains(timeZone)){
				timeZoneList.add(timeZone);
			}
		}

		timeZoneDropDownChoice = new DropDownChoice(markupId, new PropertyModel((Calendar)this.calendarModel.getObject(), "offSetTo"), timeZoneList, new IChoiceRenderer() {

			private static final long serialVersionUID = 1L;

			// Gets the display value that is visible to the end user.
	        public String getDisplayValue(Object object) {

	            Long offSet = ((Long) object) ;

	            int rawOffset = (int) ((offSet.floatValue() * CalendarUtils.getHourInMs()) / 60000);
	            int hours = rawOffset / 60;
	            int minutes = Math.abs(rawOffset) % 60;
	            String hrStr = "";

						if (Math.abs(hours) < 10) {
							if (hours < 0) {
								hrStr = "-0" + Math.abs(hours);
							} else {
								hrStr = "0" + Math.abs(hours);
							}
						} else {
							hrStr = Integer.toString(hours);
						}

						String minStr = (minutes < 10) ? ("0" + Integer
								.toString(minutes)) : Integer.toString(minutes);
						String str = "GMT " + ((offSet >= 0) ? "+" : "")
								+ hrStr + ":" + minStr;

						return str;
					}

					// Gets the value that is invisble to the end user, and that is
				// used as the selection id.
					public String getIdValue(Object object, int index) {
						return ((Long) object).toString();
					}
				});
		return timeZoneDropDownChoice;
	}

	/**
	 * Implementing class must define this method to handle the redirect of the
	 * discard button
	 */
	protected abstract void onDiscard();

	// Custom Methods

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
	 */
	@Override
	protected void onSubmit() {
		persistCalendar(this.editCalendar);
	}

	/**
	 * Implementing class must define this method to handle the persisting of
	 * the calendar
	 * 
	 * @param storeCalendar
	 */
	protected abstract void persistCalendar(Calendar storeCalendar);
}
