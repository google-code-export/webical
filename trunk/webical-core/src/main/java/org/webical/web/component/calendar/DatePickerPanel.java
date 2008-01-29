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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.webical.web.action.DaySelectedAction;
import org.webical.web.action.IAction;
import org.webical.web.app.WebicalSession;
import org.webical.web.component.AbstractBasePanel;
import org.webical.web.component.behavior.FormComponentValidationStyleBehavior;
import org.webical.web.component.calendar.model.DatePickerModel;

/**
 * Panel to pick a specific date and show it
 *
 * @author Mattijs Hoitink
 *
 */
public abstract class DatePickerPanel extends AbstractBasePanel {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	protected static Class[] PANELACTIONS = new Class[] { };

	/* Markup ID's */
	private static final String DATE_PICKER_FORM_MARKUP_ID = "datePickerForm";
	private static String DAY_HEADER_REPEATER_MARKUP_ID = "datePickerHeaderRepeater";
	private static final String DATE_PICKER_ROW_REPEATER_MARKUP_ID = "datePickerRowRepeater";

	private Label monthLabel;

	/**
	 * Constructor
	 * @param markupId The ID used in the markup
	 * @param model The model to use for the date
	 */
	public DatePickerPanel(String markupId, CompoundPropertyModel datePickerCompoundPropertyModel) {
		super(markupId, DatePickerPanel.class);

		setModel(datePickerCompoundPropertyModel);
	}

	/* (non-Javadoc)
	 * @see org.webical.web.components.IAccessibilitySwitchingComponent#setupCommonComponents()
	 */
	public void setupCommonComponents() {
		//NOT NEEDED
	}

	/* (non-Javadoc)
	 * @see org.webical.web.components.IAccessibilitySwitchingComponent#setupAccessibleComponents()
	 */
	public void setupAccessibleComponents() {
		// Add a feedback panel
		FeedbackPanel datePickerFeedBackPanel = new FeedbackPanel("datePickerFeedbackPanel");
		add(datePickerFeedBackPanel);

		// Add day headers
		RepeatingView dayHeaderRepeater = new RepeatingView(DAY_HEADER_REPEATER_MARKUP_ID);
		dayHeaderRepeater.add(new Label("headerStart", ""));

		GregorianCalendar weekCal = new GregorianCalendar();
		weekCal.set(GregorianCalendar.DAY_OF_WEEK, WebicalSession.getWebicalSession().getUserSettings().getFirstDayOfWeek());
		SimpleDateFormat sdf = new SimpleDateFormat("E", getLocale());
		// TODO mattijs: get the weekdays to show from user settings
		for(int i = 0; i < 7; i++) {
			dayHeaderRepeater.add(new Label("dayHeader" + i, sdf.format(weekCal.getTime()).substring(0, 1)));
			weekCal.add(Calendar.DAY_OF_WEEK, 1);
		}
		add(dayHeaderRepeater);
		
		// Add the date form
		DatePickerForm form = new DatePickerForm(DATE_PICKER_FORM_MARKUP_ID, getModel());
		form.add(new FormComponentValidationStyleBehavior());

        addOrReplace(form);

        renderModelDependantComponents();
	}

	/* (non-Javadoc)
	 * @see org.webical.web.components.IAccessibilitySwitchingComponent#setupNonAccessibleComponents()
	 */
	public void setupNonAccessibleComponents() {
		// NOTHING TO DO (YET?)
	}

	@Override
	protected void onModelChanged() {
		renderModelDependantComponents();
	}

	private void renderModelDependantComponents() {
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM yyyy", getLocale());
		// Add a month label
		monthLabel = new Label("monthLabel", dateFormatter.format(((DatePickerModel) this.getModelObject()).getCurrentDate().getTime()));
		addOrReplace(monthLabel);
		
		DatePickerRowRepeater rowRepeater = new DatePickerRowRepeater(DATE_PICKER_ROW_REPEATER_MARKUP_ID, (DatePickerModel) this.getModelObject()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction(IAction action) {
				DatePickerPanel.this.onAction(action);
			}

        };
        
        addOrReplace(rowRepeater);
	}

	/**
	 * Handle actions generated by this panel.
	 *
	 * @param action The action to handle
	 */
	public abstract void onAction(IAction action);

	/**
	 * Form with the Date selection field
	 * @author Mattijs Hoitink
	 *
	 */
	private class DatePickerForm extends Form {
		private static final long serialVersionUID = 1L;
		
		// Markup ID's
		private static final String CHANGE_DATE_BUTTON_MARKUP_ID = "changeDateButton";
		private static final String CURRENT_DATE_FIELD_MARKUP_ID = "changeDateField";
		// Resource ID's
		private static final String CHANGE_DATE_BUTTON_RESOURCE_ID = "change_date_label";
		
		private DateTextField changeDateTextField;
		private Button changeDateButton;

		/**
		 * Constructor
		 * @param markupId the id used in the markup
		 * @param model a CompoundPropertyModel containing an Input object with the selected date
		 */
		public DatePickerForm(String markupId, IModel model) {
			super(markupId, model);

			changeDateTextField = new DateTextField(CURRENT_DATE_FIELD_MARKUP_ID, WebicalSession.getWebicalSession().getUserSettings().getDateFormat());
			changeDateButton = new Button(CHANGE_DATE_BUTTON_MARKUP_ID, new StringResourceModel(CHANGE_DATE_BUTTON_RESOURCE_ID, this, new Model("Show")));

			add(changeDateTextField);
			add(changeDateButton);
		}

		/* (non-Javadoc)
		 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
		 */
		@Override
		protected void onSubmit() {
			GregorianCalendar dayCal = new GregorianCalendar();
			dayCal.setTime((Date) changeDateTextField.getConvertedInput());
			DatePickerPanel.this.onAction(new DaySelectedAction(dayCal));
		}

	}

}
