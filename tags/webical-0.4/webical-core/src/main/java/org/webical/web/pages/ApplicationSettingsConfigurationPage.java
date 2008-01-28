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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.webical.ApplicationSettings;
import org.webical.manager.ApplicationSettingsManager;
import org.webical.manager.EncryptionManager;
import org.webical.manager.WebicalException;
import org.webical.web.app.ConfigurableWebApplication;
import org.webical.web.app.UnauthorizedException;
import org.webical.web.app.WebicalWebAplicationException;
import org.webical.web.component.admin.PluginRegistrationsListPanel;
import org.webical.web.component.behavior.FormComponentValidationStyleBehavior;

/**
 * Page to set up the application wide settings
 * @author ivo
 *
 */
public class ApplicationSettingsConfigurationPage extends WebPage {
	//Page parameters
	public static final String PASSWORD_PARAMETER_NAME = "password";
	public static final String USERNAME_PARAMETER_NAME = "username";
	
	//Markup ids
	private static final String PAGE_TITLE_MARKUP_ID = "pageTitle";
	private static final String FEEDBACK_PANEL_MARKUP_ID = "feedbackPanel";
	private static final String STATUS_MESSAGE_LABEL_MARKUP_ID = "statusMessageLabel";
	private static final String APPLICATION_SETTINGS_FORM_MARKUP_ID = "applicationSettingsForm";
	private static final String RESOURCE_PATH_LIST_CONTAINER_MARKUP_ID = "resourcePathListContainer";
	private static final String RESOURCE_PATH_ADD_CONFIGURATION_ITEM_PANEL_MARKUP_ID = "resourcePathAddConfigurationItemPanel";
	private static final String PLUGIN_PATH_LIST_CONTAINER_MARKUP_ID = "pluginPathListContainer";
	private static final String PLUGIN_PATH_ADD_CONFIGURATION_ITEM_PANEL_MARKUP_ID = "pluginPathAddConfigurationItemPanel";
	private static final String PLUGIN_REGISTRATIONS_LIST_PANEL_MARKUP_ID = "pluginRegistrationsListPanel";
	private static final String INITIALIZE_SYSTEM_LINK_MARKUP_ID = "initializeSystemLink";
	private static final String GO_TO_CALENDAR_LINK_MARKUP_ID = "goToCalendarLink";
	private static final String LOGOUT_LINK_MARKUP_ID = "adminLogoutLink";
	
	//Resource ids
	private static final String PAGE_TITLE_RESOURCE_ID = "page_title";
	private static final String ERROR_MESSAGE_RESOURCE_ID = "error_message";
	private static final String UNAUTHORIZED_MESSAGE_RESOURCE_ID = "unauthorized_message";	
	
	//Default values
	private static final int DEFAULT_REFRESH_TIME = 5000;
	private static final String DEFAULT_PACKAGE_EXTENSION = ".zip";
	private static final String DEFAULT_PLUGIN_WORK_DIRECTORY = "${java.io.tmpdir}";
	private static final boolean DEFAULT_CLEANUP_STATE = true;
	
	// Panel components
	private Label pageTitleLabel, statusMessageLabel;
	private Link initializeSystemLink, goToCalendarLink, adminLogoutLink;
	private FeedbackPanel feedbackPanel;
	private ApplicationSettingsForm applicationSettingsForm;
	private AddConfigurationItemPanel resourcePathConfigurationPanel, pluginPathConfigurationPanel;
	private PluginRegistrationsListPanel pluginRegistrationListPanel;
	private StringConfigurationListContainer pluginPathListContainer, resourcePathListContainer;
	
	private static final long serialVersionUID = 1L;

	/**
	 * Reference to the ApplicationSettingsManager (Set by Spring)
	 */
	@SpringBean(name="applicationSettingsManager")
	private ApplicationSettingsManager applicationSettingsManager;
	
	/**
	 * Reference to the EncryptionManager (Set by Spring)
	 */
	@SpringBean(name="encryptionManager")
	private EncryptionManager encryptionManager;
	
	/** The ApplicationSettings to edit */
	private ApplicationSettings applicationSettings;
	
