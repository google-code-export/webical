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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.webical.User;
import org.webical.manager.UserManager;
import org.webical.manager.WebicalException;
import org.webical.settings.ApplicationSettingsException;
import org.webical.settings.ApplicationSettingsFactory;
import org.webical.web.app.WebicalSession;
import org.webical.web.app.WebicalWebAplicationException;
import org.webical.web.component.IAccessibilitySwitchingComponent;
import org.webical.web.components.ajax.util.JavaScriptDetector;
import org.webical.web.event.Extension;
import org.webical.web.event.ExtensionEvent;
import org.webical.web.event.ExtensionHandler;
import org.webical.web.event.ExtensionListener;
import org.webical.web.event.ExtensionListenerRegistrations;
import org.webical.web.event.ExtensionPoint;
import org.webical.web.util.Browser;
import org.webical.web.util.Browser.Agent;

/**
 * AbstractBasePage implements the IAccessibilitySwitchingComponent to include accessibility choices
 * @author ivo
 *
 */
public abstract class AbstractBasePage extends WebPage implements IAccessibilitySwitchingComponent, ExtensionHandler {
	private static Log log = LogFactory.getLog(AbstractBasePage.class);

	//Markup ids
	private static final String DEFAULT_PAGE_TITLE_LABEL_ID = "default_page_title";
	private static final String PAGE_TITLE_MARKUP_ID = "pageTitle";

	/** A list of registered ExtensionListeners */
	private List<ExtensionListener> extensionListeners = new ArrayList<ExtensionListener>();

	/** A list of extensions to add */
	private List<Extension> componentsToAdd = new ArrayList<Extension>();

	/** A list of components to replace originals with */
	private List<Component> componentsToReplace = new ArrayList<Component>();

	/** A Markupcontainer for the default extensions */
	private RepeatingView defaultExtensionPointComponent = new RepeatingView(EXTENSIONS_MARKUP_ID);

	/** Lists passed in the event */
	private List<String> replaceableComponents = new ArrayList<String>();
	private List additionalResources = new ArrayList();
	private Map<String, ExtensionPoint> extensionPoints = new HashMap<String, ExtensionPoint>();

	/** A boolean that ensures the plugins handled only once */
	private boolean eventsHandled;

	// Markup ID'S
	public static final String EXTENSIONS_MARKUP_ID = "extensions";
	private static final String ENABLE_DISABLE_AJAX_LINK_MARKUP_ID = "enableDisableAjaxLink";
	private static final String ENABLE_DISABLE_AJAX_LINK_LABEL_MARKUP_ID = "enableDisableAjaxLinkLabel";
	private static final String DISABLE_AJAX_LABEL = "disable_ajax_label";
	private static final String ENABLE_AJAX_LABEL  = "enable_ajax_label";
	private static final String NO_JAVASCRIPT_LABEL = "no_javascript_label";

	//Css references
	private static final String CSS_TEMPLATE = "css/%s.css";
	private static final String CSS_IE6ANDPREV = "css/ie6andprev.css";

	/** Set by Spring */
	@SpringBean(name="userManager")
	private UserManager userManager;

	/**	Is this session Accessible? **/
	protected boolean accessible = ((WebicalSession)getSession()).isAccessible();

	public AbstractBasePage(Class implementingClass) {
		//initSession();
		addHeaderContributions();

		//Get the extensionListeners from the ExtensionListenersRegistrations
		extensionListeners.addAll(ExtensionListenerRegistrations.getExtensionListenersForExtensionHandlerClass(implementingClass));

		//Add the default extension point
		extensionPoints.put(EXTENSIONS_MARKUP_ID, new ExtensionPoint(defaultExtensionPointComponent, null));
		add(defaultExtensionPointComponent);

		//Add accessibilty link
		Label linkLabel = new Label(ENABLE_DISABLE_AJAX_LINK_LABEL_MARKUP_ID);
		final AjaxLink ajaxLink = new AjaxLink(ENABLE_DISABLE_AJAX_LINK_MARKUP_ID, linkLabel);
		add(ajaxLink);

		if(!accessible) {
			setOutputMarkupId(true);
		}

		//Add the javascript detector
		//TODO this used to be added to the bodycontainer. Does this still work?
		add(new JavaScriptDetector() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void respond(AjaxRequestTarget target) {
				if(log.isDebugEnabled()) {
					log.debug("JavaScript available; Updating the ajax link component");
				}
				((WebicalSession)getSession()).setJavaScriptEnabled(true);
				target.addComponent(ajaxLink);
			}

		});

