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

package org.webical.web.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.webical.web.app.WebicalSession;
import org.webical.web.event.Extension;
import org.webical.web.event.ExtensionEvent;
import org.webical.web.event.ExtensionHandler;
import org.webical.web.event.ExtensionListener;
import org.webical.web.event.ExtensionListenerRegistrations;
import org.webical.web.event.ExtensionPoint;

/**
 * Abstract Panel that serves as a basis for all Panels that need to switch
 * between accessible and Ajax for example
 * @author ivo
 *
 */
public abstract class AbstractBasePanel extends Panel implements IAccessibilitySwitchingComponent, ExtensionHandler {
	public static final String EXTENSIONS_MARKUP_ID = "extensions";

	private static Log log = LogFactory.getLog(AbstractBasePanel.class);

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

	/**	Is this session Accessible? **/
	protected boolean accessible = ((WebicalSession)getSession()).isAccessible();

	/**
	 * Sets up the Panel
	 * @param markupId the id used in the markup
	 */
	public AbstractBasePanel(String markupId, Class implementingClass) {
		super(markupId);
		if(!accessible) {
			setOutputMarkupId(true);
		}

		//Get the extensionListeners from the ExtensionListenersREgistrations
		extensionListeners.addAll(ExtensionListenerRegistrations.getExtensionListenersForExtensionHandlerClass(implementingClass));

		//Add the default extension point
		extensionPoints.put(EXTENSIONS_MARKUP_ID, new ExtensionPoint(defaultExtensionPointComponent, null));
		add(defaultExtensionPointComponent);

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
	/// Extension Methods ////
	//////////////////////////

	/**
	 * Adds and replaces the extension components
	 */
	protected void setupExtensions() {
		//log.info("setupExtensions");
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
			//log.debug("Firing events before component setup");
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
			//log.debug("Firing events after component setup");
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
			//log.info("Setting up plugins");
			fireExtensionEventBeforeComponentSetup();
			setupComponents();
			fireExtensionEventAfterComponentSetup();
			setupExtensions();
			eventsHandled = true;
		}
	}

}