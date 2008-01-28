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

package org.webical.web.app;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.webical.dao.encryption.EncrypterNotSetRuntimeException;
import org.webical.manager.EncryptionManager;
import org.webical.plugin.PluginException;
import org.webical.plugin.classloading.DuplicateClassPathEntryException;
import org.webical.plugin.classloading.PluginClassResolver;
import org.webical.plugin.jaxb.Plugin;
import org.webical.plugin.registration.PluginRegistration;
import org.webical.plugin.registration.PluginRegistrationStore;
import org.webical.plugin.registration.PluginSystemInitializer;
import org.webical.plugin.registration.RegistrationException;
import org.webical.plugin.registration.PluginRegistration.PluginState;
import org.webical.settings.ApplicationSettingsException;
import org.webical.settings.ApplicationSettingsFactory;
import org.webical.web.pages.ApplicationSettingsConfigurationPage;
import org.webical.web.pages.BasePage;
import org.webical.web.pages.ConfigurationLoginPage;
import org.webical.web.pages.ErrorPage;
import org.webical.web.pages.NotInitializedPage;
import org.webical.web.pages.PageExpiredErrorPage;

/**
 * The main wicket application class
 * @author ivo
 *
 */
public class WebicalWebApplication extends WebApplication implements ConfigurableWebApplication, PluginRegistrationStore {

	private static final String PATH_SEPERATOR = "/";

	private static final String CONFIGURATION_PAGE_NAME = "configuration";
	private static final String CONFIGURATION_MOUNT_PATH = PATH_SEPERATOR + CONFIGURATION_PAGE_NAME;

	private static final String CALENDAR_PAGE_NAME = "calendar";
	private static final String CALENDAR_MOUNT_PATH = PATH_SEPERATOR + CALENDAR_PAGE_NAME;

	private static Log log = LogFactory.getLog(WebicalWebApplication.class);

	// Set the mode the application is running in: deployment or development
	private String configurationMode;

	//Keeps track of the initialization state
	private boolean initialized = false;

	//List of addidional resourcepaths
	private List<String> resourcePaths = new ArrayList<String>();

	//Custom ClassResolver to load the plugin classes
	private PluginClassResolver pluginClassResolver = new PluginClassResolver();

	private List<PluginRegistration> pluginRegistrations;

	/** Set by Spring **/
	private PluginSystemInitializer pluginSystemInitializer;

	/** Set by Spring **/
	private EncryptionManager encryptionManager;

	/** Set by Spring **/
	private String fixedUserId;

	/** Set by Spring **/
	private boolean autoInitialize = false;

	/**
	 * Returns the homepage
	 * @see wicket.Application#getHomePage()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class getHomePage() {
		return BasePage.class;
	}

	/**
	 * Returns our WebicalSession
	 * @see org.apache.wicket.protocol.http.WebApplication#newSession(org.apache.wicket.Request, org.apache.wicket.Response)
	 */
	@Override
	public Session newSession(Request request, Response response) {
		return new WebicalSession(request, fixedUserId);
	}

