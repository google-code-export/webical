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

package org.webical.manager.impl;

import org.springframework.beans.factory.InitializingBean;
import org.webical.PluginSettings;
import org.webical.Settings;
import org.webical.User;
import org.webical.UserPluginSettings;
import org.webical.UserSettings;
import org.webical.dao.DaoException;
import org.webical.dao.SettingsDao;
import org.webical.manager.SettingsManager;
import org.webical.manager.WebicalException;

/**
 * Default implementation of {@link SettingsManager}
 * @author ivo
 *
 */
public class SettingsManagerImpl implements SettingsManager, InitializingBean {

	/** Set by Spring */
	private SettingsDao settingsDao;

	/* (non-Javadoc)
	 * @see org.webical.manager.SettingsManager#getUserSettings(org.webical.User)
	 */
	public UserSettings getUserSettings(User user) throws WebicalException {
		try {
			return settingsDao.getUserSettings(user);
		} catch (DaoException e) {
			throw new WebicalException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.manager.SettingsManager#storeUserSettings(org.webical.UserSettings)
	 */
	public void storeUserSettings(UserSettings userSettings) throws WebicalException {
		try {
			settingsDao.storeUserSettings(userSettings);
		} catch (DaoException e) {
			throw new WebicalException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.manager.SettingsManager#getPluginSettings(java.lang.String)
	 */
	public PluginSettings getPluginSettings(String pluginClass) throws WebicalException {
		try {
			return settingsDao.getPluginSettings(pluginClass);
		} catch (DaoException e) {
			throw new WebicalException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.manager.SettingsManager#getUserPluginSettings(java.lang.String, org.webical.User)
	 */
	public UserPluginSettings getUserPluginSettings(String pluginClass, User user) throws WebicalException {
		try {
			return settingsDao.getUserPluginSettings(pluginClass, user);
		} catch (DaoException e) {
			throw new WebicalException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.manager.SettingsManager#removeSettings(org.webical.Settings)
	 */
	public void removeSettings(Settings settings) throws WebicalException {
		try {
			settingsDao.removeSettings(settings);
		} catch (DaoException e) {
			throw new WebicalException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.manager.SettingsManager#storePluginSettings(org.webical.PluginSettings)
	 */
	public void storePluginSettings(PluginSettings pluginSettings) throws WebicalException {
		try {
			settingsDao.storePluginSettings(pluginSettings);
		} catch (DaoException e) {
			throw new WebicalException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.webical.manager.SettingsManager#storeUserPluginSettings(org.webical.UserPluginSettings)
	 */
	public void storeUserPluginSettings(UserPluginSettings userPluginSettings) throws WebicalException {
		try {
			settingsDao.storeUserPluginSettings(userPluginSettings);
		} catch (DaoException e) {
			throw new WebicalException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		if(settingsDao == null) {
			throw new ExceptionInInitializerError("No SettingsDao configured");
		}
	}

	/**
	 * Used by Spring
	 * @param settingsDao the settingsDao to set
	 */
	public void setSettingsDao(SettingsDao settingsDao) {
		this.settingsDao = settingsDao;
	}

}
