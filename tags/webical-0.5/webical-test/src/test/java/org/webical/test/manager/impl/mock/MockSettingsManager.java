/*
 *    Webical - http://www.webical.org
 *    Copyright (C) 2012 Cebuned Software
 *
 *    This file is part of Webical.
 *
 *    $Id$
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

package org.webical.test.manager.impl.mock;

import org.webical.PluginSettings;
import org.webical.Settings;
import org.webical.User;
import org.webical.UserPluginSettings;
import org.webical.UserSettings;
import org.webical.manager.SettingsManager;
import org.webical.manager.WebicalException;

public class MockSettingsManager implements SettingsManager {

	public PluginSettings getPluginSettings(String pluginClass) throws WebicalException
	{
		if (m_pluginSettings == null || ! m_pluginSettings.getPluginClass().equals(pluginClass))
		{
			m_pluginSettings = new PluginSettings();
			m_pluginSettings.setPluginClass(pluginClass);
		}
		return m_pluginSettings;
	}
	public void storePluginSettings(PluginSettings pluginSettings) throws WebicalException
	{
		m_pluginSettings = pluginSettings;
	}

	public UserPluginSettings getUserPluginSettings(String pluginClass, User user) throws WebicalException
	{
		if (m_userPluginSettings == null  ||
			(! m_userPluginSettings.getPluginClass().equals(pluginClass))  ||
			(! m_userPluginSettings.getUser().getUserId().equals(user.getUserId())) )
		{
			m_userPluginSettings = new UserPluginSettings();
			m_userPluginSettings.setPluginClass(pluginClass);
			m_userPluginSettings.setUser(user);
		}
		return m_userPluginSettings;
	}
	public void storeUserPluginSettings(UserPluginSettings userPluginSettings) throws WebicalException
	{
		m_userPluginSettings = userPluginSettings;
	}

	public UserSettings getUserSettings(User user) throws WebicalException
	{
		if (m_userSettings == null || ! m_userSettings.getUser().getUserId().equals(user.getUserId()))
		{
			m_userSettings = new UserSettings(user);
			m_userSettings.createDefaultSettings();
		}
		return m_userSettings;
	}
	public void storeUserSettings(UserSettings userSettings) throws WebicalException
	{
		m_userSettings = userSettings;
	}

	public void removeSettings(Settings settings) throws WebicalException {
	}

	private UserSettings m_userSettings = null;
	private PluginSettings m_pluginSettings = null;
	private UserPluginSettings m_userPluginSettings = null;
}