	/**
	 * Sets the errorpage
	 * Adds the aditional resource paths
	 * @see org.apache.wicket.protocol.http.WebApplication#init()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void init() {
		super.init();

		// set configuration mode
		configurationMode = getConfigurationType();

		mountBookmarkablePage(CONFIGURATION_MOUNT_PATH, ApplicationSettingsConfigurationPage.class);
		mountBookmarkablePage(CALENDAR_MOUNT_PATH, BasePage.class);

		//Add ComponentInjector (AnnotSpringWebApplication is deprecated)
		addComponentInstantiationListener(new SpringComponentInjector(this));

		//Add custom timeout page
		getApplicationSettings().setPageExpiredErrorPage(PageExpiredErrorPage.class);

		//Set a custom ClassResolver to load the plugin classes
		if(log.isDebugEnabled()) {
			log.debug("Setting the wicket ClassResolver to class: " + PluginClassResolver.class);
		}
		getApplicationSettings().setClassResolver(pluginClassResolver);

		//Set error page
		getApplicationSettings().setInternalErrorPage(ErrorPage.class);

		// Set the correct debug settings, depending on the configuration mode
		if (configurationMode != null && configurationMode.equalsIgnoreCase(DEPLOYMENT)) {
			getDebugSettings().setAjaxDebugModeEnabled(false);
			getMarkupSettings().setStripWicketTags(true);
			getMarkupSettings().setStripXmlDeclarationFromOutput(true);
		} else {
			getDebugSettings().setAjaxDebugModeEnabled(false);
			getDebugSettings().setOutputMarkupContainerClassName(true);
		}

		//getMarkupSettings().setStripWicketTags(true);
		//getMarkupSettings().setStripXmlDeclarationFromOutput(true);
	}

	/* (non-Javadoc)
	 * @see org.webical.web.app.ConfigurableWebApplication#isConfigured()
	 */
	public boolean isConfigured() {
		try {
			return ApplicationSettingsFactory.getInstance().getApplicationSettings() != null && encryptionManager.isInitialized();
		} catch (ApplicationSettingsException e) {
			throw new WebicalWebAplicationException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.web.app.ConfigurableWebApplication#isInitialized()
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/* (non-Javadoc)
	 * @see org.webical.web.app.ConfigurableWebApplication#configurationComplete()
	 */
	public void configurationComplete() {
		log.debug("System initialization requested");
		if(initialized) {
			log.debug("Hmm, already initialized. Just skipping this one");
			return;
		}
		//Setup plugins
		try {
			pluginSystemInitializer.setupPlugins();
		} catch (Exception exception) {
			throw new WebicalWebAplicationException(exception);
		}

		log.info("Initializing webical application");
		initialized = true;

		//Add the resource paths for our plugins
		log.info("Adding initial resource paths: ");
		Set<String> uniquePaths;
		try {
			uniquePaths = ApplicationSettingsFactory.getInstance().getApplicationSettings().getResourcePaths();
		} catch (ApplicationSettingsException e) {
			throw new WebicalWebAplicationException(e);
		}

		uniquePaths.addAll(resourcePaths);

		for (String path : uniquePaths) {
			addResourcePathToLookup(path);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.plugin.registration.PluginRegistrationStore#addClassRegistration(java.lang.String, java.io.File)
	 */
	public void addClassRegistration(String className, File file) throws RegistrationException {
		try {
			getPluginClassResolver().addClassRegistration(className, file);
		} catch (DuplicateClassPathEntryException e) {
			throw new RegistrationException("Could not register class: " + className, e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.plugin.registration.PluginRegistrationStore#addClassPathResourceRegistration(java.lang.String, java.io.File)
	 */
	public void addClassPathResourceRegistration(String resourceIdentifier, File file) throws RegistrationException {
		try {
			getPluginClassResolver().addClassPathResourceRegistration(resourceIdentifier, file);
		} catch (DuplicateClassPathEntryException e) {
			throw new RegistrationException("Could not register class: " + resourceIdentifier, e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.plugin.registration.PluginRegistrationStore#addJarRegistration(java.io.File)
	 */
	public void addJarRegistration(File jarFile) throws RegistrationException {
		try {
			getPluginClassResolver().addJarFileRegistration(jarFile);
		} catch (PluginException e) {
			throw new RegistrationException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.plugin.registration.PluginRegistrationStore#addResourcePath(java.lang.String)
	 */
	public void addResourcePath(String path) {
		getResourcePaths().add(path);
	}

	/* (non-Javadoc)
	 * @see org.webical.plugin.registration.PluginRegistrationStore#resolvePluginClass(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Class resolvePluginClass(String className) throws ClassNotFoundException {
		return getPluginClassResolver().resolveClass(className);
	}

	/* (non-Javadoc)
	 * @see org.webical.plugin.registration.PluginRegistrationStore#addPluginToRegistry(org.webical.plugin.jaxb.Plugin, org.webical.plugin.registration.PluginRegistration.PluginState, java.lang.String, java.lang.Throwable)
	 */
	public void addPluginToRegistry(Plugin plugin, PluginState pluginState, String message, Throwable throwable) {
		if(plugin == null || pluginState == null) {
			return;
		}
		getPluginRegistrations().add(new PluginRegistration(plugin, pluginState, message, throwable));
	}

	/* (non-Javadoc)
	 * @see org.webical.plugin.registration.PluginRegistrationStore#getPluginRegistrations()
	 */
	public List<PluginRegistration> getPluginRegistrations() {
		if(pluginRegistrations == null) {
			pluginRegistrations = new ArrayList<PluginRegistration>();
		}
		return pluginRegistrations;
	}

	/* (non-Javadoc)
	 * @see wicket.protocol.http.WebApplication#getDefaultRequestCycleFactory()
	 */
	@Override
	public RequestCycle newRequestCycle(Request request, Response response) {

		return new WebRequestCycle(WebicalWebApplication.this, (WebRequest)request, (WebResponse)response) {


			/**
			 * Overriden to check for system configuration and initialization
			 * @see wicket.RequestCycle#onBeginRequest()
			 */
			@Override
			protected void onBeginRequest() {
				//Check if the system was initialized already
				if(!isInitialized()) {
					log.debug("System not initialized yet");
					//Check if configuration is done and the encryption is setup
					if(!isConfigured()) {
						log.debug("System not configured yet");

						//If a user path was requested throw an exception and tell 'm the app needs to be configured
						if(nonConfigurationPageRequested()) {
							throw new RestartResponseException(NotInitializedPage.class);
						}
						//else continue normally as the admin pages where reqested
					} else {
						log.debug("System already configured");
						//if auto initialization is setup do so else just show the NotInitializedPage
						if(autoInitialize) {
							log.debug("Auto initialize");
							configurationComplete();
						} else {
							log.debug("Manual initialization required");
							//If a user path was requested throw an exception and tell 'm the app needs to be initialized
							if(nonConfigurationPageRequested()) {
								throw new RestartResponseException(NotInitializedPage.class);
							}
						}
					}
				}
				//Else just continue normally
				super.onBeginRequest();
			}

			/**
			 * Dicedes what to do with uncaugth runtime exceptions
			 * @see wicket.RequestCycle#onRuntimeException(wicket.Page, java.lang.RuntimeException)
			 */
			@Override
			public Page onRuntimeException(Page page, RuntimeException e) {
				if(e.getClass().equals(EncrypterNotSetRuntimeException.class)) {
					return new ConfigurationLoginPage();
				}

				String unauthorizedMessage = getUnAuthorizedExceptionMessage(e);
				if(unauthorizedMessage != null) {
					//Wrong login, return to login page
					PageParameters parameters = new PageParameters();
					parameters.add(ConfigurationLoginPage.ERROR_MESSAGE_PARAMETER_NAME, unauthorizedMessage);
					return new ConfigurationLoginPage(parameters);
				} else if (configurationMode != null && configurationMode.equalsIgnoreCase(DEPLOYMENT)) {
					//Go to custom error page
					return new ErrorPage(page, e);
				} else {
					//Let wicket create the error page
					return super.onRuntimeException(page, e);
				}
			}

			/**
			 * Tests if an exception is caused by an UnAuthorizedException
			 * @param throwable the excetion to test
			 * @return true if this is the case
			 */
			private String getUnAuthorizedExceptionMessage(Throwable throwable) {
				if(throwable == null) {
					return null;
				}

				if(throwable instanceof UnauthorizedException) {
					return throwable.getMessage()!=null?throwable.getMessage():"Unexpected UnauthorizedException";
				} else if(throwable.getCause() != null) {
					return getUnAuthorizedExceptionMessage(throwable.getCause());
				} else {
					return null;
				}
			}

			/**
			 * Checks if an other page then the configuration page is requested
			 * @return boolean
			 * @author Mattijs Hoitink
			 */
			private boolean nonConfigurationPageRequested() {
				String requestedPath = request.getPath();
				return (requestedPath != null && (requestedPath.equals("") || requestedPath.startsWith(CALENDAR_PAGE_NAME)) && !requestedPath.equals(CONFIGURATION_PAGE_NAME));
			}
		};
	}

	/**
	 * Adds a path to the resource lookup paths
	 * @param path a String representing the path
	 */
	private void addResourcePathToLookup(String path) {
		getResourceSettings().addResourceFolder(path);
		log.info("Added resource lookup path: " + path);
	}

	/**
	 * @return the pluginClassResolver
	 */
	public PluginClassResolver getPluginClassResolver() {
		return pluginClassResolver;
	}

	/**
	 * @return the resourcePaths
	 */
	public List<String> getResourcePaths() {
		return resourcePaths;
	}

	/**
	 * @param pluginSystemInitializer the pluginSystemInitializer to set
	 */
	public void setPluginSystemInitializer(
			PluginSystemInitializer pluginSystemInitializer) {
		this.pluginSystemInitializer = pluginSystemInitializer;
	}

	public EncryptionManager getEncryptionManager() {
		return encryptionManager;
	}

	public void setEncryptionManager(EncryptionManager encryptionManager) {
		this.encryptionManager = encryptionManager;
	}

	public void setFixedUserId(String fixedUserId) {
		this.fixedUserId = fixedUserId;
	}

	/**
	 * @param autoInitialize the autoInitialize to set
	 */
	public void setAutoInitialize(boolean autoInitialize) {
		this.autoInitialize = autoInitialize;
		log.info("Auto initialization turned " + (autoInitialize?"on":"off"));
	}
}
