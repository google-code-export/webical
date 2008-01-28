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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.webical.User;
import org.webical.UserSettings;
import org.webical.manager.SettingsManager;
import org.webical.manager.UserManager;
import org.webical.manager.WebicalException;

/**
 * The Session used to maintain the user information
 * @author ivo
 *
 */
public class WebicalSession extends WebSession {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(WebicalSession.class);

	/** The User for this Session */
	private User user;

	/** The application settings for the session user */
	private UserSettings userSettings;

	/** Does the client support JavaScript? */
	private boolean javaScriptEnabled;

	/** Is this session accessible? */
	private boolean accessible;

	/** Set by Spring */
	@SpringBean
	private UserManager userManager;

	/** Set by Spring */
	@SpringBean
	private SettingsManager settingsManager;

	/** UserId for development usage **/
	private String fixedUserId;
	
	/**
	 * UserId from the http session
	 */
	private String userId;

	/**
	 * Returns a casted Session
	 * @return WebicalSession the Webical Session
	 */
	public static WebicalSession getWebicalSession() {
		return (WebicalSession) Session.get();
	}

	/**
	 * Constructor
	 * @param request The Request object
	 */
	public WebicalSession(Request request) {
		super(request);

		//Inject this Session
		InjectorHolder.getInjector().inject(this);
	}

	/**
	 * Constructor
	 * @param request The Request object
	 * @param String fixedUserId
	 */
	public WebicalSession(Request request, String fixedUserId) {
		super(request);
		this.fixedUserId = fixedUserId;

		//Inject this Session
		InjectorHolder.getInjector().inject(this);
	}

	/**
	 * Sets a persisted user in the session - Check for a FixedUserId first
	 * @throws WebicalException
	 */
	protected void initSession() {
		if(fixedUserId == null) {
			userId = ((WebRequest)RequestCycle.get().getRequest()).getHttpServletRequest().getUserPrincipal().getName();
			log.debug("Init session for user: " + userId);
		} else {
			log.warn("USING FIXED USER FOR DEVELOPMENT: " + fixedUserId);
			userId = fixedUserId;
		}
	}

	/**
	 * Creates default aplication settings for the given user.
	 * @param user The user
	 * @return defaultUserSettings Default {@link UserSettings}
	 */
	private UserSettings createDefaultSettings(User user) {
		try {
			UserSettings defaultUserSettings = new UserSettings(user);
			defaultUserSettings.createDefaultSettings();

			return defaultUserSettings;
		} catch (WebicalException e) {
			throw new WebicalWebAplicationException("Could not create application settings for user " + user.getUserId());
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
			throw new WebicalWebAplicationException("Could not retrieve user information for user: " + userId);
		}
	}

	/**
	 * Returns the {@link UserSettings} for a given {@link User}
	 * @param user The user
	 * @return The {@link UserSettings} for the given user
	 */
	private UserSettings getUserSettings(User user) {
		try {
			return settingsManager.getUserSettings(user);
		} catch (WebicalException e) {
			throw new WebicalWebAplicationException("Could not retrieve application settings for user: " + user.getUserId());
		}
	}

	/**
	 * Creates a User and retrieves the persisted copy
	 * @param userId the userId to store
	 * @return a Persisted copy of the user
	 */
	private User createUser(User user) {
		//Store the new User
		try {
			userManager.storeUser(user);
		} catch (WebicalException e) {
			throw new WebicalWebAplicationException("Could not store userinformation for user: " + user.getUserId());
		}

		//Retrieve the persisted User
		user = getUser(user.getUserId());
		if(user == null) {
			throw new WebicalWebAplicationException("User was not persisted correctly for userID: " + user.getUserId());
		}

		return user;
	}

	/**
	 * @param id
	 * @return
	 */
	private User newUser(String id) {
		User user = new User();
		user.setUserId(id);
		return user;
	}

	/**
	 * Returns the user and retrieves/creates 'm if necessary
	 * @return the user
	 */
	public User getUser() {
		//Get info from the request cycle and initialize
		if(user == null) {
			initSession();
		} else {
			userId = user.getUserId();
		}
		
		//Try to get an existing user
		user = getUser(userId);

		//If no existing user could be found create one
		if(user == null) {
			log.debug("User not found in database: " + userId + " storing now!");
			user = createUser(newUser(userId));
		}
		return user;
	}

	/**
	 * @param user the user
	 * @deprecated
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Returns the application settings for the given user.
	 * If no application settings are presents, it will try
	 * to create a default set.
	 * @return The application settings for the current user
	 */
	public UserSettings getUserSettings() {
		userSettings = getUserSettings(getUser());

		if(userSettings == null) {
			log.debug("User " + getUser().getUserId() + " has no application settings, creating default");
			userSettings = createDefaultSettings(getUser());
		}
		
		return userSettings;
	}

	/**
	 * Sets the application settings
	 * @param userSettings The application settings
	 */
	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
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
	 * @return
	 */
	public boolean isJavaScriptEnabled() {
		return javaScriptEnabled;
	}

	/**
	 * @param javaScriptEnabled
	 */
	public void setJavaScriptEnabled(boolean javaScriptEnabled) {
		this.javaScriptEnabled = javaScriptEnabled;
	}

	/**
	 * Set by Spring
	 * @param userManager
	 */
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	/**
	 * Set by Spring
	 * @param settingsManager
	 */
	public void setSettingsManager(SettingsManager settingsManager) {
		this.settingsManager = settingsManager;
	}

}
