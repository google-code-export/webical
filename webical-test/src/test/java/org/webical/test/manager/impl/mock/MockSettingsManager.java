package org.webical.manager.impl.mock;

import org.webical.PluginSettings;
import org.webical.Settings;
import org.webical.User;
import org.webical.UserPluginSettings;
import org.webical.UserSettings;
import org.webical.manager.SettingsManager;
import org.webical.manager.WebicalException;

public class MockSettingsManager implements SettingsManager {

	public PluginSettings getPluginSettings(String pluginClass) throws WebicalException {
		return null;
	}

	public UserPluginSettings getUserPluginSettings(String pluginClass, User user) throws WebicalException {
		return null;
	}

	public UserSettings getUserSettings(User user) throws WebicalException {
		return null;
	}

	public void removeSettings(Settings settings) throws WebicalException {

	}

	public void storePluginSettings(PluginSettings pluginSettings) throws WebicalException {

	}

	public void storeUserPluginSettings(UserPluginSettings userPluginSettings) throws WebicalException {

	}

	public void storeUserSettings(UserSettings userSettings) throws WebicalException {

	}

}
