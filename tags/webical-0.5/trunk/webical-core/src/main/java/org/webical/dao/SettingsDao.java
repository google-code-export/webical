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

package org.webical.dao;

import org.webical.PluginSettings;
import org.webical.Settings;
import org.webical.User;
import org.webical.UserPluginSettings;
import org.webical.UserSettings;

/**
 * Dao to store settings for the user and the system as a whole
 * @author ivo
 *
 */
public interface SettingsDao {

	/**
	 * Retrieve the settings for a user.
	 * @return the {@link UserSettings} or null
	 * @throws DaoException
	 */
	public UserSettings getUserSettings(User user) throws DaoException;

	/**
	 * Store the settings for a {@link User}
	 * @param userSettings the UserSettings
	 * @throws DaoException
	 */
	public void storeUserSettings(UserSettings userSettings) throws DaoException;

	/**
	 * Retrieves the global settings for a plugin
	 * @param pluginClass the fully qualified class of the plugin (should be unique)
	 * @return the {@link PluginSettings} or null
	 * @throws DaoException
	 */
	public PluginSettings getPluginSettings(String pluginClass) throws DaoException;

	/**
	 * Retrieves the plugin settings for a {@link User}
	 * @param pluginClass the fully qualified class of the plugin (should be unique)
	 * @param user the {@link User}
	 * @return the {@link UserPluginSettings} or null
	 * @throws DaoException
	 */
	public UserPluginSettings getUserPluginSettings(String pluginClass, User user) throws DaoException;

	/**
	 * Stores global plugin settings
	 * @param pluginSettings the {@link PluginSettings} to store
	 * @throws DaoException
	 */
	public void storePluginSettings(PluginSettings pluginSettings) throws DaoException;

	/**
	 * Stores per-user plugin settings
	 * @param userPluginSettings the {@link UserPluginSettings} to store
	 * @throws DaoException
	 */
	public void storeUserPluginSettings(UserPluginSettings userPluginSettings) throws DaoException;

	/**
	 * Removes {@link Settings} or a subclass like {@link PluginSettings} or {@link UserPluginSettings}
	 * @param settings the {@link Settings} or a subclass thereof
	 * @throws DaoException
	 */
	public void removeSettings(Settings settings) throws DaoException;
}