		//Add a Title to the page (default or user selected)
		IModel labelModel = null;
		String pageTitle;
		try {
			pageTitle = ApplicationSettingsFactory.getInstance().getApplicationSettings().getCustomPageTitle();
		} catch (ApplicationSettingsException e) {
			throw new WebicalWebAplicationException(e);
		}
		if(pageTitle != null && pageTitle.length() > 0) {
			labelModel = new Model(pageTitle);
		} else {
			labelModel = new StringResourceModel(DEFAULT_PAGE_TITLE_LABEL_ID, this, null);
		}
		add(new Label(PAGE_TITLE_MARKUP_ID, labelModel));
	}

	/**
	 * Sets a persisted user in the session
	 * @throws WebicalException
	 */
	protected void initSession() {
		WebicalSession webicalSession = (WebicalSession)getSession();
		String userId = ((WebRequest)getRequest()).getHttpServletRequest().getUserPrincipal().getName();

		if(log.isDebugEnabled()) {
			log.debug("Init session for user: " + userId);
		}
		//Try to get an existing user
		User user = getUser(userId);

		//If no existing user could be found create one
		if(user == null) {
			user = createUser(userId);
		}

		//Set the user in the session
		webicalSession.setUser(user);
	}

	/**
	 * Adds the nessecarry header contributions to the pages head
	 */
	protected void addHeaderContributions() {
		HttpServletRequest request = getWebRequestCycle().getWebRequest().getHttpServletRequest();
		Agent agent = Browser.sniffBrowser(request);
		if (agent == Agent.IE5 || agent == Agent.IE6) {
			add(HeaderContributor.forCss(CSS_IE6ANDPREV));
		}
		if (agent != Agent.IE6 && agent != Agent.GECKO) {
			add(HeaderContributor.forCss(String.format(CSS_TEMPLATE, agent.toString())));
		}
	}

	/**
	 * @param userId Retrieves a User for the given userId
	 * @return a User or null
	 */
	private User getUser(String userId) {
		try {
			return userManager.getUser(userId);
		} catch (WebicalException e) {
			throw new WebicalWebAplicationException("Could not retrieve userinformation for user: " + userId);
		}
	}

	/**
	 * Creates a User and retrieves the persisted copy
	 * @param userId the userId to store
	 * @return a Persisted copy of the user
	 */
	private User createUser(String userId) {
		//Store the new User
		User user = new User();
		user.setUserId(userId);
		try {
			userManager.storeUser(user);
		} catch (WebicalException e) {
			throw new WebicalWebAplicationException("Could not store userinformation for user: " + userId);
		}

		//Retrieve the persisted User
		user = getUser(userId);
		if(user == null) {
			throw new WebicalWebAplicationException("User was not persisted correctly for userID: " + userId);
		}

		return user;
	}

	/**
	 * Sets up the common and (non)accessible components
	 */
	protected void setupComponents() {
		setupCommonComponents();
		if(accessible) {
			setupAccessibleComponents();
		} else {
			setupNonAccessibleComponents();
		}
	}

	  //////////////////////////
	 // Plugin Capabilities ///
	//////////////////////////

	/**
	 * Adds and replaces the extension components
	 */
	protected void setupExtensions() {
		log.info("setupExtensions");
		if(componentsToAdd.size() > 0) {
			//Add new components
			for(Extension extension: componentsToAdd) {
				if(extension.getComponent() != null && extension.getExtensionPoint() != null &&
						extension.getExtensionPoint().getExtendingComponent() != null) {

					extension.getExtensionPoint().getExtendingComponent().add(extension.getComponent());

				} else {
					log.error("Invalid extension");
				}
			}

		}

		//Replace existing components
		for(Component component: componentsToReplace) {
			replace(component);
		}
	}

	/**
	 * Enables the extensionListeners (Plugins) to do their stuff before the panel is setup
	 */
	protected void fireExtensionEventBeforeComponentSetup() {
		if(log.isDebugEnabled()) {
			log.debug("Firing events before component setup");
		}
		for(ExtensionListener extensionListener: extensionListeners) {
			extensionListener.addExtensionsBeforeComponentSetup(new ExtensionEvent(this, replaceableComponents, additionalResources, extensionPoints));
		}
	}

	/**
	 * Enables the extensionListeners (Plugins) to do their stuff after the panel components are setup
	 */
	protected void fireExtensionEventAfterComponentSetup() {
		if(log.isDebugEnabled()) {
			log.debug("Firing events after component setup");
		}
		for(ExtensionListener extensionListener: extensionListeners) {
			extensionListener.addExtensionsAfterComponentSetup(new ExtensionEvent(this, replaceableComponents, additionalResources, extensionPoints));
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.web.event.ExtensionHandler#addExtension(org.webical.web.event.Extension)
	 */
	public void addExtension(Extension extension) {
		componentsToAdd.add(extension);
	}

	/* (non-Javadoc)
	 * @see org.webical.web.event.ExtensionHandler#addReplaceableComponent(java.lang.String)
	 */
	public void addReplaceableComponent(String componentId) {
		replaceableComponents.add(componentId);
	}

	/* (non-Javadoc)
	 * @see org.webical.web.event.ExtensionHandler#addTabToTabbedPanel(wicket.extensions.markup.html.tabs.AbstractTab)
	 */
	public void addTabToTabbedPanel(AbstractTab tab) {
		//Not implemented override to use
		log.warn("NOT IMPLEMENTED FOR THIS PANEL");
	}

	/* (non-Javadoc)
	 * @see org.webical.web.event.ExtensionHandler#replaceExistingComponentWithExtension(wicket.Component)
	 */
	public void replaceExistingComponentWithExtension(Component extensionComponent) {
		componentsToReplace.add(extensionComponent);
	}

	/* (non-Javadoc)
	 * @see org.webical.web.event.ExtensionHandler#getExtensionPoints()
	 */
	public Map<String, ExtensionPoint> getExtensionPoints() {
		return extensionPoints;
	}

	/**
	 * Handles the plugin system
	 * @see org.apache.wicket.Component#onBeforeRender()
	 */
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();

		//Only handle the events on the first render fase
		if(!eventsHandled) {
			log.debug("Setting up plugins");
			fireExtensionEventBeforeComponentSetup();
			setupComponents();
			fireExtensionEventAfterComponentSetup();
			setupExtensions();
			eventsHandled = true;
		}
	}

	  //////////////////////////
	 // Getters and Setters ///
	//////////////////////////

	/**
	 * Used by Spring
	 * @param userManager a UserManager implementation
	 */
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	/**
	 * @return accessible
	 */
	public boolean isAccessible() {
		return accessible;
	}

	/**
	 * @param accessible accessible
	 */
	public void setAccessible(boolean accessible) {
		this.accessible = accessible;
	}

	/**
	 * Supporting class for the ajax switch feature
	 * @author ivo
	 *
	 */
	private class AjaxLink extends Link {
		private static final long serialVersionUID = 1L;

		private Label linkLabel;

		public AjaxLink(String id, Label linkLabel) {
			super(id);
			this.linkLabel = linkLabel;

			//Allow ajax refresh
			setOutputMarkupId(true);

			//Disable the switch (gets enabled if javascript is active)
			disableSwitch();

			//Add the label
			this.add(linkLabel);

			log.debug("Accessible session: " + ((WebicalSession)getSession()).isAccessible());
		}

		/* (non-Javadoc)
		 * @see wicket.markup.html.link.Link#onClick()
		 */
		@Override
		public void onClick() {
			setupLink();
			setupComponents();
		}


		/* (non-Javadoc)
		 * @see org.apache.wicket.markup.html.link.AbstractLink#onBeforeRender()
		 */
		@Override
		protected void onBeforeRender() {
			super.onBeforeRender();
			WebicalSession webicalSession = ((WebicalSession)getSession());

			if(webicalSession.isJavaScriptEnabled()) {
				enableSwitch();
			} else {
				disableSwitch();
			}
		}

		/**
		 * Sets up the link in the rigth state
		 */
		protected void setupLink() {
			//Choose wheter to setup ajax
			if(((WebicalSession)getSession()).isAccessible()) {
				enableAjax();
			} else {
				disableAjax();
			}
		}

		/**
		 * Disables Ajax
		 */
		private void enableAjax() {
			//Enable ajax
			if(log.isDebugEnabled()) {
				log.debug("Enabling ajax");
			}

			((WebicalSession)getSession()).setAccessible(false);
			linkLabel.setModel(new StringResourceModel(DISABLE_AJAX_LABEL, this, null));
			accessible = false;
		}

		/**
		 * Enables Ajax
		 */
		private void disableAjax() {
			//Disable ajax
			if(log.isDebugEnabled()) {
				log.debug("Disabling ajax");
			}

			((WebicalSession)getSession()).setAccessible(true);
			linkLabel.setModel(new StringResourceModel(ENABLE_AJAX_LABEL, this, null));
			accessible = true;
		}

		/**
		 * Disables the switch and turns ajax off
		 */
		private void disableSwitch() {
			if(log.isDebugEnabled()) {
				log.debug("disabling ajax switch and ajax");
			}

			//Disable the link
			this.setEnabled(false);

			((WebicalSession)getSession()).setAccessible(true);
			linkLabel.setModel(new StringResourceModel(NO_JAVASCRIPT_LABEL, this, null));
			accessible = true;
		}

		/**
		 * Enables the switch but does not switch Ajax on or off
		 */
		private void enableSwitch() {
			if(log.isDebugEnabled()) {
				log.debug("enabling ajax switch");
			}

			//Enable the link
			this.setEnabled(true);

			//Set the rigth label
			if(((WebicalSession)getSession()).isAccessible()) {
				linkLabel.setModel(new StringResourceModel(ENABLE_AJAX_LABEL, this, null));
			} else {
				linkLabel.setModel(new StringResourceModel(DISABLE_AJAX_LABEL, this, null));
			}
		}

	}

}