	/** Configuration item lists */
	private List<StringConfigurationItem> pluginPathItems;
	private List<StringConfigurationItem> resourcePathItems;
	
	/** the passphrase for encryption **/
	IModel passphraseModel;
	
	/** A status message model */
	private IModel statusMessageLabelModel;
	
	/**
	 * Default constructor - sets up the components
	 */
	public ApplicationSettingsConfigurationPage() {
		try {
			applicationSettings = applicationSettingsManager.getApplicationSettings();
		} catch (WebicalException e) {
			statusMessageLabelModel = new StringResourceModel(ERROR_MESSAGE_RESOURCE_ID, this, null);
		}
		
		if(applicationSettings == null) {
			applicationSettings = new ApplicationSettings();
			applicationSettings.setCalendarRefreshTimeMs(DEFAULT_REFRESH_TIME);
			applicationSettings.setPluginCleanupEnabled(DEFAULT_CLEANUP_STATE);
			applicationSettings.setPluginPackageExtension(DEFAULT_PACKAGE_EXTENSION);
			applicationSettings.setPluginWorkPath(DEFAULT_PLUGIN_WORK_DIRECTORY);
		} else {
			//Redirect, first login
			setResponsePage(new ConfigurationLoginPage());
		}
		
		setupComponents();
	}
	
	
	/**
	 * @param parameters the pageparameters containing the username and password
	 */
	public ApplicationSettingsConfigurationPage(PageParameters parameters) {
		super(parameters);
		try {
			applicationSettings = applicationSettingsManager.getApplicationSettings();
		} catch (WebicalException e) {
			statusMessageLabelModel = new StringResourceModel(ERROR_MESSAGE_RESOURCE_ID, this, null);
		}
		if(applicationSettings == null) {
			//Redirect, first run
			setResponsePage(new ApplicationSettingsConfigurationPage());
		}
		
		//Authorize
		checkCredentials(parameters.getString(USERNAME_PARAMETER_NAME), parameters.getString(PASSWORD_PARAMETER_NAME));
		
		setupComponents();
		
	}
	
	/**
	 * Authorizes the user
	 * @param username the username
	 * @param password the password
	 * @throws UnauthorizedException on invalid login
	 */
	private void checkCredentials(String username, String password) throws UnauthorizedException {
		if(username == null || password == null || username.length() == 0 || password.length() ==0) {
			throw new UnauthorizedException("Username and password should be filled out");
		}
		
		if(!applicationSettings.getConfigurationUsername().equals(username) || !applicationSettings.getConfigurationPassword().equals(password)) {
			throw new UnauthorizedException(new StringResourceModel(UNAUTHORIZED_MESSAGE_RESOURCE_ID, this, null).getString());
		}
	}



