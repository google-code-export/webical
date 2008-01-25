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

package org.webical.web.pages;

import java.io.Serializable;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 * Page to handle the configuration page login
 * @author ivo
 *
 */
public class ConfigurationLoginPage extends WebPage {
	private static final long serialVersionUID = 1L;
	
	//Parameters
	public static final String ERROR_MESSAGE_PARAMETER_NAME = "error_message";
	
	//Markup ids
	private static final String PAGE_TITLE_MARKUP_ID = "pageTitle";
	private static final String SUBMIT_BUTTON_MARKUP_D = "submitButton";
	private static final String FEEDBACK_PANEL_MARKUP_ID = "feedbackPanel";
	private static final String ERROR_MESSAGE_LABEL_MARKUP_ID = "errorMessageLabel";
	private static final String PASSWORD_MARKUP_ID = "password";
	private static final String USERNAME_MARKUP_ID = "username";
	private static final String LOGIN_FORM_MARKUP_ID = "loginForm";
	
	//Resource ids
	private static final String SUBMIT_BUTTON_LABEL_RESOURCE_ID = "submit_button_label";
	private static final String PAGE_TITLE_RESOURCE_ID = "page_title";
	
	//Model containing the error message if any
	private IModel errorMessageModel;
	
	/**
	 * Default constructor
	 */
	public ConfigurationLoginPage() {
		errorMessageModel = new Model("");
		setupComponents();
	}

	/**
	 * @param parameters page parameters containing the error
	 */
	public ConfigurationLoginPage(PageParameters parameters) {
		super(parameters);
		errorMessageModel = new Model(parameters.getString(ERROR_MESSAGE_PARAMETER_NAME));
		setupComponents();
	}
	
	/**
	 * Adds the components to the page
	 */
	private void setupComponents() {
		//Add page title
		add(new Label(PAGE_TITLE_MARKUP_ID, new StringResourceModel(PAGE_TITLE_RESOURCE_ID, this, null)));
		
		//Add form
		Form loginForm =  new Form(LOGIN_FORM_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			/* (non-Javadoc)
			 * @see wicket.markup.html.form.Form#onSubmit()
			 */
			@Override
			protected void onSubmit() {
				PageParameters parameters = new PageParameters();
				parameters.add(ApplicationSettingsConfigurationPage.USERNAME_PARAMETER_NAME, ((Credentials)getModelObject()).getUsername());
				parameters.add(ApplicationSettingsConfigurationPage.PASSWORD_PARAMETER_NAME, ((Credentials)getModelObject()).getPassword());
				setResponsePage(new ApplicationSettingsConfigurationPage(parameters));
			}
		};
		
		loginForm.setModel(new CompoundPropertyModel(new Credentials()));
		loginForm.add(new RequiredTextField(USERNAME_MARKUP_ID));
		loginForm.add(new PasswordTextField(PASSWORD_MARKUP_ID));
		loginForm.add(new Button(SUBMIT_BUTTON_MARKUP_D, new StringResourceModel(SUBMIT_BUTTON_LABEL_RESOURCE_ID, this, null)));
		
		add(loginForm);
		
		//Add feedback components
		add(new Label(ERROR_MESSAGE_LABEL_MARKUP_ID, errorMessageModel));
		add(new FeedbackPanel(FEEDBACK_PANEL_MARKUP_ID));
	}

	/**
	 * Form field values wrapper
	 * @author ivo
	 *
	 */
	private static class Credentials implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private String username;
		private String password;
		
		/**
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}
		/**
		 * @param password the password to set
		 */
		public void setPassword(String password) {
			this.password = password;
		}
		/**
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}
		/**
		 * @param username the username to set
		 */
		public void setUsername(String username) {
			this.username = username;
		}
		
	}
}
