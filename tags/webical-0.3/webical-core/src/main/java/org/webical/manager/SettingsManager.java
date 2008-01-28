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

package org.webical.manager;

import org.webical.PluginSettings;
import org.webical.Settings;
import org.webical.User;
import org.webical.UserPluginSettings;

/**
 * Interface for the {@link Settings} related operation
 * @author ivo
 *
 */
public interface SettingsManager {

	/**
	 * Retrieves the global settings for a plugin
	 * @param pluginClass the fully qualified class of the plugin (should be unique)
	 * @return the {@link PluginSettings} or null
	 * @throws WebicalException
	 */
	public PluginSettings getPluginSettings(String pluginClass) throws WebicalException;
	
	/**
	 * Retrieves the plugin settings for a {@link User}
	 * @param pluginClass the fully qualified class of the plugin (should be unique)
	 * @param user the {@link User}
	 * @return the {@link UserPluginSettings} or null
	 * @throws WebicalException
	 */
	public UserPluginSettings getUserPluginSettings(String pluginClass, User user) throws WebicalException;
	
	/**
	 * Stores global plugin settings
	 * @param pluginSettings the {@link PluginSettings} to store
	 * @throws WebicalException
	 */
	public void storePluginSettings(PluginSettings pluginSettings) throws WebicalException;
	
	/**
	 * Stores per-user plugin settings
	 * @param userPluginSettings the {@link UserPluginSettings} to store
	 * @throws WebicalException
	 */
	public void storeUserPluginSettings(UserPluginSettings userPluginSettings) throws WebicalException;
	
	/**
	 * Removes {@link Settings} or a subclass like {@link PluginSettings} or {@link UserPluginSettings}
	 * @param settings the {@link Settings} or a subclass thereof
	 * @throws WebicalException
	 */
	public void removeSettings(Settings settings) throws WebicalException;
}