	/**
	 * Adds all components
	 */
	protected void setupComponents() {
		//Add working lists
		pluginPathItems = setToStringConfigurationItemList(applicationSettings.getPluginPaths());
		resourcePathItems = setToStringConfigurationItemList(applicationSettings.getResourcePaths());
		
		if(statusMessageLabelModel == null) {
			statusMessageLabelModel = new Model("");
		}
		
		// Add configuration form
		pageTitleLabel = new Label(PAGE_TITLE_MARKUP_ID, new StringResourceModel(PAGE_TITLE_RESOURCE_ID, this, null));
		statusMessageLabel = new Label(STATUS_MESSAGE_LABEL_MARKUP_ID, statusMessageLabelModel);
		feedbackPanel = new FeedbackPanel(FEEDBACK_PANEL_MARKUP_ID);
		applicationSettingsForm = new ApplicationSettingsForm(APPLICATION_SETTINGS_FORM_MARKUP_ID);
		
		applicationSettingsForm.add(new FormComponentValidationStyleBehavior());
		
		add(pageTitleLabel);
		add(statusMessageLabel);		
		add(feedbackPanel);
		add(applicationSettingsForm);
		
		//Add lists
		pluginPathListContainer = new StringConfigurationListContainer(PLUGIN_PATH_LIST_CONTAINER_MARKUP_ID, pluginPathItems);
		pluginPathConfigurationPanel = new AddConfigurationItemPanel(PLUGIN_PATH_ADD_CONFIGURATION_ITEM_PANEL_MARKUP_ID, pluginPathItems, pluginPathListContainer);
		
		resourcePathListContainer = new StringConfigurationListContainer(RESOURCE_PATH_LIST_CONTAINER_MARKUP_ID, resourcePathItems);
		resourcePathConfigurationPanel = new AddConfigurationItemPanel(RESOURCE_PATH_ADD_CONFIGURATION_ITEM_PANEL_MARKUP_ID, resourcePathItems, resourcePathListContainer);
		
		pluginRegistrationListPanel = new PluginRegistrationsListPanel(PLUGIN_REGISTRATIONS_LIST_PANEL_MARKUP_ID);
		
		add(pluginPathListContainer);
		add(pluginPathConfigurationPanel);
		add(resourcePathListContainer);
		add(resourcePathConfigurationPanel);
		add(pluginRegistrationListPanel);
		
		// Add otions links
		initializeSystemLink = new Link(INITIALIZE_SYSTEM_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;
			
			private boolean isSystemConfigured() {
				return ((ConfigurableWebApplication) getApplication()).isConfigured();
			}
			
			private boolean isSystemInitialized() {
				return ((ConfigurableWebApplication) getApplication()).isInitialized();
			}

			@Override
			public void onClick() {
				if(!isSystemConfigured() || isSystemInitialized()) {
					//Sanity check
					throw new WebicalWebAplicationException("How did you manage to click this link.");
				} else {
					((ConfigurableWebApplication)getApplication()).configurationComplete();
				}
				
			}

			/* (non-Javadoc)
			 * @see wicket.markup.html.link.Link#isEnabled()
			 */
			@Override
			public boolean isEnabled() {
				return isSystemConfigured() && !isSystemInitialized();
			}
			
		};
		initializeSystemLink.setRedirect(false);		
		
		goToCalendarLink = new Link(GO_TO_CALENDAR_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				ApplicationSettingsConfigurationPage.this.setResponsePage(BasePage.class);
			}
			
		};
		adminLogoutLink = new Link(LOGOUT_LINK_MARKUP_ID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				getSession().invalidate();
				ApplicationSettingsConfigurationPage.this.setResponsePage(BasePage.class);
			}
			
		};
		
		add(initializeSystemLink);
		add(goToCalendarLink);
		add(adminLogoutLink);
	}
	
	/////////////////////
	/// Inner classes ///
	/////////////////////
	
	/**
	 * The main form, holds all configuration items
	 * @author ivo
	 *
	 */
	private class ApplicationSettingsForm extends Form {
		//Markup ids 
		private static final String PLUGIN_CLEANUP_ENABLED_MARKUP_ID = "pluginCleanupEnabled";
		private static final String CALENDAR_REFRESH_TIME_MS_MARKUP_ID = "calendarRefreshTimeMs";
		private static final String PLUGIN_PACKAGE_EXTENSION_MARKUP_ID = "pluginPackageExtension";
		private static final String PLUGIN_WORK_PATH_MARKUP_ID = "pluginWorkPath";
		private static final String CUSTOM_PAGE_TITLE_MARKUP_ID = "customPageTitle";
		private static final String PASSPHRASE_FIELD_MARKUP_ID = "passphraseField";
		private static final String CONFIGURATION_PASSWORD_MARKUP_ID = "configurationPassword";
		private static final String CONFIGURATION_USERNAME_MARKUP_ID = "configurationUsername";
		private static final String SUBMIT_BUTTON_MARKUP_ID = "submitButton";
		
		//Resource ids
		private static final String PLUGIN_CLEANUP_ENABLED_PROPERTY_NAME = "pluginCleanupEnabled";
		private static final String CALENDAR_REFRESH_TIME_MS_PROPERTY_NAME = "calendarRefreshTimeMs";
		private static final String PLUGIN_PACKAGE_EXTENSION_PROPERTY_NAME = "pluginPackageExtension";
		private static final String PLUGIN_WORK_PATH_PROPERTY_NAME = "pluginWorkPath";
		private static final String CUSTOM_PAGE_TITLE_PROPERTY_NAME = "customPageTitle";
		private static final String CONFIGURATION_PASSWORD_PROPERTY_NAME = "configurationPassword";
		private static final String CONFIGURATION_USERNAME_PROPERTY_NAME = "configurationUsername";
		private static final String SUBMIT_BUTTON_LABEL_RESOURCE_ID = "submit_button_label";
		
		// Form components
		private TextField pageTitleTextField;
		private RequiredTextField pluginWorkPathTextField, pluginPackageExtensionTextField, calendarRefreshTimeTextField, configurationUsername;
		private CheckBox pluginCleanup;
		private PasswordTextField configurationPasswordField, passphraseField;
		private Button submitButton;
		
		private static final long serialVersionUID = 1L;
		
		/**
		 * @param id the id used in the markup
		 */
		public ApplicationSettingsForm(String id) {
			super(id);
			setupComponents();
		}
		
		/**
		 * Adds all components
		 */
		private void setupComponents() {
			//Add simple fields
			pageTitleTextField = new TextField(CUSTOM_PAGE_TITLE_MARKUP_ID, new PropertyModel(applicationSettings, CUSTOM_PAGE_TITLE_PROPERTY_NAME));
			
			pluginWorkPathTextField = new RequiredTextField(PLUGIN_WORK_PATH_MARKUP_ID, new PropertyModel(applicationSettings, PLUGIN_WORK_PATH_PROPERTY_NAME));
			//pluginWorkPathTextField.add(new FormComponentValidationStyleBehavior());
			
			pluginPackageExtensionTextField = new RequiredTextField(PLUGIN_PACKAGE_EXTENSION_MARKUP_ID, new PropertyModel(applicationSettings, PLUGIN_PACKAGE_EXTENSION_PROPERTY_NAME));
			//pluginPackageExtensionTextField.add(new FormComponentValidationStyleBehavior());
			
			calendarRefreshTimeTextField = new RequiredTextField(CALENDAR_REFRESH_TIME_MS_MARKUP_ID, new PropertyModel(applicationSettings, CALENDAR_REFRESH_TIME_MS_PROPERTY_NAME), Integer.class);
			//calendarRefreshTimeTextField.add(new ValidationStyleBehavior());
			
			pluginCleanup = new CheckBox(PLUGIN_CLEANUP_ENABLED_MARKUP_ID, new PropertyModel(applicationSettings, PLUGIN_CLEANUP_ENABLED_PROPERTY_NAME));
			
			configurationUsername = new RequiredTextField(CONFIGURATION_USERNAME_MARKUP_ID, new PropertyModel(applicationSettings, CONFIGURATION_USERNAME_PROPERTY_NAME));
			//configurationUsername.add(new ValidationStyleBehavior());
			
			configurationPasswordField = new PasswordTextField(CONFIGURATION_PASSWORD_MARKUP_ID, new PropertyModel(applicationSettings, CONFIGURATION_PASSWORD_PROPERTY_NAME));
			//configurationPasswordField.add(new ValidationStyleBehavior());
			
			passphraseField = new PasswordTextField(PASSPHRASE_FIELD_MARKUP_ID, (passphraseModel = new Model()));
			//Only set required on first run
			//ValidationStyleBehavior passphraseValidator = new ValidationStyleBehavior();
			try {
				if(encryptionManager.isConfigured()) {
					passphraseField.setRequired(false);		
					//passphraseField.remove(passphraseValidator);
				} else {			
					//passphraseField.add(passphraseValidator);
				}
			} catch (WebicalException e) {
				throw new WebicalWebAplicationException(e);
			}
			submitButton = new Button(SUBMIT_BUTTON_MARKUP_ID, new StringResourceModel(SUBMIT_BUTTON_LABEL_RESOURCE_ID, this, null));
			
			add(pageTitleTextField);
			add(pluginWorkPathTextField);
			add(pluginPackageExtensionTextField);
			add(calendarRefreshTimeTextField);
			add(pluginCleanup);
			add(configurationUsername);
			add(configurationPasswordField);			
			add(passphraseField);			
			add(submitButton);
			
		}

		/** 
		 * Add the StringConfigurationItems to the ApplicationSettings
		 * and store it.
		 * @see wicket.markup.html.form.Form#onSubmit()
		 */
		@SuppressWarnings("unchecked")
		@Override
		protected void onSubmit() {
			//add the lists
			applicationSettings.getPluginPaths().clear();
			applicationSettings.getPluginPaths().addAll(stringConfigurationItemListToSet(pluginPathItems));
			applicationSettings.getResourcePaths().clear();
			applicationSettings.getResourcePaths().addAll(stringConfigurationItemListToSet(resourcePathItems));
			
			try {
				//Store application settings
				applicationSettingsManager.storeApplicationSettings(applicationSettings);
				((ConfigurableWebApplication)getApplication()).configurationComplete();
				
				//Initialize the EncryptionFactoryBean if neccesarry
				String passphrase = (String)passphraseModel.getObject();
				if(passphrase != null && passphrase.length() > 0) {
					encryptionManager.updateEncryptionPassphrase(passphrase);
				}
			} catch (WebicalException e) {
				setResponsePage(new ErrorPage());
			}
			
			setResponsePage(BasePage.class);
		}
		
	}
	
	/**
	 * List of String configurationItems
	 * @author ivo
	 *
	 */
	private class StringConfigurationListContainer extends WebMarkupContainer {
		private static final String VALUE_PROPERTY_NAME = "value";
		private static final String VALUE_MARKUP_ID = "value";
		private static final String CHECKED_PROPERTY_NAME = "checked";
		private static final String ITEM_CHECKBOX_MARKUP_ID = "check";
		private static final long serialVersionUID = 1L;

		/**
		 * @param id the id used in the markup
		 * @param workList a inner reference to the list to work on
		 */
		public StringConfigurationListContainer(String id, List<StringConfigurationItem> workList) {
			super(id);
			setOutputMarkupId(true);
			add(new ListView("items", workList) {
				private static final long serialVersionUID = 1L;

				/**
				 * @param item teh listitem
				 */
				@Override
				protected void populateItem(ListItem item) {
					item.add(new AjaxCheckBox(ITEM_CHECKBOX_MARKUP_ID, new PropertyModel(item.getModel(), CHECKED_PROPERTY_NAME)) {
						private static final long serialVersionUID = 1L;

						/** No need to do anything, model gets updated automagically
						 * @see wicket.ajax.markup.html.form.AjaxCheckBox#onUpdate(wicket.ajax.AjaxRequestTarget)
						 */
						@Override
						protected void onUpdate(AjaxRequestTarget target) {}
						
					});
					//TODO add for=""
					item.add(new Label(VALUE_MARKUP_ID, new PropertyModel(item.getModel(), VALUE_PROPERTY_NAME)));
				}
				
			});
		}
		
	}
	
	/**
	 * Panel to control the list (add/remove items)
	 * @author ivo
	 *
	 */
	private class AddConfigurationItemPanel extends WebMarkupContainer {
		//Markup ids
		private static final String ADD_CONFIGURATION_ITEM_FORM_MARKUP_ID = "addConfigurationItemForm";
		private static final String REMOVE_SELECTED_LINK_MARKUP_ID = "removeSelectedLink";
		private static final String SHOW_FORM_LINK_MARKUP_ID = "showFormLink";

		private static final long serialVersionUID = 1L;
		
		/** Visibility toggle so that either the link or the form is visible. */
		private boolean linkVisible = true;
		
		/** List to work on */
		private List<StringConfigurationItem> workingList;
		
		/** List component to control */
		private StringConfigurationListContainer listContainer;

		/**
		 * @param id the id used in the markup
		 * @param workingList the list to work on
		 * @param listContainer the listContainer to refresh
		 */
		public AddConfigurationItemPanel(String id, List<StringConfigurationItem> workingList, StringConfigurationListContainer listContainer) {
			super(id);
			this.workingList = workingList;
			this.listContainer = listContainer;
			setOutputMarkupId(true);
			
			//Add components
			add(new ShowFormLink(SHOW_FORM_LINK_MARKUP_ID));
			add(new RemoveSelectedLink(REMOVE_SELECTED_LINK_MARKUP_ID));
			add(new AddConfigurationItemForm(ADD_CONFIGURATION_ITEM_FORM_MARKUP_ID));
		}
		
		/**
		 * Ajax enabled link to show the form
		 * @author ivo
		 *
		 */
		private final class ShowFormLink extends AjaxFallbackLink {
			private static final long serialVersionUID = 1L;

			/**
			 * @param id the id used in the markup
			 */
			public ShowFormLink(String id) {
				super(id);
			}

			/* (non-Javadoc)
			 * @see wicket.ajax.markup.html.AjaxFallbackLink#onClick(wicket.ajax.AjaxRequestTarget)
			 */
			@Override
			public void onClick(AjaxRequestTarget target) {
				onShowForm(target);
			}
			
			/* (non-Javadoc)
			 * @see wicket.Component#isVisible()
			 */
			@Override
			public boolean isVisible() {
				return linkVisible;
			}
		}
		
		/**
		 * Link to remve selected items in the list
		 * @author ivo
		 *
		 */
		private final class RemoveSelectedLink extends AjaxFallbackLink {
			private static final long serialVersionUID = 1L;

			/**
			 * @param id the id used in the markup
			 */
			public RemoveSelectedLink(String id) {
				super(id);
			}

			/* (non-Javadoc)
			 * @see wicket.ajax.markup.html.AjaxFallbackLink#onClick(wicket.ajax.AjaxRequestTarget)
			 */
			@Override
			public void onClick(AjaxRequestTarget target) {
				onRemoveSelectedConfigurationItems(target);
			}
			
			/* (non-Javadoc)
			 * @see wicket.Component#isVisible()
			 */
			@Override
			public boolean isVisible() {
				return linkVisible;
			}
		}
		
		/**
		 * Form to add new items to the list
		 * @author ivo
		 *
		 */
		private final class AddConfigurationItemForm extends Form {
			//MArkup ids
			private static final String CANCEL_BUTTON_LABEL_RESOURCE_ID = "cancel_button_label";
			private static final String ADD_ITEM_BUTTON_LABEL_RESOURCE_ID = "add_item_button_label"; 
			private static final String CANCEL_BUTTON_MARKUP_ID = "cancel";
			private static final String ADD_BUTTON_MARKUP_ID = "add";
			private static final String VALUE_FIELD_MARKUP_ID = "value";

			private static final long serialVersionUID = 1L;

			/**
			 * @param id the id used in the markup
			 */
			public AddConfigurationItemForm(String id) {
				super(id, new CompoundPropertyModel(new StringConfigurationItem("", false)));
				setOutputMarkupId(true);
				
				add(new TextField(VALUE_FIELD_MARKUP_ID));
				AjaxButton addButton = new AjaxButton(ADD_BUTTON_MARKUP_ID, this) {
					private static final long serialVersionUID = 1L;

					/* (non-Javadoc)
					 * @see wicket.ajax.markup.html.form.AjaxSubmitButton#onSubmit(wicket.ajax.AjaxRequestTarget, wicket.markup.html.form.Form)
					 */
					@Override
					protected void onSubmit(AjaxRequestTarget target, Form form) {
						// retrieve the StringConfigurationItem
						StringConfigurationItem item = (StringConfigurationItem)getParent().getModelObject();

						// add the item
						onAddConfigurationItem(item, target);
					}
				};
				AjaxButton cancelButton = new AjaxButton(CANCEL_BUTTON_MARKUP_ID, this) {
					private static final long serialVersionUID = 1L;

					/* (non-Javadoc)
					 * @see wicket.ajax.markup.html.form.AjaxSubmitButton#onSubmit(wicket.ajax.AjaxRequestTarget, wicket.markup.html.form.Form)
					 */
					@Override
					protected void onSubmit(AjaxRequestTarget target, Form form) {
						onCancelAddConfigurationItem(target);
					}
				};
				
				
				cancelButton.add(new AttributeModifier(VALUE_FIELD_MARKUP_ID, new StringResourceModel(CANCEL_BUTTON_LABEL_RESOURCE_ID, this, null)));
				addButton.add(new AttributeModifier(VALUE_FIELD_MARKUP_ID, new StringResourceModel(ADD_ITEM_BUTTON_LABEL_RESOURCE_ID, this, null)));
				add(cancelButton);
				add(addButton);
			}
			
			@Override
			public boolean isVisible() {
				return !linkVisible;
			}
			
		}
		
		/**
		 * @param target the current ajax target
		 */
		private void onShowForm(AjaxRequestTarget target) {
			linkVisible = false;
			target.addComponent(this);
		}
		
		/**
		 * @param item the item to add to the list
		 * @param target the current ajax target
		 */
		private void onAddConfigurationItem(StringConfigurationItem item, AjaxRequestTarget target) {
			//Add item to working list
			workingList.add(new StringConfigurationItem(item.getValue(), item.isChecked()));
			
			//Clear the model item
			item.setChecked(false);
			item.setValue("");
			
			//Refresh the form and the list
			linkVisible = true;
			target.addComponent(this);
			target.addComponent(listContainer);
		}
		
		/**
		 * @param target the current ajax target
		 */
		private void onCancelAddConfigurationItem(AjaxRequestTarget target) {
			//Show the links
			linkVisible = true;
			target.addComponent(this);
		}
		
		/**
		 * @param target the current ajax target
		 */
		private void onRemoveSelectedConfigurationItems(AjaxRequestTarget target) {
			List<StringConfigurationItem> selectedItems = new ArrayList<StringConfigurationItem>();
			for (Iterator iter = workingList.iterator(); iter.hasNext();) {
				StringConfigurationItem item = (StringConfigurationItem) iter.next();
				if(item.isChecked()) {
					selectedItems.add(item);
				}
			}
			workingList.removeAll(selectedItems);
			
			//Refresh the form and the list
			target.addComponent(this);
			target.addComponent(listContainer);
		}
	}
	
	/**
	 * Wrapper class for items used in the Editable lists
	 * 
	 * @author ivo
	 * 
	 */
	private static class StringConfigurationItem implements Serializable {
		private static final long serialVersionUID = 1L;
		private String value;
		private boolean checked;

		public StringConfigurationItem() {}
		
		public StringConfigurationItem(String value, boolean checked) {
			this.value = value;
			this.checked = checked;
		}

		/**
		 * @return the checked
		 */
		public boolean isChecked() {
			return checked;
		}

		/**
		 * @param checked the checked to set
		 */
		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * @param value the value to set
		 */
		public void setValue(String value) {
			this.value = value;
		}
	}
	
	///////////////////////
	/// Utility methods ///
	///////////////////////
	
	/**
	 * TransForms a set of Strings into a List of StringConfigurationItem
	 * @param set the set to transform
	 * @return the StringConfigurationItems
	 */
	private static List<StringConfigurationItem> setToStringConfigurationItemList(Set<String> set) {
		List<StringConfigurationItem> output = new ArrayList<StringConfigurationItem>();
		for (Iterator iter = set.iterator(); iter.hasNext();) {
			String item = (String) iter.next();
			output.add(new StringConfigurationItem(item!=null?item:"", false));
		}
		return output;
	}
	
	/**
	 * Transforms a List of StringConfigurationItem into a HashSet of Strings
	 * @param list the list to transform
	 * @return the Set
	 */
	private static Set<String> stringConfigurationItemListToSet(List<StringConfigurationItem> list) {
		HashSet<String> output = new HashSet<String>();
		for (StringConfigurationItem item : list) {
			if(item.getValue() != null && item.getValue().length() > 0) {
				output.add(item.getValue());
			}
		}
		return output;
	}
	
	//////////////////////////
	/// Setters for Spring ///
	//////////////////////////
	
	/**
	 * @param applicationSettingsManager the applicationSettingsManager to set
	 */
	public void setApplicationSettingsManager(
			ApplicationSettingsManager applicationSettingsManager) {
		this.applicationSettingsManager = applicationSettingsManager;
	}


	/**
	 * @param encryptionManager the {@link EncryptionManager} to set
	 */
	public void setEncryptionManager(EncryptionManager encryptionManager) {
		this.encryptionManager = encryptionManager;
	}
}
